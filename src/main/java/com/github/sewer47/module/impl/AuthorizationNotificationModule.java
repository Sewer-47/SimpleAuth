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

import com.github.sewer47.module.ModuleInfo;
import com.github.sewer47.user.User;
import com.github.sewer47.user.UserAuthStatus;
import com.github.sewer47.module.Module;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collection;

@ModuleInfo(name = "authorizationNotificationModule")
public class AuthorizationNotificationModule extends Module implements Runnable {

    private Collection<User> users;
    private BukkitTask bukkit;

    @Override
    public void onEnable() {
        this.users = this.getPlugin().getUserManager().getOnline();
        int delay = this.getPlugin().getConfig().getInt("authNotificationDelay");
        this.bukkit = this.getPlugin().getServer().getScheduler().runTaskTimer(this.getPlugin(), this, 1L, 20L * delay);
    }

    @Override
    public void onDisable() {
        this.bukkit.cancel();
    }

    @Override
    public void run() {
        for (User user : this.users) {
            UserAuthStatus authStatus = user.getStatus();

            if (!authStatus.isRegistered()) {
                user.sendMessage("notification.register");
                continue;
            }

            if (!authStatus.isLogged()) {
                user.sendMessage("notification.login");
                continue;
            }
        }
    }
}
