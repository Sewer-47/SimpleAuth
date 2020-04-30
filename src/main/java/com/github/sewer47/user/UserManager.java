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

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class UserManager {

    private final Map<UUID, User> byUniqueId = new ConcurrentHashMap<>();

    public User registerUser(User user) {
        this.byUniqueId.put(user.getUniqueId(), user);
        return user;
    }

    public void unregisterUser(User user) {
        this.byUniqueId.remove(user.getUniqueId());
    }

    public User getUser(CommandSender sender) {
        if (sender instanceof Player) {
            return this.getUser(((Player) sender).getUniqueId());
        }
        return null;
    }

    public User getUser(Player player) {
        return this.getUser(player.getUniqueId());
    }

    public User getUser(UUID uniqueId) {
        return this.byUniqueId.get(uniqueId);
    }

    public Collection<User> getOnline() {
        return this.byUniqueId.values();
    }
}
