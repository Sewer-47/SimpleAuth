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

import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;

public class MessageMap extends HashMap<String, String> {

    private final Locale locale;
    private final Function<String, String> undefined;

    public MessageMap(Function<String, String> undefined, Locale locale) {
        this.locale = Objects.requireNonNull(locale);
        this.undefined = Objects.requireNonNull(undefined, "undefined");
    }

    public String get(String key) {
        String result = super.get(key);
        if (result == null) {
            result = this.undefined.apply(key);
        }
        return result;
    }

    public Locale getLocale() {
        return this.locale;
    }

    public Function<String, String> getUndefined() {
        return this.undefined;
    }
}