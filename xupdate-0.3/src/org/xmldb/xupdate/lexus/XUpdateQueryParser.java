package org.xmldb.xupdate.lexus;

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

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import org.xmldb.xupdate.lexus.commands.*;

import java.util.Hashtable;
import java.util.Vector;
import java.util.HashMap;

/**
 * This class parses the query-String by a SAXParser. The SAXEvents are 
 * converted to a list of Integers.
 *
 * @version $Revision: 1.3 $ $Date: 2002/11/04 14:21:57 $
 * @author <a href="http://www.smb-tec.com">SMB</a>
 * @author <a href="mailto:tdean@gr.com">Timothy M. Dean</a>
 */
public class XUpdateQueryParser implements ContentHandler {

    /* The list of Integers. */
    protected Vector _commands = null;
    /* The list of all attributes as Hashtable for each element. */
    protected Vector _attributes = null;
    /* The list of all characters for each element. */
    protected Vector _characters = null;
    /* The list of mappings for namespace prefixes */
    protected HashMap _namespaces = null;
    /* The Integer representation of each XUpdate-command. */
    protected CommandConstants _consts = null;
    /* A flag indicating whether we are within an XUpdateOperation. */
    protected boolean _xupdateInsertOperation = false;
    /* The name of the last operation. */
    protected String _lastOperation = "";
    
    /**
     * @param constants the Object containing the Integer representation.
     */
    public XUpdateQueryParser( CommandConstants constants ) {
        _consts = constants;
        _commands = new Vector();
        _attributes = new Vector();
        _characters = new Vector();
        _namespaces = new HashMap();
    }
    
    
    /**
     *
     */
    public void startDocument( ) {
    }
    
    
    /**
     *
     */
    public void endDocument( ) {
    }
    
    
    /**
     *
     */
    public void startElement( String namespaceURI, String localName, 
            String qName, Attributes atts ) throws SAXException {
        Hashtable attributes = new Hashtable();
        for (int i=0; i<atts.getLength(); i++) {
            attributes.put( atts.getLocalName(i), atts.getValue(i) );
        }
        if (namespaceURI.equals(XUpdateQueryImpl.NAMESPACE_URI)) {
            int id = _consts.idForString ( localName );
            if (id != 0) {
                _xupdateInsertOperation = _consts.isInsertOperation( id ) ? true : _xupdateInsertOperation;
                _commands.addElement( new Integer( id ) );
                if (_consts.isInstruction( id ) && !_xupdateInsertOperation) {
                    throw new SAXException( "instruction <" + qName + 
                            "> is not valid for operation <" + _lastOperation + "> !" );
                }
                if (!attributes.isEmpty()) {
                    _commands.addElement( new Integer( _consts.ATTRIBUTES ) );
                    _attributes.addElement( attributes );
                }
            }
        } else
        if (_xupdateInsertOperation) {
            _commands.addElement( new Integer( _consts.INSTRUCTION_ELEMENT ) );
            Hashtable temp = new Hashtable( );
            temp.put("name", qName);
            temp.put("namespace", namespaceURI);
            _commands.addElement( new Integer( _consts.ATTRIBUTES ) );
            _attributes.addElement( temp );
            for (int i=0; i<atts.getLength(); i++) {
                _commands.addElement( new Integer( _consts.INSTRUCTION_ATTRIBUTE ) );
                temp = new Hashtable( );
                temp.put( "name", atts.getQName(i) );
                if (atts.getURI(i)!=null) {
                    temp.put( "namespace", atts.getURI(i) );
                }
                _commands.addElement( new Integer( _consts.ATTRIBUTES ) );
                _attributes.addElement( temp );
                _commands.addElement( new Integer( _consts.CHARACTERS ) );
                _characters.addElement( atts.getValue(i) );
                _commands.addElement( new Integer( - _consts.INSTRUCTION_ATTRIBUTE ) );
            }
        } else {
            throw new SAXException( "no insert-operation for element <" + qName + "> or wrong XUpdate-Namespace !" );
        }
        _lastOperation = qName;
    }
    
    
    /**
     *
     */
    public void endElement( String namespaceURI, String localName, String qName ) {
        if (namespaceURI.equals(XUpdateQueryImpl.NAMESPACE_URI)) {
            int id = _consts.idForString ( localName );
            if (id != 0) {
                _commands.addElement( new Integer( -id ) );
                _xupdateInsertOperation = _consts.isInsertOperation( id ) ? false : _xupdateInsertOperation;
            }
        } else
        if (_xupdateInsertOperation) {
            _commands.addElement( new Integer( - _consts.INSTRUCTION_ELEMENT ) );
        }
    }
    
    
    /**
     * If a new namespace mapping will be found we save these information
     * for further processing.
     *
     * @param prefix
     * @param uri
     */
    public void startPrefixMapping( String prefix, String uri )  {
        if ((prefix != null) && (prefix.length() > 0)) {
            _namespaces.put(prefix, uri);
        }
        else {
            _namespaces.put( null, uri );
        }
    }
    
    
    /**
     * 
     * @param prefix The namespace prefix to be ended.
     */
    public void endPrefixMapping( String prefix ) {
    }
    
    
    /**
     *
     */
    public void characters( char[] ch, int start, int length ) {
        if (!_commands.isEmpty()) {
            int lastInteger = ((Integer)_commands.lastElement()).intValue();
            if (_xupdateInsertOperation && lastInteger<0) {
                _commands.addElement( new Integer( _consts.INSTRUCTION_TEXT ) );
            }
            _commands.addElement( new Integer( _consts.CHARACTERS ) );
            _characters.addElement( new String( ch, start, length ) );
            if (_xupdateInsertOperation && lastInteger<0) {
                _commands.addElement( new Integer( - _consts.INSTRUCTION_TEXT ) );
            }
        }
    }
    
    
    /**
     *
     */
    public void ignorableWhitespace( char[] ch, int start, int length ) {
    }
   
    
    /**
     *
     */
    public void processingInstruction( String target, String data ) {
    }
    
    
    /**
     *
     */
    public void setDocumentLocator( Locator locator ) {
    }
    
    
    /**
     *
     */
    public void skippedEntity( String name ) {
    }
    
    
    /**
     * Returns the cached query String as a representation of three Vectors.
     *
     * @return The cached query String.
     */
    public Vector[] getCachedQuery( ) {
        return new Vector[]{ _commands, _attributes, _characters };
    }

    /**
     * Returns a Map of all namespace prefixes and their proper
     * namespace URIs.
     *
     * @return All saved namespace mappings.
     * @since XML:DB Lexus 0.3
     */
    public HashMap getNamespaceMappings() {
        return _namespaces;
    }

}

