/*
 * MIT License
 *
 * Copyright (c) 2020 Seweryn S
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.sewer47.module.impl;

import com.github.sewer47.event.PlayerMovementEvent;
import com.github.sewer47.module.Module;
import com.github.sewer47.module.ModuleInfo;
import com.github.sewer47.user.User;
import com.github.sewer47.user.UserManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.event.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@ModuleInfo(name = "loggedOutMoveModule")
public class AuthorizationInteractionModule extends Module {

    private UserManager userManager;
    private Collection<String> allowedCommands = new ArrayList<>();

    @Override
    public void onEnable() {
        super.onEnable();

        this.userManager = this.getPlugin().getUserManager();

        FileConfiguration config = this.getPlugin().getConfig();

        for (Map.Entry<String, Map<String, Object>> command : this.getPlugin().getDescription().getCommands().entrySet()) {

            if (command.getKey().equalsIgnoreCase("module")) {
                continue;
            }

            this.allowedCommands.add(command.getKey());

            Collection<String> allises = (Collection<String>) command.getValue().get("aliases");
            if (allises == null) {
                continue;
            }
            this.allowedCommands.addAll(allises);

        }

        this.allowedCommands.addAll(config.getStringList("allowedCommands"));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        this.cancelIfNotAuthorized(event.getEntity(), event);
        this.cancelIfNotAuthorized(event.getDamager(), event);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        this.cancelIfNotAuthorized(event.getPlayer(), event);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        this.cancelIfNotAuthorized(event.getPlayer(), event);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        this.cancelIfNotAuthorized(event.getPlayer(), event);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onMove(PlayerMovementEvent event) {
        this.cancelIfNotAuthorized(event.getPlayer(), event);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockDamage(EntityDamageByBlockEvent event) {
        this.cancelIfNotAuthorized(event.getEntity(), event);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onCombust(EntityCombustByBlockEvent event) {
        this.cancelIfNotAuthorized(event.getEntity(), event);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPickup(EntityPickupItemEvent event) {
        this.cancelIfNotAuthorized(event.getEntity(), event);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onDrop(PlayerDropItemEvent event) {
        this.cancelIfNotAuthorized(event.getPlayer(), event);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        this.cancelIfNotAuthorized(event.getPlayer(), event);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onConsume(PlayerItemConsumeEvent event) {
        this.cancelIfNotAuthorized(event.getPlayer(), event);
    }


    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage().toLowerCase().split(" ")[0];
        if (!this.allowedCommands.contains(command.substring(1))) {
            this.cancelIfNotAuthorized(event.getPlayer(), event);
        }
    }

    private void cancelIfNotAuthorized(Entity entity, Cancellable cancellable) {
        User user = this.userManager.getUser(entity);
        if (user == null) {
            return;
        }

        if (!user.getStatus().isLogged()) {
            cancellable.setCancelled(true);
        }
    }

}


  /*  private UserManager userManager;
    private Collection<String> disabledEvents = new ArrayList<>();
    private Collection<String> allowedCommands = new ArrayList<>();

    @Override
    public void onEnable() {
        super.onEnable();

        this.userManager = this.getPlugin().getUserManager();

        FileConfiguration config = this.getPlugin().getConfig();

        for (Map.Entry<String, Map<String, Object>> command : this.getPlugin().getDescription().getCommands().entrySet()) {

            if (command.getKey().equalsIgnoreCase("module")) {
                continue;
            }

            this.allowedCommands.add(command.getKey());

            Collection<String> allises = (Collection<String>) command.getValue().get("aliases");
            if (allises == null) {
                continue;
            }
            this.allowedCommands.addAll(allises);

        }

        this.allowedCommands.addAll(config.getStringList("allowedCommands"));

        this.disabledEvents.addAll(config.getStringList("disabledEvents"));

        RegisteredListener registeredListener = new RegisteredListener(this, (listener, event) -> {
            if (event instanceof Cancellable) {
                if (event instanceof PlayerEvent) {
                    this.onPlayerEvent((PlayerEvent) event);
                }
                if (event instanceof EntityEvent) {
                    this.onEntityEvent((EntityEvent) event);
                }
            }
        }, EventPriority.NORMAL, this.getPlugin(), false);

        for (HandlerList handler : HandlerList.getHandlerLists()) {
            handler.register(registeredListener);
        }
    }

    private void onPlayerEvent(PlayerEvent event) {

        this.banIfNotAuthorized(event);
    }

    private void onEntityEvent(EntityEvent event) {
        if (event.getEntity() instanceof Player) {
            this.banIfNotAuthorized(event);
        }
    }

    private void banIfNotAuthorized(Event event) {
        if (!this.disabledEvents.contains(event.getEventName())) {
            return;
        }

        UUID uniqueId;

        if (event instanceof EntityEvent) {

            uniqueId = ((EntityEvent) event).getEntity().getUniqueId();

        } else if (event instanceof PlayerEvent) {
            uniqueId = ((PlayerEvent) event).getPlayer().getUniqueId();
        } else {
            return;
        }

        User user = this.userManager.getUser(uniqueId);
        if (user == null) {
            return;
        }

        if (user.getStatus().isLogged()) {
            return;
        }

        ((Cancellable) event).setCancelled(true);
    }*/