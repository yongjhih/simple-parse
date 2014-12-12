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
import android.support.v4.util.LruCache;
import android.os.Bundle;

public class SimpleParseCache {
    public static final int CLASS_CACHE_SIZE = 32;
    //public static final int FIELD_CACHE_SIZE = 32; // Disabled, We cannot confirm fields that is compeleted or not to return.
    public static final int FILTER_CACHE_SIZE = 32;

    public final LruCache<Class<?>, String> classNameCache =
        new LruCache<Class<?>, String>(CLASS_CACHE_SIZE);

    public final LruCache<Class<? extends Filter>, Filter> filtersCache =
        new LruCache<Class<? extends Filter>, Filter>(FILTER_CACHE_SIZE);

    public final LruCache<Class<?>, Object> objectsCache =
        new LruCache<Class<?>, Object>(CLASS_CACHE_SIZE);

    public final LruCache<Class<?>, Map<SimpleField, SimpleParseColumn>> columnFieldsCache =
        new LruCache<Class<?>, Map<SimpleField, SimpleParseColumn>>(CLASS_CACHE_SIZE);

    public final Map<String, Bundle> columnDataCache =
        new LinkedHashMap<String, Bundle>();

    private static SimpleParseCache sInstance = new SimpleParseCache();

    private SimpleParseCache() {
    }

    public static SimpleParseCache get() {
        return sInstance;
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

    public Map<SimpleField, SimpleParseColumn> getColumnFields(Class<?> klass) {
        Map<SimpleField, SimpleParseColumn> columnFieldsCache = SimpleParseCache.get().columnFieldsCache.get(klass);
        if (columnFieldsCache != null) return columnFieldsCache;

        Map<SimpleField, SimpleParseColumn> declaredColumnFields = new LinkedHashMap<SimpleField, SimpleParseColumn>();

        Field[] fields = klass.getDeclaredFields();
        Arrays.sort(fields, new Comparator<Field>() {
            @Override
            public int compare(Field field1, Field field2) {
                return field2.getName().compareTo(field1.getName());
            }
        });
        for (Field field : fields) {
            if (field.isAnnotationPresent(ParseColumn.class)) {
                declaredColumnFields.put(new SimpleField(field), new SimpleParseColumn(field.getAnnotation(ParseColumn.class)));
            }
        }

        Class<?> superClass = klass.getSuperclass();
        if (superClass != null) {
            declaredColumnFields.putAll(getColumnFields(superClass));
        }

        SimpleParseCache.get().columnFieldsCache.put(klass, declaredColumnFields);
        return declaredColumnFields;
    }

    public String getColumnName(SimpleField field, SimpleParseColumn column) {
            String name = column.value();

            if (!TextUtils.isEmpty(name)) {
                return name;
            }

            name = field.getName();

            return name;
    }

    public String getColumnName(Class<?> klass, SimpleField field) {
        String name = null;

        Map<SimpleField, SimpleParseColumn> map = SimpleParseCache.get().columnFieldsCache.get(klass);

        if (map != null) {
            name = getColumnName(field, map.get(field));
        }

        if (!TextUtils.isEmpty(name)) {
            return name;
        }

        SimpleParseColumn column = new SimpleParseColumn(field.getAnnotation(ParseColumn.class));
        name = getColumnName(field, column);

        if (map != null) {
            map.put(field, column);
            SimpleParseCache.get().columnFieldsCache.put(klass, map);
        }

        return name;
    }

    public Object getObject(Class<?> klass) {
        Object object = objectsCache.get(klass);

        if (object == null) {
            try {
                object = (Object) klass.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return object;
    }

    public Filter getFilter(Class<? extends Filter> klass) {
        Filter filter = filtersCache.get(klass);

        if (filter == null) {
            try {
                filter = (Filter) klass.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return filter;
    }
}
