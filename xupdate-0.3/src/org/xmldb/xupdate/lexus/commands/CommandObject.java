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
import org.xmldb.common.xml.queries.XPathQueryFactory;

import java.util.Hashtable;
import java.util.Vector;

import org.w3c.dom.*;

/**
 *
 * @version $Id: CommandObject.java,v 1.2 2002/11/04 16:02:27 lars Exp $
 * @author <a href="http://www.smb-tec.com">SMB</a>
 */
public abstract class CommandObject extends Object {
    
    /* */
    protected static XPathQuery _xpath = null;
    /* */
    protected Document _document = null;
    /* */
    protected Node _contextNode = null;    
    /* */
    protected NodeList _selection = null;
    /* */
    protected Hashtable _attributes = null;
    /* */
    protected Vector _characters = null;
    

    /**
     * <B>
     * XPath from Xt has problems with namespaces. To ensure correct
     * working use the XPath from Xalan or any other namespace aware
     * XPath implementation.
     * </B>
     *
     * @param contextNode
     */
    public CommandObject( Node contextNode ) throws Exception {
        if ( contextNode==null ) {
            throw new IllegalArgumentException( "Argument contextNode must not be null." );
        }
        if ( _xpath == null ) {
            _xpath = XPathQueryFactory.newInstance( ).newXPathQuery( );
        }
        _contextNode = contextNode;
        switch ( contextNode.getNodeType( ) ) {
            case Node.DOCUMENT_NODE:
                _document = (Document)contextNode;
                break;
            default:
            _document = contextNode.getOwnerDocument( );
        }
        _characters = new Vector( );
    }

    /**
     *
     */
    public void reset( ) {
        _characters.clear( );
    }

    /**
     * The given XPath expression <code>qString</code> will be executed and
     * will be used as the new context node - or if more then one node is
     * selected - as an array of context nodes. All given modifications will
     * be affect the selected context node.
     *
     * @param qString the XPath expression that selects the new context node.
     * @throws IllegalArgumentException If the given XPath expression is null.
     */
    public synchronized void selectNodes( String qString ) throws Exception {
        if ( qString == null ) {
            throw new IllegalArgumentException( "Argument qString must not be null." );
        }
        //long start = System.currentTimeMillis();
        _xpath.setQString( qString );
        XObject result = _xpath.execute( _contextNode );
        if (result.getType()!=XObject.CLASS_NODESET) {
            throw new Exception( "XPath leads not to a Node or NodeList !" );
        }
        _selection = result.nodeset();
        if (_selection.getLength( )==0) {
            throw new Exception( "no nodes selected !" );
        }
        //long end = System.currentTimeMillis() - start;
        //System.err.println("[select: "+_selection.getLength()+" nodes in "+end+" ms]");
    }


    /**
     * @param attributes
     */
    public void submitAttributes( Hashtable attributes ) {
        _attributes = attributes;
    }
    

    /**
     * @param data
     */
    public void submitCharacters( String data ) {
        _characters.addElement( data );
    }
    
    
    /**
     * @param instruction
     */
    public abstract boolean submitInstruction( int instruction ) throws Exception;


    /**
     *
     */
    public abstract boolean executeInstruction( ) throws Exception;


    /**
     *
     */
    public abstract Node execute( ) throws Exception;


    /**
     * @return 
     */
    public static XPathQuery getXPath( ) {
        return _xpath;
    }
}

