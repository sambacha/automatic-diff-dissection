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
 
 import java.util.List;
 
 import org.apache.lucene.analysis.kr.morph.AnalysisOutput;
 import org.apache.lucene.analysis.kr.morph.MorphException;
 import org.apache.lucene.analysis.kr.morph.PatternConstants;
 import org.apache.lucene.analysis.kr.morph.WordEntry;
 
 public class MorphUtil {
 
   private static final char[] CHOSEONG = {
       'ã±','ã²','ã´','ã·','ã¸','ã¹','ã','ã','ã','ã',
       'ã','ã','ã','ã','ã','ã','ã','ã','ã'
   };
 
   private static final char[] JUNGSEONG = {
       'ã','ã','ã','ã','ã','ã','ã','ã','ã','ã',
       'ã','ã','ã','ã','ã','ã','ã','ã ','ã¡','ã¢',
       'ã£'
   };
   
   private static final char[] JONGSEONG = {
       '\0','ã±','ã²','ã³','ã´','ãµ','ã¶','ã·','ã¹','ãº',
       'ã»','ã¼','ã½','ã¾','ã¿','ã','ã','ã','ã','ã',
       'ã','ã','ã','ã','ã','ã','ã','ã'
   };
   
   private static final int JUNG_JONG = JUNGSEONG.length * JONGSEONG.length;
 
   
   /**
    * íê¸ íê¸ìë¥¼ ì´ì±/ì¤ì±/ì¢ì±ì ë°°ì´ë¡ ë§ë¤ì´ ë°ííë¤.
    * @param c
    */
   public static char[] decompose(char c) {
     char[] result = null;
 
     if(c>0xD7A3||c<0xAC00) return new char[]{c};
     
     c -= 0xAC00;
 
     char choseong = CHOSEONG[c/JUNG_JONG];
     c = (char)(c % JUNG_JONG);
     
     char jungseong = JUNGSEONG[c/JONGSEONG.length];
     
     char jongseong = JONGSEONG[c%JONGSEONG.length];
     
     if(jongseong != 0) {
       result = new char[] {choseong, jungseong, jongseong};
     }else {
       result = new char[] {choseong, jungseong};      
     }
     return result;
   }  
   
   public static char compound(int first, int middle, int last) {    
     return (char)(0xAC00 + first* JUNG_JONG + middle * JONGSEONG.length + last);
   }
   
 
   public static char makeChar(char ch, int mdl, int last) {    
     ch -= 0xAC00;    
     int first = ch/JUNG_JONG;     
     return compound(first,mdl,last);
   }
   
   public static char makeChar(char ch, int last) {
     ch -= 0xAC00;    
     int first = ch/JUNG_JONG;  
     ch = (char)(ch % JUNG_JONG);
     int middle = ch/JONGSEONG.length;
     
     return compound(first,middle,last);    
   }
   
   public static char replaceJongsung(char dest, char source) {
     source -= 0xAC00;    
     int last = source % JONGSEONG.length;
       
     return makeChar(dest,last);  
   }
 
   /**
    * ííì ì í ì¶ë ¥ì ìí ë¬¸ìì´ì ìì±íë¤.
    * @param word
    * @param type
    */
   public static String buildTypeString(String word, char type) {
     StringBuffer sb = new StringBuffer();
     sb.append(word);
     sb.append("(");
     sb.append(type);
     sb.append(")");
     
     return sb.toString();
   }
   
   
   public static void buildPtnVM(AnalysisOutput output, List candidates) throws MorphException {
     
     String end = output.getEomi();
     if(output.getPomi()!=null) end = output.getPomi();
     
     output.setPatn(PatternConstants.PTN_VM);
     output.setPos(PatternConstants.POS_VERB);
     
     if(output.getScore()==AnalysisOutput.SCORE_CORRECT) {
       candidates.add(output);
     }else {
       String[] irrs = IrregularUtil.restoreIrregularVerb(output.getStem(),end);
       if(irrs!=null) {
         output.setScore(AnalysisOutput.SCORE_CORRECT);
         output.setStem(irrs[0]);
         candidates.add(output);  
       }
     }
     
   }
   
   /**
    * ì©ì¸ + 'ì/ê¸°' + 'ì´' + ì´ë¯¸, ì²´ì¸ + 'ìì/ë¶í°/ììë¶í°' + 'ì´' + ì´ë¯¸
    * @param output
    * @param candidates
    * @throws MorphException
    */
   public static void buildPtnCM(AnalysisOutput output, List candidates) throws MorphException {
     
     char ch = output.getStem().charAt(output.getStem().length()-2);
     char[] jasos = MorphUtil.decompose(ch);
     if(jasos.length==3||ch=='ê¸°') {
       buildPtnVMCM(output,candidates);      
     } else {
       
     }
   }
   
   private static void buildPtnVMCM(AnalysisOutput output, List candidates) throws MorphException {
     String stem = output.getStem();
   
     output.setPatn(PatternConstants.PTN_VMCM);
     output.setPos(PatternConstants.POS_VERB);
     
     char ch = stem.charAt(stem.length()-2);
     char[] jasos = MorphUtil.decompose(ch);
 
     if(ch=='ê¸°') {
       output.addElist("ê¸°");
       output.addElist("ì´");
       output.setStem(stem.substring(0,stem.length()-2));
       
       if(DictionaryUtil.getVerb(output.getStem())!=null)
         candidates.add(output);
     }else if(jasos[2]=='ã') {
       if(stem.length()>1) stem = stem.substring(0,stem.length()-2);
       stem += MorphUtil.makeChar(ch, 0);
       output.addElist("ã");
       output.addElist("ì´");
       output.setStem(stem);
 
       if(DictionaryUtil.getVerb(stem)!=null) 
         candidates.add(output);
       else {
         String[] morphs = IrregularUtil.restoreIrregularVerb(stem,"ã");
         if(morphs!=null) {
           output.setScore(AnalysisOutput.SCORE_CORRECT);
           output.setStem(morphs[0]);
           candidates.add(output);
         }
       }
     }
   }
 
   public static boolean hasVerbOnly(String input) throws MorphException {
     
     for(int i=input.length()-1;i>=0;i--) {
       char[] feature = SyllableUtil.getFeature(input.charAt(i));
       if(feature[SyllableUtil.IDX_WDSURF]=='1'&&input.length()>i) return true;
     }
     return false;
   }
   
   /**
    * ìì  ì ì´ë¯¸ë§ì ë§ë¤ì´ì ë°ííë¤.
    * @param preword  'ì' ëë 'ì´'
    * @param endword  ì´ë¯¸[ì ì´ë¯¸ë§ì í¬í¨]
    * @return 'ì' ëë 'ì'ì ë§ë¤ì´ì ë°ííë¤.
    */
   public static String makeTesnseEomi(String preword, String endword) {
 
     if(preword==null||preword.length()==0) return endword;
     if(endword==null||endword.length()==0) return preword;
 
     if(endword.charAt(0)=='ã') {
       return preword.substring(0,preword.length()-1)+
           makeChar(preword.charAt(preword.length()-1),20)+endword.substring(1,endword.length());    
     } else if(endword.charAt(0)=='ã´') {
       return preword.substring(0,preword.length()-1)+
           makeChar(preword.charAt(preword.length()-1),4)+endword.substring(1,endword.length());
     } else if(endword.charAt(0)=='ã¹') {
       return preword.substring(0,preword.length()-1)+
           makeChar(preword.charAt(preword.length()-1),8)+endword.substring(1,endword.length());  
     } else if(endword.charAt(0)=='ã') {
       return preword.substring(0,preword.length()-1)+
           makeChar(preword.charAt(preword.length()-1),16)+endword.substring(1,endword.length());          
     } else if(endword.charAt(0)=='ã') {
       return preword.substring(0,preword.length()-1)+
           makeChar(preword.charAt(preword.length()-1),17)+endword.substring(1,endword.length());
     }
     return preword+endword;    
   }
   
   /**
    * ì©ì¸íì ë¯¸ì¬ê° ê²°í©ë  ì ìëì§ ì¬ë¶ë¥¼ ì ê²íë¤.
    * í¹í ì¬ì ì ë±ë¡ë ëë¤, íë¤í ì ì ìì´ ê°ë¥íì§ë¥¼ ì¡°ì¬íë¤.
    */
   public static boolean isValidSuffix(WordEntry entry, AnalysisOutput o) {
     
     return true;
   }
 }
