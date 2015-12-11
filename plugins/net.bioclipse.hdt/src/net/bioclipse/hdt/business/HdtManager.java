/*******************************************************************************
 * Copyright (c) 2015  Egon Willighagen <egonw@users.sf.net>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.hdt.business;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.rdfhdt.hdt.hdt.HDT;
import org.rdfhdt.hdt.hdt.HDTManager;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.shared.PrefixMapping;

import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.domain.IStringMatrix;
import net.bioclipse.core.domain.StringMatrix;
import net.bioclipse.managers.business.IBioclipseManager;

public class HdtManager implements IBioclipseManager {

    /**
     * Gives a short one word name of the manager used as variable name when
     * scripting.
     */
    public String getManagerName() {
        return "hdt";
    }

    public IHDTStore createStore(String hdtFileName, IProgressMonitor monitor)
    		throws IOException, BioclipseException, CoreException {
    	if (monitor == null) monitor = new NullProgressMonitor();
    	HDT hdt = HDTManager.mapIndexedHDT(hdtFileName, new HDTListener(hdtFileName, monitor));
    	HDTStore store = new HDTStore(hdt);
    	System.out.println("HDT file: " + hdt);
    	monitor.done();
    	return store;
    }

    public IStringMatrix sparql(IHDTStore store, String queryString)
        throws IOException, BioclipseException, CoreException {
    	if (!(store instanceof IHDTStore))
            throw new RuntimeException(
                "Can only handle IJenaStore's for now."
            );

        StringMatrix table = null;
        Model model = ((IHDTStore)store).getModel();
        Query query = QueryFactory.create(queryString);
        PrefixMapping prefixMap = query.getPrefixMapping();
        QueryExecution qexec = QueryExecutionFactory.create(query, model);
        try {
            ResultSet results = qexec.execSelect();
            table = HDTStringMatrixHelper.convertIntoTable(prefixMap, results);
        } finally {
            qexec.close();
        }
        return table;
    }
}
