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
import com.github.sewer47.module.Module;
import com.github.sewer47.module.ModuleInfo;
import com.github.sewer47.user.User;
import com.github.sewer47.user.UserManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginManager;

@ModuleInfo(name = "loginSessionModule")
public class LoginSessionModule extends Module {

    private UserManager userManager;
    private PluginManager pluginManager;

    @Override
    public void onEnable() {
        super.onEnable();
        this.userManager = this.getPlugin().getUserManager();
        this.pluginManager = this.getPlugin().getServer().getPluginManager();
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {
        User user = this.userManager.getUser(event.getPlayer());

        if (user == null) {
            return;
        }

        if (!user.getStatus().isRegistered()) {
            return;
        }

        if (user.getLastAdress().equals(event.getPlayer().getAddress().getHostString())) {

            PlayerAuthorizationEvent sessionAuthorizationEvent = new PlayerAuthorizationEvent(event.getPlayer(), PlayerAuthorizationEvent.Cause.SESSION);
            this.pluginManager.callEvent(sessionAuthorizationEvent);

            if (!sessionAuthorizationEvent.isCancelled()) {
                user.getStatus().setLogged(true);
                user.sendMessage("session.login.succesful");
            }
        }
    }
}
