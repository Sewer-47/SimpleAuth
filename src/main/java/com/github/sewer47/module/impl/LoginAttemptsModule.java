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
import com.github.sewer47.event.PlayerAuthorizationFailureEvent;
import com.github.sewer47.i18n.MessageManager;
import com.github.sewer47.module.Module;
import com.github.sewer47.module.ModuleInfo;
import com.github.sewer47.user.User;
import com.github.sewer47.user.UserManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ModuleInfo(name = "loginAttemptsModule")
public class LoginAttemptsModule extends Module {

    private final Map<UUID, Integer> attemps = new ConcurrentHashMap<>();
    private UserManager userManager;
    private MessageManager messageManager;
    private int maxAttemps;

    @Override
    public void onEnable() {
        super.onEnable();
        this.userManager = this.getPlugin().getUserManager();
        this.messageManager = this.getPlugin().getMessageManager();
        this.maxAttemps = this.getPlugin().getConfig().getInt("authMaxAttemps");
    }

    @EventHandler(ignoreCancelled = true)
    public void onLoginFailure(PlayerAuthorizationFailureEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        if (!this.attemps.containsKey(playerId)) {
            this.attemps.put(playerId, 1);
        } else {
            int attemps = this.attemps.get(playerId);
            if (attemps >= this.maxAttemps) {

                User user = this.userManager.getUser(playerId);
                if (user == null) {
                    return;
                }

                String kickMessage = ChatColor.translateAlternateColorCodes('&', this.messageManager.getMessage(user.getLocale(), "login.max.attemps"));
                player.kickPlayer(kickMessage);
            } else {
                this.attemps.replace(playerId, attemps + 1);
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        this.removePlayer(event.getPlayer());
    }

    @EventHandler
    public void onLogin(PlayerAuthorizationEvent event) {
        this.removePlayer(event.getPlayer());
    }

    private void removePlayer(Player player) {
        UUID playerId = player.getUniqueId();
        if (this.attemps.containsKey(playerId)) {
            this.attemps.remove(playerId);
        }
    }
}
