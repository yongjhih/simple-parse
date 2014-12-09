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
import java.util.Map;
//import simpleparse.converter.ConversionException;
//import simpleparse.converter.Converter;

/**
 * ParseObjectProxy.
 *
 * <pre>
 * A Type
 *
 * @ParseObjectSubclass("User")
 * public interface IParseUser {
 *   @ParseColumn("facebookId")
 *   public String getFacebookId(); // ParseObject.getString("facebookId")
 *
 *   public IParseUser setFacebookId(@ParseColumn("facebookId") String facebookId); // ParseObject.putString("facebookId", facebookId)
 * }
 *
 * SimpleParseQuery.from(IParseUser.class).is("xWMyZ4YEGZ").query(new GetCallback<ParseUser>() {
 *   public void done(ParseUser parseUser, ParseException e) {
 *     if (e == null) {
 *       parseUser.getFacebookId();
 *     }
 *   }
 * });
 *
 * SimpleParseQuery.from(IParseUser.class).setFacebookId("123").saveInBackground();
 * </pre>
 *
 * <pre>
 * B Type
 *
 * @ParseObjectSubclass("User")
 * public class ParseUser extends SimpleParseUser // extends SimpleParseObject {
 *   @ParseColumn("facebookId")
 *   public String mFacebookId;
 *
 *   public ParseUser setFacebookId(String facebookId) {
 *     mFacebookId = facebookId;
 *     return this;
 *   }
 *
 *   public ParseUser getFacebookId() {
 *     return mFacebookId;
 *   }
 * }
 *
 * new ParseUser().setFacebookId("123").saveInBackground();
 *
 * SimpleParseQuery.from(ParseUser.class).is("xWMyZ4YEGZ").query(new GetCallback<ParseUser>() {
 *   public void done(ParseUser parseUser, ParseException e) {
 *     if (e == null) {
 *       parseUser.getFacebookId();
 *     }
 *   }
 * });
 * </pre>
 */
public class ParseObjectAdapter {
    private final Map<Class<?>, Map<Method, ParseObjectMethodInfo>> mParseObjectMethodInfoCache =
        new LinkedHashMap<Class<?>, Map<Method, ParseObjectMethodInfo>>();

    private ParseObjectAdapter() {
    }

    @SuppressWarnings("unchecked")
    public <T> T create(Class<T> parseObject) {
        //validateClass(parseObject);
        return (T) Proxy.newProxyInstance(parseObject.getClassLoader(), new Class<?>[] { parseObject },
                new ParseObjectHandler(getMethodInfoCache(parseObject)));
    }

    Map<Method, ParseObjectMethodInfo> getMethodInfoCache(Class<?> parseObject) {
        synchronized (mParseObjectMethodInfoCache) {
            Map<Method, ParseObjectMethodInfo> methodInfoCache = mParseObjectMethodInfoCache.get(parseObject);
            if (methodInfoCache == null) {
                methodInfoCache = new LinkedHashMap<Method, ParseObjectMethodInfo>();
                mParseObjectMethodInfoCache.put(parseObject, methodInfoCache);
            }
            return methodInfoCache;
        }
    }

    static ParseObjectMethodInfo getMethodInfo(Map<Method, ParseObjectMethodInfo> cache, Method method) {
        synchronized (cache) {
            ParseObjectMethodInfo methodInfo = cache.get(method);
            if (methodInfo == null) {
                methodInfo = new ParseObjectMethodInfo(method);
                cache.put(method, methodInfo);
            }
            return methodInfo;
        }
    }

    private class ParseObjectHandler implements InvocationHandler {
        private final Map<Method, ParseObjectMethodInfo> mMethodCache;

        ParseObjectHandler(Map<Method, ParseObjectMethodInfo> methodCache) {
            this.mMethodCache = methodCache;
        }

        @SuppressWarnings("unchecked")
        @Override
        public Object invoke(Object proxy, Method method, final Object[] args) throws Throwable {
            // If the method is a method from Object then defer to normal invocation.
            if (method.getDeclaringClass() == Object.class) {
                return method.invoke(this, args);
            }

            final ParseObjectMethodInfo methodInfo = getMethodInfo(mMethodCache, method);

            try {
                methodInfo.init();
            } catch (Throwable t) {
            } finally {
            }
            return null; // TODO
        }
    }
}

