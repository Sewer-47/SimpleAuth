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

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.message.Message;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class LogFilter extends AbstractFilter {

    private final Collection<String> bannedCommands = new ArrayList<>();

    public void initialize(SimpleAuthPlugin plugin) {
        for (Map.Entry<String, Map<String, Object>> command : plugin.getDescription().getCommands().entrySet()) {

            if (command.getKey().equalsIgnoreCase("module")) {
                continue;
            }

            this.bannedCommands.add(command.getKey());

            Collection<String> allises = (Collection<String>) command.getValue().get("aliases");
            if (allises == null) {
                continue;
            }
            this.bannedCommands.addAll(allises);

        }
    }

    private boolean containtsList(String text, Collection<String> list) {
        for (String s : list) {
            if (text.contains("issued server command: /" + s.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Result filter(LogEvent event) {
        Message message = event.getMessage();
        if (message != null && this.containtsList(message.getFormattedMessage().toLowerCase(), this.bannedCommands)) {
            return Result.DENY;
        }
        return Result.NEUTRAL;
    }
}