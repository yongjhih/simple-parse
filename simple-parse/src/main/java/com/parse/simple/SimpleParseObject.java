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
import com.parse.SaveCallback;
import com.parse.ParseException;
import org.json.JSONObject;
import android.text.TextUtils;

public class SimpleParseObject extends ParseObject {
    public SimpleParseObject commit() {
        for (Map.Entry<Field, String> fieldEntry : SimpleParseCache.get().getColumnFields(getClass()).entrySet()) {
            final Field field = fieldEntry.getKey();
            final String columnName = fieldEntry.getValue();

            if (TextUtils.isEmpty(columnName)) continue;

            Class<?> fieldType = field.getType();
            field.setAccessible(true);
            try {
                Object value = field.get(this);

                if (value == null) {
                    put(columnName, JSONObject.NULL);
                    //remove(columnName);
                }
                else if (fieldType.equals(Byte.class) || fieldType.equals(byte.class)) {
                    put(columnName, (Byte) value);
                }
                else if (fieldType.equals(Short.class) || fieldType.equals(short.class)) {
                    put(columnName, (Short) value);
                }
                else if (fieldType.equals(Integer.class) || fieldType.equals(int.class)) {
                    put(columnName, (Integer) value);
                }
                else if (fieldType.equals(Long.class) || fieldType.equals(long.class)) {
                    put(columnName, (Long) value);
                }
                else if (fieldType.equals(Float.class) || fieldType.equals(float.class)) {
                    put(columnName, (Float) value);
                }
                else if (fieldType.equals(Double.class) || fieldType.equals(double.class)) {
                    put(columnName, (Double) value);
                }
                else if (fieldType.equals(Boolean.class) || fieldType.equals(boolean.class)) {
                    put(columnName, (Boolean) value);
                }
                else if (fieldType.equals(Character.class) || fieldType.equals(char.class)) {
                    put(columnName, value.toString());
                }
                else if (fieldType.equals(String.class)) {
                    put(columnName, value.toString());
                }
                else if (fieldType.equals(Byte[].class) || fieldType.equals(byte[].class)) {
                    put(columnName, (byte[]) value);
                }
                else if (fieldType.equals(JSONObject.class)) {
                    put(columnName, (JSONObject) value);
                }
                //else if (ReflectionUtils.isSubclassOf(fieldType, Enum.class)) {
                    //put(columnName, ((Enum<?>) value).name());
                //}
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) {
            }
        }
        return this;
    }
}
