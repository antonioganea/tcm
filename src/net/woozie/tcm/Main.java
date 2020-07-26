package net.woozie.tcm;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import scala.Int;

public class Main extends JavaPlugin  implements Listener {

	public ArrayList<UUID> sortingPlayers = new ArrayList<>();
	
    @Override
    public void onEnable() {
        System.out.println("[TCM] Booting...");
        this.getServer().getPluginManager().registerEvents(this, this);
        System.out.println("[TCM] Registered event handler!");
        System.out.println("[TCM] Enabled!");
    }
    
    @Override
    public void onDisable() {
        System.out.println("[TCM] Disabled.");
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if ( cmd.getName().equalsIgnoreCase("sort") ) {
			sender.sendMessage("Sorting...");
			if ( sender instanceof Player ) {
				UUID playerUUID = ((Player) sender).getUniqueId();
				if ( !sortingPlayers.contains(playerUUID) ) {
					sortingPlayers.add(playerUUID);
				} else {
					sender.sendMessage("Already sorting");
				}
			}
		}
		return true;
    }
    
    @EventHandler()
    public void onRightClick(PlayerInteractEvent event) {
    	Block block = event.getClickedBlock();
    	if ( block == null ) { return; }
    	
    	BlockState blockState = block.getState();
    	
    	if ( blockState instanceof Chest ) {
    		if ( sortingPlayers.contains(event.getPlayer().getUniqueId()) ) {
    			event.setCancelled(true);
    			// event.getPlayer().sendMessage("Sorted!");
    			Chest chest = (Chest)blockState;
    			Utils.compress(chest.getInventory());
    			sortingPlayers.remove(event.getPlayer().getUniqueId());
    		}
    		
    	}
    }
}
