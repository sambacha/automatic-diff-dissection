 /**
  * Licensed to the Apache Software Foundation (ASF) under one
  * or more contributor license agreements.  See the NOTICE file
  * distributed with this work for additional information
  * regarding copyright ownership.  The ASF licenses this file
  * to you under the Apache License, Version 2.0 (the
  * "License"); you may not use this file except in compliance
  * with the License.  You may obtain a copy of the License at
  *
  *     http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */
 
 package org.apache.cassandra.db;
 
 import java.io.ByteArrayInputStream;
 import java.io.DataInputStream;
 
 import org.apache.cassandra.net.IVerbHandler;
 import org.apache.cassandra.net.Message;
 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
 public class BinaryVerbHandler implements IVerbHandler
 {
    private static Logger logger_ = LoggerFactory.getLogger(BinaryVerbHandler.class);
 
     public void doVerb(Message message)
     { 
         byte[] bytes = message.getMessageBody();
         ByteArrayInputStream buffer = new ByteArrayInputStream(bytes);
 
         try
         {
             RowMutationMessage rmMsg = RowMutationMessage.serializer().deserialize(new DataInputStream(buffer));
             RowMutation rm = rmMsg.getRowMutation();
             rm.applyBinary();
         }
         catch (Exception e)
         {
             throw new RuntimeException(e);
         }
     }
 
 }
