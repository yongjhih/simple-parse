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
    String value;
    String prefix;
    String suffix;

    ParseColumn column;

    public SimpleParseColumn(ParseColumn column) {
        this.column = column;
    }

    public String value() {
        if (value == null) {
            value = column.value();
        }
        return value;
    }

    public String suffix() {
        if (suffix == null) {
            suffix = column.suffix();
        }
        return suffix;
    }

    public String prefix() {
        if (prefix == null) {
            prefix = column.prefix();
        }
        return prefix;
    }
}
