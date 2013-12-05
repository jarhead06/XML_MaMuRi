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

import java.util.Hashtable;

/**
 * This class represents all XUpdate commands as Integer values. It returns
 * the Integer for the given String and also the Commandobject for the given
 * Integer.
 *
 * @version $Id: CommandConstants.java,v 1.1.1.1 2002/01/30 09:46:44 lars Exp $
 * @author <a href="http://www.smb-tec.com">SMB</a>
 */
public class CommandConstants extends Object {
    
    /* represents <code>xupdate:modifications</code>, the root element*/
    public final static int ROOT_ELEMENT = 0;
    /* */
    public final static int COMMAND_COUNT = 7;
    /* represents <code>xupdate:remove</code> */
    public final static int COMMAND_REMOVE = 1;
    /* represents <code>xupdate:rename</code> */
    public final static int COMMAND_RENAME = 2;
    /* represents <code>xupdate:update</code> */
    public final static int COMMAND_UPDATE = 3;
    /* represents <code>xupdate:variable</code> */
    public final static int COMMAND_VARIABLE = 4;
    /* represents <code>xupdate:append</code> */
    public final static int COMMAND_APPEND = 5;
    /* represents <code>xupdate:insert-before</code> */
    public final static int COMMAND_INSERT_BEFORE = 6;
    /* represents <code>xupdate:insert-after</code> */
    public final static int COMMAND_INSERT_AFTER = 7;
    /* */
    protected final static int FIRST_INSTRUCTION = 100;
    /* represents <code>xupdate:element</code> */
    public final static int INSTRUCTION_ELEMENT = 101;
    /* represents <code>xupdate:attribute</code> */
    public final static int INSTRUCTION_ATTRIBUTE = 102;
    /* represents <code>xupdate:comment</code> */
    public final static int INSTRUCTION_COMMENT = 103;
    /* represents <code>xupdate:text</code> */
    public final static int INSTRUCTION_TEXT = 104;
    /* represents <code>xupdate:cdata</code> */
    public final static int INSTRUCTION_CDATA = 105;
    /* represents <code>xupdate:processing-instruction</code> */
    public final static int INSTRUCTION_PROCESSING_INSTRUCTION = 106;
    /* represents <code>xupdate:value-of</code> */
    public final static int INSTRUCTION_VALUE_OF = 107;
    /* */
    protected final static int LAST_INSTRUCTION = 200;
    /* */
    public final static int ATTRIBUTES = 200;
    /* */
    public final static int CHARACTERS = 300;

    /* Contains the allocation of String and Integer. */
    protected Hashtable _ids = null;
    /* Contains the Command-object for each COMMAND. */
    protected CommandObject[] _command = null;
    /* The contextNode which is to be updated. */
    public static Node _contextNode = null;
    /* A Hashtable containing all variable names and its selected nodes. */
    public static TempTree _tempTree = null;    
    
    /**
     * 
     */
    public CommandConstants( ) {
        _tempTree = new TempTree( );
        initTable( );
    }
    
    
    /**
     * Maps the given String to a Integer value.
     * @param localName the name of a XUpdate-command.
     * @return the Integer value for this String. If there is no such XUpdate 
     *         command, it returns 0.
     */
    public int idForString( String localName ) {
        Integer result = (Integer)_ids.get( localName );
        if (result==null) {
            return 0;
        }
        return result.intValue();
    }
    
    
    /**
     * Maps the given integer to a COMMAND object.
     * @param id the ID for a XUpdate command.
     * @return the object which performs the XUpdate operation. If there is a
     *         no such ID or the given ID stands for an instruction it returns
     *         null.
     */
    public CommandObject commandForID( int id ) {
        switch( id ) {
            case COMMAND_REMOVE:
                return _command[0];
            case COMMAND_RENAME:
                return _command[1];
            case COMMAND_UPDATE:
                return _command[2];
            case COMMAND_VARIABLE:
                return _command[3];
            case COMMAND_APPEND:
                return _command[4];
            case COMMAND_INSERT_BEFORE:
                return _command[5];
            case COMMAND_INSERT_AFTER:
                return _command[6];
        }
        return null;
    }
    
    
    /**
     * Sets the contextNode which is to be updated and initializes the objects
     * for each XUpdate operation.
     * @param node the contextNode which is to be updated.
     * @throws IllegalArgumentException if node is null.
     */
    public void setContextNode( Node node ) throws Exception {
        if (node==null) {
            throw new IllegalArgumentException( "context node must not be null !" );
        }
        _contextNode = node;
        
        _command = new CommandObject[COMMAND_COUNT];
        _command[0] = new RemoveCommand( _contextNode );
        _command[1] = new RenameCommand( _contextNode );
        _command[2] = new UpdateCommand( _contextNode );
        _command[3] = new VariableCommand( _contextNode );
        _command[4] = new AppendCommand( _contextNode );
        _command[5] = new InsertBeforeCommand( _contextNode );
        _command[6] = new InsertAfterCommand( _contextNode );
    }


    /**
     * @return true if the given id is an insert operation
     */
    public boolean isInsertOperation( int id ) {
        return id==COMMAND_INSERT_BEFORE ||
               id==COMMAND_INSERT_AFTER ||
               id==COMMAND_APPEND;
    }
        
    
    /**
     * @return true if the given id is an instruction
     */
    public boolean isInstruction( int id ) {
        return FIRST_INSTRUCTION < id && id < LAST_INSTRUCTION;
    }
        
    
    /**
     * Initializes the Hashtable which converts the String representation
     * of the XUpdate operation and instruction to the Integer representation.
     */
    protected void initTable( ) {
        _ids = new Hashtable();
        //root element
        _ids.put("modifications", new Integer( ROOT_ELEMENT ));
        //commands
        _ids.put("remove",        new Integer( COMMAND_REMOVE ));
        _ids.put("rename",        new Integer( COMMAND_RENAME ));
        _ids.put("update",        new Integer( COMMAND_UPDATE ));
        _ids.put("variable",      new Integer( COMMAND_VARIABLE ));
        _ids.put("append",        new Integer( COMMAND_APPEND ));
        _ids.put("insert-before", new Integer( COMMAND_INSERT_BEFORE ));
        _ids.put("insert-after",  new Integer( COMMAND_INSERT_AFTER ));
        //instructions
        _ids.put("element",   new Integer( INSTRUCTION_ELEMENT ));
        _ids.put("attribute", new Integer( INSTRUCTION_ATTRIBUTE ));
        _ids.put("comment",   new Integer( INSTRUCTION_COMMENT ));
        _ids.put("text",      new Integer( INSTRUCTION_TEXT ));
        _ids.put("cdata",     new Integer( INSTRUCTION_CDATA ));
        _ids.put("value-of",  new Integer( INSTRUCTION_VALUE_OF ));
        _ids.put("processing-instruction", 
                                       new Integer( INSTRUCTION_PROCESSING_INSTRUCTION ));

    }
}

