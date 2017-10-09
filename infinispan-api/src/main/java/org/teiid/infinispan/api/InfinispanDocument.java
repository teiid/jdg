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
package org.teiid.infinispan.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import org.teiid.translator.document.Document;

public class InfinispanDocument extends Document {
    private TreeMap<Integer, TableWireFormat> wireMap;
    private boolean matched = true;
    private Map<String, Stats> statsMap = new HashMap<String, Stats>();
    private Object identifier;
    
    public Object getIdentifier() {
		return identifier;
	}

	public void setIdentifier(Object identifier) {
		this.identifier = identifier;
	}

	static class Stats {
        AtomicInteger matched = new AtomicInteger(0);
        AtomicInteger unmatched = new AtomicInteger(0);
    }

    public InfinispanDocument(String name, TreeMap<Integer, TableWireFormat> columnMap, InfinispanDocument parent) {
        super(name, false, parent);
        this.wireMap = columnMap;
    }

    public void addProperty(int wireType, Object value) {
        TableWireFormat twf = this.wireMap.get(wireType);
        if (twf.isArrayType()) {
            super.addArrayProperty(twf.getColumnName(), value);
        } else {
            super.addProperty(twf.getColumnName(), value);
        }
    }

    public TreeMap<Integer, TableWireFormat> getWireMap() {
        return wireMap;
    }

    public boolean isMatched() {
        return matched;
    }

    public void setMatched(boolean matched) {
        this.matched = matched;
    }

    public void incrementUpdateCount(String childName, boolean matched) {
        Stats s = statsMap.get(childName);
        if (s == null) {
            s = new Stats();
            statsMap.put(childName, s);
        }

        if (matched) {
            s.matched.incrementAndGet();
        } else {
            s.unmatched.incrementAndGet();
        }
    }

    public int getUpdateCount(String childName, boolean matched) {
        Stats s = statsMap.get(childName);
        if (s == null) {
            return 0;
        }

        if (matched) {
            return s.matched.get();
        } else {
            return s.unmatched.get();
        }
    }
    public int merge(InfinispanDocument updates) {
        int updated = 1;
        for (Entry<String, Object> entry:updates.getProperties().entrySet()) {
            addProperty(entry.getKey(), entry.getValue());
        }

        // update children if any
        for (Entry<String, List<Document>> entry:updates.getChildren().entrySet()) {
            String childName = entry.getKey();

            List<? extends Document> childUpdates = updates.getChildDocuments(childName);
            InfinispanDocument childUpdate = (InfinispanDocument)childUpdates.get(0);
            if (childUpdate.getProperties().isEmpty()) {
                continue;
            }

            List<? extends Document> previousChildren = getChildDocuments(childName);
            if (previousChildren == null || previousChildren.isEmpty()) {
                addChildDocument(childName, childUpdate);
            } else {
                for (Document doc : previousChildren) {
                    InfinispanDocument previousChild = (InfinispanDocument)doc;
                    if (previousChild.isMatched()) {
                        for (Entry<String, Object> childEntry:childUpdate.getProperties().entrySet()) {
                            String key = childEntry.getKey().substring(childEntry.getKey().lastIndexOf('/')+1);
                            previousChild.addProperty(key, childEntry.getValue());
                            updated++;
                        }
                    }
                }
            }
        }
        return updated;
    }    
}
