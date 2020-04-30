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
import com.github.sewer47.event.PlayerChangePasswordEvent;
import com.github.sewer47.password.Password;
import com.github.sewer47.user.User;
import com.github.sewer47.user.UserAuthStatus;
import org.bukkit.command.Command;

public class ChangePasswordCommand extends UserCommand {

    private final static String PERMISSION = "simpleauth.changepassword";

    private final SimpleAuthPlugin plugin;

    public ChangePasswordCommand(SimpleAuthPlugin plugin) {
        super(plugin.getUserManager(), PERMISSION);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(User user, Command command, String label, String[] args) {

        UserAuthStatus authStatus = user.getStatus();

        if (args.length != 2) {
            user.sendMessage("command.changepassword.correct.usage");
            return false;
        }

        if (!authStatus.isRegistered()) {
            user.sendMessage("command.changepassword.error.unregistered");
            return true;
        }

        if (!authStatus.isLogged()) {
            user.sendMessage("command.changepassword.error.no.logged");
            return true;
        }

        Password userPassword = user.getPassword();

        Password password = this.plugin.getPasswordGenerator().generate(args[0], userPassword.getSalt());

        if (password == null | !userPassword.equals(password)) {
            user.sendMessage("command.changepassword.error.incorrect");
            return true;
        }

        //moduly na blackliste hasel, nicki etx

        PlayerChangePasswordEvent event = new PlayerChangePasswordEvent(user.getBukkit());
        this.plugin.getServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) {

            user.setPassword(this.plugin.getPasswordGenerator().generate(args[1]));
            user.sendMessage("command.changepassword.success");

            this.plugin.getStorage().saveUser(user);
        }
        return true;
    }
}
