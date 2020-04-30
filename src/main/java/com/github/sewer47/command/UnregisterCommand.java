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
import com.github.sewer47.event.PlayerLogoutEvent;
import com.github.sewer47.event.PlayerUnregisterEvent;
import com.github.sewer47.password.Password;
import com.github.sewer47.storage.Storage;
import com.github.sewer47.user.User;
import com.github.sewer47.user.UserAuthStatus;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

public class UnregisterCommand extends UserCommand {

    private final static String PERMISSION = "simpleauth.unregister";

    private final Storage storage;
    private final PluginManager pluginManager;
    private final SimpleAuthPlugin plugin;

    public UnregisterCommand(SimpleAuthPlugin plugin) {
        super(plugin.getUserManager(), PERMISSION);
        this.storage = plugin.getStorage();
        this.pluginManager = plugin.getServer().getPluginManager();
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(User user, Command command, String label, String[] args) {
        if (args.length != 1) {
            user.sendMessage("command.unregister.correct.usage");
            return false;
        }

        UserAuthStatus authStatus = user.getStatus();

        if (!authStatus.isRegistered()) {
            user.sendMessage("command.unregister.error.unregistered");
            return true;
        }

        if (!authStatus.isLogged()) {
            user.sendMessage("command.unregister.error.no.logged");
            return true;
        }



        Password userPassword = user.getPassword();
        Password password = this.plugin.getPasswordGenerator().generate(args[0], userPassword.getSalt());

        if (password != null && !password.equals(userPassword)) {
            user.sendMessage("command.unregister.password.incorrect");
            return true;
        }

        Player player = user.getBukkit();

        PlayerUnregisterEvent unregisterEvent = new PlayerUnregisterEvent(player);
        PlayerLogoutEvent logoutEvent = new PlayerLogoutEvent(player);
        this.pluginManager.callEvent(unregisterEvent);
        this.pluginManager.callEvent(logoutEvent);

        if (!unregisterEvent.isCancelled() && !logoutEvent.isCancelled()) {
            authStatus.setRegistered(false);
            authStatus.setLogged(false);
            user.setPassword(null);
            this.storage.deleteUser(user);
            user.sendMessage("command.unregister.success");
            return true;
        }
        return true;
    }
}
