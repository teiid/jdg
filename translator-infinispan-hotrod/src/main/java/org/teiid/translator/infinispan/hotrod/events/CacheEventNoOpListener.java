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
public class CacheEventNoOpListener implements CacheEventListenerInterface {


	@Override
	public void reset() {
	}

	@Override
	public void addEvent(final Object okey) {
	}

	@Override
	public 	void addEvents(Set<Object> keyset) {
		
	}
	
	@Override
	public int getEventCount() {
		return 0;
	}
	
	@Override
	public boolean eventsMonitored() {
		return false;
	}
	
	

}
