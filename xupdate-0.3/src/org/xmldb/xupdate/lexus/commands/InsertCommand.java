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

import org.w3c.dom.*;

import java.util.Hashtable;

/**
 *
 * @version $Id: InsertCommand.java,v 1.3 2002/11/13 11:38:12 jbreedveld Exp $
 * @author <a href="http://www.smb-tec.com">SMB</a>
 */
public abstract class InsertCommand extends CommandObject {

    /* */
    protected Node _current = null;
    /* */
    protected Node _result = null;
    /* */
    protected int _state = -1;
    /* */
    protected static InsertStates[] _states = null;
    /* */
    public final static int STATE_COUNT = 7;

    /**
     *
     */
    static {
        _states = new InsertStates[STATE_COUNT];
        _states[0] = new InsertElement( );
        _states[1] = new InsertAttribute( );
        _states[2] = new InsertComment( );
        _states[3] = new InsertText( );
        _states[4] = new InsertCDATA( );
        _states[5] = new InsertProcessingInstruction( );
        _states[6] = new InsertVariable( );
    }



    /**
     *
     */
    public InsertCommand( Node contextNode ) throws Exception {
        super( contextNode );
    }


    /**
     *
     */
    public void submitAttributes( Hashtable attributes ) {
        if (_state<0) {
            super.submitAttributes( attributes );
        } else {
            _states[_state].submitAttributes( attributes );
        }
    }


    /**
     *
     */
    public void submitCharacters( String data ) {
        if (_state<0) {
            super.submitCharacters( data );
        } else {
            _states[_state].submitCharacters( data );
        }
    }


    /**
     *
     */
    public boolean submitInstruction( int instruction ) throws Exception {
        if (_result==null) {
            _result = _document.createElementNS( null, "temporaryXUpdateTree" );
            _current = _result;
            _document.getDocumentElement().appendChild( _result );
        }
        if (_state>=0) {
            _current = _states[_state].execute( _current );
        }
        switch (instruction) {
            case CommandConstants.INSTRUCTION_ELEMENT:
                _state = 0;
                break;
            case CommandConstants.INSTRUCTION_ATTRIBUTE:
                _state = 1;
                break;
            case CommandConstants.INSTRUCTION_COMMENT:
                _state = 2;
                break;
            case CommandConstants.INSTRUCTION_TEXT:
                _state = 3;
                break;
            case CommandConstants.INSTRUCTION_CDATA:
                _state = 4;
                break;
            case CommandConstants.INSTRUCTION_PROCESSING_INSTRUCTION:
                _state = 5;
                break;
            case CommandConstants.INSTRUCTION_VALUE_OF:
                _state = 6;
                break;
            default:
                _state = -1;
        }
        if (_state>=0) {
            _states[_state].reset( );
        }
        return _state>=0;
    }


    /**
     *
     */
    public boolean executeInstruction( ) throws Exception {
        if (_state>=0) {
            _current = _states[_state].execute( _current );
            _state = -1;
        }
        if (!_current.equals(_result)) {
            switch ( _current.getNodeType() ) {
                case Node.ATTRIBUTE_NODE:
                    _current = ((Attr)_current).getOwnerElement( );
                    return true;
                default:
                    _current = _current.getParentNode();
                    return true;
            }
        }
        return false;
    }


    /**
     *
     */
    protected void insertAttributes( NamedNodeMap attributes, Node node ) throws Exception {
        if (attributes==null) {
            return;
        }
        if (node.getNodeType()!=Node.ELEMENT_NODE) {
            throw new Exception( "can't append attribute to !" );
        }
        int attributesLength = attributes.getLength( );
        for (int j=0; j<attributesLength; j++) {
            Attr attribute = (Attr)attributes.item( j );
            String namespaceURI = attribute.getNamespaceURI( );
            ((Element)node).setAttributeNS( namespaceURI, attribute.getNodeName(), attribute.getNodeValue() );
        }
    }


    /**
     *
     */
    public abstract Node execute( ) throws Exception;
}

