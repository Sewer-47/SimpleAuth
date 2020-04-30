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

package com.github.sewer47.storage;

import com.github.sewer47.SimpleAuthPlugin;
import com.github.sewer47.password.Password;
import com.github.sewer47.user.User;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class YamlStorage implements Storage {

    private final File file;
    private final FileConfiguration bukkit;
    private final SimpleAuthPlugin plugin;

    public YamlStorage(SimpleAuthPlugin plugin) {
        this.file = new File(plugin.getDataFolder(), "storage.yml");
        this.bukkit = new YamlConfiguration();
        this.plugin = plugin;
    }

    @Override
    public void initialize() {
        if (!this.file.exists()) {
            try {
                this.file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            this.bukkit.load(this.file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveUser(User user) {
        UUID uniqueId = user.getUniqueId();
        Password password = user.getPassword();

        if (password == null) {
            return;
        }

        ConfigurationSection userSection = this.bukkit.createSection(uniqueId.toString());

        userSection.set("password", password.getEncoded());
        userSection.set("salt", password.getSalt());
        userSection.set("lastAdress", user.getBukkit().getAddress().getHostString());

        try {
            this.bukkit.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteUser(User user) {
        this.bukkit.set(user.getUniqueId().toString(), null);
        try {
            this.bukkit.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public User loadUser(Player bukkit) {

        UUID userId = bukkit.getUniqueId();

        ConfigurationSection userSection = this.bukkit.getConfigurationSection(userId.toString());

        if (userSection == null) {
            return null;
        }

        byte[] passwordEncoded = (byte[]) userSection.get("password");
        byte[] passwordSalt = (byte[]) userSection.get("salt");
        String lastAdress = userSection.getString("lastAdress");
        User user = new User(bukkit, this.plugin);
        user.getStatus().setRegistered(true);
        user.setLastAdress(lastAdress);


        Password password = new Password(passwordSalt, passwordEncoded);
        user.setPassword(password);
        return user;
    }
}
