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

package com.github.sewer47.task;

import com.github.sewer47.SimpleAuthPlugin;
import com.github.sewer47.event.PlayerMovementEvent;
import com.github.sewer47.user.User;
import com.github.sewer47.user.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.Vector;

public class PlayerMovementTask implements Runnable {

    private final UserManager userManager;
    private final PluginManager pluginManager;

    public PlayerMovementTask(SimpleAuthPlugin plugin) {
        this.userManager = plugin.getUserManager();
        this.pluginManager = plugin.getServer().getPluginManager();
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {

            User user = this.userManager.getUser(player);

            if (user == null) {
                continue;
            }

            Vector lastPosition = user.getPosition();
            Vector newPosition = player.getLocation().toVector();

            PlayerMovementEvent event = new PlayerMovementEvent(player, lastPosition, newPosition);
            this.pluginManager.callEvent(event);

            if (!event.isCancelled()) {
                user.setPosition(newPosition);
            } else {
                player.teleport(new Location(player.getLocation().getWorld(), lastPosition.getX(), lastPosition.getY(), lastPosition.getZ()));
            }
        }
    }
}
