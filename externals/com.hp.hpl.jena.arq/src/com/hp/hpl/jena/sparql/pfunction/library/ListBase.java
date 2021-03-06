/*
 * (c) Copyright 2007, 2008, 2009 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package com.hp.hpl.jena.sparql.pfunction.library;

import java.util.Collection ;
import java.util.List ;
import java.util.Set ;

import com.hp.hpl.jena.graph.Graph ;
import com.hp.hpl.jena.graph.Node ;
import com.hp.hpl.jena.sparql.core.Var ;
import com.hp.hpl.jena.sparql.engine.ExecutionContext ;
import com.hp.hpl.jena.sparql.engine.QueryIterator ;
import com.hp.hpl.jena.sparql.engine.binding.Binding ;
import com.hp.hpl.jena.sparql.engine.binding.BindingFactory ;
import com.hp.hpl.jena.sparql.engine.iterator.QueryIterConcat ;
import com.hp.hpl.jena.sparql.pfunction.PropFuncArg ;
import com.hp.hpl.jena.sparql.pfunction.PropFuncArgType ;
import com.hp.hpl.jena.sparql.pfunction.PropertyFunctionEval ;
import com.hp.hpl.jena.sparql.util.graph.GraphList ;

/** Base class for list realted operations */ 
public abstract class ListBase extends PropertyFunctionEval
{
    private PropFuncArgType objFuncArgType ;


    public ListBase(PropFuncArgType objFuncArgType)
    { 
        super(PropFuncArgType.PF_ARG_SINGLE, objFuncArgType) ;
    }
    
    /** If the subject is a list (well, at least not an unbound variable), dispatch to execOneList
     *  else dispatch to one of object a var, a list or a node.
     */
    @Override
    final
    public QueryIterator execEvaluated(Binding binding, PropFuncArg argSubject, Node predicate, PropFuncArg argObject, ExecutionContext execCxt)
    {
        Node listNode = argSubject.getArg() ;
        if ( !Var.isVar(listNode) )
            // Subject bound or constant
            return execOneList(binding, listNode, predicate, argObject, execCxt) ;
        
        // Subject unbound.
        Var listVar = Var.alloc(listNode) ;
        return listUnboundSubject(binding, listVar, predicate, argObject, execCxt) ;
    }
    
    
    private QueryIterator listUnboundSubject(Binding binding, Var listVar, Node predicate, PropFuncArg argObject,
                                             ExecutionContext execCxt)
    {
        // Object?
        if ( argObject.isList() )
        {
            List<Node> objectArgs = argObject.getArgList() ;
            return execObjectList(binding, listVar, predicate, objectArgs, execCxt) ;
        }
        Node obj = argObject.getArg() ;
        if ( Var.isVar(obj))
        {
            Graph graph = execCxt.getActiveGraph() ;
            Set<Node> x = GraphList.findAllLists(graph) ;
            return allLists(binding, x, listVar, obj, argObject, execCxt) ;
        }
        
//         {
//            // Gulp.  Subject unbound.  Object unbound. BFI: Find all lists; work hard.
//            Set<Node> x = GraphList.findAllLists(graph) ;
//            QueryIterConcat qIter = new QueryIterConcat(execCxt) ;
//            for ( Node n : x )
//            {
//                Binding b = new Binding1(binding, listVar, n) ;
//                QueryIterator q = execOneList(b, n, predicate, argObject, execCxt) ;
//                qIter.add(q) ;
//            }
//            return qIter ;
//        }
        // Subject unbound.  Object a bound node.
        return execObjectBound(binding, listVar, predicate, obj, execCxt) ;
    }

    protected QueryIterator allLists(Binding binding, Collection<Node> x, Var listVar, Node predicate, PropFuncArg argObject, ExecutionContext execCxt)
    {
        // BFI: Find all lists; work hard.
        QueryIterConcat qIter = new QueryIterConcat(execCxt) ;
        for ( Node n : x )
        {
            Binding b = BindingFactory.binding(binding, listVar, n) ;
            QueryIterator q = execOneList(b, n, predicate, argObject, execCxt) ;
            qIter.add(q) ;
        }
        return qIter ;
    }
    
    /**
     * @param binding     current binding as input
     * @param listVar     a variable.
     * @param predicate   the predicate used to invoke this property function 
     * @param object      the object of the property function 
     * @param execCxt     Execution context
     * @return QueryIterator    
     */
    protected abstract
    QueryIterator execObjectBound(Binding binding, Var listVar, Node predicate, Node object,
                                          ExecutionContext execCxt) ;

    /**
     * @param binding     current binding as input
     * @param listVar     a variable.
     * @param predicate   the predicate used to invoke this property function 
     * @param objectArgs  the object of the property function 
     * @param execCxt     Execution context
     * @return QueryIterator    
     */
    protected abstract
    QueryIterator execObjectList(Binding binding, Var listVar, Node predicate, List<Node> objectArgs,
                                          ExecutionContext execCxt) ;

    /** Dispatch when the subject is a list - also used when subject and object all unbound
     *  after finding all lists. 
     * @param binding     current binding as input
     * @param listNode    the list; maybe a variable.
     * @param predicate   the predicate used to invoke this property function 
     * @param object      the object of the property function; maybe a variable 
     * @param execCxt     Execution context
     * @return QueryIterator    
     */
    protected abstract
    QueryIterator execOneList(Binding binding, Node listNode, Node predicate, PropFuncArg object, ExecutionContext execCxt) ;
}

/*
 * (c) Copyright 2007, 2008, 2009 Hewlett-Packard Development Company, LP
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