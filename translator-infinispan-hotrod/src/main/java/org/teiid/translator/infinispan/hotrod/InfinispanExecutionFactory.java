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

package org.teiid.translator.infinispan.hotrod;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.resource.cci.ConnectionFactory;

import org.teiid.core.util.PropertiesUtils;
import org.teiid.infinispan.api.InfinispanConnection;
import org.teiid.infinispan.api.ProtobufResource;
import org.teiid.language.Argument;
import org.teiid.language.Command;
import org.teiid.language.QueryExpression;
import org.teiid.metadata.Column;
import org.teiid.metadata.MetadataFactory;
import org.teiid.metadata.RuntimeMetadata;
import org.teiid.metadata.Schema;
import org.teiid.metadata.Table;
import org.teiid.translator.ExecutionContext;
import org.teiid.translator.ExecutionFactory;
import org.teiid.translator.MetadataProcessor;
import org.teiid.translator.ProcedureExecution;
import org.teiid.translator.ResultSetExecution;
import org.teiid.translator.Translator;
import org.teiid.translator.TranslatorException;
import org.teiid.translator.TranslatorProperty;
import org.teiid.translator.UpdateExecution;


@Translator(name = "infinispan-hotrod", description = "The Infinispan Translator Using Protobuf & Hotrod")
public class InfinispanExecutionFactory extends ExecutionFactory<ConnectionFactory, InfinispanConnection>{
	public static final int MAX_SET_SIZE = 1024;
    public static final String TEIID_ALIAS_NAMING_CACHE = "teiid-alias-naming-cache"; //$NON-NLS-1$

	private boolean supportsCompareCriteriaOrdered = true;
	private boolean supportsUpsert = true;

	public InfinispanExecutionFactory() {
				
		setMaxInCriteriaSize(MAX_SET_SIZE);
		setMaxDependentInPredicates(MAX_SET_SIZE);
		setSupportsOrderBy(true);
		setSupportsSelectDistinct(false);
		setSupportsInnerJoins(true);
		setSupportsFullOuterJoins(true);
		setSupportsOuterJoins(true);
		setSupportedJoinCriteria(SupportedJoinCriteria.KEY);
		setSourceRequiredForMetadata(true);
//		setTransactionSupport(TransactionSupport.NONE);
	}

    @Override
    public void start() throws TranslatorException {
        super.start();
    }

    @Override
    public ResultSetExecution createResultSetExecution(QueryExpression command,
            ExecutionContext executionContext, RuntimeMetadata metadata,
            InfinispanConnection connection) throws TranslatorException {
        return new InfinispanQueryExecution(this, command, executionContext, metadata, connection,
	                                supportsDirectQueryProcedure());
        }

    @Override
    public UpdateExecution createUpdateExecution(Command command,
            ExecutionContext executionContext, RuntimeMetadata metadata,
            InfinispanConnection connection) throws TranslatorException {
        return new InfinispanUpdateExecution(command, executionContext, metadata,
        		 connection, supportsDirectQueryProcedure());
    }

	@Override
	public ProcedureExecution createDirectExecution(List<Argument> arguments, Command command,
			ExecutionContext executionContext, RuntimeMetadata metadata, InfinispanConnection connection)
			throws TranslatorException {
		return new InfinispanDirectQueryExecution(arguments, command, executionContext, metadata, connection);
	}

    @Override
    public void getMetadata(MetadataFactory metadataFactory, InfinispanConnection conn) throws TranslatorException {
        ProtobufMetadataProcessor metadataProcessor = (ProtobufMetadataProcessor)getMetadataProcessor();
        PropertiesUtils.setBeanProperties(metadataProcessor, metadataFactory.getModelProperties(), "importer"); //$NON-NLS-1$

        // This block is only invoked when NATIVE metadata is defined, by the time code got here if we have
        // tables already in schema, then user defined through other metadata repositories. In this case, 
        // a .proto file need to be generated based on schema, then use that to generate final metadata and
        // register the .proto with the Infinispan
        Schema schema = metadataFactory.getSchema();
        ProtobufResource resource = null;
        ArrayList<Table> removedTables = new ArrayList<Table>();
// not backported to 6.4.x
        if (schema.getTables() != null && !schema.getTables().isEmpty()) {
            SchemaToProtobufProcessor stpp = new SchemaToProtobufProcessor();
            stpp.setIndexMessages(true);
            resource = stpp.process(metadataFactory, conn);
            metadataProcessor.setProtobufResource(resource);
            ArrayList<Table> tables = new ArrayList<Table>(schema.getTables().values());
            for (Table t : tables) {
                // remove the previous tables, as we want to introduce them with necessary
                // extension metadata generated with generated .proto file. As some of the default
                // extension metadata can be added.
                removedTables.add(schema.removeTable(t.getName()));
            }
        }

        metadataProcessor.process(metadataFactory, conn);

        
         // TEIID-4896: In the process of DDL->proto, the extension properties defined on the schema
         // may be not carried forward, we need to make sure we copy those back.
         for (Table oldT : removedTables) {
             Table newT = schema.getTable(oldT.getName());
             Map<String, String> properties = oldT.getProperties();
             for (Map.Entry<String, String> entry:properties.entrySet()) {
                 newT.setProperty(entry.getKey(), entry.getValue());
             }
             newT.setSupportsUpdate(oldT.supportsUpdate());
             if (oldT.getAnnotation() != null) {
                 newT.setAnnotation(oldT.getAnnotation());
             }
             
             List<Column> columns = oldT.getColumns();
             for (Column c : columns) {
                 Column newCol = newT.getColumnByName(c.getName());
                 if (newCol != null) {
                     Map<String, String> colProperties = c.getProperties();
                     for (Map.Entry<String, String> entry:colProperties.entrySet()) {
                         newCol.setProperty(entry.getKey(), entry.getValue());
                     }
                     newCol.setUpdatable(c.isUpdatable());
                     if (c.getAnnotation() != null) {
                         newCol.setAnnotation(c.getAnnotation());
                     }
                 }
             }
         }               
        
        resource = metadataProcessor.getProtobufResource();
        if(resource == null) {
            SchemaToProtobufProcessor stpp = new SchemaToProtobufProcessor();
            resource = stpp.process(metadataFactory, conn);
        }
        // register protobuf
        if (resource != null) {
            conn.registerProtobufFile(resource);
        }
    }

    @Override
    public MetadataProcessor<InfinispanConnection> getMetadataProcessor() {
        return new ProtobufMetadataProcessor();
    }

    @Override
    public boolean isSourceRequiredForCapabilities() {
        return true;
    }

    @Override
    public boolean supportsAliasedTable() {
        return true;
    }

    @Override
    public boolean supportsInCriteria() {
        return true;
    }

    @Override
    public boolean supportsIsNullCriteria() {
        return false;
    }

    @Override
    public boolean supportsOrCriteria() {
        return true;
    }

    @Override
    public boolean supportsCompareCriteriaEquals() {
        return true;
    }

    @TranslatorProperty(display="CompareCriteriaOrdered", description="If true, translator can support comparison criteria with the operator '=>' or '<=' ",advanced=true)
    @Override
    public boolean supportsCompareCriteriaOrdered() {
        return supportsCompareCriteriaOrdered;
    }

    public boolean setSupportsCompareCriteriaOrdered(boolean supports) {
        return supportsCompareCriteriaOrdered = supports;
    }

// Not backported to 6.4
//    @TranslatorProperty(display="Upsert", description="If true, translator can support Upsert command",advanced=true)
//    @Override
//    public boolean supportsUpsert() {
//        return supportsUpsert;
//    }
//
//    public boolean setSupportsUpsert(boolean supports) {
//        return supportsUpsert = supports;
//    }

    @Override
    public boolean supportsLikeCriteria() {
        return true;
    }

    @Override
    public boolean supportsLikeCriteriaEscapeCharacter() {
        return true;
    }

    @Override
    public boolean supportsNotCriteria() {
        return false;
    }

    @Override
    public boolean supportsGroupBy() {
        return true;
    }

    @Override
    public boolean supportsRowLimit() {
        return true;
    }

    @Override
    public boolean supportsRowOffset() {
        return true;
    }

    @Override
    public boolean useAnsiJoin() {
        return true;
    }

    @Override
    public boolean supportsOnlyLiteralComparison() {
        return true;
    }
    @Override
    public boolean supportsAggregatesSum() {
        return false;
    }

    @Override
    public boolean supportsAggregatesAvg() {
        return true;
    }

    @Override
    public boolean supportsAggregatesMin() {
        return true;
    }

    @Override
    public boolean supportsAggregatesMax() {
        return true;
    }

    @Override
    public boolean supportsAggregatesCount() {
        return true;
    }

    @Override
    public boolean supportsHaving() {
        return true;
    }
}
