 /*
 
    Derby - Class org.apache.derby.impl.sql.compile.Token
 
   Copyright 1997, 2004 The Apache Software Foundation or its licensors, as applicable.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
 
       http://www.apache.org/licenses/LICENSE-2.0
 
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
 
  */
 
 package org.apache.derby.impl.sql.compile;
 
 /**
  * Describes the input token stream.
  */
 
 public class Token {
 
   /**
    * An integer that describes the kind of this token.  This numbering
    * system is determined by JavaCCParser, and a table of these numbers is
    * stored in the file ...Constants.java.
    */
   public int kind;
 
   /**
    * beginLine and beginColumn describe the position of the first character
    * of this token; endLine and endColumn describe the position of the
    * last character of this token.
    */
   public int beginLine, beginColumn, endLine, endColumn;
 
   /**
    * beginOffset and endOffset are useful for siphoning substrings out of
    * the Statement so that we can recompile the substrings at upgrade time.
    * For instance, VIEW definitions and the Restrictions on Published Tables
    * need to be recompiled at upgrade time.
    */
   public int beginOffset, endOffset;
 
   /**
    * The string image of the token.
    */
   public String image;
 
   /**
    * A reference to the next regular (non-special) token from the input
    * stream.  If this is the last token from the input stream, or if the
    * token manager has not read tokens beyond this one, this field is
    * set to null.  This is true only if this token is also a regular
    * token.  Otherwise, see below for a description of the contents of
    * this field.
    */
   public Token next;
 
   /**
    * This field is used to access special tokens that occur prior to this
    * token, but after the immediately preceding regular (non-special) token.
    * If there are no such special tokens, this field is set to null.
    * When there are more than one such special token, this field refers
    * to the last of these special tokens, which in turn refers to the next
    * previous special token through its specialToken field, and so on
    * until the first special token (whose specialToken field is null).
    * The next fields of special tokens refer to other special tokens that
    * immediately follow it (without an intervening regular token).  If there
    * is no such token, this field is null.
    */
   public Token specialToken;
 
   /**
    * Returns the image.
    */
   public String toString()
   {
      return image;
   }
 
   /**
    * Returns a new Token object, by default. However, if you want, you
    * can create and return subclass objects based on the value of ofKind.
    * Simply add the cases to the switch for all those special cases.
    * For example, if you have a subclass of Token called IDToken that
    * you want to create if ofKind is ID, simlpy add something like :
    *
    *    case MyParserConstants.ID : return new IDToken();
    *
    * to the following switch statement. Then you can cast matchedToken
    * variable to the appropriate type and use it in your lexical actions.
    */
   public static final Token newToken(int ofKind)
   {
      switch(ofKind)
      {
        default : return new Token();
      }
   }
 
 }