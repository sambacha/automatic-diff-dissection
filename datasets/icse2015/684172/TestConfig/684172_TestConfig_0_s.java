 /**
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  *     http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */
 
 package org.apache.solr.core;
 
 import org.apache.solr.util.AbstractSolrTestCase;
 import org.apache.solr.update.SolrIndexConfig;
 import org.w3c.dom.Node;
 import org.w3c.dom.NodeList;
 
 import javax.xml.xpath.XPathConstants;
 
 public class TestConfig extends AbstractSolrTestCase {
 
   public String getSchemaFile() { return "schema.xml"; }
   public String getSolrConfigFile() { return "solrconfig.xml"; }
 
   public void testJavaProperty() {
     // property values defined in build.xml
 
     String s = solrConfig.get("propTest");
     assertEquals("prefix-proptwo-suffix", s);
 
     s = solrConfig.get("propTest/@attr1", "default");
     assertEquals("propone-${literal}", s);
 
     s = solrConfig.get("propTest/@attr2", "default");
     assertEquals("default-from-config", s);
 
     s = solrConfig.get("propTest[@attr2='default-from-config']", "default");
     assertEquals("prefix-proptwo-suffix", s);
 
     NodeList nl = (NodeList) solrConfig.evaluate("propTest", XPathConstants.NODESET);
     assertEquals(1, nl.getLength());
     assertEquals("prefix-proptwo-suffix", nl.item(0).getTextContent());
 
     Node node = solrConfig.getNode("propTest", true);
     assertEquals("prefix-proptwo-suffix", node.getTextContent());
   }
 
   public void testLucene23Upgrades() throws Exception {
     double bufferSize = solrConfig.getDouble("indexDefaults/ramBufferSizeMB");
     assertTrue(bufferSize + " does not equal: " + 32, bufferSize == 32);
     String mergePolicy = solrConfig.get("indexDefaults/mergePolicy");
     assertTrue(mergePolicy + " is not equal to " + SolrIndexConfig.DEFAULT_MERGE_POLICY_CLASSNAME, mergePolicy.equals(SolrIndexConfig.DEFAULT_MERGE_POLICY_CLASSNAME) == true);
     String mergeSched = solrConfig.get("indexDefaults/mergeScheduler");
     assertTrue(mergeSched + " is not equal to " + SolrIndexConfig.DEFAULT_MERGE_SCHEDULER_CLASSNAME, mergeSched.equals(SolrIndexConfig.DEFAULT_MERGE_SCHEDULER_CLASSNAME) == true);
     boolean luceneAutoCommit = solrConfig.getBool("indexDefaults/luceneAutoCommit");
     assertTrue(luceneAutoCommit + " does not equal: " + false, luceneAutoCommit == false);
   }
 }