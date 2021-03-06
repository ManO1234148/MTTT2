package com.elytraforce.mttt2.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;

import com.elytraforce.mttt2.Main;
import com.elytraforce.mttt2.enums.GameStateEnum;
import com.elytraforce.mttt2.objects.Manager;
import com.elytraforce.mttt2.objects.MapObject;
import com.elytraforce.mttt2.objects.arena.Arena;
import com.elytraforce.mttt2.utils.WorldUtils;


public class TTTCommand implements CommandExecutor{

	private final Main mainClass;
	
	public TTTCommand(Main main) {
		this.mainClass = main;
	}
	
	@Override
	//debug command
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		Player player = (Player) sender;
		Location playerLocation = player.getLocation();
		
		//make this better later but tbh im too tired to atm
		if (args.length == 0) {
			player.sendMessage(parseColor("&c&lMTTT2&7 by Aurium_"));
			player.sendMessage("");
			player.sendMessage(parseColor("&7Usage:"));
			player.sendMessage(parseColor("&7/ttt join &c<numerical>"));  
			player.sendMessage(parseColor("&7/ttt autojoin"));  
			player.sendMessage(parseColor("&7/ttt leave")); 
			player.sendMessage("");
			player.sendMessage(parseColor("&7/ttt setLobby"));
			player.sendMessage(parseColor("&7/ttt createMap &c<map_id>"));
			player.sendMessage(parseColor("&7/ttt setSpawn &c<map_id>"));
			player.sendMessage(parseColor("&7/ttt setTester &c<map_id>"));
			player.sendMessage(parseColor("&7/ttt addGunLocation &c<map_id>"));
			player.sendMessage("");
			return true;
		}
		
		if (args[0].equalsIgnoreCase("join")) {
			Arena currentArena = null;
			try {
				 currentArena = Manager.getInstance().getArenas().get(Integer.parseInt(args[1]));
			} catch (NullPointerException e) {
				player.sendMessage(mainClass.getMessageHandler().getMessage("prefix", false) + parseColor(
						"&cNo maps have been set up. If you are not a staff member and are seeing this,"
						+ " please report this to the staff team!"));
				return true;
			} catch (IndexOutOfBoundsException e) {
				player.sendMessage(mainClass.getMessageHandler().getMessage("prefix", false) + parseColor(
						"&cNo maps have been set up. If you are not a staff member and are seeing this,"
						+ " please report this to the staff team!"));
				return true;
			}
			

			if (!currentArena.getArenaState().equals(GameStateEnum.WAITING) || !currentArena.getArenaState().equals(GameStateEnum.COUNTDOWN)  ) {
				
				if (currentArena.containsPlayer(player)) {
					player.sendMessage(mainClass.getMessageHandler().getMessage("prefix", false) + parseColor(
							"&cYou are already in a match!"));
					return true;
				}
				currentArena.addPlayer(player);
				return true;
			}
			return true;
		}
		
		if (args[0].equalsIgnoreCase("leave")) {
			Arena currentArena = null;
			try {
				 currentArena = Manager.getInstance().getArenas().get(Manager.getInstance().getSelectedArena());
			} catch (NullPointerException e) {
				player.sendMessage(mainClass.getMessageHandler().getMessage("prefix", false) + parseColor(
						"&cNo maps have been set up. If you are not a staff member and are seeing this,"
						+ " please report this to the staff team!"));
				return true;
			} catch (IndexOutOfBoundsException e) {
				player.sendMessage(mainClass.getMessageHandler().getMessage("prefix", false) + parseColor(
						"&cNo maps have been set up. If you are not a staff member and are seeing this,"
						+ " please report this to the staff team!"));
				return true;
			}
			

			if (!currentArena.getArenaState().equals(GameStateEnum.WAITING) || !currentArena.getArenaState().equals(GameStateEnum.COUNTDOWN)  ) {
				
				Manager.getInstance().findPlayerArena(player).removePlayer(player);
				return true;
			}
			return true;
		}
		
		if (args[0].equalsIgnoreCase("setLobby")) {
			if (mainClass.getMapConfigHandler().getLobbySection().getName().equals(null)) {
				player.sendMessage(mainClass.getMessageHandler().getMessage("prefix", false) + parseColor(
						"&cSomething went wrong and your Lobby section does not exist!"));
				return true;
			}
			
			mainClass.getMapConfigHandler().setLobbySection(playerLocation);
			mainClass.getMapConfigHandler().save();
			
			return true;
		}
		
		if (args[0].equalsIgnoreCase("autoJoin")) {
			Arena currentArena = null;
			try {
				 currentArena = Manager.getInstance().getArenas().get(Manager.getInstance().getSelectedArena());
			} catch (NullPointerException e) {
				player.sendMessage(mainClass.getMessageHandler().getMessage("prefix", false) + parseColor(
						"&cNo maps have been set up. If you are not a staff member and are seeing this,"
						+ " please report this to the staff team!"));
				return true;
			} catch (IndexOutOfBoundsException e) {
				player.sendMessage(mainClass.getMessageHandler().getMessage("prefix", false) + parseColor(
						"&cNo maps have been set up. If you are not a staff member and are seeing this,"
						+ " please report this to the staff team!"));
				return true;
			}
			

			if (!currentArena.getArenaState().equals(GameStateEnum.WAITING) || !currentArena.getArenaState().equals(GameStateEnum.COUNTDOWN)  ) {
				
				Manager.getInstance().getSelectedArenaAsArena().addPlayer(player);
				return true;
			}
			return true;
		}

		if (args[0].equalsIgnoreCase("createMap")) {
			if (!(args.length == 2)) {
				//not enough args!
				player.sendMessage(mainClass.getMessageHandler().getMessage("prefix", false) + parseColor(
						"&cIncorrect arguments!"));
				return true;
			}
			
			if (mainClass.getMapConfigHandler().getMapSection().contains(args[1])) {
				player.sendMessage(mainClass.getMessageHandler().getMessage("prefix", false) + parseColor(
						"&cMap already exists!"));
				return true;
			}
			
				
				String mapName = args[1];
				new MapObject(mainClass, mapName);
				
				//attempt to create the map, or if it exists teleport. i think its case sensitive idk im not a dev
				
				if (Bukkit.getWorld(mapName) != null) {
					((Player) sender).teleport(Bukkit.getWorld(mapName).getSpawnLocation());
					player.sendMessage("&eFound a world with the map name in server root, teleporting...");
				} else {

					World w = WorldUtils.createEmptyWorld(mapName);
					if (w != null)
						((Player) sender).teleport(Bukkit.getWorld(mapName).getSpawnLocation());
					player.sendMessage("&6There's no world named &e" + mapName + " &e. A new world has been created.");
				}
				
				player.sendMessage(mainClass.getMessageHandler().getMessage("prefix", false) + parseColor(
						"&cSuccessfully created map! Use commands &7/ttt setSpawn, /ttt setTester, and"
						+ "/ttt addGunLocation &cto complete the map, then run /ttt finish <name> to save it!"));
				return true;

		}
		
		if (args[0].equalsIgnoreCase("setSpawn")) {
			if (!(args.length == 2)) {
				//not enough args!
				player.sendMessage(mainClass.getMessageHandler().getMessage("prefix", false) + parseColor(
						"&cIncorrect arguments!"));
				return true;
			}
			
			if (!mainClass.getMapConfigHandler().getMapSection().contains(args[1])) {
				player.sendMessage(mainClass.getMessageHandler().getMessage("prefix", false) + parseColor(
						"&cMap does not exist!"));
				return true;
			}
			
			if (mainClass.getMapConfigHandler().getMapSection().contains(args[1])) {
				
				String mapName = args[1];
				
				mainClass.getMapConfigHandler().getMapFromString(mapName).setSpawn(playerLocation);
				
				player.sendMessage(mainClass.getMessageHandler().getMessage("prefix", false) + parseColor(
						"&cSet spawn location for map &7" + mapName + " &c!"));
				return true;
			}
			return true;
		}
		
		if (args[0].equalsIgnoreCase("setTester")) {
			if (!(args.length == 2)) {
				//not enough args!
				player.sendMessage(mainClass.getMessageHandler().getMessage("prefix", false) + parseColor(
						"&cIncorrect arguments!"));
				return true;
			}
			
			if (!mainClass.getMapConfigHandler().getMapSection().contains(args[1])) {
				player.sendMessage(mainClass.getMessageHandler().getMessage("prefix", false) + parseColor(
						"&cMap does not exist!"));
				return true;
			}
			
			if (mainClass.getMapConfigHandler().getMapSection().contains(args[1])) {
				
				String mapName = args[1];
				
				mainClass.getMapConfigHandler().getMapFromString(mapName).setTester(playerLocation);
				
				player.sendMessage(mainClass.getMessageHandler().getMessage("prefix", false) + parseColor(
						"&cSet tester location for map &7" + mapName + " &c!"));
				return true;
			}
			return true;
		}
		
		if (args[0].equalsIgnoreCase("addGunLocation")) {
			if (!(args.length == 2)) {
				//not enough args!
				player.sendMessage(mainClass.getMessageHandler().getMessage("prefix", false) + parseColor(
						"&cIncorrect arguments!"));
				return true;
			}
			
			if (!mainClass.getMapConfigHandler().getMapSection().contains(args[1])) {
				player.sendMessage(mainClass.getMessageHandler().getMessage("prefix", false) + parseColor(
						"&cMap does not exist!"));
				return true;
			}
			
			if (mainClass.getMapConfigHandler().getMapSection().contains(args[1])) {
				
				String mapName = args[1];
				
				Integer nextLocation = mainClass.getMapConfigHandler().getMapFromString(mapName).addGunLocation(playerLocation);
				
				player.sendMessage(mainClass.getMessageHandler().getMessage("prefix", false) + parseColor(
						"&cAdded gun location &7" + nextLocation + " &cfor map &7" + mapName + " &c!"));
				return true;
			}
			return true;
		}
		
		
		
		
		return false;
	}
	
	public String parseColor(String string) {
		return ChatColor.translateAlternateColorCodes('&', string);
	}
	
	

}
