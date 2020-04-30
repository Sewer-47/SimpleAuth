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

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class MessageManager {

    private final Map<Locale, MessageMap> localeMap = new HashMap<>();
    private final Locale fallback;

    public MessageManager(Locale fallback) {
        this.fallback = fallback;
    }

    public void registerLocale(MessageMap messageMap) {
        this.localeMap.put(messageMap.getLocale(), messageMap);
    }

    public void unregisterLocale(MessageMap messageMap) {
        this.localeMap.remove(messageMap.getLocale());
    }

    public Optional<MessageMap> getMessageMap(Locale locale) {
        return Optional.ofNullable(this.localeMap.get(locale));
    }

    public Map<Locale, MessageMap> getLocaleMap() {
        return this.localeMap;
    }

    public Locale getFallback() {
        return this.fallback;
    }

    public String getMessage(Locale locale, String message, Object... params) {
        String text = this.localeMap.containsKey(locale)
                ? this.localeMap.get(locale).get(message)
                : this.localeMap.get(this.fallback).get(message);
        return MessageFormat.format(text, params);
    }
}
