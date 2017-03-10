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

package org.teiid.resource.adapter.infinispan.hotrod.metadata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.FileReader;
import java.util.Properties;

import org.jboss.as.quickstarts.datagrid.hotrod.query.domain.PersonCacheSource;
import org.jboss.teiid.jdg_remote.pojo.AllTypesCacheSource;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.teiid.adminapi.impl.ModelMetaData;
import org.teiid.core.util.ObjectConverterUtil;
import org.teiid.core.util.UnitTestUtil;
import org.teiid.metadata.MetadataFactory;
import org.teiid.query.metadata.DDLStringVisitor;
import org.teiid.query.metadata.MetadataValidator;
import org.teiid.query.metadata.SystemMetadata;
import org.teiid.query.metadata.TransformationMetadata;
import org.teiid.query.parser.QueryParser;
import org.teiid.query.unittest.RealMetadataFactory;
import org.teiid.query.validator.ValidatorReport;
import org.teiid.resource.adapter.infinispan.hotrod.InfinispanManagedConnectionFactory;
import org.teiid.resource.adapter.infinispan.hotrod.RemoteInfinispanTestHelper;
import org.teiid.translator.infinispan.hotrod.InfinispanHotRodExecutionFactory;
import org.teiid.translator.infinispan.hotrod.InfinispanHotRodExecutionFactory;
import org.teiid.translator.object.ObjectConnection;

@SuppressWarnings("nls")
public class TestMetadataProcessor {
	protected static InfinispanHotRodExecutionFactory TRANSLATOR_PERSON;
	protected static InfinispanHotRodExecutionFactory TRANSLATOR_ALLTYPES;

	private static InfinispanManagedConnectionFactory FACTORY_PERSON = null;
	private static InfinispanManagedConnectionFactory FACTORY_ALLTYPES = null;

	private static RemoteInfinispanTestHelper RemoteServer = new RemoteInfinispanTestHelper();

	@BeforeClass
	public static void beforeEachClass() throws Exception {
		RemoteServer.startServer();

		FACTORY_PERSON = new InfinispanManagedConnectionFactory();

		FACTORY_PERSON.setRemoteServerList(
				RemoteInfinispanTestHelper.hostAddress() + ":" + RemoteInfinispanTestHelper.hostPort());
		FACTORY_PERSON.setCacheTypeMap(RemoteInfinispanTestHelper.PERSON_CACHE_NAME + ":"
				+ RemoteInfinispanTestHelper.PERSON_CLASS.getName() + ";" + RemoteInfinispanTestHelper.PKEY_COLUMN);
		FACTORY_PERSON.setChildClasses(
				PersonCacheSource.ADDRESSTYPE_CLASS_NAME + "," + PersonCacheSource.PHONENUMBER_CLASS_NAME);

		FACTORY_ALLTYPES = new InfinispanManagedConnectionFactory();

		FACTORY_ALLTYPES.setRemoteServerList(
				RemoteInfinispanTestHelper.hostAddress() + ":" + RemoteInfinispanTestHelper.hostPort());
		FACTORY_ALLTYPES.setCacheTypeMap(AllTypesCacheSource.ALLTYPES_CACHE_NAME + ":"
				+ AllTypesCacheSource.ALLTYPES_CLASS_NAME + ":" + AllTypesCacheSource.ALLTYPES_PKEY_NAME);

		FACTORY_ALLTYPES.setProtobufDefinitionFile("allTypes.proto");
		FACTORY_ALLTYPES.setMessageMarshallers(
				"org.jboss.teiid.jdg_remote.pojo.AllTypes:org.jboss.teiid.jdg_remote.pojo.AllTypesMarshaller");
		FACTORY_ALLTYPES.setMessageDescriptor("org.jboss.teiid.jdg_remote.pojo.AllTypes");

		TRANSLATOR_PERSON = new InfinispanHotRodExecutionFactory();
		TRANSLATOR_PERSON.start();

		TRANSLATOR_ALLTYPES = new InfinispanHotRodExecutionFactory();
		TRANSLATOR_ALLTYPES.start();

	}

	@AfterClass
	public static void closeConnection() throws Exception {
		RemoteServer.releaseServer();
	}

	@Test
	public void testPersonMetadata() throws Exception {
		ObjectConnection conn = FACTORY_PERSON.createConnectionFactory().getConnection();

		TRANSLATOR_PERSON.initCapabilities(conn);

		MetadataFactory mf = new MetadataFactory("vdb", 1, "objectvdb",
				SystemMetadata.getInstance().getRuntimeTypeMap(), new Properties(), null);

		TRANSLATOR_PERSON.getMetadataProcessor().process(mf, conn);

		String metadataDDL = DDLStringVisitor.getDDLString(mf.getSchema(), null, null);

		assertEquals(ObjectConverterUtil.convertFileToString(UnitTestUtil.getTestDataFile("personMetadata.ddl")).trim(),
				metadataDDL.trim());

		Class<?> clz = conn.getCacheClassType();

		assertEquals(RemoteInfinispanTestHelper.PERSON_CLASS, clz);

		Class<?> t = conn.getCacheKeyClassType();

		assertNull(t);

		assertEquals(RemoteInfinispanTestHelper.PKEY_COLUMN, conn.getPkField());

		assertNotNull(conn.getCache());

		conn.cleanUp();

	}

	@Test
	public void testAllTypesMetadata() throws Exception {
		ObjectConnection conn = FACTORY_ALLTYPES.createConnectionFactory().getConnection();

		TRANSLATOR_ALLTYPES.initCapabilities(conn);

		MetadataFactory mf = new MetadataFactory("vdb", 1, "objectvdb",
				SystemMetadata.getInstance().getRuntimeTypeMap(), new Properties(), null);

		TRANSLATOR_ALLTYPES.getMetadataProcessor().process(mf, conn);

		String metadataDDL = DDLStringVisitor.getDDLString(mf.getSchema(), null, null);

		assertEquals(
				ObjectConverterUtil.convertFileToString(UnitTestUtil.getTestDataFile("allTypesMetadata.ddl")).trim(),
				metadataDDL.trim());

		Class<?> clz = conn.getCacheClassType();

		assertEquals(AllTypesCacheSource.ALLTYPES_CLASS_NAME, clz.getName());

		Class<?> t = conn.getCacheKeyClassType();

		assertNull(t);

		assertEquals(AllTypesCacheSource.ALLTYPES_PKEY_NAME, conn.getPkField());

		assertNotNull(conn.getCache());

		conn.cleanUp();

	}

	public static TransformationMetadata queryMetadataInterface(String modelName, String translator, String ddlFile) {

		try {
			ModelMetaData mmd = new ModelMetaData();
			mmd.setName(modelName);

			MetadataFactory mf = new MetadataFactory(translator, 1, SystemMetadata.getInstance().getRuntimeTypeMap(),
					mmd);
			mf.setParser(new QueryParser());
			mf.parse(new FileReader(UnitTestUtil.getTestDataFile(ddlFile)));

			TransformationMetadata tm = RealMetadataFactory.createTransformationMetadata(mf.asMetadataStore(), "x");
			ValidatorReport report = new MetadataValidator().validate(tm.getVdbMetaData(), tm.getMetadataStore());
			if (report.hasItems()) {
				throw new RuntimeException(report.getFailureMessage());
			}
			return tm;

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static MetadataFactory getMetadataFactory(String modelName, String vdb, String ddlFile) {
		try {

			ModelMetaData mmd = new ModelMetaData();
			mmd.setName(modelName);

			MetadataFactory mf = new MetadataFactory(vdb, 1, SystemMetadata.getInstance().getRuntimeTypeMap(), mmd);
			mf.setParser(new QueryParser());
			mf.parse(new FileReader(UnitTestUtil.getTestDataFile(ddlFile)));

			return mf;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

}