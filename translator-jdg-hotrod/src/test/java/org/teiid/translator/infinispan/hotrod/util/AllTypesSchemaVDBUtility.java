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
package org.teiid.translator.infinispan.hotrod.util;
 
import java.io.IOException;

import org.teiid.cdk.api.TranslationUtility;
import org.teiid.core.util.ObjectConverterUtil;
import org.teiid.core.util.UnitTestUtil;
import org.teiid.query.metadata.TransformationMetadata;
import org.teiid.query.unittest.RealMetadataFactory;

/**
 * This VDBUtility is building the metadata based on the JDG quickstart:  remote-query
 * because this is where the protobuf definitions are defined that are used in unit tests.
 * 
 * Also, the JDG quickstart in Teiid depends upon that quickstart so that user doesn't 
 * have to go generate the proto bin's.
 * 
 * 
 * 
 * @author vanhalbert
 *
 */

@SuppressWarnings("nls")
public class AllTypesSchemaVDBUtility {
	
	public static TranslationUtility createAllTypesMetadata() {

		try {
			return createTranslationUtility("Alltypes", "AlltypesVDB", "allTypesMetadata.ddl");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
		
	public static TranslationUtility createTranslationUtility(String modelName, String vdbName, String ddlFile)
			throws IOException, Exception {

		TransformationMetadata metadata = RealMetadataFactory.fromDDL(
				ObjectConverterUtil.convertFileToString(UnitTestUtil.getTestDataFile(ddlFile)), vdbName, modelName);
		return new TranslationUtility(metadata);

	}
	
}
