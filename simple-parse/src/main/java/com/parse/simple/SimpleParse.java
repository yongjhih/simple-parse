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
import java.util.Date;

import android.text.TextUtils;
import android.os.Bundle;

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

    public synchronized static ParseObject commit(Object from, ParseObject to) {
        for (Map.Entry<SimpleField, SimpleParseColumn> fieldEntry : SimpleParseCache.get().getColumnFields(from.getClass()).entrySet()) {
            final SimpleField field = fieldEntry.getKey();
            final SimpleParseColumn column = fieldEntry.getValue();
            final String columnName = SimpleParseCache.get().getColumnName(field, column);

            if (TextUtils.isEmpty(columnName)) continue;

            Bundle icicle = SimpleParseCache.get().columnDataCache.get(columnName);
            if (icicle == null) {
                icicle = new Bundle();
                SimpleParseCache.get().columnDataCache.put(columnName, icicle);
            }

            Class<?> fieldType = field.getType();
            try {
                Object value = field.get(from);

                Class<? extends Filter> filter = column.filter();
                if (!Optional.class.equals(column.onSave())) {
                    value = column.onSave();
                } else if (!NullValue.class.equals(column.onSave())) {
                    continue;
                } else if (!OptionalFilter.class.equals(filter)) {
                    value = SimpleParseCache.get().getFilter(filter).onSave(value, icicle, from, to);
                }

                if (column.self() && fieldType.isAssignableFrom(ParseObject.class)) {
                    // do nothing
                } else if (value == null) {
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
                    String valueString = value.toString();
                    String prefix = column.prefix();
                    String suffix = column.suffix();

                    Class<?> prefixClass = column.prefixClass();
                    if (!Object.class.equals(prefixClass)) {
                        prefix = (String) ((Value) SimpleParseCache.get().getObject(prefixClass)).value();
                    }

                    Class<?> suffixClass = column.suffixClass();
                    if (!Object.class.equals(suffixClass)) {
                        suffix = (String) ((Value) SimpleParseCache.get().getObject(suffixClass)).value();
                    }

                    if (!TextUtils.isEmpty(prefix) && valueString.startsWith(prefix)) {
                        //valueString.replace(prefix, "");
                        valueString = valueString.substring(prefix.length(), valueString.length());
                    }

                    if (!TextUtils.isEmpty(suffix) && valueString.endsWith(suffix)) {
                        valueString = valueString.substring(0, valueString.length() - suffix.length());
                    }

                    if (SimpleParseObject.OBJECT_ID.equals(columnName)) {
                        to.setObjectId(valueString);
                    } else {
                        to.put(columnName, valueString);
                    }
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
                else if (fieldType.equals(ParseUser.class)) {
                    to.put(columnName, (ParseUser) value);
                }
                else if (fieldType.equals(ParseGeoPoint.class)) {
                    to.put(columnName, (ParseGeoPoint) value);
                }
                else if (fieldType.equals(ParseObject.class)) {
                    to.put(columnName, (ParseObject) value);
                }
                //else if (ReflectionUtils.isSubclassOf(fieldType, Enum.class)) {
                    //to.put(columnName, ((Enum<?>) value).name());
                //}
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) {
            }
        }
        SimpleParseCache.get().columnDataCache.clear();
        return to;
    }

    public static <T> T load(ParseObject to) {
        return (T) load(to, to);
    }

    public synchronized static <T> T load(T from, ParseObject to) {
        for (Map.Entry<SimpleField, SimpleParseColumn> fieldEntry : SimpleParseCache.get().getColumnFields(from.getClass()).entrySet()) {
            final SimpleField field = fieldEntry.getKey();
            final SimpleParseColumn column = fieldEntry.getValue();
            final String columnName = SimpleParseCache.get().getColumnName(field, column);

            if (TextUtils.isEmpty(columnName)) continue;

            Bundle icicle = SimpleParseCache.get().columnDataCache.get(columnName);
            if (icicle == null) {
                icicle = new Bundle();
                SimpleParseCache.get().columnDataCache.put(columnName, icicle);
            }

            Class<?> fieldType = field.getType();
            field.setAccessible(true);
            try {
                Filter filter = SimpleParseCache.get().getFilter(column.filter());
                boolean filtered = false;
                Object saveValue = null;

                if (!Optional.class.equals(column.onLoad())) {
                    filtered = true;
                    saveValue = column.onLoad();
                } else if (!NullValue.class.equals(column.onLoad())) {
                    continue;
                } else if (!(filter instanceof OptionalFilter)) {
                    Class<?> saveType = filter.getSaveType();
                    if (!saveType.equals(fieldType)) {
                        filtered = true;
                        if (column.self() && saveType.isAssignableFrom(ParseObject.class)) {
                            saveValue = (ParseObject) to;
                            saveValue = (ParseObject) filter.onLoad(saveValue, icicle, from, to);
                        }
                        else if (saveType.equals(Byte.class) || saveType.equals(byte.class)) {
                            saveValue = (byte) to.getInt(columnName);
                            saveValue = (byte) filter.onLoad(saveValue, icicle, from, to);
                        }
                        else if (saveType.equals(Short.class) || saveType.equals(short.class)) {
                            saveValue = (short) to.getInt(columnName);
                            saveValue = (short) filter.onLoad(saveValue, icicle, from, to);
                        }
                        else if (saveType.equals(Integer.class) || saveType.equals(int.class)) {
                            saveValue = to.getInt(columnName);
                            saveValue = (int) filter.onLoad(saveValue, icicle, from, to);
                        }
                        else if (saveType.equals(Long.class) || saveType.equals(long.class)) {
                            saveValue = to.getLong(columnName);
                            saveValue = (long) filter.onLoad(saveValue, icicle, from, to);
                        }
                        else if (saveType.equals(Float.class) || saveType.equals(float.class)) {
                            saveValue = (float) to.getDouble(columnName);
                            saveValue = (float) filter.onLoad(saveValue, icicle, from, to);
                        }
                        else if (saveType.equals(Double.class) || saveType.equals(double.class)) {
                            saveValue = to.getDouble(columnName);
                            saveValue = (double) filter.onLoad(saveValue, icicle, from, to);
                        }
                        else if (saveType.equals(Boolean.class) || saveType.equals(boolean.class)) {
                            saveValue = to.getBoolean(columnName);
                            saveValue = (boolean) filter.onLoad(saveValue, icicle, from, to);
                        }
                        else if (saveType.equals(Character.class) || saveType.equals(char.class)) {
                            saveValue = to.getString(columnName);
                            saveValue = (String) filter.onLoad(saveValue, icicle, from, to);
                        }
                        else if (saveType.equals(String.class)) {
                            if (SimpleParseObject.OBJECT_ID.equals(columnName)) {
                                saveValue = to.getObjectId();
                            } else {
                                saveValue = to.getString(columnName);
                            }

                            saveValue = (String) filter.onLoad(saveValue, icicle, from, to);

                            String prefix = column.prefix();
                            Class<?> prefixClass = column.prefixClass();

                            if (!Optional.class.equals(prefixClass)) {
                                prefix = (String) ((Value) SimpleParseCache.get().getObject(prefixClass)).value();
                            }

                            String suffix = column.suffix();
                            Class<?> suffixClass = column.suffixClass();

                            if (!Optional.class.equals(suffixClass)) {
                                suffix = (String) ((Value) SimpleParseCache.get().getObject(suffixClass)).value();
                            }

                            saveValue = prefix + saveValue + suffix;
                        }
                        else if (saveType.equals(Byte[].class) || saveType.equals(byte[].class)) {
                            saveValue = to.getString(columnName);
                            saveValue = (String) filter.onLoad(saveValue, icicle, from, to);
                        }
                        else if (saveType.equals(JSONObject.class)) {
                            saveValue = to.getJSONObject(columnName);
                            saveValue = (JSONObject) filter.onLoad(saveValue, icicle, from, to);
                        }
                        else if (saveType.equals(List.class)) {
                            saveValue = to.getList(columnName);
                            saveValue = (List) filter.onLoad(saveValue, icicle, from, to);
                        }
                        else if (saveType.equals(Date.class)) {
                            saveValue = to.getDate(columnName);
                            saveValue = (Date) filter.onLoad(saveValue, icicle, from, to);
                        }
                        else if (saveType.equals(ParseUser.class)) {
                            saveValue = to.getParseUser(columnName);
                            saveValue = (ParseUser) filter.onLoad(saveValue, icicle, from, to);
                        }
                        else if (saveType.equals(ParseGeoPoint.class)) {
                            saveValue = to.getParseGeoPoint(columnName);
                            saveValue = (ParseGeoPoint) filter.onLoad(saveValue, icicle, from, to);
                        }
                        else if (saveType.equals(ParseObject.class)) {
                            saveValue = to.getParseObject(columnName);
                            saveValue = (ParseObject) filter.onLoad(saveValue, icicle, from, to);
                        }
                    }
                }

                if (column.self() && fieldType.isAssignableFrom(ParseObject.class)) {
                    ParseObject value = (ParseObject) (filtered ? saveValue : to);
                    if (!filtered && !(filter instanceof OptionalFilter)) {
                        value = (ParseObject) filter.onLoad(value, icicle, from, to);
                    }

                    field.set(from, value);
                }
                else if (fieldType.equals(Byte.class) || fieldType.equals(byte.class)) {
                    byte value = (byte) (filtered ? saveValue : to.getInt(columnName));
                    if (!filtered && !(filter instanceof OptionalFilter)) {
                        value = (byte) filter.onLoad(value, icicle, from, to);
                    }

                    field.setByte(from, value);
                }
                else if (fieldType.equals(Short.class) || fieldType.equals(short.class)) {
                    short value = (short) (filtered ? saveValue : to.getInt(columnName));
                    if (!filtered && !(filter instanceof OptionalFilter)) {
                        value = (short) filter.onLoad(value, icicle, from, to);
                    }

                    field.setShort(from, value);
                }
                else if (fieldType.equals(Integer.class) || fieldType.equals(int.class)) {
                    int value = (int) (filtered ? saveValue : to.getInt(columnName));
                    if (!filtered && !(filter instanceof OptionalFilter)) {
                        value = (int) filter.onLoad(value, icicle, from, to);
                    }

                    field.setInt(from, value);
                }
                else if (fieldType.equals(Long.class) || fieldType.equals(long.class)) {
                    long value = (long) (filtered ? saveValue : to.getLong(columnName));
                    if (!filtered && !(filter instanceof OptionalFilter)) {
                        value = (long) filter.onLoad(value, icicle, from, to);
                    }

                    field.setLong(from, value);
                }
                else if (fieldType.equals(Float.class) || fieldType.equals(float.class)) {
                    float value = (float) (filtered ? saveValue : to.getDouble(columnName));
                    if (!filtered && !(filter instanceof OptionalFilter)) {
                        value = (float) filter.onLoad(value, icicle, from, to);
                    }

                    field.setFloat(from, value);
                }
                else if (fieldType.equals(Double.class) || fieldType.equals(double.class)) {
                    double value = (double) (filtered ? saveValue : to.getDouble(columnName));
                    if (!filtered && !(filter instanceof OptionalFilter)) {
                        value = (double) filter.onLoad(value, icicle, from, to);
                    }

                    field.setDouble(from, value);
                }
                else if (fieldType.equals(Boolean.class) || fieldType.equals(boolean.class)) {
                    boolean value = (boolean) (filtered ? saveValue : to.getBoolean(columnName));
                    if (!filtered && !(filter instanceof OptionalFilter)) {
                        value = (boolean) filter.onLoad(value, icicle, from, to);
                    }

                    field.setBoolean(from, value);
                }
                else if (fieldType.equals(Character.class) || fieldType.equals(char.class)) {
                    String value = (String) (filtered ? saveValue : to.getString(columnName));
                    if (!filtered && !(filter instanceof OptionalFilter)) {
                        value = (String) filter.onLoad(value, icicle, from, to);
                    }

                    field.set(from, value);
                }
                else if (fieldType.equals(String.class)) {
                    String value = null;
                    if (filtered) {
                        value = (String) saveValue;
                    } else {
                    if (SimpleParseObject.OBJECT_ID.equals(columnName)) {
                        value = to.getObjectId();
                    } else {
                        value = to.getString(columnName);
                    }

                    if (!(filter instanceof OptionalFilter)) {
                        value = (String) filter.onLoad(value, icicle, from, to);
                    }

                    String prefix = column.prefix();
                    Class<?> prefixClass = column.prefixClass();
                    if (!Object.class.equals(prefixClass)) {
                        prefix = (String) ((Value) SimpleParseCache.get().getObject(prefixClass)).value();
                    }

                    String suffix = column.suffix();
                    Class<?> suffixClass = column.suffixClass();
                    if (!Object.class.equals(suffixClass)) {
                        suffix = (String) ((Value) SimpleParseCache.get().getObject(suffixClass)).value();
                    }

                    value = prefix + value + suffix;
                    }

                    field.set(from, value);
                }
                else if (fieldType.equals(Byte[].class) || fieldType.equals(byte[].class)) {
                    String value = (String) (filtered ? saveValue : to.getString(columnName));
                    if (!filtered && !(filter instanceof OptionalFilter)) {
                        value = (String) filter.onLoad(value, icicle, from, to);
                    }

                    field.set(from, value);
                }
                else if (fieldType.equals(JSONObject.class)) {
                    JSONObject value = (JSONObject) (filtered ? saveValue : to.getJSONObject(columnName));
                    if (!filtered && !(filter instanceof OptionalFilter)) {
                        value = (JSONObject) filter.onLoad(value, icicle, from, to);
                    }

                    field.set(from, value);
                }
                else if (fieldType.equals(List.class)) {
                    List value = (List) (filtered ? saveValue : to.getList(columnName));
                    if (!filtered && !(filter instanceof OptionalFilter)) {
                        value = (List) filter.onLoad(value, icicle, from, to);
                    }

                    field.set(from, value);
                }
                else if (fieldType.equals(Date.class)) {
                    Date value = (Date) (filtered ? saveValue : to.getDate(columnName));
                    if (!filtered && !(filter instanceof OptionalFilter)) {
                        value = (Date) filter.onLoad(value, icicle, from, to);
                    }

                    field.set(from, value);
                }
                else if (fieldType.equals(ParseUser.class)) {
                    ParseUser value = (ParseUser) (filtered ? saveValue : to.getParseUser(columnName));
                    if (!filtered && !(filter instanceof OptionalFilter)) {
                        value = (ParseUser) filter.onLoad(value, icicle, from, to);
                    }

                    field.set(from, value);
                }
                else if (fieldType.equals(ParseGeoPoint.class)) {
                    ParseGeoPoint value = (ParseGeoPoint) (filtered ? saveValue : to.getParseGeoPoint(columnName));
                    if (!filtered && !(filter instanceof OptionalFilter)) {
                        value = (ParseGeoPoint) filter.onLoad(value, icicle, from, to);
                    }

                    field.set(from, value);
                }
                else if (fieldType.equals(ParseObject.class)) {
                    ParseObject value = (ParseObject) (filtered ? saveValue : to.getParseObject(columnName));
                    if (!filtered && !(filter instanceof OptionalFilter)) {
                        value = (ParseObject) filter.onLoad(value, icicle, from, to);
                    }

                    field.set(from, value);
                }
                //else if (ReflectionUtils.isSubclassOf(fieldType, Enum.class)) {
                    //to.put(columnName, ((Enum<?>) value).name());
                //}
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) {
            }
        }
        SimpleParseCache.get().columnDataCache.clear();
        return from;
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
        if (!(mKlass.isAssignableFrom(ParseObject.class))) {
            return null;
        }

        Class<? extends ParseObject> klass = (Class<? extends ParseObject>) mKlass;

        if (mQuery == null) {
            mQuery = ParseQuery.getQuery(klass);
        }

        return mQuery;
    }

    public static <T extends ParseObject> ParseQuery<T> getQuery(Class<T> klass) {
        return from(klass).getQuery();
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
