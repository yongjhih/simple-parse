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
package simpleparse;

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
import java.lang.reflect.Field;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseClassName;
import com.parse.GetCallback;
import com.parse.FindCallback;
import org.json.JSONObject;

public class SimpleParseObject {
    private ParseObject mParseObject;
    private Class<?> mKlass;

    private final Map<Class<?>, String> mClassNameCache =
        new LinkedHashMap<Class<?>, String>();

    private final Map<Class<?>, Map<Field, FieldInfo>> mFieldInfoCache =
        new LinkedHashMap<Class<?>, Map<Field, FieldInfo>>();

    private final Map<Class<?>, Set<Field>> mColumnFieldsCache =
        new LinkedHashMap<Class<?>, Set<Field>>();

    private final Map<Field, String> mColumnNameCache =
        new LinkedHashMap<Field, String>();

    private SimpleParseObject() {
    }

    public SimpleParseObject(Class<?> klass) {
        mKlass = klass;
    }

    public static SimpleParseObject from(Class<?> klass) {
        return new SimpleParseObject(klass);
    }

    public static class FieldInfo {
        public String name;
    }

    private String getClassName(Class<?> klass) {
        String name = mClassNameCache.get(klass);

        if (name != null) {
            return name;
        }

        final ParseClassName classAnnotation = klass.getAnnotation(ParseClassName.class);

        if (classAnnotation != null) {
            name = classAnnotation.value();
        } else {
            name = klass.getSimpleName();
        }

        mClassNameCache.put(klass, name); // assert(name)

        return name;
    }

    private Set<Field> getColumnFields(Class<?> klass) {
        Set<Field> columnFieldsCache = mColumnFieldsCache.get(klass);
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
                mColumnNameCache.put(field, parseColumn.value());
            }
        }

        Class<?> superClass = klass.getSuperclass();
        if (superClass != null) {
            declaredColumnFields.addAll(getColumnFields(superClass));
        }

        mColumnFieldsCache.put(klass, declaredColumnFields);
        return declaredColumnFields;
    }

    private String getColumnName(Field field) {
        String name = mColumnNameCache.get(field);
        if (name != null) return name;

        return field.getAnnotation(ParseColumn.class).value();
    }

    public void saveInBackground(Object object) {
        mParseObject = new ParseObject(getClassName(mKlass));
        for (Field field : getColumnFields(mKlass)) {
            final String columnName = getColumnName(field);
            Class<?> fieldType = field.getType();
            field.setAccessible(true);
            try {
                Object value = field.get(object);

                if (value == null) {
                    mParseObject.put(columnName, JSONObject.NULL);
                }
                else if (fieldType.equals(Byte.class) || fieldType.equals(byte.class)) {
                    mParseObject.put(columnName, (Byte) value);
                }
                else if (fieldType.equals(Short.class) || fieldType.equals(short.class)) {
                    mParseObject.put(columnName, (Short) value);
                }
                else if (fieldType.equals(Integer.class) || fieldType.equals(int.class)) {
                    mParseObject.put(columnName, (Integer) value);
                }
                else if (fieldType.equals(Long.class) || fieldType.equals(long.class)) {
                    mParseObject.put(columnName, (Long) value);
                }
                else if (fieldType.equals(Float.class) || fieldType.equals(float.class)) {
                    mParseObject.put(columnName, (Float) value);
                }
                else if (fieldType.equals(Double.class) || fieldType.equals(double.class)) {
                    mParseObject.put(columnName, (Double) value);
                }
                else if (fieldType.equals(Boolean.class) || fieldType.equals(boolean.class)) {
                    mParseObject.put(columnName, (Boolean) value);
                }
                else if (fieldType.equals(Character.class) || fieldType.equals(char.class)) {
                    mParseObject.put(columnName, value.toString());
                }
                else if (fieldType.equals(String.class)) {
                    mParseObject.put(columnName, value.toString());
                }
                else if (fieldType.equals(Byte[].class) || fieldType.equals(byte[].class)) {
                    mParseObject.put(columnName, (byte[]) value);
                }
                else if (fieldType.equals(JSONObject.class)) {
                    mParseObject.put(columnName, (JSONObject) value);
                }
                //else if (ReflectionUtils.isSubclassOf(fieldType, Enum.class)) {
                    //mParseObject.put(columnName, ((Enum<?>) value).name());
                //}
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) {
            }
        }
        mParseObject.saveInBackground();
    }

    /*
    public static void registerSubclass(Class<?> klass) {
        getClassName(klass);
        getColumnFields(klass);
        ParseObject.registerSubclass(klass);
    }
    */

    // SimpleParseQuery.from(Profile.class).is("xWMyZ4YEGZ").query(new GetCallback<ParseUser>() {});

    public SimpleParseObject is(String objectId) {
        mObjectId = objectId;
        return this;
    }

    public SimpleParseObject isNot(String objectId) {
        return this;
    }

    public SimpleParseObject is(String key, Object value) {
        mQuery = ParseQuery.getQuery(getClassName(mKlass));
        mQuery.whereEqualTo(key, value);
        return this;
    }

    private ParseQuery<ParseObject> mQuery;
    private String mObjectId;

    private void get(GetCallback<ParseObject> getCallback) {
        mQuery.getInBackground(mObjectId, getCallback);
    }

    private void findInBackground(FindCallback<ParseObject> findCallback) {
        find(findCallback);
    }

    private void find(FindCallback<ParseObject> findCallback) {
        mQuery.findInBackground(findCallback);
    }
}
