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
import com.github.sewer47.event.PlayerAuthorizationEvent;
import com.github.sewer47.event.PlayerAuthorizationFailureEvent;
import com.github.sewer47.password.Password;
import com.github.sewer47.user.User;
import com.github.sewer47.user.UserAuthStatus;
import org.bukkit.command.Command;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;

public class LoginCommand extends UserCommand {

    private final static String PERMISSION = "simpleauth.changepassword";

    private final PluginManager pluginManager;
    private final SimpleAuthPlugin plugin;

    public LoginCommand(SimpleAuthPlugin plugin) {
        super(plugin.getUserManager(), PERMISSION);
        this.pluginManager = plugin.getServer().getPluginManager();
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(User user, Command command, String label, String[] args) {
        UserAuthStatus authStatus = user.getStatus();

        if (!authStatus.isRegistered()) {
            user.sendMessage("command.login.error.unregistered");
            return true;
        }

        if (authStatus.isLogged()) {
            user.sendMessage("command.login.error.logged");
            return true;
        }

        if (args.length != 1) {
            user.sendMessage("command.login.correct.usage");
            return false;
        }

        Password userPassword = user.getPassword();
        Password password = this.plugin.getPasswordGenerator().generate(args[0], userPassword.getSalt());

        if (!userPassword.equals(password)) {
            user.sendMessage("command.login.incorrect");
            Event event = new PlayerAuthorizationFailureEvent(user.getBukkit());
            this.pluginManager.callEvent(event);
            return true;
        }

        PlayerAuthorizationEvent event = new PlayerAuthorizationEvent(user.getBukkit(), PlayerAuthorizationEvent.Cause.LOGIN);
        this.pluginManager.callEvent(event);

        if (!event.isCancelled()) {
            authStatus.setLogged(true);
            user.sendMessage("command.login.success");
        }
        return true;
    }
}
