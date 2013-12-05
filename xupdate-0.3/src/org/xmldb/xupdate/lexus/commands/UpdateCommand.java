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

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;

/**
 *
 * @version $Id: UpdateCommand.java,v 1.2 2002/04/19 10:18:02 jbreedveld Exp $
 * @author <a href="http://www.smb-tec.com">SMB</a>
 */
public class UpdateCommand extends CommandObject {


    /**
     *
     */
    public UpdateCommand( Node contextNode ) throws Exception {
        super( contextNode );
    }


    /**
     * @return Always <code>false</code>.
     */
    public boolean submitInstruction( int instruction ) {
        //does nothing
        return false;
    }


    /**
     * @return Always <code>false</code>.
     */
    public boolean executeInstruction( ) {
        //does nothing
        return false;
    }


    /**
     *
     */
    public Node execute( ) throws Exception {
        String data = "";
        for (int i=0; i<_characters.size(); i++) {
          data += (String)_characters.elementAt(i);
        }
        String selection = (String)_attributes.get("select");
        selectNodes( selection );
        int selectionLength = _selection.getLength( );
        for (int i=0; i<_selection.getLength(); i++) {
            Node current = _selection.item(i);
            switch ( current.getNodeType() ) {
                case Node.ELEMENT_NODE:
                    current.normalize();
                    NodeList children = current.getChildNodes( );
                    int childrenLength = children.getLength( );
                    for (int j=0; j<childrenLength; j++) {
                        if (children.item( j ).getNodeType( )==Node.TEXT_NODE) {
                            current.removeChild( children.item( j ) );
                        }
                    }
                    if (_characters.size()>0) {
                        current.appendChild( _document.createTextNode( data ) );
                    }
                    break;
                case Node.TEXT_NODE:
                    if (_characters.size()>0) {
                        current.setNodeValue( data );
                    } else {
                        Node parent = current.getParentNode();
                        parent.removeChild( current );
                    }
                    break;
                case Node.COMMENT_NODE:
                    ((Comment)current).setData( data );
                    break;
                case Node.ATTRIBUTE_NODE:
                    ((Attr)current).setValue( data );
                    break;
                default:
                    if (_characters.size()==0) {
                        throw new Exception( "[update] new values must not be empty !" );
                    }
                    current.setNodeValue( data );
            }
        }
        return _contextNode;
    }
}
