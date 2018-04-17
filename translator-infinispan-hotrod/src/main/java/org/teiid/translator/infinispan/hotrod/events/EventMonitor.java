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
package org.teiid.translator.infinispan.hotrod.events;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.infinispan.client.hotrod.RemoteCache;
import org.teiid.logging.LogConstants;
import org.teiid.logging.LogManager;

/**
 * @author vhalbert
 *
 */
final public class EventMonitor {
	
	static Map<String, CacheEventListenerInterface> ALIAS_CACHE_EVENTS = new HashMap<String, CacheEventListenerInterface>(); 
	
	private static final CacheEventNoOpListener NOOP_LISTENER = new CacheEventNoOpListener();

	/**
	 * Should be only called by the direct query that triggers the cleaning of the cache before starting materialization
	 * @param cache
	 */
	public static synchronized void addListenerInstance(@SuppressWarnings("rawtypes") RemoteCache cache) {	
		
		clearListeners(cache);
		CacheEventListener m = (CacheEventListener) ALIAS_CACHE_EVENTS.get(cache.getName());
		if (m == null) {
			m = new CacheEventListener();
			ALIAS_CACHE_EVENTS.put(cache.getName(), m);
		}
		m.reset();		
		cache.addClientListener(m);

	}
	
	public static synchronized CacheEventListenerInterface getListenerInstance(String cacheName) {
		
		if (ALIAS_CACHE_EVENTS.containsKey(cacheName)) {
			return ALIAS_CACHE_EVENTS.get(cacheName);
		}
		
	    LogManager.logTrace(LogConstants.CTX_CONNECTOR, "[MaterializationEventMonitor] getListenerInstance is returning CacheEventNoOPListener for cache " + cacheName);
		return NOOP_LISTENER;
	}
	
	private static void clearListeners(@SuppressWarnings("rawtypes") RemoteCache cache) {
		// clean out any existing listeners
		Set l = cache.getListeners();
		if (l != null && ! l.isEmpty()) {
			for (Object o:l) {
				cache.removeClientListener(o);
			}
		}	
	}
}
