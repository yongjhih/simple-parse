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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Request metadata about a service interface declaration. */
final class ParseObjectMethodInfo {

  final Method mMethod;
  String mMethodName;
  Type mReturnType;

  boolean mLoaded = false;

  // Parameter-level details
  Annotation[] mParamAnnotations;

  ParseObjectMethodInfo(Method method) {
    this.mMethod = method;
    mReturnType = getReturnType();
  }

  private RuntimeException methodError(String message, Object... args) {
    if (args.length > 0) {
      message = String.format(message, args);
    }
    return new IllegalArgumentException(
        mMethod.getDeclaringClass().getSimpleName() + "." + mMethod.getName() + ": " + message);
  }

  private RuntimeException parameterError(int index, String message, Object... args) {
    return methodError(message + " (parameter #" + (index + 1) + ")", args);
  }

  synchronized void init() {
    if (mLoaded) return;

    updateMethodAnnotations();
    updateParamAnnotations();

    mLoaded = true;
  }

  private void updateMethodAnnotations() {
    for (Annotation methodAnnotation : mMethod.getAnnotations()) {
      Class<? extends Annotation> annotationType = methodAnnotation.annotationType();
      ParseObjectMethod methodInfo = null;

      for (Annotation innerAnnotation : annotationType.getAnnotations()) {
        if (ParseObjectMethod.class == innerAnnotation.annotationType()) {
          methodInfo = (ParseObjectMethod) innerAnnotation;
          break;
        }
      }

      if (methodInfo != null) {
        if (mMethodName != null) {
          throw methodError("Found: %s and %s.", mMethodName,
              methodInfo.value());
        }
        String column;
        try {
          column = (String) annotationType.getMethod("value").invoke(methodAnnotation);
        } catch (Exception e) {
          throw methodError("Failed to extract String 'value' from @%s annotation.",
              annotationType.getSimpleName());
        }
        mMethodName = methodInfo.value();
      }
    }

    if (mMethodName == null) {
      throw methodError("Method annotation is required (e.g., @ParseColumn, etc.).");
    }
  }

  private Type getReturnType() {
    Type returnType = mMethod.getGenericReturnType();

    return returnType;
  }

  private static Type getParameterUpperBound(ParameterizedType type) {
    Type[] types = type.getActualTypeArguments();
    for (int i = 0; i < types.length; i++) {
      Type paramType = types[i];
      if (paramType instanceof WildcardType) {
        types[i] = ((WildcardType) paramType).getUpperBounds()[0];
      }
    }
    return types[0];
  }

  private void updateParamAnnotations() {
    Class<?>[] methodParameterTypes = mMethod.getParameterTypes();
    Annotation[][] methodParameterAnnotationArrays = mMethod.getParameterAnnotations();
    int count = methodParameterAnnotationArrays.length;
    Annotation[] mParamAnnotations = new Annotation[count];

    for (int i = 0; i < count; i++) {
      Class<?> methodParameterType = methodParameterTypes[i];
      Annotation[] methodParameterAnnotations = methodParameterAnnotationArrays[i];
      if (methodParameterAnnotations != null) {
        for (Annotation methodParameterAnnotation : methodParameterAnnotations) {
          Class<? extends Annotation> methodAnnotationType =
              methodParameterAnnotation.annotationType();

          if (methodAnnotationType == ParseColumn.class) {
            String name = ((ParseColumn) methodParameterAnnotation).value();
          } else {
            continue;
          }

          //if (mParamAnnotations[i] != null) { }
          mParamAnnotations[i] = methodParameterAnnotation;
        }
      }

      // if (mParamAnnotations[i] == null) { }
    }

    this.mParamAnnotations = mParamAnnotations;
  }
}
