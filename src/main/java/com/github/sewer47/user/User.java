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

package com.github.sewer47.user;

import com.github.sewer47.SimpleAuthPlugin;
import com.github.sewer47.i18n.MessageManager;
import com.github.sewer47.password.Password;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.UUID;

public class User implements UserProfile {

    private final Reference<Player> bukkit;
    private final String username;
    private final UUID uniqueId;
    private final MessageManager messageManager;
    private final UserAuthStatus status;
    private Password password;
    private Vector position;
    private String lastAdress;
    private Locale locale;

    public User(Player bukkit, SimpleAuthPlugin plugin) {
        this.bukkit = new WeakReference<>(bukkit);
        this.username = bukkit.getName();
        this.uniqueId = bukkit.getUniqueId();
        this.messageManager = plugin.getMessageManager();
        this.status = new UserAuthStatus();
        this.position = bukkit.getLocation().toVector();
        if (!plugin.getConfig().isBoolean("forceDefaultLang")) {
            this.setLocale(bukkit.getLocale());
        } else {
            this.locale = this.messageManager.getFallback();
        }
    }

    public void sendMessage(String message, Object... params) {
        Player bukkit = this.bukkit.get();

        if (this.locale == null) {
            return;
        }

        if (bukkit == null) {
            return;
        }

        bukkit.sendMessage(ChatColor.translateAlternateColorCodes('&', MessageFormat.format(this.messageManager.getMessage(this.locale, message), params)));
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public UUID getUniqueId() {
        return this.uniqueId;
    }

    public Player getBukkit() {
        return this.bukkit.get();
    }

    public UserAuthStatus getStatus() {
        return this.status;
    }

    public Vector getPosition() {
        return this.position;
    }

    public void setPosition(Vector position) {
        this.position = position;
    }

    public Locale getLocale() {
        return this.locale;
    }

    public void setLocale(String bukkit) {
        String language = StringUtils.substring(bukkit, 0 ,2);
        String country = StringUtils.substring(bukkit, 3, 5);

        Locale locale = new Locale(language, country);

        if (locale == null || !this.messageManager.getLocaleMap().containsKey(locale)) {
            this.locale = this.messageManager.getFallback();
        } else {
            this.locale = locale;
        }
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public Password getPassword() {
        return this.password;
    }

    public void setPassword(Password password) {
        this.password = password;
    }

    public String getLastAdress() {
        return this.lastAdress;
    }

    public void setLastAdress(String lastAdress) {
        this.lastAdress = lastAdress;
    }
}
