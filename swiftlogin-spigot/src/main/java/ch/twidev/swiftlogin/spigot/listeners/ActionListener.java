/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.spigot.listeners;

import ch.twidev.swiftlogin.common.configuration.schema.BackendConfiguration;
import ch.twidev.swiftlogin.spigot.RegistrationState;
import ch.twidev.swiftlogin.spigot.SwiftLoginSpigot;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;

public class ActionListener implements Listener {

    private final SwiftLoginSpigot spigot;

    public ActionListener(SwiftLoginSpigot spigot) {
        this.spigot = spigot;
    }

    public boolean isCancellable(Entity entity) {
        if (!(entity instanceof Player)) return false;
        if(entity.hasMetadata("NPC")) return false;

        if(!BackendConfiguration.getPreventInteraction()) return false;

        return !SwiftLoginSpigot.getPlayerRegistrations().containsKey(entity.getUniqueId()) || SwiftLoginSpigot.getPlayerRegistrations().get(entity.getUniqueId()).getRegistrationState() != RegistrationState.AUTHORIZED;
    }

    public void checkCancel(Cancellable cancellable, Entity entity) {
        if(isCancellable(entity)) {
            cancellable.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        this.checkCancel(event, event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        this.checkCancel(event, event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPickup(PlayerPickupItemEvent event) {
        this.checkCancel(event, event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDrop(PlayerDropItemEvent event) {
        this.checkCancel(event, event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        this.checkCancel(event, event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInteractAtEntity(PlayerInteractAtEntityEvent event) {
        this.checkCancel(event, event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        this.checkCancel(event, event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        this.checkCancel(event, event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCommand(InventoryClickEvent event) {
        this.checkCancel(event, event.getWhoClicked());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCommand(FoodLevelChangeEvent event) {
        this.checkCancel(event, event.getEntity());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCommand(InventoryOpenEvent event) {
        this.checkCancel(event, event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCommand(EntityDamageByEntityEvent event) {
        this.checkCancel(event, event.getEntity());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCommand(EntityDamageEvent event) {
        this.checkCancel(event, event.getEntity());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCommand(EntityTargetLivingEntityEvent event) {
        this.checkCancel(event, event.getEntity());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCommand(EntityDamageByBlockEvent event) {
        this.checkCancel(event, event.getEntity());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onHeld(PlayerItemHeldEvent event) {
        this.checkCancel(event, event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        if(!BackendConfiguration.getPreventMovement()) return;

        Location from = event.getFrom();
        Location to = event.getTo();

        if(from.distance(to) >= 0.01D) {
            if(isCancellable(event.getPlayer())) {
                from.setYaw(to.getYaw());
                from.setPitch(to.getPitch());
                event.setTo(from);
            }
        }
    }

}
