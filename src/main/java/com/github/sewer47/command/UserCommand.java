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

import com.github.sewer47.user.User;
import com.github.sewer47.user.UserManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class UserCommand implements CommandExecutor {

    private final static String PLAYER_PERMISSION = "simpleauth.player";
    private final UserManager userManager;
    private final String permission;

    public UserCommand(UserManager userManager, String permission) {
        this.userManager = userManager;
        this.permission = permission;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if (sender instanceof Player) {
            User user = this.userManager.getUser(sender);

            if (user == null) {
                return true;
            }

            if (sender.hasPermission(PLAYER_PERMISSION) || sender.hasPermission(this.permission)) {
                return this.onCommand(user, command, s, strings);
            } else {
                user.sendMessage("permission.insufficient", this.permission);
            }
        } else {
            sender.sendMessage("Only players can use this command!");
        }
        return true;
    }

    public abstract boolean onCommand(User user, Command command, String label, String[] args);
}
