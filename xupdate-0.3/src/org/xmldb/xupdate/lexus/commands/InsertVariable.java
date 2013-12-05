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

import org.xmldb.common.xml.queries.XObject;
import org.xmldb.common.xml.queries.XPathQuery;

import org.w3c.dom.*;

/**
 * 
 * @version $Id: InsertVariable.java,v 1.2 2002/11/04 16:02:27 lars Exp $
 * @author <a href="http://www.smb-tec.com">SMB</a>
 */
public class InsertVariable extends InsertStates {

    /**
     *
     */
    public InsertVariable( ) {
        super( );
    }
    
    
    /**
     *
     */
    public Node execute( Node contextNode ) throws Exception {
        String selection = (String)_attributes.get( "select" );

        NodeList newNodes = null;
        if (selection.charAt( 0 )=='$') {
            String name = selection.substring( 1 );
            newNodes = CommandConstants._tempTree.getTreeForName( name );
        } else {
            newNodes = selectNewNodes( selection );
        }
        
        Node result = contextNode;
        int newNodesLength = newNodes.getLength( );
        for (int i=0; i<newNodesLength; i++) {
            Node current = newNodes.item( i );
            switch (current.getNodeType( )) {
                case Node.ATTRIBUTE_NODE:
                    switch (contextNode.getNodeType( )) {
                        case Node.ELEMENT_NODE:
                            Attr attribute = (Attr) current.cloneNode( true );
                            ((Element)contextNode).setAttributeNode( attribute );
                            result = attribute;
                            break;
                        default:
                            throw new Exception( "can't append attribute !" );
                    }
                    break;
                default:
                    result = contextNode.appendChild( current.cloneNode( true ) );
            }
        }
        return result;
    }

    /**
     *
     */
    protected NodeList selectNewNodes( String query ) throws Exception {
//long start = System.currentTimeMillis();
        XPathQuery xpath = CommandObject.getXPath();
        xpath.setQString( query );
        XObject xResult = xpath.execute( CommandConstants._contextNode );
        if (xResult.getType()!=XObject.CLASS_NODESET) {
            throw new Exception( "XPath leads not to a Node or NodeList !" );
        }
        NodeList result = xResult.nodeset();
//long end = System.currentTimeMillis() - start;
//System.err.println("value-of select:"+end);
        return result;
    }
}

