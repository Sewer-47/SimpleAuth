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

import com.github.sewer47.event.PlayerRegistrationEvent;
import com.github.sewer47.event.PlayerUnregisterEvent;
import com.github.sewer47.i18n.MessageManager;
import com.github.sewer47.module.Module;
import com.github.sewer47.module.ModuleInfo;
import com.github.sewer47.user.User;
import com.github.sewer47.user.UserManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@ModuleInfo(name = "captchaModule")
public class CaptchaModule extends Module {

    private UserManager userManager;
    private MessageManager messageManager;
    private Collection<String> labels = new ArrayList<>();
    private Map<UUID, String> byUniqueId = new ConcurrentHashMap<>();
    private int length;
    private char[] allowedChars;
    private ChatMessageType position;

    @Override
    public void onEnable() {
        super.onEnable();

        this.userManager = this.getPlugin().getUserManager();
        this.messageManager = this.getPlugin().getMessageManager();

        FileConfiguration config = this.getPlugin().getConfig();
        this.length = config.getInt("captchaLength");
        this.allowedChars = config.getString("captchaChars").toCharArray();
        this.position = ChatMessageType.valueOf(config.getString("captchaPosition").toUpperCase());

        Map<String, Object> command = this.getPlugin().getDescription().getCommands().get("register");
        this.labels.add("register");
        Collection<String> alises = (Collection<String>) command.get("aliases");
        this.labels.addAll(alises);
    }

    @EventHandler(ignoreCancelled = true)
    public void onCommand(PlayerCommandPreprocessEvent event) {

        User user = this.userManager.getUser(event.getPlayer());

        if (user == null) {
            return;
        }

        String[] args = event.getMessage().split(" ");
        if (!this.labels.contains(args[0].toLowerCase().substring(1, args[0].length()))) {
            return;
        }
        if (args.length != 4) {
            user.sendMessage("command.register.correct.usage");
            event.setCancelled(true);
            return;
        }
        if (!args[3].equals(this.byUniqueId.get(user.getUniqueId()))) {
            user.sendMessage("captcha.code.wrong");
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        User user = this.userManager.getUser(event.getPlayer());
        if (user == null) {
            return;
        }

        if (user.getStatus().isRegistered()) {
            return;
        }
        this.add(user);
    }

    @EventHandler
    public void onUnregister(PlayerUnregisterEvent event) {
        User user = this.userManager.getUser(event.getPlayer());
        if (user == null) {
            return;
        }

        this.add(user);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        this.removeIfExist(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onRegister(PlayerRegistrationEvent event) {
        this.removeIfExist(event.getPlayer().getUniqueId());
    }

    private void removeIfExist(UUID playerId) {
        if (this.byUniqueId.containsKey(playerId)) {
            this.byUniqueId.remove(playerId);
        }
    }

    private void add(User user) {
        String captcha = this.generateCaptcha(this.length);

        this.byUniqueId.put(user.getUniqueId(), captcha);

        String text = this.messageManager.getMessage(user.getLocale(), "captcha.code", captcha);
        BaseComponent message = new TextComponent(ChatColor.translateAlternateColorCodes('&', text));
        user.getBukkit().spigot().sendMessage(this.position, message);
    }

    private String generateCaptcha(int size) {
        Random random = new Random();
        String captha = "";
        for (int i = 0; i < size; i++) {
            captha = captha + this.allowedChars[random.nextInt(this.allowedChars.length)];
        }
        return captha;
    }
}
