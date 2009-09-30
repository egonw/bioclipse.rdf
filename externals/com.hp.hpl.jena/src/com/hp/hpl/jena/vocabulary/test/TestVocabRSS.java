/*
  (c) Copyright 2003, 2004, 2005, 2006, 2007, 2008, 2009 Hewlett-Packard Development Company, LP
  [See end of file]
  $Id: TestVocabRSS.java,v 1.8 2008/12/28 19:32:18 andy_seaborne Exp $
*/

package com.hp.hpl.jena.vocabulary.test;

import com.hp.hpl.jena.vocabulary.*;

import junit.framework.*;

/**

 	@author kers
*/
public class TestVocabRSS extends VocabTestBase
    {
    public TestVocabRSS(String name)
        { super(name); }

     public static TestSuite suite()
        { return new TestSuite( TestVocabRSS.class ); }

    public void testRSS()
        {
		String ns = "http://purl.org/rss/1.0/";
        assertResource( ns + "channel", RSS.channel );
        assertResource( ns + "item", RSS.item );
        assertProperty( ns + "description", RSS.description );
        assertProperty( ns + "image", RSS.image );
        assertProperty( ns + "items", RSS.items );
        assertProperty( ns + "link", RSS.link );
        assertProperty( ns + "name", RSS.name );
        assertProperty( ns + "textinput", RSS.textinput );
        assertProperty( ns + "title", RSS.title );
        assertProperty( ns + "url", RSS.url );
        }
    }


/*
    (c) Copyright 2003, 2004, 2005, 2006, 2007, 2008, 2009 Hewlett-Packard Development Company, LP
    All rights reserved.

    Redistribution and use in source and binary forms, with or without
    modification, are permitted provided that the following conditions
    are met:

    1. Redistributions of source code must retain the above copyright
       notice, this list of conditions and the following disclaimer.

    2. Redistributions in binary form must reproduce the above copyright
       notice, this list of conditions and the following disclaimer in the
       documentation and/or other materials provided with the distribution.

    3. The name of the author may not be used to endorse or promote products
       derived from this software without specific prior written permission.

    THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
    IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
    OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
    IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
    INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
    NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
    DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
    THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
    (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
    THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/