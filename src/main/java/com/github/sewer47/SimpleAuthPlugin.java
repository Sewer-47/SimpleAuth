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

package com.github.sewer47;

import com.github.sewer47.command.*;
import com.github.sewer47.hook.Hook;
import com.github.sewer47.hook.SkriptHook;
import com.github.sewer47.i18n.MessageListeners;
import com.github.sewer47.i18n.MessageLoader;
import com.github.sewer47.i18n.MessageManager;
import com.github.sewer47.module.Module;
import com.github.sewer47.module.ModuleManager;
import com.github.sewer47.module.impl.*;
import com.github.sewer47.module.impl.AuthorizationNotificationModule;
import com.github.sewer47.password.PasswordGenerator;
import com.github.sewer47.password.SimplePasswordGenerator;
import com.github.sewer47.storage.Storage;
import com.github.sewer47.storage.YamlStorage;
import com.github.sewer47.task.PlayerMovementTask;
import com.github.sewer47.user.User;
import com.github.sewer47.user.UserManager;
import com.github.sewer47.user.UserTrackerListeners;
import org.apache.commons.lang.LocaleUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.apache.logging.log4j.core.Logger;
import java.util.Locale;

public class SimpleAuthPlugin extends JavaPlugin {

    private UserManager userManager;
    private MessageManager messageManager;
    private ModuleManager moduleManager;
    private PasswordGenerator passwordGenerator;
    private Storage storage;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        FileConfiguration config = this.getConfig();

        this.passwordGenerator = new SimplePasswordGenerator();

        this.storage = new YamlStorage(this);
        this.storage.initialize();

        this.userManager = new UserManager();
        Locale defaultLang = LocaleUtils.toLocale(config.getString("defaultLang"));
        this.messageManager = new MessageManager(defaultLang);

        MessageLoader messageLoader = new MessageLoader(this);
        messageLoader.extract(this.getFile().getAbsoluteFile());
        messageLoader.loadAll();

        this.moduleManager = new ModuleManager();

        PluginManager pluginManager = this.getServer().getPluginManager();
        BukkitScheduler scheduler = this.getServer().getScheduler();

        this.registerListeners(pluginManager);
        this.registerCommands();
        this.registerUsers();
        this.registerModules(config.getConfigurationSection("modules"));
        this.registerTasks(scheduler);

        Logger logger = (Logger) LogManager.getRootLogger();
        LogFilter filter = new LogFilter();
        filter.initialize(this);
        logger.addFilter(filter);

        Hook skriptHook = new SkriptHook(this);
        skriptHook.tryHook();
    }

    @Override
    public void onDisable() {

    }

    private void registerCommands() {
        this.getCommand("register").setExecutor(new RegisterCommand(this));
        this.getCommand("unregister").setExecutor(new UnregisterCommand(this));
        this.getCommand("login").setExecutor(new LoginCommand(this));
        this.getCommand("logout").setExecutor(new LogoutCommand(this));
        this.getCommand("changepassword").setExecutor(new ChangePasswordCommand(this));
        this.getCommand("module").setExecutor(new ModuleCommand(this));
    }

    private void registerModules(ConfigurationSection moduleConfig) {
        for (Module module : new Module[] {
            new AuthorizationInteractionModule(),
            new PasswordMatchModule(),
            new NicknamePasswordModule(),
            new PasswordLengthModule(),
            new AuthorizationNotificationModule(),
            new RegistrationBanModule(),
            new BlackListPasswordModule(),
            new LoginAttemptsModule(),
            new AuthorizationTimeoutModule(),
            new LoginSessionModule(),
            new PasswordAllowedCharsModule(),
            new CaptchaModule(),
        }) {
            module.initialize(this);
            if (moduleConfig.getBoolean(module.getName())) {
                module.setEnabled(true);
            }

            this.moduleManager.registerModule(module);
        }
    }

    private void registerListeners(PluginManager pluginManager) {
        pluginManager.registerEvents(new UserTrackerListeners(this), this);
        pluginManager.registerEvents(new MessageListeners(this), this);
    }

    private void registerUsers() {
        for (Player bukkit : this.getServer().getOnlinePlayers()) {
            User user = this.storage.loadUser(bukkit);
            if (user == null) {
                user = new User(bukkit, this);
            }
            this.userManager.registerUser(user);
        }
    }

    private void registerTasks(BukkitScheduler scheduler) {
        scheduler.runTaskTimer(this, new PlayerMovementTask(this), 1L, 5L);
    }

    public UserManager getUserManager() {
        return this.userManager;
    }

    public MessageManager getMessageManager() {
        return this.messageManager;
    }

    public ModuleManager getModuleManager() {
        return this.moduleManager;
    }

    public PasswordGenerator getPasswordGenerator() {
        return this.passwordGenerator;
    }

    public Storage getStorage() {
        return this.storage;
    }
}
