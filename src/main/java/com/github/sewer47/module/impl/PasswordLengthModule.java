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

import com.github.sewer47.module.Module;
import com.github.sewer47.module.ModuleInfo;
import com.github.sewer47.user.User;
import com.github.sewer47.user.UserManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@ModuleInfo(name = "passwordLengthModule")
public class PasswordLengthModule extends Module {

    private final Collection<String> labels = new ArrayList<>();
    private UserManager userManager;
    private int minPasswordLength;
    private int maxPasswordLength;

    @Override
    public void onEnable() {
        super.onEnable();
        this.userManager = this.getPlugin().getUserManager();

        Map<String, Object> command = this.getPlugin().getDescription().getCommands().get("register");
        this.labels.add("register");
        Collection<String> alises = (Collection<String>) command.get("aliases");
        this.labels.addAll(alises);

        FileConfiguration config = this.getPlugin().getConfig();

        this.minPasswordLength = config.getInt("minPasswordLength");
        this.maxPasswordLength = config.getInt("maxPasswordLength");
    }

    @EventHandler(ignoreCancelled = true)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        String[] args = event.getMessage().split(" ");
        if (args.length != 3) {
            return;
        }
        if (this.labels.contains(args[0].toLowerCase().substring(1, args[0].length()))) {
            User user = this.userManager.getUser(event.getPlayer());

            if (args[1].length() < this.minPasswordLength) {
                user.sendMessage("command.register.error.too.short");
                event.setCancelled(true);
            }

            if (args[1].length() > this.maxPasswordLength) {
                user.sendMessage("command.register.error.too.long");
                event.setCancelled(true);
            }
        }
    }
}
