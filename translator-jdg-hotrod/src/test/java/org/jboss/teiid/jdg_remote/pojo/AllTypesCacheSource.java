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



import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
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
//import org.infinispan.query.dsl.Query;
import org.teiid.translator.infinispan.hotrod.InfinispanHotRodConnection;
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
public class AllTypesCacheSource<K, V>  extends JDGTestCache<K, V>{
	
	public static final String ALLTYPES_CACHE_NAME = "AllTypesCache";
	
	public static final String ALLTYPES_CLASS_NAME = AllTypes.class.getName();
	
	public static final String ALLTYPES_PKEY_NAME = "intKey";

	
	public static Random RANDOM = new Random();
	
	static {

		try {
			CLASS_REGISTRY.registerClass(AllTypes.class);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static InfinispanHotRodConnection createConnection() {
		return createConnection(true);
		
	}
		
	public static InfinispanHotRodConnection createConnection(final boolean useKeyClassType) {

		final RemoteCache objects = AllTypesCacheSource.loadCache();
		
		return AllTypeCacheConnection.createConnection(objects, useKeyClassType);
	}
	
	public static void loadCache(Map<Object, Object> cache) {
		
		for (int i = 1; i <= 10; i++) {
			
			AllTypes p = new AllTypes();			
			p.setIntKey(i);
			p.setIntNum(i+100);
			p.setStringKey("aaa");
			p.setStringNum( String.valueOf(RANDOM.nextInt()));
			p.setLongNum(RANDOM.nextLong());
			
			p.setBooleanValue(RANDOM.nextBoolean());
						
			p.setDoubleNum(RANDOM.nextDouble());
			
			p.setFloatNum(RANDOM.nextFloat());
			
			byte[] bytes = new byte[RANDOM.nextInt(100)]; 
			
			p.setByteArrayValue(bytes);
			
			p.setCharValue((char)(RANDOM.nextInt(26) + 'a'));
			p.setShortValue((short) RANDOM.nextInt(Short.MAX_VALUE + 1));
				
			p.setDateValue(generateRandomDate());
			p.setTimeValue(generateRandomTime());
			p.setTimeStampValue(generateRandomTimeStamp());
			
			p.setBigIntegerValue(BigInteger.valueOf(RANDOM.nextLong()));
			p.setBigDecimalValue(BigDecimal.valueOf(RANDOM.nextLong()));
				
			cache.put(p.getIntKey(), p);


		}
	}
	
	private static Date generateRandomDate() {
		Date    dt;
		long    ms;

		// Get an Epoch value roughly between 1940 and 2010
		// -946771200000L = January 1, 1940
		// Add up to 70 years to it (using modulus on the next long)
		ms = -946771200000L + (Math.abs(RANDOM.nextLong()) % (70L * 365 * 24 * 60 * 60 * 1000));

		// Construct a date
		dt = new Date(ms);
		return dt;
	}
	
	private static Time generateRandomTime() {
		final int millisInDay = 24*60*60*1000;
		Time time = new Time((long)RANDOM.nextInt(millisInDay));
		return time;
	}
	
	private static Timestamp generateRandomTimeStamp() {
		long offset = Timestamp.valueOf("2012-01-01 00:00:00").getTime();
		long end = Timestamp.valueOf("2013-01-01 00:00:00").getTime();
		long diff = end - offset + 1;
		Timestamp rand = new Timestamp(offset + (long)(Math.random() * diff));
		return rand;
	}

	public static RemoteCache  loadCache() {
		AllTypesCacheSource tcs = new AllTypesCacheSource();
		AllTypesCacheSource.loadCache(tcs);
		return tcs;
	}
	
	public List<Object> get(int key) {
		List<Object> objs = new ArrayList<Object>(1);
		objs.add(this.get(key));
		return objs;
	}	

}
