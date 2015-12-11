package net.bioclipse.hdt.business;

import org.rdfhdt.hdt.hdt.HDT;
import org.rdfhdt.hdtjena.HDTGraph;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class HDTStore implements IHDTStore {

	Model model;
	
	public HDTStore(HDT hdt) {
		HDTGraph graph = new HDTGraph(hdt);
		this.model = ModelFactory.createModelForGraph(graph);
	}

	@Override
	public Model getModel() {
		return model;
	}

}
