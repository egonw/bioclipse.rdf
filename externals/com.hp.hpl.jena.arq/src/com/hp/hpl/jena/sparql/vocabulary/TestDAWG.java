package com.hp.hpl.jena.sparql.vocabulary;

/* CVS $Id: TestDAWG.java,v 1.1 2005/02/09 14:59:46 andy_seaborne Exp $ */
 
import com.hp.hpl.jena.rdf.model.Model ;
import com.hp.hpl.jena.rdf.model.ModelFactory ;
import com.hp.hpl.jena.rdf.model.Property ;
import com.hp.hpl.jena.rdf.model.Resource ;
 
/**
 * Vocabulary definitions from test-dawg.n3 
 * @author Auto-generated by schemagen on 26 Jul 2004 15:01 
 */
public class TestDAWG {
    /** <p>The RDF model that holds the vocabulary terms</p> */
    private static Model m_model = ModelFactory.createDefaultModel();
    
    /** <p>The namespace of the vocabulary as a string ({@value})</p> */
    public static final String NS = "http://www.w3.org/2001/sw/DataAccess/tests/test-dawg#";
    
    /** <p>The namespace of the vocabulary as a string</p>
     *  @see #NS */
    public static String getURI() {return NS;}
    
    /** <p>The namespace of the vocabulary as a resource</p> */
    public static final Resource NAMESPACE = m_model.createResource( NS );
    
    public static final Property queryForm = m_model.createProperty( "http://www.w3.org/2001/sw/DataAccess/tests/test-dawg#queryForm" );
    
    /** <p>Indicates that while the test should pass, it may generate a warning.</p> */
    public static final Property warning = m_model.createProperty( "http://www.w3.org/2001/sw/DataAccess/tests/test-dawg#warning" );
    
    /** <p>Contains a pointer to the associated issue on the RDF Data Access Working 
     *  Group Tracking document.</p>
     */
    public static final Property issue = m_model.createProperty( "http://www.w3.org/2001/sw/DataAccess/tests/test-dawg#issue" );
    
    /** <p>Contains a reference to the minutes of the RDF Data Access Working Group where 
     *  the test case status was last changed.</p>
     */
    public static final Property approval = m_model.createProperty( "http://www.w3.org/2001/sw/DataAccess/tests/test-dawg#approval" );
    
    public static final Property resultForm = m_model.createProperty( "http://www.w3.org/2001/sw/DataAccess/tests/test-dawg#resultForm" );
    
    /** <p>A human-readable summary of the test case.</p> */
    public static final Property description = m_model.createProperty( "http://www.w3.org/2001/sw/DataAccess/tests/test-dawg#description" );
    
    public static final Property status = m_model.createProperty( "http://www.w3.org/2001/sw/DataAccess/tests/test-dawg#status" );
    
    /** <p>Super class of all test status classes</p> */
    public static final Resource Status = m_model.createResource( "http://www.w3.org/2001/sw/DataAccess/tests/test-dawg#Status" );
    
    /** <p>Super class of all result forms</p> */
    public static final Resource ResultForm = m_model.createResource( "http://www.w3.org/2001/sw/DataAccess/tests/test-dawg#ResultForm" );
    
    /** <p>Super class of all query forms</p> */
    public static final Resource QueryForm = m_model.createResource( "http://www.w3.org/2001/sw/DataAccess/tests/test-dawg#QueryForm" );
    
    /** <p>Class of result expected to be from a SELECT query</p> */
    public static final Resource resultResultSet = m_model.createResource( "http://www.w3.org/2001/sw/DataAccess/tests/test-dawg#resultResultSet" );
    
    /** <p>Class of queries that are seeking a constructed graph</p> */
    public static final Resource queryConstruct = m_model.createResource( "http://www.w3.org/2001/sw/DataAccess/tests/test-dawg#queryConstruct" );
    
    /** <p>Class of queries that are seeking a yes/no question</p> */
    public static final Resource queryAsk = m_model.createResource( "http://www.w3.org/2001/sw/DataAccess/tests/test-dawg#queryAsk" );
    
    /** <p>Class of result expected to be a graph</p> */
    public static final Resource resultGraph = m_model.createResource( "http://www.w3.org/2001/sw/DataAccess/tests/test-dawg#resultGraph" );
    
    /** <p>Class of tests that have not been classified</p> */
    public static final Resource NotClassified = m_model.createResource( "http://www.w3.org/2001/sw/DataAccess/tests/test-dawg#NotClassified" );
    
    /** <p>Class of result expected to be a boolean</p> */
    public static final Resource booleanResult = m_model.createResource( "http://www.w3.org/2001/sw/DataAccess/tests/test-dawg#booleanResult" );
    
    /** <p>Class of tests that are Approved</p> */
    public static final Resource Approved = m_model.createResource( "http://www.w3.org/2001/sw/DataAccess/tests/test-dawg#Approved" );
    
    /** <p>Class of tests that have been Withdrawn</p> */
    public static final Resource Withdrawn = m_model.createResource( "http://www.w3.org/2001/sw/DataAccess/tests/test-dawg#Withdrawn" );
    
    /** <p>Class of tests that are Obsolete</p> */
    public static final Resource Obsoleted = m_model.createResource( "http://www.w3.org/2001/sw/DataAccess/tests/test-dawg#Obsoleted" );
    
    /** <p>Class of queries that are seeking a descriptive graph</p> */
    public static final Resource queryDescribe = m_model.createResource( "http://www.w3.org/2001/sw/DataAccess/tests/test-dawg#queryDescribe" );
    
    /** <p>Class of queries that are seeking variable bindings</p> */
    public static final Resource querySelect = m_model.createResource( "http://www.w3.org/2001/sw/DataAccess/tests/test-dawg#querySelect" );
    
    /** <p>Class of tests that are Rejected</p> */
    public static final Resource Rejected = m_model.createResource( "http://www.w3.org/2001/sw/DataAccess/tests/test-dawg#Rejected" );
    
}
