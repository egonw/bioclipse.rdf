/*
 * (c) Copyright 2010, 2011 Epimorphics Ltd.
 * All rights reserved.
 * [See end of file]
 */

package org.openjena.riot.pipeline.normalize;

import java.math.BigDecimal ;
import java.math.BigInteger ;
import java.text.DecimalFormat ;
import java.text.NumberFormat ;

import javax.xml.datatype.DatatypeConfigurationException ;
import javax.xml.datatype.DatatypeFactory ;
import javax.xml.datatype.XMLGregorianCalendar ;

import com.hp.hpl.jena.datatypes.RDFDatatype ;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype ;
import com.hp.hpl.jena.graph.Node ;
import com.hp.hpl.jena.sparql.ARQException ;
import com.hp.hpl.jena.sparql.graph.NodeConst ;
import com.hp.hpl.jena.sparql.util.DateTimeStruct ;

class NormalizeValue
{
    // Auxillary class of datatype handers, placed here to avoid static initialization
    // ordering problems (if in CanonicalizeLiteral, all this low-level machinary would
    // need to be in the file before the external API, which I consider bad style).  It
    // is a source of obscure bugs.

    // See Normalizevalue2 for "faster" versions (less parsing overhead). 
    
    static DatatypeHandler dtBoolean = new DatatypeHandler() {
        public Node handle(Node node, String lexicalForm, RDFDatatype datatype)
        {
            if ( lexicalForm.equals("1") ) return NodeConst.nodeTrue ;
            if ( lexicalForm.equals("0") ) return NodeConst.nodeFalse ;
            return node ;
        }
    } ;
    
    private static DatatypeFactory datatypeFactory = null ;
    static
    { 
        try 
        { datatypeFactory = DatatypeFactory.newInstance() ; }
        catch (DatatypeConfigurationException ex)
        { throw new ARQException("NormalizeValue", ex) ; }
    }
    
    static DatatypeHandler dtAnyDateTime = new DatatypeHandler() {
        public Node handle(Node node, String lexicalForm, RDFDatatype datatype)
        {
            // Fast test: 
            if ( lexicalForm.indexOf('.') < 0 )
                // No fractional seconds.
                return node ;
            
            // Could use XMLGregorianCalendar but still need to canonicalize fractional seconds.
            // Record for history. 
            if ( false )
            {
                XMLGregorianCalendar xcal = datatypeFactory.newXMLGregorianCalendar(lexicalForm) ;
                if ( xcal.getFractionalSecond() != null )
                {
                    if ( xcal.getFractionalSecond().compareTo(BigDecimal.ZERO) == 0 )
                        xcal.setFractionalSecond(null) ;
                    else
                        // stripTrailingZeros does the right thing on fractional values. 
                        xcal.setFractionalSecond(xcal.getFractionalSecond().stripTrailingZeros()) ;
                }
                String lex2 = xcal.toXMLFormat() ;
                if ( lex2.equals(lexicalForm) )
                    return node ;
                return Node.createLiteral(lex2, null, datatype) ;
            }
            // The only variablity for a valid date/dateTime/g* type is:
            //   Second part can have fractional seconds '.' s+ (if present) represents the fractional seconds;
            DateTimeStruct dts = DateTimeStruct.parseDateTime(lexicalForm) ;
            int idx = dts.second.indexOf('.') ;     // We have already tested for the existence of '.'
            int i = dts.second.length()-1 ;
            for ( ; i > idx ; i-- )
            {
                if ( dts.second.charAt(i) != '0' )
                    break ;
            }
            if ( i == dts.second.length() )
                return node ;
            
            if ( i == idx )
                // All trailings zeros, drop the '.' as well.
                dts.second = dts.second.substring(0, idx) ;    
            else
                dts.second = dts.second.substring(0, i+1) ;
            
            String lex2 = dts.toString() ;
            // Can't happen.  We munged dts.second. 
//            if ( lex2.equals(lexicalForm) )
//                return node ;
            return Node.createLiteral(lex2, null, datatype) ;
        }
    } ;
    
    static DatatypeHandler dtDateTime = dtAnyDateTime ;

    static DatatypeHandler dtInteger = new DatatypeHandler() {
        public Node handle(Node node, String lexicalForm, RDFDatatype datatype)
        {
            char[] chars = lexicalForm.toCharArray() ;
            if ( chars.length == 0 )
                // Illegal lexical form.
                return node ;
            
            // If valid and one char, it must be legal.
            // If valid, and two chars and not leading 0, it must be valid.
            String lex2 = lexicalForm ;
            
            if ( lex2.startsWith("+") )
                lex2 = lex2.substring(1) ;
            
            if ( lex2.length() > 8 )
                // Maybe large than an int so do carefully.
                lex2 = new BigInteger(lexicalForm).toString() ;
            else
            {
                // Avoid object churn.
                int x = Integer.parseInt(lex2) ;
                lex2 = Integer.toString(x) ;
            }
            
            // If it's a subtype of integer, then output a new node of datatype integer.
            if ( datatype.equals(XSDDatatype.XSDinteger) && lex2.equals(lexicalForm) )
                return node ;
            return Node.createLiteral(lex2, null, XSDDatatype.XSDinteger) ;
        }
    } ;

    static DatatypeHandler dtDecimal = new DatatypeHandler() {
        public Node handle(Node node, String lexicalForm, RDFDatatype datatype)
        {
            BigDecimal bd = new BigDecimal(lexicalForm).stripTrailingZeros() ;
            String lex2 = bd.toPlainString() ;
            
            // Ensure there is a "."
            //if ( bd.scale() <= 0 )
            if ( lex2.indexOf('.') == -1 )
                // Must contain .0
                lex2 = lex2+".0" ;
            if ( lex2.equals(lexicalForm) )
                return node ;
            return Node.createLiteral(lex2, null, datatype) ;
        }
    } ;
    
    static private NumberFormat fmtFloatingPoint = new DecimalFormat("0.0#################E0") ;
    
    /* http://www.w3.org/TR/xmlschema-2/#double-canonical-representation */
    /*
     * The canonical representation for double is defined by prohibiting certain
     * options from the Lexical representation (§3.2.5.1). Specifically, the
     * exponent must be indicated by "E". Leading zeroes and the preceding
     * optional "+" sign are prohibited in the exponent. If the exponent is
     * zero, it must be indicated by "E0". For the mantissa, the preceding
     * optional "+" sign is prohibited and the decimal point is required.
     * Leading and trailing zeroes are prohibited subject to the following:
     * number representations must be normalized such that there is a single
     * digit which is non-zero to the left of the decimal point and at least a
     * single digit to the right of the decimal point unless the value being
     * represented is zero. The canonical representation for zero is 0.0E0.
     */
    
    static DatatypeHandler dtDouble = new DatatypeHandler() {
        public Node handle(Node node, String lexicalForm, RDFDatatype datatype)
        {
            double d = Double.parseDouble(lexicalForm) ;
            String lex2 = fmtFloatingPoint.format(d) ;
            if ( lex2.equals(lexicalForm) )
                return node ;
            return Node.createLiteral(lex2, null, datatype) ;
        }
    } ;
    
    static DatatypeHandler dtFloat = new DatatypeHandler() {
        public Node handle(Node node, String lexicalForm, RDFDatatype datatype)
        {
            float f = Float.parseFloat(lexicalForm) ;
            String lex2 = fmtFloatingPoint.format(f) ;
            if ( lex2.equals(lexicalForm) )
                return node ;
            return Node.createLiteral(lex2, null, datatype) ;
        }
    } ;

    /** Convert xsd:string to simple literal */
    static DatatypeHandler dtXSDString = new DatatypeHandler() {
        public Node handle(Node node, String lexicalForm, RDFDatatype datatype)
        {
            return Node.createLiteral(lexicalForm) ;
        }
    } ;
    
    /** Convert simple literal to xsd:string */
    static DatatypeHandler dtSimpleLiteral = new DatatypeHandler() {
        public Node handle(Node node, String lexicalForm, RDFDatatype datatype)
        {
            return Node.createLiteral(lexicalForm, "", datatype) ;
        }
    } ;

    
    static DatatypeHandler dtPlainLiteral = new DatatypeHandler() {
        public Node handle(Node node, String lexicalForm, RDFDatatype datatype)
        {
            int idx = lexicalForm.lastIndexOf('@') ;
            if ( idx == -1 )
            {
                // Bad.
                return node ;
            }
            
            String lex = lexicalForm.substring(0, idx) ;
            if ( idx == lexicalForm.length()-1 )
                return Node.createLiteral(lex) ;
            String lang = lexicalForm.substring(idx+1) ;
            return Node.createLiteral(lex,lang, null) ;
        }
    } ;
}

/*
 * (c) Copyright 2010, 2011 Epimorphics Ltd.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */