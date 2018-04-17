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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.infinispan.client.hotrod.annotation.ClientCacheEntryCreated;
import org.infinispan.client.hotrod.annotation.ClientCacheEntryModified;
import org.infinispan.client.hotrod.annotation.ClientCacheEntryRemoved;
import org.infinispan.client.hotrod.annotation.ClientListener;
import org.infinispan.client.hotrod.event.ClientCacheEntryCreatedEvent;
import org.infinispan.client.hotrod.event.ClientCacheEntryModifiedEvent;
import org.infinispan.client.hotrod.event.ClientCacheEntryRemovedEvent;
import org.teiid.logging.LogConstants;
import org.teiid.logging.LogManager;

/**
 * @author vhalbert
 *
 */
@ClientListener
public class CacheEventListener implements CacheEventListenerInterface {

	private float default_load_factor = .75f;
	private int default_size = 100000;
	private int cnt = 0;
	private int pre = 0;

	Set<Object> key = Collections.synchronizedSet(new HashSet<Object>(default_size, default_load_factor));

	@Override
	public void reset() {
		key.clear();
		cnt = 0;
		pre = 0;
	}
	@Override
	public boolean eventsMonitored() {
		return true;
	}

	@Override
	public int getEventCount() {
		return key.size();
	}

	@Override
	public void addEvent(final Object okey) {
		key.add(okey);
	    LogManager.logTrace(LogConstants.CTX_CONNECTOR, ++pre + " [CacheEventListener] Add event (pre) " + okey);
	}

	@Override
	public void addEvents(Set<Object> keyset) {
		key.addAll(keyset);
	}

	@ClientCacheEntryModified
	public void handle(ClientCacheEntryModifiedEvent event) {
		if (pre > 0) {
			key.remove(event.getKey());
			LogManager.logTrace(LogConstants.CTX_CONNECTOR, ++cnt + " [CacheEventListener] Modified entry (post) " + event.getKey());
		}
	}

	@ClientCacheEntryCreated
	public void handle(ClientCacheEntryCreatedEvent event) {
		if (pre > 0) {
			key.remove(event.getKey());
			LogManager.logTrace(LogConstants.CTX_CONNECTOR, ++cnt + " [CacheEventListener] Created entry (post) " + event.getKey());
		}
	}

	@ClientCacheEntryRemoved
	public void handle(ClientCacheEntryRemovedEvent event) {
		if (pre > 0) {
			key.remove(event.getKey());
			LogManager.logTrace(LogConstants.CTX_CONNECTOR, ++cnt + " [CacheEventListener] Removed entry (post) " + event.getKey());
		}
	}

}
