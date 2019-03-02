 /*
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
 
 package org.apache.solr.update.processor;
 
 import java.util.ArrayList;
 import java.util.List;
 import org.apache.solr.SolrTestCaseJ4;
 import org.apache.solr.common.SolrInputDocument;
 import org.apache.solr.common.params.ModifiableSolrParams;
 import org.apache.solr.core.SolrCore;
 import org.junit.Before;
 import org.junit.BeforeClass;
 import org.junit.Test;
 import org.apache.solr.request.SolrQueryRequest;
 import org.apache.solr.response.SolrQueryResponse;
 import org.apache.solr.servlet.SolrRequestParsers;
 
 public abstract class LanguageIdentifierUpdateProcessorFactoryTestCase extends SolrTestCaseJ4 {
 
   protected static SolrRequestParsers _parser;
   protected static SolrQueryRequest req;
   protected static SolrQueryResponse resp = new SolrQueryResponse();
   protected static LanguageIdentifierUpdateProcessor liProcessor;
   protected static ModifiableSolrParams parameters;
 
   @BeforeClass
   public static void beforeClass() throws Exception {
     initCore("solrconfig-languageidentifier.xml", "schema.xml", getFile("langid/solr").getAbsolutePath());
     SolrCore core = h.getCore();
     UpdateRequestProcessorChain chained = core.getUpdateProcessingChain("lang_id");
     assertNotNull(chained);
     _parser = new SolrRequestParsers(null);
   }
 
   @Override
   @Before
   public void setUp() throws Exception {
     super.setUp();
     clearIndex();
     assertU(commit());
   }
 
   @Test
   public void testLangIdGlobal() throws Exception {
     parameters = new ModifiableSolrParams();
     parameters.add("langid.fl", "name,subject");
     parameters.add("langid.langField", "language_s");
     parameters.add("langid.fallback", "un");
     liProcessor = createLangIdProcessor(parameters);
     
     assertLang("no", "id", "1no", "name", "Lucene", "subject", "Lucene er et fri/Ã¥pen kildekode programvarebibliotek for informasjonsgjenfinning, opprinnelig utviklet i programmeringssprÃ¥ket Java av Doug Cutting. Lucene stÃ¸ttes av Apache Software Foundation og utgis under Apache-lisensen.");
     assertLang("en", "id", "2en", "name", "Lucene", "subject", "Apache Lucene is a free/open source information retrieval software library, originally created in Java by Doug Cutting. It is supported by the Apache Software Foundation and is released under the Apache Software License.");
     assertLang("sv", "id", "3sv", "name", "Maven", "subject", "Apache Maven Ã¤r ett verktyg utvecklat av Apache Software Foundation och anvÃ¤nds inom systemutveckling av datorprogram i programsprÃ¥ket Java. Maven anvÃ¤nds fÃ¶r att automatiskt paketera (bygga) programfilerna till en distribuerbar enhet. Maven anvÃ¤nds inom samma omrÃ¥de som Apache Ant men dess byggfiler Ã¤r deklarativa till skillnad ifrÃ¥n Ants skriptbaserade.");
     assertLang("es", "id", "4es", "name", "EspaÃ±ol", "subject", "El espaÃ±ol, como las otras lenguas romances, es una continuaciÃ³n moderna del latÃ­n hablado (denominado latÃ­n vulgar), desde el siglo III, que tras el desmembramiento del Imperio romano fue divergiendo de las otras variantes del latÃ­n que se hablaban en las distintas provincias del antiguo Imperio, dando lugar mediante una lenta evoluciÃ³n a las distintas lenguas romances. Debido a su propagaciÃ³n por AmÃ©rica, el espaÃ±ol es, con diferencia, la lengua romance que ha logrado mayor difusiÃ³n.");
     assertLang("un", "id", "5un", "name", "a", "subject", "b");
     assertLang("th", "id", "6th", "name", "à¸à¸à¸à¸§à¸²à¸¡à¸à¸±à¸à¸ªà¸£à¸£à¹à¸à¸·à¸­à¸à¸à¸µà¹", "subject", "à¸­à¸±à¸à¹à¸à¸­à¸¥à¸µà¸ª à¸¡à¸²à¸£à¸µ à¸­à¸±à¸à¹à¸à¸­ à¸à¸£à¸±à¸à¸à¹ à¸«à¸£à¸·à¸­à¸¡à¸±à¸à¸£à¸¹à¹à¸à¸±à¸à¹à¸à¸ à¸²à¸©à¸²à¹à¸à¸¢à¸§à¹à¸² à¹à¸­à¸à¸à¹ à¹à¸à¸£à¸à¸à¹ à¹à¸à¹à¸à¹à¸à¹à¸à¸«à¸à¸´à¸à¸à¸²à¸§à¸¢à¸´à¸§ à¹à¸à¸´à¸à¸à¸µà¹à¹à¸¡à¸·à¸­à¸à¹à¸à¸£à¸à¸à¹à¹à¸à¸´à¸£à¹à¸ à¸à¸£à¸°à¹à¸à¸¨à¹à¸¢à¸­à¸£à¸¡à¸à¸µ à¹à¸à¸­à¸¡à¸µà¸à¸·à¹à¸­à¹à¸ªà¸µà¸¢à¸à¹à¸à¹à¸à¸à¸±à¸à¹à¸à¸à¸²à¸à¸°à¸à¸¹à¹à¹à¸à¸µà¸¢à¸à¸à¸±à¸à¸à¸¶à¸à¸à¸£à¸°à¸à¸³à¸§à¸±à¸à¸à¸¶à¹à¸à¸à¹à¸­à¸¡à¸²à¹à¸à¹à¸£à¸±à¸à¸à¸²à¸£à¸à¸µà¸à¸´à¸¡à¸à¹à¹à¸à¹à¸à¸«à¸à¸±à¸à¸ªà¸·à¸­ à¸à¸£à¸£à¸¢à¸²à¸¢à¹à¸«à¸à¸¸à¸à¸²à¸£à¸à¹à¸à¸à¸°à¸«à¸¥à¸à¸à¹à¸­à¸à¸à¸±à¸§à¸à¸²à¸à¸à¸²à¸£à¸¥à¹à¸²à¸à¸²à¸§à¸¢à¸´à¸§à¹à¸à¸à¸£à¸°à¹à¸à¸¨à¹à¸à¹à¸à¸­à¸£à¹à¹à¸¥à¸à¸à¹ à¸£à¸°à¸«à¸§à¹à¸²à¸à¸à¸µà¹à¸à¸¹à¸à¹à¸¢à¸­à¸£à¸¡à¸à¸µà¹à¸à¹à¸²à¸à¸£à¸­à¸à¸à¸£à¸­à¸à¹à¸à¸à¹à¸§à¸à¸ªà¸à¸à¸£à¸²à¸¡à¹à¸¥à¸à¸à¸£à¸±à¹à¸à¸à¸µà¹à¸ªà¸­à¸");
     assertLang("ru", "id", "7ru", "name", "Lucene", "subject", "The Apache Lucene â ÑÑÐ¾ ÑÐ²Ð¾Ð±Ð¾Ð´Ð½Ð°Ñ Ð±Ð¸Ð±Ð»Ð¸Ð¾ÑÐµÐºÐ° Ð´Ð»Ñ Ð²ÑÑÐ¾ÐºÐ¾ÑÐºÐ¾ÑÐ¾ÑÑÐ½Ð¾Ð³Ð¾ Ð¿Ð¾Ð»Ð½Ð¾ÑÐµÐºÑÑÐ¾Ð²Ð¾Ð³Ð¾ Ð¿Ð¾Ð¸ÑÐºÐ°, Ð½Ð°Ð¿Ð¸ÑÐ°Ð½Ð½Ð°Ñ Ð½Ð° Java. ÐÐ¾Ð¶ÐµÑ Ð±ÑÑÑ Ð¸ÑÐ¿Ð¾Ð»ÑÐ·Ð¾Ð²Ð°Ð½Ð° Ð´Ð»Ñ Ð¿Ð¾Ð¸ÑÐºÐ° Ð² Ð¸Ð½ÑÐµÑÐ½ÐµÑÐµ Ð¸ Ð´ÑÑÐ³Ð¸Ñ Ð¾Ð±Ð»Ð°ÑÑÑÑ ÐºÐ¾Ð¼Ð¿ÑÑÑÐµÑÐ½Ð¾Ð¹ Ð»Ð¸Ð½Ð³Ð²Ð¸ÑÑÐ¸ÐºÐ¸ (Ð°Ð½Ð°Ð»Ð¸ÑÐ¸ÑÐµÑÐºÐ°Ñ ÑÐ¸Ð»Ð¾ÑÐ¾ÑÐ¸Ñ).");
     assertLang("de", "id", "8de", "name", "Lucene", "subject", "Lucene ist ein Freie-Software-Projekt der Apache Software Foundation, das eine Suchsoftware erstellt. Durch die hohe LeistungsfÃ¤higkeit und Skalierbarkeit kÃ¶nnen die Lucene-Werkzeuge fÃ¼r beliebige ProjektgrÃ¶Ãen und Anforderungen eingesetzt werden. So setzt beispielsweise Wikipedia Lucene fÃ¼r die Volltextsuche ein. Zudem verwenden die beiden Desktop-Suchprogramme Beagle und Strigi eine C#- bzw. C++- Portierung von Lucene als Indexer.");
     assertLang("fr", "id", "9fr", "name", "Lucene", "subject", "Lucene est un moteur de recherche libre Ã©crit en Java qui permet d'indexer et de rechercher du texte. C'est un projet open source de la fondation Apache mis Ã  disposition sous licence Apache. Il est Ã©galement disponible pour les langages Ruby, Perl, C++, PHP.");
     assertLang("nl", "id", "10nl", "name", "Lucene", "subject", "Lucene is een gratis open source, tekst gebaseerde information retrieval API van origine geschreven in Java door Doug Cutting. Het wordt ondersteund door de Apache Software Foundation en is vrijgegeven onder de Apache Software Licentie. Lucene is ook beschikbaar in andere programeertalen zoals Perl, C#, C++, Python, Ruby en PHP.");
     assertLang("it", "id", "11it", "name", "Lucene", "subject", "Lucene Ã¨ una API gratuita ed open source per il reperimento di informazioni inizialmente implementata in Java da Doug Cutting. Ã supportata dall'Apache Software Foundation ed Ã¨ resa disponibile con l'Apache License. Lucene Ã¨ stata successivamente reimplementata in Perl, C#, C++, Python, Ruby e PHP.");
     assertLang("pt", "id", "12pt", "name", "Lucene", "subject", "Apache Lucene, ou simplesmente Lucene, Ã© um software de busca e uma API de indexaÃ§Ã£o de documentos, escrito na linguagem de programaÃ§Ã£o Java. Ã um software de cÃ³digo aberto da Apache Software Foundation licenciado atravÃ©s da licenÃ§a Apache.");
     // New in Tika1.0
     assertLang("ca", "id", "13ca", "name", "Catalan", "subject", "El catalÃ  posseeix dos estÃ ndards principals: el regulat per l'Institut d'Estudis Catalans, o estÃ ndard general, que pren com a base l'ortografia establerta per Pompeu Fabra amb els trets gramaticals i ortogrÃ fics caracterÃ­stics del catalÃ  central; i el regulat per l'AcadÃ¨mia Valenciana de la Llengua, estÃ ndard d'Ã mbit restringit, centrat en l'estandarditzaciÃ³ del valenciÃ  i que pren com a base les Normes de CastellÃ³, Ã©s a dir, l'ortografia de Pompeu Fabra perÃ² mÃ©s adaptada a la pronÃºncia del catalÃ  occidental i als trets que caracteritzen els dialectes valencians.");
     assertLang("be", "id", "14be", "name", "Belarusian", "subject", "ÐÐ°ÑÑÑÐ¿Ð½Ð°Ð¹ Ð±ÑÐ¹Ð½Ð¾Ð¹ Ð´Ð·ÑÑÐ¶Ð°Ð²Ð°Ð¹ Ð½Ð° Ð±ÐµÐ»Ð°ÑÑÑÐºÐ°Ð¹ Ð·ÑÐ¼Ð»Ñ Ð±ÑÐ»Ð¾ ÐÑÐ»ÑÐºÐ°Ðµ ÐºÐ½ÑÑÑÐ²Ð° ÐÑÑÐ¾ÑÑÐºÐ°Ðµ, Ð ÑÑÐºÐ°Ðµ Ñ ÐÐ°Ð¼Ð¾Ð¹ÑÐºÐ°Ðµ (ÐÐÐ). ÐÐ°Ð´ÑÐ°Ñ ÑÑÐ²Ð°ÑÑÐ½Ð½Ñ Ñ Ð¿Ð°ÑÐ°ÑÐºÐ¾Ð²Ð°Ð³Ð° ÑÐ°Ð·Ð²ÑÑÑÑ Ð³ÑÑÐ°Ð¹ Ð´Ð·ÑÑÐ¶Ð°Ð²Ñ Ð½Ð°Ð¹Ð±ÑÐ¹Ð½ÐµÐ¹ÑÑÐ¼ Ñ Ð°ÑÐ½Ð¾ÑÐ½ÑÐ¼ ÑÐµ ÑÑÐ½ÑÑÐ°Ð¼ Ð±ÑÑ ÐÐ¾Ð²Ð°Ð³Ð°ÑÐ¾Ð´Ð°Ðº. ÐÐºÑÐ°Ð¼Ñ ÑÑÑÐ°ÑÐ½ÑÑ Ð·ÐµÐ¼Ð»ÑÑ ÐÐµÐ»Ð°ÑÑÑÑ, Ñ ÑÐºÐ»Ð°Ð´ Ð³ÑÑÐ°Ð¹ Ð´Ð·ÑÑÐ¶Ð°Ð²Ñ ÑÐ²Ð°ÑÐ¾Ð´Ð·ÑÐ»Ñ ÑÐ°ÐºÑÐ°Ð¼Ð° Ð·ÐµÐ¼Ð»Ñ ÑÑÑÐ°ÑÐ½Ð°Ð¹ ÐÑÑÐ²Ñ, Ð¿Ð°ÑÐ½Ð¾ÑÐ½Ð°Ñ ÑÐ°ÑÑÐºÐ° ÑÑÑÐ°ÑÐ½Ð°Ð¹ Ð£ÐºÑÐ°ÑÐ½Ñ Ñ ÑÐ°ÑÑÐºÐ° ÑÑÑÐ°ÑÐ½Ð°Ð¹ Ð Ð°ÑÑÑ.");
     assertLang("eo", "id", "15eo", "name", "Esperanto", "subject", "La vortprovizo de Esperanto devenas plejparte el la okcidenteÅ­ropaj lingvoj, dum Äia sintakso kaj morfologio montras ankaÅ­ slavlingvan influon. La morfemoj ne ÅanÄiÄas kaj oni povas ilin preskaÅ­ senlime kombini, kreante diverssignifajn vortojn, Esperanto do havas multajn kunaÄµojn kun la analizaj lingvoj, al kiuj apartenas ekzemple la Äina; kontraÅ­e la interna strukturo de Esperanto certagrade respegulas la aglutinajn lingvojn, kiel la japanan, svahilan aÅ­ turkan.");
     assertLang("gl", "id", "16gl", "name", "Galician", "subject", "A cifra de falantes medrou axiÃ±a durante as dÃ©cadas seguintes, nun principio no Imperio ruso e na Europa oriental, logo na Europa occidental, AmÃ©rica, China e no XapÃ³n. Nos primeiros anos do movemento, os esperantistas mantiÃ±an contacto por correspondencia, pero en 1905 o primeiro Congreso Universal de Esperanto levouse a cabo na cidade francesa de Boulogne-sur-Mer. Dende entÃ³n, os congresos mundiais organizÃ¡ronse nos cinco continentes ano tras ano agÃ¡s durante as dÃºas Guerras Mundiais.");
     assertLang("ro", "id", "17ro", "name", "Romanian", "subject", "La momentul destrÄmÄrii Uniunii Sovietice Èi a Ã®nlÄturÄrii regimului comunist instalat Ã®n RomÃ¢nia (1989), Èara a iniÈiat o serie de reforme economice Èi politice. DupÄ un deceniu de probleme economice, RomÃ¢nia a introdus noi reforme economice de ordin general (precum cota unicÄ de impozitare, Ã®n 2005) Èi a aderat la Uniunea EuropeanÄ la 1 ianuarie 2007.");
     assertLang("sk", "id", "18sk", "name", "Slovakian", "subject", "Boli vytvorenÃ© dva nÃ¡rodnÃ© parlamenty - ÄeskÃ¡ nÃ¡rodnÃ¡ rada a SlovenskÃ¡ nÃ¡rodnÃ¡ rada a spoloÄnÃ½ jednokomorovÃ½ Äesko-slovenskÃ½ parlament bol premenovanÃ½ z NÃ¡rodnÃ©ho zhromaÅ¾denia na FederÃ¡lne zhromaÅ¾denie s dvoma komorami - SnemovÅou Ä¾udu a SnemovÅu nÃ¡rodov.");
     assertLang("sl", "id", "19sl", "name", "Slovenian", "subject", "Slovenska Wikipedija je razliÄica spletne enciklopedije Wikipedije v slovenskem jeziku. Projekt slovenske Wikipedije se je zaÄel 26. februarja 2002 z ustanovitvijo njene spletne strani, njen pobudnik pa je bil uporabnik Jani Melik.");
     assertLang("uk", "id", "20uk", "name", "Ukrainian", "subject", "ÐÐ°ÑÐ¾Ð´Ð½Ð¾-Ð³Ð¾ÑÐ¿Ð¾Ð´Ð°ÑÑÑÐºÐ¸Ð¹ ÐºÐ¾Ð¼Ð¿Ð»ÐµÐºÑ ÐºÑÐ°ÑÐ½Ð¸ Ð²ÐºÐ»ÑÑÐ°Ñ ÑÐ°ÐºÑ Ð²Ð¸Ð´Ð¸ Ð¿ÑÐ¾Ð¼Ð¸ÑÐ»Ð¾Ð²Ð¾ÑÑÑ ÑÐº Ð²Ð°Ð¶ÐºÐµ Ð¼Ð°ÑÐ¸Ð½Ð¾Ð±ÑÐ´ÑÐ²Ð°Ð½Ð½Ñ, ÑÐ¾ÑÐ½Ð° ÑÐ° ÐºÐ¾Ð»ÑÐ¾ÑÐ¾Ð²Ð° Ð¼ÐµÑÐ°Ð»ÑÑÐ³ÑÑ, ÑÑÐ´Ð½Ð¾Ð±ÑÐ´ÑÐ²Ð°Ð½Ð½Ñ, Ð²Ð¸ÑÐ¾Ð±Ð½Ð¸ÑÑÐ²Ð¾ Ð°Ð²ÑÐ¾Ð±ÑÑÑÐ², Ð»ÐµÐ³ÐºÐ¾Ð²Ð¸Ñ ÑÐ° Ð²Ð°Ð½ÑÐ°Ð¶Ð½Ð¸Ñ Ð°Ð²ÑÐ¾Ð¼Ð¾Ð±ÑÐ»ÑÐ², ÑÑÐ°ÐºÑÐ¾ÑÑÐ² ÑÐ° ÑÐ½ÑÐ¾Ñ ÑÑÐ»ÑÑÑÐºÐ¾Ð³Ð¾ÑÐ¿Ð¾Ð´Ð°ÑÑÑÐºÐ¾Ñ ÑÐµÑÐ½ÑÐºÐ¸, ÑÐµÐ¿Ð»Ð¾Ð²Ð¾Ð·ÑÐ², Ð²ÐµÑÑÑÐ°ÑÑÐ², ÑÑÑÐ±ÑÐ½, Ð°Ð²ÑÐ°ÑÑÐ¹Ð½Ð¸Ñ Ð´Ð²Ð¸Ð³ÑÐ½ÑÐ² ÑÐ° Ð»ÑÑÐ°ÐºÑÐ², Ð¾Ð±Ð»Ð°Ð´Ð½Ð°Ð½Ð½Ñ Ð´Ð»Ñ ÐµÐ»ÐµÐºÑÑÐ¾ÑÑÐ°Ð½ÑÑÐ¹, Ð½Ð°ÑÑÐ¾-Ð³Ð°Ð·Ð¾Ð²Ð¾Ñ ÑÐ° ÑÑÐ¼ÑÑÐ½Ð¾Ñ Ð¿ÑÐ¾Ð¼Ð¸ÑÐ»Ð¾Ð²Ð¾ÑÑÑ ÑÐ¾ÑÐ¾. ÐÑÑÐ¼ ÑÐ¾Ð³Ð¾, Ð£ÐºÑÐ°ÑÐ½Ð° Ñ Ð¿Ð¾ÑÑÐ¶Ð½Ð¸Ð¼ Ð²Ð¸ÑÐ¾Ð±Ð½Ð¸ÐºÐ¾Ð¼ ÐµÐ»ÐµÐºÑÑÐ¾ÐµÐ½ÐµÑÐ³ÑÑ. Ð£ÐºÑÐ°ÑÐ½Ð° Ð¼Ð°Ñ ÑÐ¾Ð·Ð²Ð¸Ð½ÑÑÐµ ÑÑÐ»ÑÑÑÐºÐµ Ð³Ð¾ÑÐ¿Ð¾Ð´Ð°ÑÑÑÐ²Ð¾ Ñ Ð·Ð°Ð¹Ð¼Ð°Ñ Ð¾Ð´Ð½Ðµ Ð· Ð¿ÑÐ¾Ð²ÑÐ´Ð½Ð¸Ñ Ð¼ÑÑÑÑ ÑÐµÑÐµÐ´ ÐµÐºÑÐ¿Ð¾ÑÑÐµÑÑÐ² Ð´ÐµÑÐºÐ¸Ñ Ð²Ð¸Ð´ÑÐ² ÑÑÐ»ÑÑÑÐºÐ¾Ð³Ð¾ÑÐ¿Ð¾Ð´Ð°ÑÑÑÐºÐ¾Ñ Ð¿ÑÐ¾Ð´ÑÐºÑÑÑ Ñ Ð¿ÑÐ¾Ð´Ð¾Ð²Ð¾Ð»ÑÑÑÐ²Ð° (Ð·Ð¾ÐºÑÐµÐ¼Ð°, ÑÐ¾Ð½ÑÑÐ½Ð¸ÐºÐ¾Ð²Ð¾Ñ Ð¾Ð»ÑÑ).");
   }
     
   @Test
   public void testMapFieldName() throws Exception {
     parameters = new ModifiableSolrParams();
     parameters.add("langid.fl", "name");
     parameters.add("langid.map.lcmap", "jp:s zh:cjk ko:cjk");
     parameters.set("langid.enforceSchema", "false");
     liProcessor = createLangIdProcessor(parameters);
     
     assertEquals("test_no", liProcessor.getMappedField("test", "no"));
     assertEquals("test_en", liProcessor.getMappedField("test", "en"));
     assertEquals("test_s", liProcessor.getMappedField("test", "jp"));
     assertEquals("test_cjk", liProcessor.getMappedField("test", "zh"));
     assertEquals("test_cjk", liProcessor.getMappedField("test", "ko"));
 
     // Test that enforceSchema correctly catches illegal field and returns null
     parameters.set("langid.enforceSchema", "true");
     liProcessor = createLangIdProcessor(parameters);
     assertEquals(null, liProcessor.getMappedField("inputfield", "sv"));
 
     // Prove support for other mapping regex, still with enforceSchema=true
     parameters.add("langid.map.pattern", "text_(.*?)_field");
     parameters.add("langid.map.replace", "$1_{lang}_s");
     liProcessor = createLangIdProcessor(parameters);
     assertEquals("title_no_s", liProcessor.getMappedField("text_title_field", "no"));
     assertEquals("body_sv_s", liProcessor.getMappedField("text_body_field", "sv"));
   }
 
   @Test
   public void testMapLangcode() throws Exception {
     parameters = new ModifiableSolrParams();
     parameters.add("langid.fl", "name");
     parameters.add("langid.lcmap", "zh_cn:zh zh_tw:zh");
     parameters.set("langid.enforceSchema", "false");
     liProcessor = createLangIdProcessor(parameters);
 
     assertEquals("zh", liProcessor.resolveLanguage("zh_cn", "NA"));
     assertEquals("zh", liProcessor.resolveLanguage("zh_tw", "NA"));
     assertEquals("no", liProcessor.resolveLanguage("no", "NA"));
     List<DetectedLanguage> langs = new ArrayList<DetectedLanguage>();
     langs.add(new DetectedLanguage("zh_cn", 0.8));
     assertEquals("zh", liProcessor.resolveLanguage(langs, "NA"));
   }
 
   @Test
   public void testPreExisting() throws Exception {
     SolrInputDocument doc;
     parameters = new ModifiableSolrParams();
     parameters.add("langid.fl", "text");
     parameters.add("langid.langField", "language");
     parameters.add("langid.langsField", "languages");
     parameters.add("langid.enforceSchema", "false");
     parameters.add("langid.map", "true");
     liProcessor = createLangIdProcessor(parameters);
     
     doc = englishDoc();
     assertEquals("en", liProcessor.process(doc).getFieldValue("language"));
     assertEquals("en", liProcessor.process(doc).getFieldValue("languages"));
     
     doc = englishDoc();
     doc.setField("language", "no");
     assertEquals("no", liProcessor.process(doc).getFieldValue("language"));
     assertEquals("no", liProcessor.process(doc).getFieldValue("languages"));
     assertNotNull(liProcessor.process(doc).getFieldValue("text_no"));
   }
 
  /**
   * Test not only 1st value taken into account (empty string),
   * but all other values of 'text_multivalue' field ('en').
   */
  @Test
  public void testPreExistingMultiValue() throws Exception {
    SolrInputDocument doc;
    parameters = new ModifiableSolrParams();
    parameters.add("langid.fl", "text_multivalue");
    parameters.add("langid.langField", "language");
    parameters.add("langid.langsField", "languages");
    parameters.add("langid.enforceSchema", "false");
    parameters.add("langid.map", "true");
    liProcessor = createLangIdProcessor(parameters);
    
    doc = englishDoc();
    assertEquals("en", liProcessor.process(doc).getFieldValue("language"));
    assertEquals("en", liProcessor.process(doc).getFieldValue("languages"));
    
    doc = englishDoc();
    doc.setField("language", "no");
    assertEquals("no", liProcessor.process(doc).getFieldValue("language"));
    assertEquals("no", liProcessor.process(doc).getFieldValue("languages"));
    assertNotNull(liProcessor.process(doc).getFieldValue("text_multivalue_no"));
  }

  /**
   * Test not only 1st value taken into account (ru text),
   * but all values of 'text_multivalue' field ('ru' and 'en').
   */
  @Test
  public void testPreExistingMultiValueMixedLang() throws Exception {
    SolrInputDocument doc;
    parameters = new ModifiableSolrParams();
    parameters.add("langid.fl", "text_multivalue");
    parameters.add("langid.langField", "language");
    parameters.add("langid.langsField", "languages");
    parameters.add("langid.enforceSchema", "false");
    parameters.add("langid.map", "true");
    liProcessor = createLangIdProcessor(parameters);

    doc = mixedEnglishRussianDoc();
    assertEquals("en", liProcessor.process(doc).getFieldValue("language"));
    assertEquals("en", liProcessor.process(doc).getFieldValue("languages"));

    doc = mixedEnglishRussianDoc();
    doc.setField("language", "no");
    assertEquals("no", liProcessor.process(doc).getFieldValue("language"));
    assertEquals("no", liProcessor.process(doc).getFieldValue("languages"));
    assertNotNull(liProcessor.process(doc).getFieldValue("text_multivalue_no"));
  }

   @Test
   public void testDefaultFallbackEmptyString() throws Exception {
     SolrInputDocument doc;
     parameters = new ModifiableSolrParams();
     parameters.add("langid.fl", "text");
     parameters.add("langid.langField", "language");
     parameters.add("langid.enforceSchema", "false");
     liProcessor = createLangIdProcessor(parameters);
     
     doc = tooShortDoc();
     assertEquals("", liProcessor.process(doc).getFieldValue("language"));
   }
 
   @Test
   public void testFallback() throws Exception {
     SolrInputDocument doc;
     parameters = new ModifiableSolrParams();
     parameters.add("langid.fl", "text");
     parameters.add("langid.langField", "language");
     parameters.add("langid.fallbackFields", "noop,fb");
     parameters.add("langid.fallback", "fbVal");
     parameters.add("langid.enforceSchema", "false");
     liProcessor = createLangIdProcessor(parameters);
       
     // Verify fallback to field fb (noop field does not exist and is skipped)
     doc = tooShortDoc();
     doc.addField("fb", "fbField");
     assertEquals("fbField", liProcessor.process(doc).getFieldValue("language"));
 
     // Verify fallback to fallback value since no fallback fields exist
     doc = tooShortDoc();
     assertEquals("fbVal", liProcessor.process(doc).getFieldValue("language"));  
   }
   
   @Test
   public void testResolveLanguage() throws Exception {
     List<DetectedLanguage> langs;
     parameters = new ModifiableSolrParams();
     parameters.add("langid.fl", "text");
     parameters.add("langid.langField", "language");
     liProcessor = createLangIdProcessor(parameters);
 
     // No detected languages
     langs = new ArrayList<DetectedLanguage>();
     assertEquals("", liProcessor.resolveLanguage(langs, null));
     assertEquals("fallback", liProcessor.resolveLanguage(langs, "fallback"));
 
     // One detected language
     langs.add(new DetectedLanguage("one", 1.0));
     assertEquals("one", liProcessor.resolveLanguage(langs, "fallback"));    
 
     // One detected language under default threshold
     langs = new ArrayList<DetectedLanguage>();
     langs.add(new DetectedLanguage("under", 0.1));
     assertEquals("fallback", liProcessor.resolveLanguage(langs, "fallback"));    
   }
   
   
   // Various utility methods
   
   private SolrInputDocument englishDoc() {
     SolrInputDocument doc = new SolrInputDocument();
     doc.addField("text", "Apache Lucene is a free/open source information retrieval software library, originally created in Java by Doug Cutting. It is supported by the Apache Software Foundation and is released under the Apache Software License.");
    doc.addField("text_multivalue", new String[]{"", "Apache Lucene is a free/open source information retrieval software library, originally created in Java by Doug Cutting. It is supported by the Apache Software Foundation and is released under the Apache Software License."});
    return doc;
  }

  /**
   * Construct document containing multi-value fields in different languages.
   * @return solr input document
   */
  private SolrInputDocument mixedEnglishRussianDoc() {
    SolrInputDocument doc = new SolrInputDocument();
    doc.addField("text_multivalue", new String[]{"The Apache Lucene â ÑÑÐ¾ ÑÐ²Ð¾Ð±Ð¾Ð´Ð½Ð°Ñ Ð±Ð¸Ð±Ð»Ð¸Ð¾ÑÐµÐºÐ° Ð´Ð»Ñ Ð²ÑÑÐ¾ÐºÐ¾ÑÐºÐ¾ÑÐ¾ÑÑÐ½Ð¾Ð³Ð¾ Ð¿Ð¾Ð»Ð½Ð¾ÑÐµÐºÑÑÐ¾Ð²Ð¾Ð³Ð¾ Ð¿Ð¾Ð¸ÑÐºÐ°, Ð½Ð°Ð¿Ð¸ÑÐ°Ð½Ð½Ð°Ñ Ð½Ð° Java. ÐÐ¾Ð¶ÐµÑ Ð±ÑÑÑ Ð¸ÑÐ¿Ð¾Ð»ÑÐ·Ð¾Ð²Ð°Ð½Ð° Ð´Ð»Ñ Ð¿Ð¾Ð¸ÑÐºÐ° Ð² Ð¸Ð½ÑÐµÑÐ½ÐµÑÐµ Ð¸ Ð´ÑÑÐ³Ð¸Ñ Ð¾Ð±Ð»Ð°ÑÑÑÑ ÐºÐ¾Ð¼Ð¿ÑÑÑÐµÑÐ½Ð¾Ð¹ Ð»Ð¸Ð½Ð³Ð²Ð¸ÑÑÐ¸ÐºÐ¸ (Ð°Ð½Ð°Ð»Ð¸ÑÐ¸ÑÐµÑÐºÐ°Ñ ÑÐ¸Ð»Ð¾ÑÐ¾ÑÐ¸Ñ).",
                                                 "Apache Lucene is a free/open source information retrieval software library, originally created in Java by Doug Cutting. It is supported by the Apache Software Foundation and is released under the Apache Software License.",
        "Solr (pronounced \"solar\") is an open source enterprise search platform from the Apache Lucene project. Its major features include full-text search, hit highlighting, faceted search, dynamic clustering, database integration, and rich document (e.g., Word, PDF) handling."
    });
     return doc;
   }
 
   protected SolrInputDocument tooShortDoc() {
     SolrInputDocument doc = new SolrInputDocument();
     doc.addField("text", "This text is too short");
     return doc;
   }
 
   protected abstract LanguageIdentifierUpdateProcessor createLangIdProcessor(ModifiableSolrParams parameters) throws Exception;
 
   protected void assertLang(String langCode, String... fieldsAndValues) throws Exception {
     if(liProcessor == null)
       throw new Exception("Processor must be initialized before calling assertLang()");
     SolrInputDocument doc = sid(fieldsAndValues);
     assertEquals(langCode, liProcessor.process(doc).getFieldValue(liProcessor.langField));
   }
   
   private SolrInputDocument sid(String... fieldsAndValues) {
     SolrInputDocument doc = new SolrInputDocument();
     for (int i = 0; i < fieldsAndValues.length; i+=2) {
       doc.addField(fieldsAndValues[i], fieldsAndValues[i+1]);
     }
     return doc;
   }
 }
