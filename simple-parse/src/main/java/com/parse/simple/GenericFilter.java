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

import android.os.Bundle;
import com.parse.ParseObject;
//import java.lang.reflect.ParameterizedType;
import net.jodah.typetools.TypeResolver;

public class GenericFilter<F, T> implements Filter<F, T> {
    private final Class<F> loadType;
    private final Class<T> saveType;

    public GenericFilter() {
        Class<?>[] typeArgs = TypeResolver.resolveRawArguments(GenericFilter.class, Filter.class);
        //Class<?>[] typeArgs = TypeResolver.resolveRawArguments(getClass(), Filter.class);
        this.loadType = (Class<F>) typeArgs[0];
        this.saveType = (Class<T>) typeArgs[1];
    }

    @Override
    public Class<T> getSaveType() {
        return this.saveType;
    }

    @Override
    public Class<F> getLoadType() {
        return this.loadType;
    }

    public T onSave(F value) {
        return (T) value;
    }

    public F onLoad(T value) {
        return (F) value;
    }

    public T onSave(F value, Bundle icicle) {
        return onSave(value);
    }

    public F onLoad(T value, Bundle icicle) {
        return onLoad(value);
    }

    @Override
    public T onSave(F value, Bundle icicle, Object from, ParseObject to) {
        return onSave(value, icicle);
    }

    @Override
    public F onLoad(T value, Bundle icicle, Object from, ParseObject to) {
        return onLoad(value, icicle);
    }
}
