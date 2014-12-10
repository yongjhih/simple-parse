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

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.Collection;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.lang.reflect.Field;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseClassName;
import com.parse.GetCallback;
import com.parse.FindCallback;
import com.parse.CountCallback;
import com.parse.ParseException;
import org.json.JSONObject;
import android.text.TextUtils;

public class SimpleParseCache {
    public final Map<Class<?>, String> classNameCache =
        new LinkedHashMap<Class<?>, String>();

    public final Map<Class<?>, Map<Field, FieldInfo>> fieldInfoCache =
        new LinkedHashMap<Class<?>, Map<Field, FieldInfo>>();

    public final Map<Class<?>, Set<Field>> columnFieldsCache =
        new LinkedHashMap<Class<?>, Set<Field>>();

    public final Map<Field, String> columnNameCache =
        new LinkedHashMap<Field, String>();

    private static SimpleParseCache sInstance = new SimpleParseCache();

    private SimpleParseCache() {
    }

    public static SimpleParseCache get() {
        return sInstance;
    }

    public static class FieldInfo {
        public String name;
    }

    public String getClassName(Class<?> klass) {
        String name = SimpleParseCache.get().classNameCache.get(klass);

        if (name != null) {
            return name;
        }

        final ParseClassName classAnnotation = klass.getAnnotation(ParseClassName.class);

        if (classAnnotation != null) {
            name = classAnnotation.value();
        } else {
            name = klass.getSimpleName();
        }

        SimpleParseCache.get().classNameCache.put(klass, name); // assert(name)

        return name;
    }

    public Set<Field> getColumnFields(Class<?> klass) {
        Set<Field> columnFieldsCache = SimpleParseCache.get().columnFieldsCache.get(klass);
        if (columnFieldsCache != null) return columnFieldsCache;

        Set<Field> declaredColumnFields = new LinkedHashSet<Field>(); //Set<Field> declaredColumnFields = Collections.emptySet();

        Field[] fields = klass.getDeclaredFields();
        Arrays.sort(fields, new Comparator<Field>() {
            @Override
            public int compare(Field field1, Field field2) {
                return field2.getName().compareTo(field1.getName());
            }
        });
        for (Field field : fields) {
            if (field.isAnnotationPresent(ParseColumn.class)) {
                final ParseColumn parseColumn = field.getAnnotation(ParseColumn.class);
                declaredColumnFields.add(field);
                String name = parseColumn.value();
                if (TextUtils.isEmpty(name)) {
                    name = field.getName();
                }
                SimpleParseCache.get().columnNameCache.put(field, name);
            }
        }

        Class<?> superClass = klass.getSuperclass();
        if (superClass != null) {
            declaredColumnFields.addAll(getColumnFields(superClass));
        }

        SimpleParseCache.get().columnFieldsCache.put(klass, declaredColumnFields);
        return declaredColumnFields;
    }

    public String getColumnName(Field field) {
        String name = SimpleParseCache.get().columnNameCache.get(field);
        if (name != null) return name;

        return field.getAnnotation(ParseColumn.class).value();
    }

}
