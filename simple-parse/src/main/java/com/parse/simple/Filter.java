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

public interface Filter<F, T> {
    Class<T> getSaveType();
    Class<F> getLoadType();
    T onSave(F value, Bundle icicle, Object from, ParseObject to);
    F onLoad(T value, Bundle icicle, Object from, ParseObject to);
}
