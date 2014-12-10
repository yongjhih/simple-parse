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
import com.parse.ParseUser;
import com.parse.ParseGeoPoint;
import com.parse.ParseClassName;
import com.parse.GetCallback;
import com.parse.FindCallback;
import com.parse.CountCallback;
import com.parse.ParseException;
import org.json.JSONObject;
import android.text.TextUtils;
import java.util.Date;

public class SimpleParse {
    private Class<?> mKlass;
    private Object mFrom;
    private ParseObject mTo;

    private ParseQuery mQuery;
    private String mObjectId;

    private SimpleParse() {
    }

    public SimpleParse(Class<?> klass) {
        mKlass = klass;
    }

    public SimpleParse(Object object) {
        mFrom = object;
        mKlass = object.getClass();
    }

    public static SimpleParse from(Class<?> klass) {
        return new SimpleParse(klass);
    }

    public static SimpleParse from(Object object) {
        return new SimpleParse(object);
    }

    public void saveInBackground() {
        saveInBackground(mFrom, mTo);
    }

    public void saveInBackground(Object object) {
        if (mFrom == null) {
            mFrom = object;
        } else {
            mTo = (ParseObject) object;
        }
        saveInBackground(mFrom, mTo);
    }

    public SimpleParse to(ParseObject to) {
        mTo = to;
        return this;
    }

    public static ParseObject commit(ParseObject from) {
        return commit(from, from);
    }

    public static ParseObject commit(Object from, ParseObject to) {
        for (Map.Entry<Field, String> fieldEntry : SimpleParseCache.get().getColumnFields(from.getClass()).entrySet()) {
            final Field field = fieldEntry.getKey();
            final String columnName = fieldEntry.getValue();

            if (TextUtils.isEmpty(columnName)) continue;

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
                else if (fieldType.equals(List.class)) {
                    to.addAll(columnName, (List) value);
                }
                else if (fieldType.equals(Date.class)) {
                    to.put(columnName, (Date) value);
                }
                else if (fieldType.equals(ParseObject.class)) {
                    to.put(columnName, (ParseObject) value);
                }
                else if (fieldType.equals(ParseUser.class)) {
                    to.put(columnName, (ParseUser) value);
                }
                else if (fieldType.equals(ParseGeoPoint.class)) {
                    to.put(columnName, (ParseGeoPoint) value);
                }
                //else if (ReflectionUtils.isSubclassOf(fieldType, Enum.class)) {
                    //to.put(columnName, ((Enum<?>) value).name());
                //}
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) {
            }
        }
        return to;
    }

    public static <T> T load(ParseObject to) {
        return (T) load(to, to);
    }

    public static <T> T load(T from, ParseObject to) {
        for (Map.Entry<Field, String> fieldEntry : SimpleParseCache.get().getColumnFields(from.getClass()).entrySet()) {
            final Field field = fieldEntry.getKey();
            final String columnName = fieldEntry.getValue();

            if (TextUtils.isEmpty(columnName)) continue;

            Class<?> fieldType = field.getType();
            field.setAccessible(true);
            try {
                Object value = field.get(from);

                if (value == null) {
                    //to.put(columnName, JSONObject.NULL);
                    //to.remove(columnName);
                    field.set(from, null);
                }
                else if (fieldType.equals(Byte.class) || fieldType.equals(byte.class)) {
                    field.setByte(from, (byte) to.getInt(columnName));
                }
                else if (fieldType.equals(Short.class) || fieldType.equals(short.class)) {
                    field.setShort(from, (short) to.getInt(columnName));
                }
                else if (fieldType.equals(Integer.class) || fieldType.equals(int.class)) {
                    field.setInt(from, to.getInt(columnName));
                }
                else if (fieldType.equals(Long.class) || fieldType.equals(long.class)) {
                    field.setLong(from, to.getLong(columnName));
                }
                else if (fieldType.equals(Float.class) || fieldType.equals(float.class)) {
                    field.setFloat(from, (float) to.getDouble(columnName));
                }
                else if (fieldType.equals(Double.class) || fieldType.equals(double.class)) {
                    field.setDouble(from, (float) to.getDouble(columnName));
                }
                else if (fieldType.equals(Boolean.class) || fieldType.equals(boolean.class)) {
                    field.setBoolean(from, to.getBoolean(columnName));
                }
                else if (fieldType.equals(Character.class) || fieldType.equals(char.class)) {
                    field.set(from, to.getString(columnName));
                }
                else if (fieldType.equals(String.class)) {
                    field.set(from, to.getString(columnName));
                }
                else if (fieldType.equals(Byte[].class) || fieldType.equals(byte[].class)) {
                    field.set(from, to.getString(columnName));
                }
                else if (fieldType.equals(JSONObject.class)) {
                    field.set(from, to.getJSONObject(columnName));
                }
                else if (fieldType.equals(List.class)) {
                    field.set(from, to.getList(columnName));
                }
                else if (fieldType.equals(Date.class)) {
                    field.set(from, to.getDate(columnName));
                }
                else if (fieldType.equals(ParseObject.class)) {
                    field.set(from, to.getParseObject(columnName));
                }
                else if (fieldType.equals(ParseUser.class)) {
                    field.set(from, to.getParseUser(columnName));
                }
                else if (fieldType.equals(ParseGeoPoint.class)) {
                    field.set(from, to.getParseGeoPoint(columnName));
                }
                //else if (ReflectionUtils.isSubclassOf(fieldType, Enum.class)) {
                    //to.put(columnName, ((Enum<?>) value).name());
                //}
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) {
            }
        }
        return (T) from;
    }

    public void saveInBackground(Object from, ParseObject to) {
        if (to == null) {
            to = new ParseObject(SimpleParseCache.get().getClassName(mKlass));
        }
        commit(from, to);
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

    public SimpleParse is(String objectId) {
        mObjectId = objectId;
        mIsObjectId = true;
        return this;
    }

    public SimpleParse isNot(String objectId) {
        mObjectId = objectId;
        mIsObjectId = false;
        return this;
    }

    public SimpleParse is(String key, Object value) {
        getQuery().whereEqualTo(key, value);
        return this;
    }

    public SimpleParse isNot(String key, Object value) {
        getQuery().whereNotEqualTo(key, value);
        return this;
    }

    public SimpleParse up(String key, Object value) {
        getQuery().whereGreaterThanOrEqualTo(key, value);
        return this;
    }

    public SimpleParse upOf(String key, Object value) {
        getQuery().whereGreaterThan(key, value);
        return this;
    }

    public SimpleParse down(String key, Object value) {
        getQuery().whereLessThanOrEqualTo(key, value);
        return this;
    }

    public SimpleParse downOf(String key, Object value) {
        getQuery().whereLessThan(key, value);
        return this;
    }

    public SimpleParse in(String key, List<Object> values) {
        getQuery().whereContainedIn(key, values);
        return this;
    }

    public SimpleParse notIn(String key, List<Object> values) {
        getQuery().whereNotContainedIn(key, values);
        return this;
    }

    public SimpleParse has(String key) {
        getQuery().whereExists(key);
        return this;
    }

    public SimpleParse hasNot(String key) {
        getQuery().whereDoesNotExist(key);
        return this;
    }

    public SimpleParse in(String key, String value, ParseQuery<? extends ParseObject> query) {
        getQuery().whereMatchesKeyInQuery(key, value, query);
        return this;
    }

    public SimpleParse in(String key, String value, SimpleParse simpleParseObject) {
        in(key, value, simpleParseObject.getQuery());
        return this;
    }

    public SimpleParse notIn(String key, String value, ParseQuery<? extends ParseObject> query) {
        getQuery().whereDoesNotMatchKeyInQuery(key, value, query);
        return this;
    }

    public SimpleParse notIn(String key, String value, SimpleParse simpleParseObject) {
        in(key, value, simpleParseObject.getQuery());
        return this;
    }

    public SimpleParse keys(List<String> keys) {
        getQuery().selectKeys(keys);
        return this;
    }

    /*
    public SimpleParse keys(String... keys) {
        keys(keys);
        return this;
    }
    */

    public SimpleParse hasAll(String key, List<Object> values) {
        getQuery().whereContainsAll(key, values);
        return this;
    }

    public SimpleParse starts(String key, String value) {
        getQuery().whereStartsWith(key, value);
        return this;
    }

    public SimpleParse matches(String key, ParseQuery<? extends ParseObject> query) {
        getQuery().whereMatchesQuery(key, query);
        return this;
    }

    public SimpleParse matches(String key, SimpleParse simpleParseObject) {
        matches(key, simpleParseObject.getQuery());
        return this;
    }

    public SimpleParse notMatches(String key, ParseQuery<? extends ParseObject> query) {
        getQuery().whereDoesNotMatchQuery(key, query);
        return this;
    }

    public SimpleParse notMatches(String key, SimpleParse simpleParseObject) {
        notMatches(key, simpleParseObject.getQuery());
        return this;
    }

    public SimpleParse descending(String key) {
        getQuery().orderByDescending(key);
        return this;
    }

    public SimpleParse ascending(String key) {
        getQuery().orderByAscending(key);
        return this;
    }

    public SimpleParse addDescending(String key) {
        getQuery().addDescendingOrder(key);
        return this;
    }

    public SimpleParse addAscending(String key) {
        getQuery().addAscendingOrder(key);
        return this;
    }

    public SimpleParse skip(int size) {
        getQuery().setSkip(size);
        return this;
    }

    public SimpleParse limit(int size) {
        getQuery().setLimit(size);
        return this;
    }

    public SimpleParse include(String key) {
        getQuery().include(key);
        return this;
    }

    public SimpleParse local() {
        getQuery().fromLocalDatastore();
        return this;
    }

    // unpinAllInBackground
    // pinAllInBackground

    public SimpleParse cachePolicy(ParseQuery.CachePolicy policy) { // extends Enum<ParseQuery.CachePolicy>
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

    public <T extends ParseObject> ParseQuery<T> getQuery() {
        if (mQuery == null) {
            mQuery = ParseQuery.getQuery(SimpleParseCache.get().getClassName(mKlass));
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
