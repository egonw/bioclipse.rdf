package net.bioclipse.hdt.business;

import com.hp.hpl.jena.rdf.model.Model;

import net.bioclipse.rdf.business.IRDFStore;

public interface IHDTStore extends IRDFStore {

	Model getModel();

}
