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

package com.github.sewer47.command;

import com.github.sewer47.SimpleAuthPlugin;
import com.github.sewer47.module.Module;
import com.github.sewer47.module.ModuleManager;
import com.github.sewer47.user.User;
import com.github.sewer47.user.UserManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ModuleCommand implements CommandExecutor {

    private final UserManager userManager;
    private final ModuleManager moduleManager;

    public ModuleCommand(SimpleAuthPlugin plugin) {
        this.userManager = plugin.getUserManager();
        this.moduleManager = plugin.getModuleManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        User user = this.userManager.getUser(sender);
        if (user == null) {
            return true;
        }

        if (!sender.hasPermission("simpleauth.module") && !sender.hasPermission("simpleauth.admin")) {
            user.sendMessage("permission.insufficient", "simpleauth.module");
            return true;
        }

        if (args.length == 0) {
            user.sendMessage("command.module.correct.usage");
            return false;
        }


        if (args[0].equalsIgnoreCase("list")) {
            user.sendMessage("command.module.list.start");
            for (Module module : this.moduleManager.getRegistered()) {
                user.sendMessage("command.module.list.module", module.getName(), module.isEnabled());
            }
            user.sendMessage("command.module.list.end");
            return true;
        }

        String firstArgument = args[0].toLowerCase();

        if (args.length == 2 && firstArgument.equals("enable") || firstArgument.equals("disable")) {

            Module module = this.moduleManager.getModule(args[1]);

            if (module == null) {
                user.sendMessage("command.module.unknown");
                return true;
            }

            switch (firstArgument) {
                case "enable": {
                    if (module.isEnabled()) {
                        user.sendMessage("command.module.enable.failure", module.getName());
                    } else {
                        module.setEnabled(true);
                        user.sendMessage("command.module.enable.success", module.getName());
                    }
                    return true;
                }

                case "disable": {
                    if (!module.isEnabled()) {
                        user.sendMessage("command.module.disable.failure", module.getName());
                    } else {
                        module.setEnabled(false);
                        user.sendMessage("command.module.disable.success", module.getName());
                    }
                    return true;
                }
            }
        }

        user.sendMessage("command.module.correct.usage");
        return false;
    }
}
