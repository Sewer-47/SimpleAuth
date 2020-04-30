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

package com.github.sewer47.module;

import com.github.sewer47.SimpleAuthPlugin;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.logging.Level;

public abstract class Module implements ModuleListener, Listener {

    private String name;
    private boolean enabled;
    private boolean enabling;
    private SimpleAuthPlugin plugin;

    public final void initialize(SimpleAuthPlugin plugin) {
        this.plugin = plugin;

        ModuleInfo info = this.getClass().getAnnotation(ModuleInfo.class);
        if (info == null || info.name() == null) {
            throw new RuntimeException("Module must have have name");
        }
        this.name = info.name();
    }

    public void setEnabled(boolean enabled) {
        if (this.enabled == enabled && !enabling) {
            throw new IllegalStateException(this.getName() + " already " + (enabled ? "enabled" : "disabled") + "!");
        }

        try {

            this.plugin.getLogger().log(Level.INFO, (enabled ? "Enabling" : "Disabling") + " module " + this.getName() + "...");

            if (enabled) {
                this.enabling = true;

                this.onEnable();
            } else {
               this.onDisable();
            }

            this.enabled = enabled;
            this.enabling = false;

        } catch (Throwable th) {
            this.plugin.getLogger().log(Level.SEVERE, "Could not " + (enabled ? "enable" : "disable") + " " + this.getName() + ".", th);
        }
    }

    @Override
    public void onEnable() {
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public final String getName() {
        return this.name;
    }

    public SimpleAuthPlugin getPlugin() {
        return this.plugin;
    }
}
