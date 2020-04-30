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

import com.github.sewer47.event.PlayerAuthorizationEvent;
import com.github.sewer47.event.PlayerLogoutEvent;
import com.github.sewer47.i18n.MessageManager;
import com.github.sewer47.module.Module;
import com.github.sewer47.module.ModuleInfo;
import com.github.sewer47.user.User;
import com.github.sewer47.user.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ModuleInfo(name = "authorizationTimeoutModule")
public class AuthorizationTimeoutModule extends Module implements Runnable {

    private UserManager userManager;
    private MessageManager messageManager;
    private BukkitTask bukkit;
    private int maxTime;
    private final Map<UUID, Integer> byUniqueId = new ConcurrentHashMap<>();

    @Override
    public void onEnable() {
        super.onEnable();

        this.maxTime = this.getPlugin().getConfig().getInt("authTimeout");
        this.userManager = this.getPlugin().getUserManager();
        this.messageManager = this.getPlugin().getMessageManager();
        this.bukkit = this.getPlugin().getServer().getScheduler().runTaskTimer(this.getPlugin(), this, 1L, 20L * 1);

        for (User user : this.userManager.getOnline()) {
            this.byUniqueId.put(user.getUniqueId(), 0);
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.bukkit.cancel();
    }

    @Override
    public void run() {
        for (UUID playerId : this.byUniqueId.keySet()) {
            int time = this.byUniqueId.get(playerId);

            if (time >= this.maxTime) {

                User user = this.userManager.getUser(playerId);
                if (user == null) {
                    continue;
                }

                if (user.getStatus().isLogged()) {//trzeba to potem ogarnacXD
                    this.remove(user.getBukkit());
                    continue;
                }

                String kickMessage = ChatColor.translateAlternateColorCodes('&', this.messageManager.getMessage(user.getLocale(), "login.timeout"));
                Bukkit.getPlayer(user.getUniqueId()).kickPlayer(kickMessage);

                continue;
            }

            this.byUniqueId.replace(playerId, time + 1);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        this.byUniqueId.put(event.getPlayer().getUniqueId(), 0);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        this.remove(event.getPlayer());
    }

    @EventHandler
    public void onLogin(PlayerAuthorizationEvent event) {
        this.remove(event.getPlayer());
    }

    @EventHandler
    public void onLogout(PlayerLogoutEvent event) {
        this.byUniqueId.put(event.getPlayer().getUniqueId(), 0);
    }

    private void remove(Player player) {
        UUID playerId = player.getUniqueId();
        if (this.byUniqueId.containsKey(playerId)) {
            this.byUniqueId.remove(playerId);
        }
    }
}
