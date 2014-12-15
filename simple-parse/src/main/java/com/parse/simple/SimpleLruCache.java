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
import java.util.Set;
import java.util.Collection;
import android.support.v4.util.LruCache;

public class SimpleLruCache<K, V> extends LinkedHashMap<K, V> {
    private LruCache<K, V> cache;

    private class MyLruCache<E, T> extends LruCache<E, T> {
        public MyLruCache(int maxSize) {
            super(maxSize);
        }

        @Override
        protected void entryRemoved(boolean evicted, E key, T oldValue, T newValue) {
            super.entryRemoved(evicted, key, oldValue, newValue);
            superRemove(key);
        }
    }

    public SimpleLruCache(int maxSize) {
        cache = new MyLruCache<K, V>(maxSize);
    }

    @Override
    public void clear() {
        super.clear();
        cache.evictAll();
    }

    @Override
    public V put(K key, V value) {
        V v = super.put(key, value);
        cache.put(key, value);
        return v;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        super.putAll(map);

        for (Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
            cache.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public V remove(Object key) {
        V v = super.remove(key);
        cache.remove((K) key);
        return v;
    }

    public V superRemove(Object key) {
        return super.remove(key);
    }

    /* LruCache {{{ */

    public synchronized /*final*/ int createCount() {
        return cache.createCount();
    }

    public /*final*/ void evictAll() {
        cache.evictAll();
    }

    public synchronized /*final*/ int evictionCount() {
        return cache.evictionCount();
    }

    //public [>final<] V get(K key)
    public synchronized /*final*/ int hitCount() {
        return cache.hitCount();
    }

    public synchronized /*final*/ int maxSize() {
        return cache.maxSize();
    }

    public synchronized /*final*/ int missCount() {
        return cache.missCount();
    }

    //public [>final<] V put(K key, V value)
    public synchronized /*final*/ int putCount() {
        return cache.putCount();
    }

    //public [>final<] V remove(K key)
    //
    //public void resize(int maxSize) {
        //cache.resize(maxSize);
    //}

    @Override
    public synchronized /*final*/ int size() {
        return cache.size();
    }

    public synchronized /*final*/ Map<K, V> snapshot() {
        return cache.snapshot();
        // return this();
    }

    //public synchronized [>final<] String toString()
    public void trimToSize(int maxSize) {
        cache.trimToSize(maxSize);
    }

    /* LruCache }}} */
}
