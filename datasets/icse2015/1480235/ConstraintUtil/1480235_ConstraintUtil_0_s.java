 package org.apache.lucene.analysis.kr.utils;
 
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
 
 import java.util.HashMap;
 import java.util.Map;
 
 import org.apache.lucene.analysis.kr.morph.PatternConstants;
 
 /**
  * ê²°í©ì´ ê°ë¥í ì¡°ê±´ì ì²ë¦¬íë í´ëì¤
  */
 public class ConstraintUtil {
 
   private static Map hahes = new HashMap(); // "ê¸ë¡ë²íí´ ", "ë¯¼ì¡±íí´" ì²ë¼ íí´ì ê²°í©ì´ ê°ë¥í ëªì¬
   static {
     hahes.put("ë¯¼ì¡±", "Y");hahes.put("ëì", "Y");hahes.put("ë¨ë¶", "Y");
   }
   
   private static Map eomiPnouns = new HashMap(); 
   static {
     eomiPnouns.put("ã´", "Y");eomiPnouns.put("ã¹", "Y");eomiPnouns.put("ã", "Y");
   }
   
   private static Map PTN_MLIST= new HashMap();
   static {
     PTN_MLIST.put(PatternConstants.PTN_NSM, PatternConstants.PTN_NSM);
     PTN_MLIST.put(PatternConstants.PTN_NSMXM, PatternConstants.PTN_NSMXM);
     PTN_MLIST.put(PatternConstants.PTN_NJCM, PatternConstants.PTN_NJCM);
     PTN_MLIST.put(PatternConstants.PTN_VM, PatternConstants.PTN_VM);
     PTN_MLIST.put(PatternConstants.PTN_VMCM, PatternConstants.PTN_VMCM);
     PTN_MLIST.put(PatternConstants.PTN_VMXM, PatternConstants.PTN_VMXM);
     PTN_MLIST.put(PatternConstants.PTN_NVM, PatternConstants.PTN_NVM);
   }
   
   private static Map PTN_JLIST= new HashMap();
   static {
     PTN_JLIST.put(PatternConstants.PTN_NJ, PatternConstants.PTN_NJ);
     PTN_JLIST.put(PatternConstants.PTN_NSMJ, PatternConstants.PTN_NSMJ);
     PTN_JLIST.put(PatternConstants.PTN_VMJ, PatternConstants.PTN_VMJ);
     PTN_JLIST.put(PatternConstants.PTN_VMXMJ, PatternConstants.PTN_VMXMJ);
   }
   
   private static Map WORD_GUKS= new HashMap();
   static {
     WORD_GUKS.put("ë ê²", "Y");
     WORD_GUKS.put("ë¤ê²", "Y");
     WORD_GUKS.put("ë³ê²", "Y");
     WORD_GUKS.put("ì°°ê²", "Y");
     WORD_GUKS.put("íê²", "Y");
     WORD_GUKS.put("íìê²", "Y");
   }
   
   // ì¢ì±ì´ ìë ìì ê³¼ ì°ê²°ë  ì ìë ì¡°ì¬
   private static Map JOSA_TWO= new HashMap();
   static {
     JOSA_TWO.put("ê°", "Y");
     JOSA_TWO.put("ë", "Y");
     JOSA_TWO.put("ë¤", "Y");
     JOSA_TWO.put("ë", "Y");
     JOSA_TWO.put("ë", "Y");
     JOSA_TWO.put("ê³ ", "Y");
     JOSA_TWO.put("ë¼", "Y");
     JOSA_TWO.put("ì", "Y");
     JOSA_TWO.put("ë", "Y");
     JOSA_TWO.put("ë¥¼", "Y");
     JOSA_TWO.put("ë©°", "Y");
     JOSA_TWO.put("ë ", "Y");
     JOSA_TWO.put("ì¼", "Y");
     JOSA_TWO.put("ì¬", "Y");
   }
   
   // ì¢ì±ì´ ìë ìì ê³¼ ì°ê²°ë  ì ìë ì¡°ì¬
   private static Map JOSA_THREE= new HashMap();
   static {
     JOSA_THREE.put("ê³¼", "Y");
     JOSA_THREE.put("ì", "Y");
     JOSA_THREE.put("ì", "Y");
     JOSA_THREE.put("ì¼", "Y");
     JOSA_THREE.put("ì", "Y");
     JOSA_THREE.put("ì", "Y");
   }
   
   public static boolean canHaheCompound(String key) {
     if(hahes.get(key)!=null) return true;
     return false;
   }
     
   /**
    * ì´ë¯¸ê° ã´,ã¹,ã ì¼ë¡ ëëëì§ ì¡°ì¬íë¤.
   * @param eomi
   * @return
    */
   public static boolean isNLM(String eomi) {
     
     if(eomi==null || "".equals(eomi)) return false;
     
     if(eomiPnouns.get(eomi)!=null) return true;
     
     char[] chrs = MorphUtil.decompose(eomi.charAt(eomi.length()-1));
     if(chrs.length==3  && eomiPnouns.get(Character.toString(chrs[2]))!=null) return true;
     
     return true;
   }
   
   public static boolean isEomiPhrase(int ptn) {
     
     if(PTN_MLIST.get(ptn)!=null) return true;
     
     return false;
   }
   
   public static boolean isJosaNounPhrase(int ptn) {
     
     if(PTN_JLIST.get(ptn)!=null) return true;
     
     return false;
   }
   
   public static boolean isJosaAdvPhrase(int ptn) {
     
     if(PatternConstants.PTN_ADVJ==ptn) return true;
     
     return false;
   }
   
   public static boolean isAdvPhrase(int ptn) {
     
     if(PatternConstants.PTN_ADVJ==ptn || PatternConstants.PTN_AID==ptn) return true;
     
     return false;
   }
   
   public static boolean isTwoJosa(String josa) {
     
     return (JOSA_TWO.get(josa)!=null);
     
   }
   public static boolean isThreeJosa(String josa) {
     
     return (JOSA_THREE.get(josa)!=null);
   }  
 }
