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

import java.util.Set;

/**
 * @author vhalbert
 *
 */
public interface CacheEventListenerInterface {

	/**
	 * Called to add a single event to the list to be monitored for an event from JDG
	 * @param okey
	 */
	void addEvent(Object okey);
	
	/**
	 * Called to add a set of events to the list to be monitored for an event from JDG
	 * @param keyset
	 */
	void addEvents(Set<Object> keyset);
	
	/**
	 * Called to determine the number of events still in the queue to be listened for
	 * @return int
	 */
	int getEventCount();
	
	/**
	 * Utility method to differentiate between the actual listener and the noop listener.
	 * @return boolean
	 */
	boolean eventsMonitored();
	
	/**
	 * Called to clear out any events that are being monitored and reset counters.
	 */
	void reset();

}