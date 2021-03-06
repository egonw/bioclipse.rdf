/*
 * (c) Copyright 2008, 2009 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package org.openjena.atlas.json.io.parserjavacc;

import java.io.InputStream ;
import java.io.Reader ;

import org.openjena.atlas.json.JsonParseException ;
import org.openjena.atlas.json.io.JSONHandler ;
import org.openjena.atlas.json.io.parserjavacc.javacc.JSON_Parser ;
import org.openjena.atlas.json.io.parserjavacc.javacc.ParseException ;

import com.hp.hpl.jena.n3.turtle.parser.TokenMgrError ;


public class JSONParserJavaCC
{    
    /** Parse to get a Json object */ 
    public static void parse(InputStream input, JSONHandler handler)
    {
        JSON_Parser p = new JSON_Parser(input) ;
        parse(p, handler) ;
    }

    /** Parse to get a Json object */ 
    public static void parse(Reader reader, JSONHandler handler)
    {
        JSON_Parser p = new JSON_Parser(reader) ;
        parse(p, handler) ;
    }
    
    private static void parse(JSON_Parser p, JSONHandler handler)
    {
        p.setHandler(handler) ;
        try
        {
            p.unit() ;
            // Checks for EOF 
            //        //<EOF> test : EOF is always token 0.
            //        if ( p.token_source.getNextToken().kind != 0 )
            //            throw new JSONParseException("Trailing characters after "+item, item.getLine(), item.getColumn()) ;
        } 
        catch (ParseException ex)
        { throw new JsonParseException(ex.getMessage(), ex.currentToken.beginLine, ex.currentToken.beginColumn) ; }
        catch (TokenMgrError tErr)
        { 
            // Last valid token : not the same as token error message - but this should not happen
            int col = p.token.endColumn ;
            int line = p.token.endLine ;
            throw new JsonParseException(tErr.getMessage(), line, col) ;
        }
    }

    /** Parse to get a Json object */ 
    public static void parseAny(InputStream input, JSONHandler handler)
    { 
        JSON_Parser p = new JSON_Parser(input) ;
        parseAny(p, handler) ;
    }

    /** Parse to get a Json object */ 
    public static void parseAny(Reader reader, JSONHandler handler)
    {
        JSON_Parser p = new JSON_Parser(reader) ;
        parseAny(p, handler) ;
    }
        
    private static void parseAny(JSON_Parser p, JSONHandler handler)
    {
        p.setHandler(handler) ;
        try
        {
            p.any() ;
        } 
        catch (ParseException ex)
        { throw new JsonParseException(ex.getMessage(), ex.currentToken.beginLine, ex.currentToken.beginColumn) ; }
        catch (TokenMgrError tErr)
        { 
            // Last valid token : not the same as token error message - but this should not happen
            int col = p.token.endColumn ;
            int line = p.token.endLine ;
            throw new JsonParseException(tErr.getMessage(), line, col) ;
        }
    }

}

/*
 * (c) Copyright 2008, 2009 Hewlett-Packard Development Company, LP
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