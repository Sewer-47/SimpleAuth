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
import org.apache.commons.lang.LocaleUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;

public class MessageLoader {

    private final SimpleAuthPlugin plugin;

    public MessageLoader(SimpleAuthPlugin plugin) {
        this.plugin = plugin;
    }

    public void loadAll() {
        File directory =  new File(this.plugin.getDataFolder(), "i18n");

        for (File file : directory.listFiles()) {
            this.plugin.getLogger().log(Level.INFO, "Loading language " + file.getName());
            this.load(file);
            this.plugin.getLogger().log(Level.INFO, "Language " + file.getName() + " was loaded succesful");
        }
    }

    private void load(File file) {
       String name = file.getName();
       Locale locale = LocaleUtils.toLocale(name.replace(".properties", ""));
       MessageMap messageMap = new MessageMap(key -> "Missing " + key + " message.", locale);
       Properties properties = new Properties();
       try {
           InputStream inputStream = new FileInputStream(file);
           properties.load(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
           Collections.list(properties.keys()).forEach(key -> messageMap.put(key.toString(), properties.getProperty(key.toString())));
       } catch (IOException exception) {
           this.plugin.getLogger().log(Level.SEVERE, "Cannot load language file " + name + " ", exception);
       }
       this.plugin.getMessageManager().registerLocale(messageMap);
    }

    public void extract(File path) {
        File folder = new File(this.plugin.getDataFolder(), "i18n");
        if (!folder.exists() && !folder.isDirectory()) {
            folder.mkdirs();
            this.plugin.getLogger().log(Level.INFO, "Folder " + folder + " was created succesful!");
        }


        JarFile jarFile;
        try {
            jarFile = new JarFile(path);
        } catch (IOException e) {
            this.plugin.getLogger().log(Level.SEVERE, "Error occurred while extracting file caused by exception", e);
            return;
        }
        Enumeration enumEntries = jarFile.entries();
        while (enumEntries.hasMoreElements()) {
            JarEntry entry = (JarEntry) enumEntries.nextElement();
            File file = new File(this.plugin.getDataFolder(), entry.getName());
            if (!entry.isDirectory() && file.getName().endsWith(".properties")) {
                File langFile = new File("i18n", file.getName());
                if (!new File(folder, file.getName()).exists()) {
                    this.plugin.saveResource(langFile.toString(), false);
                }
            }
        }
    }
}
