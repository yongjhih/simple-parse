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

public class SimpleFilter extends GenericFilter<Object, Object> {
    public Object onSave(Object value) {
        return value;
    }

    public Object onLoad(Object value) {
        return value;
    }

    public Object onSave(Object value, Bundle icicle) {
        return onSave(value);
    }

    public Object onLoad(Object value, Bundle icicle) {
        return onLoad(value);
    }

    @Override
    public Object onSave(Object value, Bundle icicle, Object from, ParseObject to) {
        return onSave(value, icicle);
    }

    @Override
    public Object onLoad(Object value, Bundle icicle, Object from, ParseObject to) {
        return onLoad(value, icicle);
    }
}
