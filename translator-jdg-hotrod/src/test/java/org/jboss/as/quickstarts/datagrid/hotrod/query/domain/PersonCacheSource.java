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
package org.jboss.as.quickstarts.datagrid.hotrod.query.domain;



import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.infinispan.client.hotrod.RemoteCache;
import org.jboss.teiid.jdg_remote.pojo.JDGTestCache;
import org.teiid.translator.infinispan.hotrod.InfinispanHotRodConnection;
import org.teiid.translator.object.ObjectConnection;
import org.teiid.translator.object.Version;

/**
 * Sample cache of objects
 * 
 * @author vhalbert
 * @param <K>
 * @param <V>
 *
 */
@SuppressWarnings({ "nls" })
public class PersonCacheSource<K, V>  extends JDGTestCache<K, V>{
	
	public static final String PERSON_CACHE_NAME = "PersonsCache";
	
	public static final String PERSON_KEY_FIELD = "id";
	
	public static final String PERSON_CLASS_NAME = Person.class.getName();
	public static final String PHONENUMBER_CLASS_NAME = PhoneNumber.class.getName();
	public static final String PHONETYPE_CLASS_NAME = PhoneType.class.getName();
	public static final String ADDRESSTYPE_CLASS_NAME = Address.class.getName();
	
//	public static Map<String, Descriptor> DESCRIPTORS = new HashMap<String, Descriptor>();
	
	public static final int NUMPERSONS = 10;
	public static final int NUMPHONES = 2;
		
	static {
		try {
			CLASS_REGISTRY.registerClass(Person.class);
			CLASS_REGISTRY.registerClass(PhoneNumber.class);
			CLASS_REGISTRY.registerClass(PhoneType.class);
			CLASS_REGISTRY.registerClass(Address.class);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	
	public static InfinispanHotRodConnection createConnection(final boolean useKeyClass) {
		return createConnection(useKeyClass, null);
	}
	
	public static InfinispanHotRodConnection createConnection(final boolean useKeyClass, Version version) {
		return createConnection("id", useKeyClass, version);
	}
	
	public static InfinispanHotRodConnection createConnection(final String keyField, final boolean useKeyClass, Version version) {
		final RemoteCache objects = PersonCacheSource.loadCache();

		return PersonCacheConnection.createConnection(objects, keyField, useKeyClass, version);

	}
	public static ObjectConnection createConnection(final boolean useKeyClass, boolean staging, Version version) {
		final RemoteCache objects = PersonCacheSource.loadCache();

		return PersonCacheConnection.createConnection(objects, "id", useKeyClass, staging, version);
	}
	
	public static void loadCache(Map<Object, Object> cache) {
		PhoneType[] types = new PhoneType[] {PhoneType.HOME, PhoneType.MOBILE, PhoneType.WORK};
		int t = 0;
		
		for (int i = 1; i <= NUMPERSONS; i++) {
			
			ArrayList<PhoneNumber> pns = new ArrayList<PhoneNumber>();
			double d = 0;
			for (int j = 1; j <= NUMPHONES; j++) {
				PhoneNumber pn = new PhoneNumber("(111)222-345" + j, types[t++]);
				if (t > 2) t = 0;				
				pns.add(pn);
			}
			
			Person p = new Person();
			p.setId(i);
			p.setName("Person " + i);
			p.setPhones(pns);
			
			Address a = new Address();
			a.setAddress(p.getName() + " address");
			a.setCity(p.getName() + " city");
			a.setState("VA");	
			p.setAddress(a);
			
			cache.put(new Integer(i), p);

		}
	}

	public static RemoteCache loadCache() {
		PersonCacheSource tcs = new PersonCacheSource();
		PersonCacheSource.loadCache(tcs);
		return tcs;
	}
	
	public List<Object> get(int key) {
		List<Object> objs = new ArrayList<Object>(1);
		objs.add(this.get(key));
		return objs;
	}	

}
