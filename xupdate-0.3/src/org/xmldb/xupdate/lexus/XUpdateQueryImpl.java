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

import org.xmldb.xupdate.lexus.commands.*;
import org.xmldb.common.xml.queries.XUpdateQuery;

import java.io.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.HashMap;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.Parser;
import org.xml.sax.helpers.ParserAdapter;

import org.w3c.dom.*;
import org.w3c.dom.traversal.NodeFilter;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;

/**
 * <b>Note:</b>
 *       If the DOM implementation you use does not support DOM-Level 2, some 
 *       features will not work properly. XUpdate uses the method
 *       <code>getNamespaceURI</code> of the <code>org.w3c.dom.Node</code> 
 *       interface. This method returns <code>null</code> if the current Node 
 *       has no namespace. Your DOM implementation should implement this method 
 *       and alway return null, then XUpdate should work better. Nevertheless 
 *       you cannot use some features. Inserting elements and attributes just 
 *       works, if you omit the optional attribute called namespace. For more 
 *       detail about XUpdate look at the 
 *       <a href="http://www.xmldb.org/xupdate/xupdate-wd.html">XUpdate Working Draft</a>.
 * @version $Revision: 1.5 $ $Date: 2002/11/04 17:29:57 $
 * @author <a href="http://www.smb-tec.com">SMB</a>
 * @author <a href="mailto:tdean@gr.com">Timothy M. Dean</a>
 */
public class XUpdateQueryImpl implements XUpdateQuery {

    /* The XUpdate namespace-uri. */
    public final static String NAMESPACE_URI = "http://www.xmldb.org/xupdate";
    
    /* Another representation of the query. It is faster than many String comparisons.*/
    protected Vector _query[] = null;
    /* Representation of commands and instructions as IDs. */
    protected CommandConstants _commands = null;
    /* */
    protected NodeFilter _filter = null;
    /* */
    protected Node _namespace = null;
    /* */
    protected HashMap _namespaces = null;
    
    
    /**
     * 
     */
    public XUpdateQueryImpl( ) {
        _commands = new CommandConstants( );
    }
    
    
    /**
     *
     */
    public void setQString( String query ) throws SAXException {
        XUpdateQueryParser xuParser = new XUpdateQueryParser( _commands );

        try {
            SAXParser parser = SAXParserFactory.newInstance( ).newSAXParser( );
            ParserAdapter saxParser = new ParserAdapter( parser.getParser( ) );
            saxParser.setContentHandler( xuParser );
            saxParser.parse( new InputSource( new StringReader( query ) ) );
        } catch (Exception e) {
            throw new SAXException( e.getMessage( ) );
        }

        _namespaces = xuParser.getNamespaceMappings();
        _query = xuParser.getCachedQuery( );
        if (_query[0].size()==0) {
            throw new SAXException( "query contains no XUpdateOperation !" );
        }
    }
    
    
    /**
     *
     */
    public void setNamespace( Node node ) {
        _namespace = node;
    }
    
    
    /**
     *
     */
    public void setNodeFilter( NodeFilter filter ) {
        _filter = filter;
    }


    /**
     *
     */
    public void execute( Node contextNode ) throws Exception {
        CommandObject currentCommand = new DefaultCommand( contextNode );
        Enumeration commands   = _query[0].elements( );
        Enumeration attributes = _query[1].elements( );
        Enumeration characters = _query[2].elements( );
        while (commands.hasMoreElements( )) {
            int id = ((Integer)commands.nextElement( )).intValue( );
            if (id>0) {
                switch ( id ) {
                    case CommandConstants.ATTRIBUTES:
                        currentCommand.submitAttributes( (Hashtable)attributes.nextElement( ) );
                        break;
                    case CommandConstants.CHARACTERS:
                        currentCommand.submitCharacters( (String)characters.nextElement( ) );
                        break;
                    default:
                        if (!currentCommand.submitInstruction( id )) {
                            _commands.setContextNode( contextNode );
                            currentCommand = _commands.commandForID( id );
                            if (currentCommand==null) {
                                throw new Exception( "operation can not have any XUpdate-instruction !" );
                            }
                            currentCommand.reset( );
                        }
                }
            } else {
                if (!currentCommand.executeInstruction( )) {
                    contextNode = currentCommand.execute( );
                    currentCommand = new DefaultCommand( contextNode );
                }
                
            }
        }
    }
    
    
    /**
     * Main-method. You can manually test your update operations.
     */
    public static void main( String args[] ) throws Exception {
        if ( args.length==0 ) {
            System.err.println( "usage: java org.xmldb.xupdate.lexus.XUpdateQueryImpl update document" );
            System.err.println( "       update   - filename of the file which contains XUpdate operations" );
            System.err.println( "       document - filename of the file which contains the content to update" );
            System.exit( 0 );
        }

        // parse the update file
        File file = new File( args[0] );
        BufferedReader br = new BufferedReader( new FileReader ( file ) );
        char[] characters = new char[ new Long(file.length()).intValue() ];
        br.read( characters, 0, new Long( file.length() ).intValue() );
        String queryStr = new String( characters );

        // parse the document file
        Node myDocument = null;

				DocumentBuilderFactory parserFactory = DocumentBuilderFactory.newInstance();

				parserFactory.setValidating(false);
				parserFactory.setNamespaceAware(true);
        parserFactory.setIgnoringElementContentWhitespace(true);

        DocumentBuilder builder = parserFactory.newDocumentBuilder();
				myDocument = builder.parse(args[1]);

        System.setProperty ("org.xmldb.common.xml.queries.XPathQueryFactory",
                            "org.xmldb.common.xml.queries.xalan2.XPathQueryFactoryImpl");
        // update the document and print time used for the updates
        XUpdateQueryImpl xq = new XUpdateQueryImpl();

        System.err.println("Starting updates...");
        long timeStart = System.currentTimeMillis();

        xq.setQString ( queryStr );
        xq.execute(myDocument);

        long timeEnd = System.currentTimeMillis();
        System.err.println("Updates done in "+(timeEnd-timeStart)+" ms ...");

				StringWriter writer = new StringWriter();
				OutputFormat OutFormat = new OutputFormat("xml", "UTF-8", true);
			  XMLSerializer serializer = new XMLSerializer(writer, OutFormat);
				serializer.asDOMSerializer().serialize((Document) myDocument);
				System.err.println("Result: \n"+writer.toString());

    }

}

