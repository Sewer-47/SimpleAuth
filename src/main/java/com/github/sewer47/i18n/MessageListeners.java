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

package com.github.sewer47.i18n;

import com.github.sewer47.SimpleAuthPlugin;
import com.github.sewer47.user.User;
import com.github.sewer47.user.UserManager;
import org.apache.commons.lang.StringUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLocaleChangeEvent;

import java.util.Locale;

public class MessageListeners implements Listener {

    private final UserManager userManager;
    private final MessageManager messageManager;

    public MessageListeners(SimpleAuthPlugin plugin) {
        this.userManager = plugin.getUserManager();
        this.messageManager = plugin.getMessageManager();
    }

    @EventHandler
    public void onLocaleChange(PlayerLocaleChangeEvent event) {
        User user = this.userManager.getUser(event.getPlayer());
        if (user == null) {
            return;
        }
        String name = event.getLocale();
        String language = StringUtils.substring(name, 0 ,2);
        String country = StringUtils.substring(name, 3, 5);
        Locale locale = new Locale(language, country);
        if (this.messageManager.getLocaleMap().keySet().contains(locale)) {
            user.setLocale(locale);
        }
    }
}
