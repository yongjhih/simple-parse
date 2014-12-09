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

    private ParseQuery<ParseObject> mQuery;
    private String mObjectId;

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
        saveInBackground(object, (ParseObject) null);
    }

    public void saveInBackground(Object from, ParseObject to) {
        if (to == null) {
            mParseObject = new ParseObject(getClassName(mKlass));
            to = mParseObject;
        }
        for (Field field : getColumnFields(mKlass)) {
            final String columnName = getColumnName(field);
            Class<?> fieldType = field.getType();
            field.setAccessible(true);
            try {
                Object value = field.get(from);

                if (value == null) {
                    to.put(columnName, JSONObject.NULL);
                    //to.remove(columnName);
                }
                else if (fieldType.equals(Byte.class) || fieldType.equals(byte.class)) {
                    to.put(columnName, (Byte) value);
                }
                else if (fieldType.equals(Short.class) || fieldType.equals(short.class)) {
                    to.put(columnName, (Short) value);
                }
                else if (fieldType.equals(Integer.class) || fieldType.equals(int.class)) {
                    to.put(columnName, (Integer) value);
                }
                else if (fieldType.equals(Long.class) || fieldType.equals(long.class)) {
                    to.put(columnName, (Long) value);
                }
                else if (fieldType.equals(Float.class) || fieldType.equals(float.class)) {
                    to.put(columnName, (Float) value);
                }
                else if (fieldType.equals(Double.class) || fieldType.equals(double.class)) {
                    to.put(columnName, (Double) value);
                }
                else if (fieldType.equals(Boolean.class) || fieldType.equals(boolean.class)) {
                    to.put(columnName, (Boolean) value);
                }
                else if (fieldType.equals(Character.class) || fieldType.equals(char.class)) {
                    to.put(columnName, value.toString());
                }
                else if (fieldType.equals(String.class)) {
                    to.put(columnName, value.toString());
                }
                else if (fieldType.equals(Byte[].class) || fieldType.equals(byte[].class)) {
                    to.put(columnName, (byte[]) value);
                }
                else if (fieldType.equals(JSONObject.class)) {
                    to.put(columnName, (JSONObject) value);
                }
                //else if (ReflectionUtils.isSubclassOf(fieldType, Enum.class)) {
                    //to.put(columnName, ((Enum<?>) value).name());
                //}
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) {
            }
        }
        to.saveInBackground();
    }

    /*
    String objectId = gameScore.getObjectId();
    Date updatedAt = gameScore.getUpdatedAt();
    Date createdAt = gameScore.getCreatedAt();
    */

    // pinInBackground()

    // createWithoutData

    // fetchFromLocalDatastoreInBackground()

    // unpinInBackground()

    /*
     * gameScore.increment("score");
     * gameScore.saveInBackground();
     */

    /* gameScore.addAllUnique("skills", Arrays.asList("flying", "kungfu"));
     * gameScore.saveInBackground();
     */

    // saveEventuall()

    /*
    public static void registerSubclass(Class<?> klass) {
        getClassName(klass);
        getColumnFields(klass);
        ParseObject.registerSubclass(klass);
    }
    */

    // SimpleParseQuery.from(Profile.class).is("xWMyZ4YEGZ").query(new GetCallback<ParseUser>() {});

    private Boolean mIsObjectId;
    public SimpleParseObject is(String objectId) {
        mObjectId = objectId;
        mIsObjectId = true;
        return this;
    }

    public SimpleParseObject isNot(String objectId) {
        mObjectId = objectId;
        mIsObjectId = false;
        return this;
    }

    public SimpleParseObject is(String key, Object value) {
        getQuery().whereEqualTo(key, value);
        return this;
    }

    public SimpleParseObject isNot(String key, Object value) {
        getQuery().whereNotEqualTo(key, value);
        return this;
    }

    public SimpleParseObject up(String key, Object value) {
        getQuery().whereGreaterThanOrEqualTo(key, value);
        return this;
    }

    public SimpleParseObject upOf(String key, Object value) {
        getQuery().whereGreaterThan(key, value);
        return this;
    }

    public SimpleParseObject down(String key, Object value) {
        getQuery().whereLessThanOrEqualTo(key, value);
        return this;
    }

    public SimpleParseObject downOf(String key, Object value) {
        getQuery().whereLessThan(key, value);
        return this;
    }

    public SimpleParseObject in(String key, List<Object> values) {
        getQuery().whereContainedIn(key, values);
        return this;
    }

    public SimpleParseObject notIn(String key, List<Object> values) {
        getQuery().whereNotContainedIn(key, values);
        return this;
    }

    public SimpleParseObject has(String key) {
        getQuery().whereExists(key);
        return this;
    }

    public SimpleParseObject hasNot(String key) {
        getQuery().whereDoesNotExist(key);
        return this;
    }

    public SimpleParseObject in(String key, String value, ParseQuery<? extends ParseObject> query) {
        getQuery().whereMatchesKeyInQuery(key, value, query);
        return this;
    }

    public SimpleParseObject in(String key, String value, SimpleParseObject simpleParseObject) {
        in(key, value, simpleParseObject.getQuery());
        return this;
    }

    public SimpleParseObject notIn(String key, String value, ParseQuery<? extends ParseObject> query) {
        getQuery().whereDoesNotMatchKeyInQuery(key, value, query);
        return this;
    }

    public SimpleParseObject notIn(String key, String value, SimpleParseObject simpleParseObject) {
        in(key, value, simpleParseObject.getQuery());
        return this;
    }

    public SimpleParseObject keys(List<String> keys) {
        getQuery().selectKeys(keys);
        return this;
    }

    /*
    public SimpleParseObject keys(String... keys) {
        keys(keys);
        return this;
    }
    */

    public SimpleParseObject hasAll(String key, List<Object> values) {
        getQuery().whereContainsAll(key, values);
        return this;
    }

    public SimpleParseObject starts(String key, String value) {
        getQuery().whereStartsWith(key, value);
        return this;
    }

    public SimpleParseObject matches(String key, ParseQuery<? extends ParseObject> query) {
        getQuery().whereMatchesQuery(key, query);
        return this;
    }

    public SimpleParseObject matches(String key, SimpleParseObject simpleParseObject) {
        matches(key, simpleParseObject.getQuery());
        return this;
    }

    public SimpleParseObject notMatches(String key, ParseQuery<? extends ParseObject> query) {
        getQuery().whereDoesNotMatchQuery(key, query);
        return this;
    }

    public SimpleParseObject notMatches(String key, SimpleParseObject simpleParseObject) {
        notMatches(key, simpleParseObject.getQuery());
        return this;
    }

    public SimpleParseObject descending(String key) {
        getQuery().orderByDescending(key);
        return this;
    }

    public SimpleParseObject ascending(String key) {
        getQuery().orderByAscending(key);
        return this;
    }

    public SimpleParseObject addDescending(String key) {
        getQuery().addDescendingOrder(key);
        return this;
    }

    public SimpleParseObject addAscending(String key) {
        getQuery().addAscendingOrder(key);
        return this;
    }

    public SimpleParseObject skip(int size) {
        getQuery().setSkip(size);
        return this;
    }

    public SimpleParseObject limit(int size) {
        getQuery().setLimit(size);
        return this;
    }

    public SimpleParseObject include(String key) {
        getQuery().include(key);
        return this;
    }

    public SimpleParseObject local() {
        getQuery().fromLocalDatastore();
        return this;
    }

    // unpinAllInBackground
    // pinAllInBackground

    public SimpleParseObject cachePolicy(ParseQuery.CachePolicy policy) { // extends Enum<ParseQuery.CachePolicy>
        getQuery().setCachePolicy(policy);
        return this;
    }

    /*
    public boolean hasCachedResult() {
        return getQuery().hasCachedResult();
    }

    public boolean clearCachedResult() {
        return getQuery().clearCachedResult();
    }
    */

    public ParseQuery<ParseObject> getQuery() {
        if (mQuery == null) {
            mQuery = ParseQuery.getQuery(getClassName(mKlass));
        }
        return mQuery;
    }

    private void get(GetCallback<ParseObject> getCallback) {
        if (mIsObjectId != null) {
            getQuery().getInBackground(mObjectId, getCallback);
        }
    }

    private void findInBackground(FindCallback<ParseObject> findCallback) {
        find(findCallback);
    }

    private void find(FindCallback<ParseObject> findCallback) {
        getQuery().findInBackground(findCallback);
    }

    private void find() {
        try {
            getQuery().find();
        } catch (ParseException e) {
        }
    }

    private void count(CountCallback countCallback) {
        getQuery().countInBackground(countCallback);
    }
}
