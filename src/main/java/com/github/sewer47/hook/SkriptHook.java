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

package com.github.sewer47.hook;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.util.SimpleEvent;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import com.github.sewer47.SimpleAuthPlugin;
import com.github.sewer47.event.*;
import org.bukkit.plugin.PluginManager;

import java.util.logging.Level;

public class SkriptHook implements Hook {

    private final PluginManager pluginManager;
    private final SimpleAuthPlugin plugin;

    public SkriptHook(SimpleAuthPlugin plugin) {
        this.pluginManager = plugin.getServer().getPluginManager();
        this.plugin = plugin;
    }

    @Override
    public void tryHook() {
        if (this.pluginManager.getPlugin("skript") != null) {
            Skript.registerEvent("auth login", SimpleEvent.class, PlayerAuthorizationEvent.class, "auth login");//sprawdzic czy zadziala bez ""
            Skript.registerEvent("auth loginFailure", SimpleEvent.class, PlayerAuthorizationFailureEvent.class, "auth loginFailure");
            Skript.registerEvent("auth logout", SimpleEvent.class, PlayerLogoutEvent.class, "auth logout");
            Skript.registerEvent("auth register", SimpleEvent.class, PlayerRegistrationEvent.class, "auth register");
            Skript.registerEvent("auth unregister", SimpleEvent.class, PlayerUnregisterEvent.class, "auth unregister");
            Skript.registerEvent("auth changepassword", SimpleEvent.class, PlayerChangePasswordEvent.class, "auth changepassword");

            EventValues.registerEventValue(PlayerAuthorizationEvent.class, PlayerAuthorizationEvent.Cause.class, new Getter<>() {
                @Override
                public PlayerAuthorizationEvent.Cause get(PlayerAuthorizationEvent event) {
                    return event.getCause();
                }
            }, 0);
            this.plugin.getLogger().log(Level.INFO, "Skript hook has been enabled succesful");
        }
    }
}