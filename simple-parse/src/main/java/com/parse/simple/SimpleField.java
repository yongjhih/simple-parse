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

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class SimpleField {
    String name;
    Class<?> type;

    Map<Class<?>, Object> annotations = new LinkedHashMap<Class<?>, Object>();

    Field field;

    public SimpleField(Field field) {
        this.field = field;
    }

    public String getName() {
        if (name == null) {
            name = field.getName();
        }
        return name;
    }

    public Class<?> getType() {
        if (type == null) {
            type = field.getType();
        }
        return type;
    }

    public <A extends Annotation> A getAnnotation(Class<A> annotationClass) {
        if (annotations.containsKey(annotationClass)) {
            return (A) annotations.get(annotationClass);
        }

        A annotation = (A) annotations.get(annotationClass);

        if (annotation == null) {
            annotation = (A) field.getAnnotation(annotationClass);
        }

        return annotation;
    }

    public void setAccessible(boolean accessible) {
        field.setAccessible(accessible);
    }

    public void setByte(Object from, byte value) throws IllegalAccessException {
        field.setByte(from, value);
    }

    public void setShort(Object from, short value) throws IllegalAccessException {
        field.setShort(from, value);
    }

    public void setInt(Object from, int value) throws IllegalAccessException {
        field.setInt(from, value);
    }

    public void setLong(Object from, long value) throws IllegalAccessException {
        field.setLong(from, value);
    }

    public void setFloat(Object from, float value) throws IllegalAccessException {
        field.setFloat(from, value);
    }

    public void setDouble(Object from, double value) throws IllegalAccessException {
        field.setDouble(from, value);
    }

    public void setBoolean(Object from, boolean value) throws IllegalAccessException {
        field.setBoolean(from, value);
    }

    public void set(Object from, Object value) throws IllegalAccessException {
        field.set(from, value);
    }

    public Object get(Object from) throws IllegalAccessException {
        return field.get(from);
    }
}
