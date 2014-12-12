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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@ParseObjectMethod("COLUMN")
public @interface ParseColumn {
    String value() default "";
    String prefix() default "";
    String suffix() default "";
    Class<? extends Filter> filter() default OptionalFilter.class;
    Class<? extends Value> prefixClass() default Optional.class;
    Class<? extends Value> suffixClass() default Optional.class;
    boolean self() default false;
    Class<? extends Value> onSave() default Optional.class;
    Class<? extends Value> onLoad() default Optional.class;
    //boolean objectId() default false;
    //boolean remove() default false;
    //String equals() default "";
    //String contains() default "";
}
