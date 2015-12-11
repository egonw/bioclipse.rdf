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

import net.bioclipse.core.PublishedClass;
import net.bioclipse.core.PublishedMethod;
import net.bioclipse.core.Recorded;
import net.bioclipse.core.business.BioclipseException;
import net.bioclipse.core.domain.IStringMatrix;
import net.bioclipse.managers.business.IBioclipseManager;
import net.bioclipse.rdf.business.IRDFStore;

@PublishedClass(
    value="Manager that adds support for HDT files."
)
public interface IHdtManager extends IBioclipseManager {

	@Recorded
    @PublishedMethod(
        params = "String hdtFileName",
        methodSummary = "Loads a HDT file as a store"
    )
	public IRDFStore createStore(String hdtFileName)
		throws IOException, BioclipseException, CoreException;

	@Recorded
    @PublishedMethod(
        params = "IRDFStore store, String queryString",
        methodSummary = "Returns the results matching the SPARQL query"
    )
    public IStringMatrix sparql(IHDTStore store, String queryString)
        throws IOException, BioclipseException, CoreException;
}
