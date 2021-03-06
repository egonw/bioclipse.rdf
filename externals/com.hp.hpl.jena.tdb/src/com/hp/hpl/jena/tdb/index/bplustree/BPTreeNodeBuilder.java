/*
 * (c) Copyright 2010 Talis Systems Ltd.
 * (c) Copyright 2010 Epimorphics Ltd.
 * All rights reserved.
 * [See end of file]
 */

package com.hp.hpl.jena.tdb.index.bplustree;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.openjena.atlas.lib.Pair;

import com.hp.hpl.jena.tdb.base.buffer.PtrBuffer;
import com.hp.hpl.jena.tdb.base.buffer.RecordBuffer;
import com.hp.hpl.jena.tdb.base.record.Record;
import com.hp.hpl.jena.tdb.base.record.RecordFactory ;

/** Take a stream of (block id,split record) pairs and generate B+Tree Nodes */ 
class BPTreeNodeBuilder implements Iterator<Pair<Integer, Record>>
{
    private Pair<Integer, Record> slot ;
    private Iterator<Pair<Integer, Record>> iter ;
    private final BPTreeNodeMgr mgr ;

    private final boolean leafLayer ;
    private final RecordFactory recordFactory ;
    
    BPTreeNodeBuilder(Iterator<Pair<Integer, Record>> iter, BPTreeNodeMgr mgr, boolean leafLayer, RecordFactory recordFactory)
    {
        this.iter = iter ;
        this.mgr = mgr ;
        this.leafLayer = leafLayer ;
        this.recordFactory = recordFactory ;
    }
    
    //@Override
    public boolean hasNext()
    {
        if ( slot != null )
            return true ;

        if ( iter == null )
            return false ;
            
        if ( ! iter.hasNext() )
        {
            // Start of next block, no more input.
            iter = null ;
            return false ;
        }

        // At least one element to put in a new node.
        // Unknown parent.  Does not matter (parent is only in-memory) 

        BPTreeNode bptNode = mgr.createNode(-1) ;
        bptNode.setIsLeaf(leafLayer) ;
        
        RecordBuffer recBuff = bptNode.getRecordBuffer() ;
        PtrBuffer ptrBuff = bptNode.getPtrBuffer() ;
        recBuff.setSize(0) ;
        ptrBuff.setSize(0) ;    // Creation leaves this junk.
        
        final boolean debug = false ;
        int rMax = recBuff.maxSize() ;
        int pMax = ptrBuff.maxSize() ;
        if ( debug ) System.out.printf("Max: (%d, %d)\n", rMax, pMax) ;

        for ( ; iter.hasNext() ; )
        {
            
            int X = bptNode.getCount() ;
            int X2 = bptNode.getMaxSize() ;
            int P = ptrBuff.size() ;
            int P2 = ptrBuff.maxSize() ;
            int R = recBuff.size() ;
            int R2 = recBuff.maxSize() ;
            
            // bptNode.getMaxSize() is drivel
            //System.out.printf("N: %d/%d : P %d/%d : R %d/%d\n", X, X2, P, P2, R, R2) ;
            
            Pair<Integer, Record> pair = iter.next() ;
            if ( debug ) System.out.println("** Item: "+pair) ;
            Record r = pair.cdr() ;
            
            // [Issue: FREC]
            // The record buffer size is wrong.
            // Writes the whole record, only need to write the key part.
            // **** r = recordFactory.createKeyOnly(r) ;
            
            // [Issue: FREC]
            // The record is key-only (which is correct) but until FREC fixed, we need key,value
            r = recordFactory.create(r.getKey()) ;
            // -- End FREC
            
            // Always add - so ptrBuff is one ahead when we finish.
            // There is always one more ptr than record in a B+Tree node.
            if ( ptrBuff.isFull() )
                System.err.println("PtrBuffer is full") ;
            
            // Add pointer.
            ptrBuff.add(pair.car()) ;

            // [Issue: FREC]
            // Either test shoudl work but due to the missetting of record buffer size
            // testing recBuff does not work. 
            //if ( recBuff.isFull() )
            // .... test ptrBuff
            if ( ptrBuff.isFull() )
            {
                // End of this block.

                // Does not add to ptrBuff so the one extra slot is done.
                // Instead, the high point goes to the next level up.
                
                // Internal consistency check.
                if ( ! ptrBuff.isFull() )
                    System.err.println("PtrBuffer is not full") ;
                
                // The split point for the next level up.
                slot = new Pair<Integer,Record>(bptNode.getId(), pair.cdr()) ;

                if ( debug ) System.out.printf("Write(1): %d\n", bptNode.getId()) ;
                if ( debug ) System.out.println(bptNode) ;
                if ( debug ) System.out.println("Slot = "+slot) ;
                mgr.put(bptNode) ;
                // No count increment needed.
                return true ;
            }

            recBuff.add(r) ;
            bptNode.setCount(bptNode.getCount()+1) ;
        }
        
        
        

        // If we get here, the input stream ran out before we finished a complete block.
        // Fix up block (remove the last record)
        Record r = recBuff.getHigh() ;
        recBuff.removeTop() ;
        bptNode.setCount(bptNode.getCount()-1) ;
        slot = new Pair<Integer,Record>(bptNode.getId(), r) ;
        
        if ( debug ) System.out.printf("Write(2): %d\n", bptNode.getId()) ;
        if ( debug ) System.out.println(bptNode) ;
        if ( debug ) System.out.println("Slot = "+slot) ;
        mgr.put(bptNode) ;
        return true ;
    }

    //@Override
    public Pair<Integer, Record> next()
    {
        if ( ! hasNext() ) throw new NoSuchElementException() ;
        Pair<Integer, Record> x = slot ;
        slot = null ;
        return x ;
    }
    
    //@Override
    public void remove()
    { throw new UnsupportedOperationException() ; }
}

/*
 * (c) Copyright 2010 Talis Systems Ltd.
 * (c) Copyright 2010 Epimorphics Ltd.
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