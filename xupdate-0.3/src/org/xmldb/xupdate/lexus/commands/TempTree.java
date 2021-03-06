package org.xmldb.xupdate.lexus.commands;

/*
 *  The XML:DB Initiative Software License, Version 1.0
 *
 *
 * Copyright (c) 2000-2001 The XML:DB Initiative.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        XML:DB Initiative (http://www.xmldb.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The name "XML:DB Initiative" must not be used to endorse or
 *    promote products derived from this software without prior written
 *    permission. For written permission, please contact info@xmldb.org.
 *
 * 5. Products derived from this software may not be called "XML:DB",
 *    nor may "XML:DB" appear in their name, without prior written
 *    permission of the XML:DB Initiative.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the XML:DB Initiative. For more information
 * on the XML:DB Initiative, please see <http://www.xmldb.org/>.
 */

import java.util.Hashtable;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class temporary stores variables with their name and selected
 * nodes.
 *
 * @version $Id: TempTree.java,v 1.3 2002/02/26 11:11:37 jbreedveld Exp $
 * @author <a href="http://www.smb-tec.com">SMB</a>
 */
public class TempTree extends Object {
    
    /* Contains variable names and values. */
    protected Hashtable _variables = null;
    
    
    /**
     * Add a new variable to the Hashtable. Existing variables will be
     * overwritten.
     * @param name the name of the variable.
     * @param tree the selected nodes.
     */
    public void addVariable( String name, NodeList tree ) {
        if (name==null) {
            throw new IllegalArgumentException( "variable name must not be null !" );
        }
        if (tree==null) {
            throw new IllegalArgumentException( "variable value must not be null !" );
        }
        _variables.put( name, tree );
    }
    
    
    /**
     * Returns the selection for the given variable name.
     * @return the selected nodes.
     * @throws Exception if there is no such variable.
     * @throws IllegalArgumentException if the variable name is null.
     */
    public NodeList getTreeForName( String name ) throws Exception {
        if (name==null) {
            throw new IllegalArgumentException( "variable-name must not be null !" );
        }
        
        NodeList result = (NodeList)_variables.get( name );
        if (result==null) {
            throw new Exception( "no variable named " + name + " found !" );
        }
        
        return result;        
    }
    
    
    /**
     *
     */
    public TempTree( ) {
        _variables = new Hashtable( );
    }
}

