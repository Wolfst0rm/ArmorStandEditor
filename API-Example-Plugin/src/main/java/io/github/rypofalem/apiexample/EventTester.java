package main.java.io.github.rypofalem.apiexample;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

public class EventTester implements Listener {

	Player player;

	//ArmorStandRenameEvent
	@EventHandler
	public void renameArmorStand(PlayerInteractAtEntityEvent ASRenameEvent){
		player = ASRenameEvent.getPlayer();
		ASRenameEvent.setCancelled(true);
		if(ASRenameEvent.isCancelled()) {
			player.sendMessage("ArmorStandRenameEvent has been cancelled");
		} else{
			player.sendMessage("ArmorStandRenameEvent has not been cancelled. Continuing....");
		}
	}

	//ArmorStandManipEvent
	@EventHandler
	public void manipulateArmorStand(PlayerInteractAtEntityEvent ASManipEvent){
		player = ASManipEvent.getPlayer();
		ASManipEvent.setCancelled(true);
		if(ASManipEvent.isCancelled()) {
			player.sendMessage("ArmorStandManipulationEvent has been cancelled");
		} else{
			player.sendMessage("ArmorStandManipulationEvent has not been cancelled. Continuing....");
		}
	}

	//ArmorStandTargetedEvent
	@EventHandler
	public void targetEvent(PlayerSwapHandItemsEvent targetASEvent){
		player = targetASEvent.getPlayer();
		targetASEvent.setCancelled(true);
		if(targetASEvent.isCancelled()) {
			player.sendMessage("ArmorStandTargetedEvent has been cancelled");
		} else{
			player.sendMessage("ArmorStandTargetedEvent has not been cancelled. Continuing....");
		}
	}

	//PlayerOpenMenuEvent
	//onArmorStandDamage EntityDamageByEntityEvent event
	@EventHandler
	public void playerOpeningMenuEvent(EntityDamageByEntityEvent ASEDamageMenuOpenEvent){
		player = (Player) ASEDamageMenuOpenEvent.getDamager();
		ASEDamageMenuOpenEvent.setCancelled(true);
		if(ASEDamageMenuOpenEvent.isCancelled()) {
			player.sendMessage("PlayerOpenMenuEvent has been cancelled");
		} else{
			player.sendMessage("PlayerOpenMenuEvent has not been cancelled. Continuing....");
		}
	}

	//Also PlayerOpenMenuEvent when RightClicking/Interacting
	@EventHandler
	public void playerOpeningMenuRightClickEvent(PlayerInteractEvent ASERightClickMenuOpenEvent){
		player = ASERightClickMenuOpenEvent.getPlayer();
		ASERightClickMenuOpenEvent.setCancelled(true);
		if(ASERightClickMenuOpenEvent.isCancelled()) {
			player.sendMessage("PlayerOpenMenuEvent has been cancelled");
		} else{
			player.sendMessage("PlayerOpenMenuEvent has not been cancelled. Continuing....");
		}
	}

}
