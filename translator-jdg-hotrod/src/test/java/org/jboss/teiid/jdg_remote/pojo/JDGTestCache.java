/*
 * JBoss, Home of Professional Open Source.
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.  Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */
package org.jboss.teiid.jdg_remote.pojo;



import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.infinispan.client.hotrod.CacheTopologyInfo;
import org.infinispan.client.hotrod.Flag;
import org.infinispan.client.hotrod.MetadataValue;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.ServerStatistics;
import org.infinispan.client.hotrod.StreamingRemoteCache;
import org.infinispan.client.hotrod.VersionedValue;
import org.infinispan.commons.util.CloseableIterator;
import org.infinispan.query.dsl.Query;
import org.teiid.translator.object.ClassRegistry;

/**
 * Sample cache of objects
 * 
 * @author vhalbert
 * @param <K>
 * @param <V>
 *
 */
@SuppressWarnings({ "nls" })
public class JDGTestCache<K, V>  implements RemoteCache<K, V>{
		
	public static ClassRegistry CLASS_REGISTRY = new ClassRegistry();
	
	protected Map cache = Collections.synchronizedMap(new HashMap<Object, Object>());
		
	/**
	 * 
	 *
	 * @param arg0 
	 * @param arg1 
	 * @return V
	 * @see org.infinispan.commons.api.BasicCache#put(java.lang.Object, java.lang.Object)
	 */
     @Override
	public V put(K arg0, V arg1) {
		cache.put(arg0, arg1);
		return arg1;
	}

	/**
	 *  
	 *
	 * @see org.infinispan.commons.api.BasicCache#remove(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
     @Override
	public V remove(Object arg0) {
		return (V) cache.remove(arg0);
	}

	/**
	 *  
	 *
	 * @see java.util.concurrent.ConcurrentMap#remove(java.lang.Object, java.lang.Object)
	 */
     @Override
	public boolean remove(Object arg0, Object arg1) {
		boolean exist = cache.containsKey(arg0);
		if (exist) cache.remove(arg0);
		return exist;
	}

	/**
	 *  
	 *
	 * @see java.util.concurrent.ConcurrentMap#replace(java.lang.Object, java.lang.Object)
	 */
     @Override
	public V replace(K arg0, V arg1) {
		cache.put(arg0, arg1);
		return arg1;
	}

	/**
	 *  
	 *
	 * @see java.util.Map#clear()
	 */
     @Override
	public void clear() {
		cache.clear();
	}

	/**
	 *  
	 *
	 * @see java.util.Map#containsKey(java.lang.Object)
	 */
     @Override
	public boolean containsKey(Object arg0) {
		return cache.containsKey(arg0);
	}

	/**
	 *  
	 *
	 * @see java.util.Map#get(java.lang.Object)
	 */
     @Override
	public V get(Object arg0) {
		return (V) cache.get(arg0);
	}

	/**
	 *  
	 *
	 * @see org.infinispan.client.hotrod.RemoteCache#size()
	 */
     @Override
	public int size() {
		return cache.size();
	}

	/**
	 *  
	 *
	 * @see org.infinispan.client.hotrod.RemoteCache#isEmpty()
	 */
     @Override
	public boolean isEmpty() {
		return cache.isEmpty();
	}

	/**
	 *  
	 *
	 * @see org.infinispan.client.hotrod.RemoteCache#containsValue(java.lang.Object)
	 */
     @Override
	public boolean containsValue(Object value) {
		return cache.containsValue(value);
	}

	/**
	 *  
	 *
	 * @see org.infinispan.client.hotrod.RemoteCache#keySet()
	 */     @Override
	public Set<K> keySet() {
		return (Set<K>) cache.keySet();
	}

	/**
	 *  
	 *
	 * @see org.infinispan.client.hotrod.RemoteCache#values()
	 */
	 @Override
	public Collection<V> values() {
		return (Collection<V>) cache.values();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.infinispan.commons.api.BasicCache#getName()
	 */
	@Override
	public String getName() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.infinispan.commons.api.BasicCache#getVersion()
	 */
	@Override
	public String getVersion() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.infinispan.commons.api.BasicCache#put(java.lang.Object, java.lang.Object, long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public V put(K arg0, V arg1, long arg2, TimeUnit arg3) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.infinispan.commons.api.BasicCache#put(java.lang.Object, java.lang.Object, long, java.util.concurrent.TimeUnit, long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public V put(K arg0, V arg1, long arg2, TimeUnit arg3, long arg4, TimeUnit arg5) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.infinispan.commons.api.BasicCache#putIfAbsent(java.lang.Object, java.lang.Object, long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public V putIfAbsent(K arg0, V arg1, long arg2, TimeUnit arg3) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.infinispan.commons.api.BasicCache#putIfAbsent(java.lang.Object, java.lang.Object, long, java.util.concurrent.TimeUnit, long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public V putIfAbsent(K arg0, V arg1, long arg2, TimeUnit arg3, long arg4, TimeUnit arg5) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.infinispan.commons.api.BasicCache#replace(java.lang.Object, java.lang.Object, long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public V replace(K arg0, V arg1, long arg2, TimeUnit arg3) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.infinispan.commons.api.BasicCache#replace(java.lang.Object, java.lang.Object, java.lang.Object, long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public boolean replace(K arg0, V arg1, V arg2, long arg3, TimeUnit arg4) {
		return false;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.infinispan.commons.api.BasicCache#replace(java.lang.Object, java.lang.Object, long, java.util.concurrent.TimeUnit, long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public V replace(K arg0, V arg1, long arg2, TimeUnit arg3, long arg4, TimeUnit arg5) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.infinispan.commons.api.BasicCache#replace(java.lang.Object, java.lang.Object, java.lang.Object, long, java.util.concurrent.TimeUnit, long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public boolean replace(K arg0, V arg1, V arg2, long arg3, TimeUnit arg4, long arg5, TimeUnit arg6) {
		return false;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.infinispan.commons.api.AsyncCache#clearAsync()
	 */
	@Override
	public CompletableFuture<Void> clearAsync() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.infinispan.commons.api.AsyncCache#getAsync(java.lang.Object)
	 */
	@Override
	public CompletableFuture<V> getAsync(K arg0) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.infinispan.commons.api.AsyncCache#putAsync(java.lang.Object, java.lang.Object)
	 */
	@Override
	public CompletableFuture<V> putAsync(K arg0, V arg1) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.infinispan.commons.api.AsyncCache#putAsync(java.lang.Object, java.lang.Object, long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public CompletableFuture<V> putAsync(K arg0, V arg1, long arg2, TimeUnit arg3) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.infinispan.commons.api.AsyncCache#putAsync(java.lang.Object, java.lang.Object, long, java.util.concurrent.TimeUnit, long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public CompletableFuture<V> putAsync(K arg0, V arg1, long arg2, TimeUnit arg3, long arg4, TimeUnit arg5) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.infinispan.commons.api.AsyncCache#putIfAbsentAsync(java.lang.Object, java.lang.Object)
	 */
	@Override
	public CompletableFuture<V> putIfAbsentAsync(K arg0, V arg1) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.infinispan.commons.api.AsyncCache#putIfAbsentAsync(java.lang.Object, java.lang.Object, long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public CompletableFuture<V> putIfAbsentAsync(K arg0, V arg1, long arg2, TimeUnit arg3) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.infinispan.commons.api.AsyncCache#putIfAbsentAsync(java.lang.Object, java.lang.Object, long, java.util.concurrent.TimeUnit, long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public CompletableFuture<V> putIfAbsentAsync(K arg0, V arg1, long arg2, TimeUnit arg3, long arg4, TimeUnit arg5) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.infinispan.commons.api.AsyncCache#removeAsync(java.lang.Object)
	 */
	@Override
	public CompletableFuture<V> removeAsync(Object arg0) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.infinispan.commons.api.AsyncCache#removeAsync(java.lang.Object, java.lang.Object)
	 */
	@Override
	public CompletableFuture<Boolean> removeAsync(Object arg0, Object arg1) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.infinispan.commons.api.AsyncCache#replaceAsync(java.lang.Object, java.lang.Object)
	 */
	@Override
	public CompletableFuture<V> replaceAsync(K arg0, V arg1) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.infinispan.commons.api.AsyncCache#replaceAsync(java.lang.Object, java.lang.Object, java.lang.Object)
	 */
	@Override
	public CompletableFuture<Boolean> replaceAsync(K arg0, V arg1, V arg2) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.infinispan.commons.api.AsyncCache#replaceAsync(java.lang.Object, java.lang.Object, long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public CompletableFuture<V> replaceAsync(K arg0, V arg1, long arg2, TimeUnit arg3) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.infinispan.commons.api.AsyncCache#replaceAsync(java.lang.Object, java.lang.Object, java.lang.Object, long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public CompletableFuture<Boolean> replaceAsync(K arg0, V arg1, V arg2, long arg3, TimeUnit arg4) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.infinispan.commons.api.AsyncCache#replaceAsync(java.lang.Object, java.lang.Object, long, java.util.concurrent.TimeUnit, long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public CompletableFuture<V> replaceAsync(K arg0, V arg1, long arg2, TimeUnit arg3, long arg4, TimeUnit arg5) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.infinispan.commons.api.AsyncCache#replaceAsync(java.lang.Object, java.lang.Object, java.lang.Object, long, java.util.concurrent.TimeUnit, long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public CompletableFuture<Boolean> replaceAsync(K arg0, V arg1, V arg2, long arg3, TimeUnit arg4, long arg5,
			TimeUnit arg6) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see java.util.concurrent.ConcurrentMap#putIfAbsent(java.lang.Object, java.lang.Object)
	 */
	@Override
	public V putIfAbsent(K key, V value) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see java.util.concurrent.ConcurrentMap#replace(java.lang.Object, java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean replace(K key, V oldValue, V newValue) {
		return false;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.infinispan.commons.api.Lifecycle#start()
	 */
	@Override
	public void start() {
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.infinispan.commons.api.Lifecycle#stop()
	 */
	@Override
	public void stop() {
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.infinispan.client.hotrod.RemoteCache#removeWithVersion(java.lang.Object, long)
	 */
	@Override
	public boolean removeWithVersion(K key, long version) {
		return false;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.infinispan.client.hotrod.RemoteCache#removeWithVersionAsync(java.lang.Object, long)
	 */
	@Override
	public CompletableFuture<Boolean> removeWithVersionAsync(K key, long version) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.infinispan.client.hotrod.RemoteCache#replaceWithVersion(java.lang.Object, java.lang.Object, long)
	 */
	@Override
	public boolean replaceWithVersion(K key, V newValue, long version) {
		return false;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.infinispan.client.hotrod.RemoteCache#replaceWithVersion(java.lang.Object, java.lang.Object, long, int)
	 */
	@Override
	public boolean replaceWithVersion(K key, V newValue, long version, int lifespanSeconds) {
		return false;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.infinispan.client.hotrod.RemoteCache#replaceWithVersion(java.lang.Object, java.lang.Object, long, int, int)
	 */
	@Override
	public boolean replaceWithVersion(K key, V newValue, long version, int lifespanSeconds, int maxIdleTimeSeconds) {
		return false;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.infinispan.client.hotrod.RemoteCache#replaceWithVersion(java.lang.Object, java.lang.Object, long, long, java.util.concurrent.TimeUnit, long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public boolean replaceWithVersion(K key, V newValue, long version, long lifespan, TimeUnit lifespanTimeUnit,
			long maxIdle, TimeUnit maxIdleTimeUnit) {
		return false;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.infinispan.client.hotrod.RemoteCache#replaceWithVersionAsync(java.lang.Object, java.lang.Object, long)
	 */
	@Override
	public CompletableFuture<Boolean> replaceWithVersionAsync(K key, V newValue, long version) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.infinispan.client.hotrod.RemoteCache#replaceWithVersionAsync(java.lang.Object, java.lang.Object, long, int)
	 */
	@Override
	public CompletableFuture<Boolean> replaceWithVersionAsync(K key, V newValue, long version, int lifespanSeconds) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.infinispan.client.hotrod.RemoteCache#replaceWithVersionAsync(java.lang.Object, java.lang.Object, long, int, int)
	 */
	@Override
	public CompletableFuture<Boolean> replaceWithVersionAsync(K key, V newValue, long version, int lifespanSeconds,
			int maxIdleSeconds) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.infinispan.client.hotrod.RemoteCache#retrieveEntries(java.lang.String, java.util.Set, int)
	 */
	@Override
	public CloseableIterator<java.util.Map.Entry<Object, Object>> retrieveEntries(String filterConverterFactory,
			Set<Integer> segments, int batchSize) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.infinispan.client.hotrod.RemoteCache#retrieveEntries(java.lang.String, java.lang.Object[], java.util.Set, int)
	 */
	@Override
	public CloseableIterator<java.util.Map.Entry<Object, Object>> retrieveEntries(String filterConverterFactory,
			Object[] filterConverterParams, Set<Integer> segments, int batchSize) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.infinispan.client.hotrod.RemoteCache#retrieveEntries(java.lang.String, int)
	 */
	@Override
	public CloseableIterator<java.util.Map.Entry<Object, Object>> retrieveEntries(String filterConverterFactory,
			int batchSize) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.infinispan.client.hotrod.RemoteCache#retrieveEntriesByQuery(org.infinispan.query.dsl.Query, java.util.Set, int)
	 */
	@Override
	public CloseableIterator<java.util.Map.Entry<Object, Object>> retrieveEntriesByQuery(Query filterQuery,
			Set<Integer> segments, int batchSize) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.infinispan.client.hotrod.RemoteCache#retrieveEntriesWithMetadata(java.util.Set, int)
	 */
	@Override
	public CloseableIterator<java.util.Map.Entry<Object, MetadataValue<Object>>> retrieveEntriesWithMetadata(
			Set<Integer> segments, int batchSize) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.infinispan.client.hotrod.RemoteCache#getVersioned(java.lang.Object)
	 */
	@Override
	public VersionedValue<V> getVersioned(K key) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.infinispan.client.hotrod.RemoteCache#getWithMetadata(java.lang.Object)
	 */
	@Override
	public MetadataValue<V> getWithMetadata(K key) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.infinispan.client.hotrod.RemoteCache#entrySet()
	 */
	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.infinispan.client.hotrod.RemoteCache#putAll(java.util.Map, long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public void putAll(Map<? extends K, ? extends V> map, long lifespan, TimeUnit unit) {
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.infinispan.client.hotrod.RemoteCache#putAll(java.util.Map, long, java.util.concurrent.TimeUnit, long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public void putAll(Map<? extends K, ? extends V> map, long lifespan, TimeUnit lifespanUnit, long maxIdleTime,
			TimeUnit maxIdleTimeUnit) {
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.infinispan.client.hotrod.RemoteCache#putAllAsync(java.util.Map)
	 */
	@Override
	public CompletableFuture<Void> putAllAsync(Map<? extends K, ? extends V> data) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.infinispan.client.hotrod.RemoteCache#putAllAsync(java.util.Map, long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public CompletableFuture<Void> putAllAsync(Map<? extends K, ? extends V> data, long lifespan, TimeUnit unit) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.infinispan.client.hotrod.RemoteCache#putAllAsync(java.util.Map, long, java.util.concurrent.TimeUnit, long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public CompletableFuture<Void> putAllAsync(Map<? extends K, ? extends V> data, long lifespan, TimeUnit lifespanUnit,
			long maxIdle, TimeUnit maxIdleUnit) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.infinispan.client.hotrod.RemoteCache#putAll(java.util.Map)
	 */
	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.infinispan.client.hotrod.RemoteCache#stats()
	 */
	@Override
	public ServerStatistics stats() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.infinispan.client.hotrod.RemoteCache#withFlags(org.infinispan.client.hotrod.Flag[])
	 */
	@Override
	public RemoteCache<K, V> withFlags(Flag... flags) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.infinispan.client.hotrod.RemoteCache#getRemoteCacheManager()
	 */
	@Override
	public RemoteCacheManager getRemoteCacheManager() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.infinispan.client.hotrod.RemoteCache#getBulk()
	 */
	@Override
	public Map<K, V> getBulk() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.infinispan.client.hotrod.RemoteCache#getBulk(int)
	 */
	@Override
	public Map<K, V> getBulk(int size) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.infinispan.client.hotrod.RemoteCache#getAll(java.util.Set)
	 */
	@Override
	public Map<K, V> getAll(Set<? extends K> keys) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.infinispan.client.hotrod.RemoteCache#getProtocolVersion()
	 */
	@Override
	public String getProtocolVersion() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.infinispan.client.hotrod.RemoteCache#addClientListener(java.lang.Object)
	 */
	@Override
	public void addClientListener(Object listener) {
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.infinispan.client.hotrod.RemoteCache#addClientListener(java.lang.Object, java.lang.Object[], java.lang.Object[])
	 */
	@Override
	public void addClientListener(Object listener, Object[] filterFactoryParams, Object[] converterFactoryParams) {
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.infinispan.client.hotrod.RemoteCache#removeClientListener(java.lang.Object)
	 */
	@Override
	public void removeClientListener(Object listener) {
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.infinispan.client.hotrod.RemoteCache#getListeners()
	 */
	@Override
	public Set<Object> getListeners() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.infinispan.client.hotrod.RemoteCache#execute(java.lang.String, java.util.Map)
	 */
	@Override
	public <T> T execute(String scriptName, Map<String, ?> params) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.infinispan.client.hotrod.RemoteCache#getCacheTopologyInfo()
	 */
	@Override
	public CacheTopologyInfo getCacheTopologyInfo() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.infinispan.client.hotrod.RemoteCache#streaming()
	 */
	@Override
	public StreamingRemoteCache<K> streaming() {
		return null;
	}
	 
	 

}
