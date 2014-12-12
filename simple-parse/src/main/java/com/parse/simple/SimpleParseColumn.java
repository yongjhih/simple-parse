/*
 * Copyright (C) 2014 8tory, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.parse.simple;

public class SimpleParseColumn {
    ParseColumn column;

    public SimpleParseColumn(ParseColumn column) {
        this.column = column;
    }

    public String value;

    public String value() {
        if (value == null) {
            value = column.value();
        }
        return value;
    }

    public String suffix;

    public String suffix() {
        if (suffix == null) {
            suffix = column.suffix();
        }
        return suffix;
    }

    public String prefix;

    public String prefix() {
        if (prefix == null) {
            prefix = column.prefix();
        }
        return prefix;
    }

    public Class<? extends Filter> filter;

    public Class<? extends Filter> filter() {
        if (filter == null) {
            filter = column.filter();
        }
        return filter;
    }

    public Class<? extends Value> prefixClass;

    public Class<? extends Value> prefixClass() {
        if (prefixClass == null) {
            prefixClass = column.prefixClass();
        }
        return prefixClass;
    }

    public Class<? extends Value> suffixClass;

    public Class<? extends Value> suffixClass() {
        if (suffixClass == null) {
            suffixClass = column.suffixClass();
        }
        return suffixClass;
    }

    public Boolean self;

    public boolean self() {
        if (self == null) {
            self = column.self();
        }
        return self;
    }

    public Class<? extends Value> onSave;

    public Class<? extends Value> onSave()  {
        if (onSave == null) {
            onSave = column.onSave();
        }
        return onSave;
    }

    public Class<? extends Value> onLoad;

    public Class<? extends Value> onLoad() {
        if (onLoad == null) {
            onLoad = column.onLoad();
        }
        return onLoad;
    }
}
