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
 
 import java.util.ArrayList;
 import java.util.List;
 
 import org.apache.lucene.analysis.kr.morph.AnalysisOutput;
 import org.apache.lucene.analysis.kr.morph.MorphException;
 import org.apache.lucene.analysis.kr.morph.PatternConstants;
 
 public class EomiUtil {
   
   public static final String RESULT_FAIL = "0";
   
   public static final String RESULT_SUCCESS = "1";
   
   public static final String[] verbSuffix = {
       "ì´","í","ë","ì¤ë½","ì¤ë¬ì°","ìí¤","ì","ì","ê°","ë¹í","ë§í","ëë¦¬","ë°","ë","ë´"
   };
   
   /**
    * ê°ì¥ ê¸¸ì´ê° ê¸´ ì´ë¯¸ë¥¼ ë¶ë¦¬íë¤.
    * @param term
   * @return
    * @throws MorphException
    */
   public static String[] longestEomi(String term) throws MorphException  {
     
     String[] result = new String[2];
     result[0] = term;
     
     String stem;
     String eomi;
     char[] efeature;
     
     for(int i=term.length();i>0;i--) {
       
       stem = term.substring(0,i);      
     
       if(i!=term.length()) {
         eomi = term.substring(i);
         efeature  = SyllableUtil.getFeature(eomi.charAt(0));        
       } else {
         efeature = SyllableUtil.getFeature(stem.charAt(i-1));
         eomi="";
       }
 
       if(SyllableUtil.isAlpanumeric(stem.charAt(i-1))) break;
       
       char[] jasos = MorphUtil.decompose(stem.charAt(i-1));
   
       if(!"".equals(eomi)&&!DictionaryUtil.existEomi(eomi)) {
         // do not anything.
       } else if(jasos.length>2&&
           (jasos[2]=='ã´'||jasos[2]=='ã¹'||jasos[2]=='ã'||jasos[2]=='ã')&&
           DictionaryUtil.combineAndEomiCheck(jasos[2], eomi)!=null) {
         result[0] = Character.toString(MorphUtil.makeChar(stem.charAt(i-1), 0));
         if(i!=0) result[0] = stem.substring(0,i-1)+result[0];
         result[1] = Character.toString(jasos[2]);
       }else if(i>0&&(stem.endsWith("í")&&"ì¬".equals(eomi))||
           (stem.endsWith("ê°")&&"ê±°ë¼".equals(eomi))||
           (stem.endsWith("ì¤")&&"ëë¼".equals(eomi))) {
         result[0] = stem;
         result[1] = eomi;      
       }else if(jasos.length==2&&(!stem.endsWith("ì")&&!stem.endsWith("ì´"))&&
           (jasos[1]=='ã'||jasos[1]=='ã'||jasos[1]=='ã'||jasos[1]=='ã')&&
           (DictionaryUtil.combineAndEomiCheck('ì´', eomi)!=null)) {    
         char[] chs = MorphUtil.decompose(stem.charAt(stem.length()-1));        
         result[0] = stem;
         result[1] = "ì´"+eomi;
       }else if((jasos[1]=='ã'||jasos[1]=='ã'||jasos[1]=='ã'||jasos[1]=='ã'||jasos[1]=='ã')&&
           (DictionaryUtil.combineAndEomiCheck('ì´', eomi)!=null)) {        
         String end = "";        
         if(jasos[1]=='ã')
           end=MorphUtil.makeChar(stem.charAt(i-1), 8, 0)+"ì";  
         else if(jasos[1]=='ã')
           end=MorphUtil.makeChar(stem.charAt(i-1), 13, 0)+"ì´";  
         else if(jasos[1]=='ã')
           end=Character.toString(MorphUtil.makeChar(stem.charAt(i-1), 6, 0));
         else if(jasos[1]=='ã')
           end=MorphUtil.makeChar(stem.charAt(i-1), 0, 0)+"ì´";  
         else if(jasos[1]=='ã')
           end=MorphUtil.makeChar(stem.charAt(i-1), 20, 0)+"ì ";                    
         
         if(jasos.length==3) {          
           end = end.substring(0,end.length()-1)+MorphUtil.replaceJongsung(end.charAt(end.length()-1),stem.charAt(i-1));
         }
         
         if(stem.length()<2) result[0] = end;
         else result[0] = stem.substring(0,stem.length()-1)+end;
         result[1] = eomi;  
         
       }else if(efeature!=null&&efeature[SyllableUtil.IDX_EOMI1]!='0'&&
           DictionaryUtil.existEomi(eomi)) {
         if(!(((jasos.length==2&&jasos[0]=='ã¹')||(jasos.length==3&&jasos[2]=='ã¹'))&&eomi.equals("ë¬"))) { // ã¹ ë¶ê·ì¹ì ìì¸
           result[0] = stem;
           result[1] = eomi;
         }
       }
 
       if(efeature!=null&&efeature[SyllableUtil.IDX_EOMI2]=='0') break;
     }  
 
     return result;
     
   }  
   
   /**
    * ì ì´ë§ì´ë¯¸ë¥¼ ë¶ìíë¤.
   * @param stem
   * @return
    */
   public static String[] splitPomi(String stem) throws MorphException  {
 
     //   results[0]:ì±ê³µ(1)/ì¤í¨(0), results[1]: ì´ê·¼, results[2]: ì ì´ë§ì´ë¯¸
     String[] results = new String[2];  
     results[0] = stem;
 
     if(stem==null||stem.length()==0||"ì".equals(stem)) return results;
   
     char[] chrs = stem.toCharArray();
     int len = chrs.length;
     String pomi = "";
     int index = len-1;
   
     char[] jaso = MorphUtil.decompose(chrs[index]);
     if(chrs[index]!='ì'&&chrs[index]!='ã'&&jaso[jaso.length-1]!='ã') return results;  // ì ì´ë§ì´ë¯¸ê° ë°ê²¬ëì§ ììë¤
     
     if(chrs[index]=='ê² ') {
       pomi = "ê² ";
       setPomiResult(results,stem.substring(0,index),pomi);    
       if(--index<=0||
           (chrs[index]!='ì'&&chrs[index]!='ã'&&jaso[jaso.length-1]!='ã')) 
         return results; // ë¤ìì´ê±°ë ì ì´ë§ì´ë¯¸ê° ìë¤ë©´...
       jaso = MorphUtil.decompose(chrs[index]);
     }
 
     if(chrs[index]=='ì') { // ìì, ãì, ì
       pomi = chrs[index]+pomi;  
       setPomiResult(results,stem.substring(0,index),pomi);    
       if(--index<=0||
           (chrs[index]!='ì'&&chrs[index]!='ã'&&jaso[jaso.length-1]!='ã')) 
         return results; // ë¤ìì´ê±°ë ì ì´ë§ì´ë¯¸ê° ìë¤ë©´...        
       jaso = MorphUtil.decompose(chrs[index]);
     }
 
     if(chrs[index]=='ì'){
       pomi = MorphUtil.replaceJongsung('ì´',chrs[index])+pomi;  
       if(index>0&&chrs[index-1]=='í') 
         stem = stem.substring(0,index);  
       else
         stem = stem.substring(0,index)+"ì´";
       setPomiResult(results,stem,pomi);  
     }else if(chrs[index]=='ì¨'){
       pomi = MorphUtil.replaceJongsung('ì´',chrs[index])+pomi;  
       stem = stem.substring(0,index);    
       setPomiResult(results,stem,"ì"+pomi);        
     }else if(chrs[index]=='ì'||chrs[index]=='ì') {
       pomi = chrs[index]+pomi;  
       setPomiResult(results,stem.substring(0,index),pomi);    
       if(--index<=0||
           (chrs[index]!='ì'&&chrs[index]!='ì¼')) return results; // ë¤ìì´ê±°ë ì ì´ë§ì´ë¯¸ê° ìë¤ë©´...        
       jaso = MorphUtil.decompose(chrs[index]);    
     }else if(jaso.length==3&&jaso[2]=='ã') {
     
       if(jaso[0]=='ã'&&jaso[1]=='ã') {       
         pomi = MorphUtil.replaceJongsung('ì´',chrs[index])+pomi;  
         stem = stem.substring(0,index)+"í";  
       }else if(jaso[0]!='ã'&&(jaso[1]=='ã'||jaso[1]=='ã'||jaso[1]=='ã'||jaso[1]=='ã')) {    
         pomi = "ì"+pomi;
         stem = stem.substring(0,index)+MorphUtil.makeChar(chrs[index], 0);        
       }else if(jaso[0]!='ã'&&(jaso[1]=='ã')) {
         pomi = "ì"+pomi;
         stem = stem.substring(0,index)+MorphUtil.makeChar(chrs[index],11, 0);        
       } else if(jaso[1]=='ã') {      
         pomi = MorphUtil.replaceJongsung('ì',chrs[index])+pomi;  
         stem = stem.substring(0,index)+MorphUtil.makeChar(chrs[index],8, 0);
       } else if(jaso[1]=='ã') {
         pomi = MorphUtil.replaceJongsung('ì´',chrs[index])+pomi;  
         stem = stem.substring(0,index)+MorphUtil.makeChar(chrs[index],13, 0);
       } else if(jaso[1]=='ã') {          
         pomi = MorphUtil.replaceJongsung('ì´',chrs[index])+pomi;        
         stem = stem.substring(0,index)+MorphUtil.makeChar(chrs[index],20, 0);          
       } else if(jaso[1]=='ã') {
         pomi = MorphUtil.replaceJongsung('ì´',chrs[index])+pomi;
         stem = stem.substring(0,index);
       } else if(jaso[1]=='ã') {
         pomi = MorphUtil.replaceJongsung('ì ',chrs[index])+pomi;  
         stem = stem.substring(0,index);
       } else {
         pomi = "ì"+pomi;
       }
       setPomiResult(results,stem,pomi);        
       if(chrs[index]!='ì'&&chrs[index]!='ì¼') return results; // ë¤ìì´ê±°ë ì ì´ë§ì´ë¯¸ê° ìë¤ë©´...        
       jaso = MorphUtil.decompose(chrs[index]);        
     }
 
     char[] nChrs = null;
     if(index>0) nChrs = MorphUtil.decompose(chrs[index-1]);
     else nChrs = new char[2];
 
     if(nChrs.length==2&&chrs[index]=='ì'&&(chrs.length<=index+1||
         (chrs.length>index+1&&chrs[index+1]!='ì¨'))) {
       if(DictionaryUtil.getWord(results[0])!=null) return results;  //'ì'ê° í¬í¨ë ë¨ì´ê° ìë¤. ì±ê°ìë¤/ëìë¤/ë¤ì¤ìë¤ 
       pomi = chrs[index]+pomi;  
       setPomiResult(results,stem.substring(0,index),pomi);      
       if(--index==0||chrs[index]!='ì¼') return results; // ë¤ìì´ê±°ë ì ì´ë§ì´ë¯¸ê° ìë¤ë©´...        
       jaso = MorphUtil.decompose(chrs[index]);
     }
     
     if(index>0) nChrs = MorphUtil.decompose(chrs[index-1]);
     else nChrs = new char[2];
     if(chrs.length>index+1&&nChrs.length==3&&(chrs[index+1]=='ì¨'||chrs[index+1]=='ì')&&chrs[index]=='ì¼') {
       pomi = chrs[index]+pomi;  
       setPomiResult(results,stem.substring(0,index),pomi);    
     }
   
     return results;
   }
   
   /**
    * ë¶ê·ì¹ ì©ì¸ì ìíì êµ¬íë¤.
   * @param output
   * @return
   * @throws MorphException
    */
   public static List irregular(AnalysisOutput output) throws MorphException {
     
     List results = new ArrayList();
   
     if(output.getStem()==null||output.getStem().length()==0) 
       return results;    
     
     String ending = output.getEomi();
     if(output.getPomi()!=null) ending = output.getPomi();
     
     List<String[]> irrs = new ArrayList();
     
     irregularStem(irrs,output.getStem(),ending);
     irregularEnding(irrs,output.getStem(),ending);
     irregularAO(irrs,output.getStem(),ending);
 
     try {
       for(String[] irr: irrs) {
         AnalysisOutput result = output.clone();
         result.setStem(irr[0]);
         if(output.getPatn()==PatternConstants.PTN_VM) {
           if(output.getPomi()==null) result.setEomi(irr[1]);
           else result.setPomi(irr[1]);
         }  
         results.add(result);
       }        
     } catch (CloneNotSupportedException e) {
       throw new MorphException(e.getMessage(),e);
     }
         
     return results;
     
   }
   
   /**
    * ì´ê°ë§ ë³íë ê²½ì°
    * @param results
    * @param stem
    * @param ending
    */
   private static void irregularStem(List results, String stem, String ending) {  
 
     char feCh = ending.charAt(0);
     char[] fechJaso =  MorphUtil.decompose(feCh);
     char ls = stem.charAt(stem.length()-1);
     char[] lsJaso = MorphUtil.decompose(ls);
   
     if(feCh=='ì'||feCh=='ì´'||feCh=='ì¼') {
       if(lsJaso[lsJaso.length-1]=='ã¹') { // ã· ë¶ê·ì¹
         results.add(
             new String[]{stem.substring(0,stem.length()-1)+
                 MorphUtil.makeChar(stem.charAt(stem.length()-1),7)
                 ,ending
                 ,String.valueOf(PatternConstants.IRR_TYPE_DI)});
       } else if(lsJaso.length==2) { // ã ë¶ê·ì¹
         results.add(
             new String[]{stem.substring(0,stem.length()-1)+
                 MorphUtil.makeChar(stem.charAt(stem.length()-1),19)
                 ,ending
                 ,String.valueOf(PatternConstants.IRR_TYPE_SI)});        
       }      
     }
     
     if((fechJaso[0]=='ã´'||fechJaso[0]=='ã¹'||fechJaso[0]=='ã'||  feCh=='ì¤'||feCh=='ì')
         &&(ls=='ì°')) { // ã ë¶ê·ì¹
       results.add(
           new String[]{stem.substring(0,stem.length()-1)+
               MorphUtil.makeChar(stem.charAt(stem.length()-1),17)
               ,ending
               ,String.valueOf(PatternConstants.IRR_TYPE_BI)});        
     }
     
     if((fechJaso[0]=='ã´'||fechJaso[0]=='ã'||fechJaso[0]=='ã'||  feCh=='ì¤')
         &&(lsJaso.length==2)) { // ã¹ íë½
 
       results.add(
           new String[]{stem.substring(0,stem.length()-1)+
               MorphUtil.makeChar(stem.charAt(stem.length()-1),8)
               ,ending
               ,String.valueOf(PatternConstants.IRR_TYPE_LI)});      
     }
     
     if(lsJaso.length==2
         &&(fechJaso[0]=='ã´'||fechJaso[0]=='ã¹'||fechJaso[0]=='ã'||fechJaso[0]=='ã'||
         lsJaso[1]=='ã'||lsJaso[1]=='ã'||lsJaso[1]=='ã'||lsJaso[1]=='ã')
         &&!"ë".equals(stem)) { // ã ë¶ê·ì¹, ê·¸ë¬ë [ë³ë¤]ë ã ë¶ê·ì¹ì´ ìëë¤.
       results.add(
           new String[]{stem.substring(0,stem.length()-1)+
               MorphUtil.makeChar(stem.charAt(stem.length()-1),27)
               ,ending
               ,String.valueOf(PatternConstants.IRR_TYPE_HI)});      
     }    
   }
   
   /**
    * ì´ë¯¸ë§ ë³íë ê²½ì°
    * @param results
    * @param stem
    * @param ending
    */
   private static void irregularEnding(List results, String stem, String ending) {
     if(ending.startsWith("ã")) return;
     
     char feCh = ending.charAt(0);
     char ls = stem.charAt(stem.length()-1);
 
     if(feCh=='ë¬'&&ls=='ë¥´') { // 'ë¬' ë¶ê·ì¹
       results.add(
           new String[]{stem
               ,"ì´"+ending.substring(1)
               ,String.valueOf(PatternConstants.IRR_TYPE_RO)});        
     } else if("ë¼".equals(ending)&&"ê°ê±°".equals(stem)) { // 'ê±°ë¼' ë¶ê·ì¹
       results.add( 
           new String[]{stem.substring(0,stem.length()-1)
               ,"ì´ë¼"
               ,String.valueOf(PatternConstants.IRR_TYPE_GU)});              
     } else if("ë¼".equals(ending)&&"ì¤ë".equals(stem)) { // 'ëë¼' ë¶ê·ì¹
       results.add(
           new String[]{stem.substring(0,stem.length()-1)
               ,"ì´ë¼"
               ,String.valueOf(PatternConstants.IRR_TYPE_NU)});      
     }
     
     if("ì¬".equals(ending)&&ls=='í') { // 'ì¬' ë¶ê·ì¹
       results.add(
           new String[]{stem
               ,"ì´"
               ,String.valueOf(PatternConstants.IRR_TYPE_NU)});        
     }
   }
   
   /**
    * ì´ê°ê³¼ ì´ë¯¸ê° ëª¨ë ë³íë ê²½ì°
    * @param results
    * @param stem
    * @param ending
    */
   private static void irregularAO(List results, String stem, String ending) {
     
     char ls = stem.charAt(stem.length()-1);
     char[] lsJaso = MorphUtil.decompose(ls);
     
     if(lsJaso.length<2) return;
     
     if(lsJaso[1]=='ã') {
       if(stem.endsWith("ëì")||stem.endsWith("ê³ ì")) { // 'ê³±ë¤', 'ëë¤'ì 'ã' ë¶ê·ì¹
         results.add(
             new String[]{stem.substring(0,stem.length()-2)+
                 MorphUtil.makeChar(stem.charAt(stem.length()-2),17) // + 'ã'
                 ,makeTesnseEomi("ì",ending)
                 ,String.valueOf(PatternConstants.IRR_TYPE_BI)});          
       }else { // 'ì' ì¶ì½
         results.add(
             new String[]{stem.substring(0,stem.length()-1)+
                 MorphUtil.makeChar(stem.charAt(stem.length()-1),8,0) // ìì + ã 
                 ,makeTesnseEomi("ì",ending)
                 ,String.valueOf(PatternConstants.IRR_TYPE_WA)});        
       }
     } else if(stem.endsWith("í¼")) {
       results.add(
           new String[]{stem.substring(0,stem.length()-1)+
               MorphUtil.makeChar(stem.charAt(stem.length()-1),18,0) // ìì + - 
               ,makeTesnseEomi("ì´",ending)
               ,String.valueOf(PatternConstants.IRR_TYPE_WA)});  
     } else if(lsJaso[1]=='ã') {
       if(stem.length()>=2) // 'ã' ë¶ê·ì¹
         results.add(
             new String[]{stem.substring(0,stem.length()-2)+
                 MorphUtil.makeChar(stem.charAt(stem.length()-2),17) // + 'ã'
                 ,makeTesnseEomi("ì´",ending)
                 ,String.valueOf(PatternConstants.IRR_TYPE_BI)});  
 
       results.add(
           new String[]{stem.substring(0,stem.length()-1)+
               MorphUtil.makeChar(stem.charAt(stem.length()-1),13,0) // ìì + ã 
               ,makeTesnseEomi("ì´",ending)
               ,String.valueOf(PatternConstants.IRR_TYPE_WA)});  
     } else if(stem.length()>=2&&ls=='ë¼') {
       char[] ns = MorphUtil.decompose(stem.charAt(stem.length()-2));
       if(ns.length==3&&ns[2]=='ã¹') { // ë¥´ ë¶ê·ì¹
         results.add(
             new String[]{stem.substring(0,stem.length()-2)+
                 MorphUtil.makeChar(stem.charAt(stem.length()-2),0) + "ë¥´"
                 ,makeTesnseEomi("ì",ending)
                 ,String.valueOf(PatternConstants.IRR_TYPE_RO)});          
       }      
     } else if(stem.length()>=2&&ls=='ë¬') {
       char[] ns = MorphUtil.decompose(stem.charAt(stem.length()-2));
       if(stem.charAt(stem.length()-2)=='ë¥´') { // ë¬ ë¶ê·ì¹
         results.add(
             new String[]{stem.substring(0,stem.length()-1)
                 ,makeTesnseEomi("ì´",ending)
                 ,String.valueOf(PatternConstants.IRR_TYPE_LO)});  
       } else if(ns.length==3&&ns[2]=='ã¹') { // ë¥´ ë¶ê·ì¹
         results.add(
             new String[]{stem.substring(0,stem.length()-2)+
                 MorphUtil.makeChar(stem.charAt(stem.length()-2),0) + "ë¥´"
                 ,makeTesnseEomi("ì´",ending)
                 ,String.valueOf(PatternConstants.IRR_TYPE_RO)});  
       }
     } else if(stem.endsWith("í´")||stem.endsWith("ì¼")) {
       results.add(
           new String[]{stem.substring(0,stem.length()-1)+
               MorphUtil.makeChar(stem.charAt(stem.length()-1),20,0)
               ,makeTesnseEomi("ì´",ending)
               ,String.valueOf(PatternConstants.IRR_TYPE_EI)});  
     } else if(stem.endsWith("í´")) {
       results.add(
           new String[]{stem.substring(0,stem.length()-1)+
               MorphUtil.makeChar(stem.charAt(stem.length()-1),0,0)
               ,makeTesnseEomi("ì´",ending)
               ,String.valueOf(PatternConstants.IRR_TYPE_EI)});        
     } else if(lsJaso.length==2&&lsJaso[1]=='ã') {
       results.add(
           new String[]{stem.substring(0,stem.length()-1)+
               MorphUtil.makeChar(stem.charAt(stem.length()-1),18,0)
               ,makeTesnseEomi("ì´",ending)
               ,String.valueOf(PatternConstants.IRR_TYPE_UO)});  
     } else if(lsJaso.length==2&&lsJaso[1]=='ã') {
       // ì¼ íë½
       results.add(
           new String[]{stem.substring(0,stem.length()-1)+
               MorphUtil.makeChar(stem.charAt(stem.length()-1),18,0)
               ,makeTesnseEomi("ì´",ending)
               ,String.valueOf(PatternConstants.IRR_TYPE_UO)});  
       //   ì ë¶ê·ì¹
       results.add(
           new String[]{stem
               ,makeTesnseEomi("ì´",ending)
               ,String.valueOf(PatternConstants.IRR_TYPE_AH)});  
     } else if(lsJaso[1]=='ã') {
       results.add(
           new String[]{stem.substring(0,stem.length()-1)+
               MorphUtil.makeChar(stem.charAt(stem.length()-1),20,0)
               ,makeTesnseEomi("ì´",ending)
               ,String.valueOf(PatternConstants.IRR_TYPE_EI)});  
     } else if(lsJaso[1]=='ã') {
       results.add(
           new String[]{stem.substring(0,stem.length()-1)+
               MorphUtil.makeChar(stem.charAt(stem.length()-1),11,0)
               ,makeTesnseEomi("ì´",ending)
               ,String.valueOf(PatternConstants.IRR_TYPE_OE)});  
     } else if(lsJaso[1]=='ã') {
       results.add(
           new String[]{stem.substring(0,stem.length()-1)+
               MorphUtil.makeChar(stem.charAt(stem.length()-1),0,27)
               ,makeTesnseEomi("ì",ending)
               ,String.valueOf(PatternConstants.IRR_TYPE_HI)});
     } else if(lsJaso[1]=='ã') {
       results.add(
           new String[]{stem.substring(0,stem.length()-1)+
               MorphUtil.makeChar(stem.charAt(stem.length()-1),2,27)
               ,makeTesnseEomi("ì",ending)
               ,String.valueOf(PatternConstants.IRR_TYPE_HI)});              
     }
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
           MorphUtil.makeChar(preword.charAt(preword.length()-1),20)+endword.substring(1,endword.length());    
     } else if(endword.charAt(0)=='ã´') {
       return preword.substring(0,preword.length()-1)+
           MorphUtil.makeChar(preword.charAt(preword.length()-1),4)+endword.substring(1,endword.length());
     } else if(endword.charAt(0)=='ã¹') {
       return preword.substring(0,preword.length()-1)+
           MorphUtil.makeChar(preword.charAt(preword.length()-1),8)+endword.substring(1,endword.length());  
     } else if(endword.charAt(0)=='ã') {
       return preword.substring(0,preword.length()-1)+
           MorphUtil.makeChar(preword.charAt(preword.length()-1),16)+endword.substring(1,endword.length());          
     } else if(endword.charAt(0)=='ã') {
       return preword.substring(0,preword.length()-1)+
           MorphUtil.makeChar(preword.charAt(preword.length()-1),17)+endword.substring(1,endword.length());
     }
     return preword+endword;    
   }
  
    /**
     * 'ì/ê¸°' + 'ì´' + ì´ë¯¸, 'ìì/ë¶í°/ììë¶í°' + 'ì´' + ì´ë¯¸ ì¸ì§ ì¡°ì¬íë¤.
    * @param stem
    * @return
     */
    public static boolean endsWithEEomi(String stem) {
      int len = stem.length();
      if(len<2||!stem.endsWith("ì´")) return false;
     
      char[] jasos = MorphUtil.decompose(stem.charAt(len-2));
      if(jasos.length==3&&jasos[2]=='ã')
        return true;
      else {
        int index = stem.lastIndexOf("ê¸°");
        if(index==-1) index = stem.lastIndexOf("ìì");
        if(index==-1) index = stem.lastIndexOf("ë¶í°");
        if(index==-1) return false;
        return true;
      }
    }
    
   private static void setPomiResult(String[] results,String stem, String pomi ) {
     results[0] = stem;
     results[1] = pomi;
   }  
   
  /**
   * 
   * @param ch
   * @return
   */
   public static boolean IsNLMBSyl(char ech, char lch) throws MorphException {
   
     char[] features = SyllableUtil.getFeature(ech);
 
     switch(lch) {
 
       case 'ã´' :
         return (features[SyllableUtil.IDX_YNPNA]=='1' || features[SyllableUtil.IDX_YNPLN]=='1');        
       case 'ã¹' :
         return (features[SyllableUtil.IDX_YNPLA]=='1');
       case 'ã' :
         return (features[SyllableUtil.IDX_YNPMA]=='1');    
       case 'ã' :
         return (features[SyllableUtil.IDX_YNPBA]=='1');          
     }
   
     return false;
   }
   
   /**
    * ì´ë¯¸ë¥¼ ë¶ë¦¬íë¤.
    * 
    * 1. ê·ì¹ì©ì¸ê³¼ ì´ê°ë§ ë°ëë ë¶ê·ì¹ ì©ì¸
    * 2. ì´ë¯¸ê° ì¢ì± 'ã´/ã¹/ã/ã'ì¼ë¡ ììëë ì´ì 
    * 3. 'ì¬/ê±°ë¼/ëë¼'ì ë¶ê·ì¹ ì´ì 
    * 4. ì´ë¯¸ 'ì/ì´'ê° íë½ëë ì´ì 
    * 5. 'ì/ì´'ì ë³ì´ì²´ ë¶ë¦¬
   * 
   * @param stem
   * @param end
   * @return
   * @throws MorphException
    */
   public static String[] splitEomi(String stem, String end) throws MorphException {
 
     String[] strs = new String[2];
     int strlen = stem.length();
     if(strlen==0) return strs;
 
     char estem = stem.charAt(strlen-1);
     char[] chrs = MorphUtil.decompose(estem);
     if(chrs.length==1) return strs; // íê¸ì´ ìëë¼ë©´...
 
     if((chrs.length==3)&&(chrs[2]=='ã´'||chrs[2]=='ã¹'||chrs[2]=='ã'||chrs[2]=='ã')&&
         EomiUtil.IsNLMBSyl(estem,chrs[2])&&
         DictionaryUtil.combineAndEomiCheck(chrs[2], end)!=null) {    
       strs[1] = Character.toString(chrs[2]);
       if(end.length()>0) strs[1] += end;
       strs[0] = stem.substring(0,strlen-1) + MorphUtil.makeChar(estem, 0);  
     } else if(estem=='í´'&&DictionaryUtil.existEomi("ì´"+end)) {      
       strs[0] = stem.substring(0,strlen-1)+"í";
       strs[1] = "ì´"+end;  
     } else if(estem=='í'&&DictionaryUtil.existEomi("ì´"+end)) {      
       strs[0] = stem.substring(0,strlen-1)+"í";
       strs[1] = "ì´"+end;        
     } else if(chrs[0]!='ã'&&
         (chrs[1]=='ã'||chrs[1]=='ã'||chrs[1]=='ã'||chrs[1]=='ã')&&
         (chrs.length==2 || SyllableUtil.getFeature(estem)[SyllableUtil.IDX_YNPAH]=='1')&&
         (DictionaryUtil.combineAndEomiCheck('ì´', end)!=null)) {    
     
       strs[0] = stem;
       if(chrs.length==2) strs[1] = "ì´"+end;  
       else strs[1] = end;  
     } else if(stem.endsWith("í")&&"ì¬".equals(end)) {      
       strs[0] = stem;
       strs[1] = "ì´";  
     }else if((chrs.length==2)&&(chrs[1]=='ã'||chrs[1]=='ã'||chrs[1]=='ã'||chrs[1]=='ã'||chrs[1]=='ã'||chrs[1]=='ã')&&
         (DictionaryUtil.combineAndEomiCheck('ì´', end)!=null)) {    
   
       StringBuffer sb = new StringBuffer();
       
       if(strlen>1) sb.append(stem.substring(0,strlen-1));
       
       if(chrs[1]=='ã')
         sb.append(MorphUtil.makeChar(estem, 8, 0)).append(MorphUtil.replaceJongsung('ì',estem));  
       else if(chrs[1]=='ã')
         sb.append(MorphUtil.makeChar(estem, 13, 0)).append(MorphUtil.replaceJongsung('ì´',estem));  
       else if(chrs[1]=='ã')
         sb.append(MorphUtil.makeChar(estem, 11, 0)).append(MorphUtil.replaceJongsung('ì´',estem));        
       else if(chrs[1]=='ã')
         sb.append(Character.toString(MorphUtil.makeChar(estem, 20, 0))).append(MorphUtil.replaceJongsung('ì´',estem));
       else if(chrs[1]=='ã')
         sb.append(MorphUtil.makeChar(estem, 0, 0)).append(MorphUtil.replaceJongsung('ì´',estem));
       else if(chrs[1]=='ã')
         sb.append(MorphUtil.makeChar(estem, 20, 0)).append(MorphUtil.replaceJongsung('ì ',estem));  
     
       strs[0] = sb.toString();
     
       end = strs[0].substring(strs[0].length()-1)+end;        
       strs[0] = strs[0].substring(0,strs[0].length()-1);
       
       strs[1] = end;    
 
     }else if(!"".equals(end)&&DictionaryUtil.existEomi(end)) {    
       strs = new String[]{stem, end};
     }
 
     return strs;
   }
 }
