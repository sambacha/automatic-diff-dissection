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
 
 import org.apache.lucene.analysis.kr.morph.MorphException;
 import org.apache.lucene.analysis.kr.morph.WordEntry;
 
 /**
  * 
  * ëì¬ì ë¶ê·ì¹ ë³íì ì²ë¦¬íë Utility Class
  */
 public class IrregularUtil {
   
   // ã ë¶ê·ì¹
   public static final char IRR_TYPE_BIUP = 'B';
   
   // ã ë¶ê·ì¹
   public static final char IRR_TYPE_HIOOT = 'H';
   
   // ã¹ ë¶ê·ì¹
   public static final char IRR_TYPE_LIUL = 'U';
   
   // ë¥´ ë¶ê·ì¹
   public static final char IRR_TYPE_LOO = 'L';
 
   // ã ë¶ê·ì¹
   public static final char IRR_TYPE_SIUT = 'S';
   
   // ã· ë¶ê·ì¹
   public static final char IRR_TYPE_DI = 'D';
   
   // ë¬ ë¶ê·ì¹
   public static final char IRR_TYPE_RU = 'R';
   
   // ì¼ íë½
   public static final char IRR_TYPE_UI = 'X';  
   
   // ê·ì¹í
   public static final char IRR_TYPE_REGULAR = 'X';
   
   public static String[] restoreIrregularVerb(String start, String end) throws MorphException {
 
     if(end==null) end="";
     char[] jasos = new char[0];    
 
     if(end.length()>0) jasos = MorphUtil.decompose(end.charAt(0));
 
     if(end.startsWith("ã´")) {      
       String[] irrs = restoreBIrregular(start,end);
       if(irrs!=null) return irrs;  
       irrs = restoreHIrregular(start,end);
       if(irrs!=null) return irrs;  
       irrs = restoreELIrregular(start,end);
       if(irrs!=null) return irrs;        
     }else if(end.startsWith("ã¹")) {      
       String[] irrs = restoreBIrregular(start,end);
       if(irrs!=null) return irrs;  
       irrs = restoreHIrregular(start,end);
       if(irrs!=null) return irrs;  
       irrs = restoreELIrregular(start,end);
       if(irrs!=null) return irrs;      
     }else if(end.startsWith("ã")) {      
       String[] irrs = restoreBIrregular(start,end);
       if(irrs!=null) return irrs;
       irrs = restoreHIrregular(start,end);
       if(irrs!=null) return irrs;        
     }else if(end.startsWith("ã")) {      
       String[] irrs = restoreBIrregular(start,end);
       if(irrs!=null) return irrs;  
       irrs = restoreHIrregular(start,end);
       if(irrs!=null) return irrs;  
       irrs = restoreELIrregular(start,end);
       if(irrs!=null) return irrs;          
     }else if(start.endsWith("ì°")||start.endsWith("ì¤")) {      
       String[] irrs = restoreBIrregular(start,end);
       if(irrs!=null) return irrs;        
     }else if(end.startsWith("ì¤")) {      
       String[] irrs = restoreBIrregular(start,end);
       if(irrs!=null) return irrs;  
     }else if(end.startsWith("ì")) {
       String[] irrs = restoreBIrregular(start,end);
       if(irrs!=null) return irrs;  
       irrs = restoreELIrregular(start,end);
       if(irrs!=null) return irrs;        
     }else if(end.startsWith("ì¼")) {      
       String[] irrs = restoreBIrregular(start,end);
       if(irrs!=null) return irrs;        
     }else if(jasos.length>1&&jasos[0]=='ã'&&(jasos[1]=='ã'||jasos[1]=='ã')) {      
       String[] irrs = restoreDIrregular(start,end);
       if(irrs!=null) return irrs;  
       irrs = restoreSIrregular(start,end);
       if(irrs!=null) return irrs;  
       irrs = restoreLIrregular(start,end);
       if(irrs!=null) return irrs;    
       irrs = restoreHIrregular(start,end);
       if(irrs!=null) return irrs;  
       irrs = restoreUIrregular(start,end);
       if(irrs!=null) return irrs;    
       irrs = restoreRUIrregular(start,end);
       if(irrs!=null) return irrs;            
     }else if(jasos.length>1&&jasos[0]=='ã'&&jasos[1]=='ã¡') {      
       String[] irrs = restoreDIrregular(start,end);
       if(irrs!=null) return irrs;    
       irrs = restoreSIrregular(start,end);
       if(irrs!=null) return irrs;        
     }else if(("ê°".equals(start)&&"ê±°ë¼".equals(end))||
         ("ì¤".equals(start)&&"ëë¼".equals(end))) {      
       return new String[]{start,end};
     }
     
     return null;
   }
   
   /**
    * ã ë¶ê·ì¹ ìíì ë³µìíë¤. (ëë¤, ê³±ë¤)
    * @param start
    * @param end
   * @return
    * @throws MorphException
    */
   private static String[] restoreBIrregular(String start, String end) throws MorphException {
 
     if(start==null||"".equals(start)||end==null) return null;
       
     if(start.length()<2) return null;
       
     if(!(start.endsWith("ì¤")||start.endsWith("ì°"))) return null;
       
     char convEnd = MorphUtil.makeChar(end.charAt(0), 0);
     if("ã".equals(end)||"ã´".equals(end)||"ã¹".equals(end)||
         convEnd=='ì'||convEnd=='ì´') { // ëì°(ë), ê³ ì¤(ê³±), ì¤ë¬ì°(ì¤ë½) ë±ì¼ë¡ ë³íëë¯ë¡ ë°ëì 2ì ì´ìì
       
       char ch = start.charAt(start.length()-2);
       ch = MorphUtil.makeChar(ch, 17);
     
       if(start.length()>2) 
         start = Utilities.arrayToString(new String[]{start.substring(0,start.length()-2),Character.toString(ch)});
       else
         start = Character.toString(ch);    
 
       WordEntry entry = DictionaryUtil.getVerb(start);
       if(entry!=null&&entry.getFeature(WordEntry.IDX_REGURA)==IRR_TYPE_BIUP)
         return new String[]{start,end};      
     }
 
     return null;     
   }
   
   /**
    * ã· ë¶ê·ì¹ ìíì ë³µìíë¤. (ê¹¨ë«ë¤, ë¬»ë¤)
    * @param start
    * @param end
   * @return
    * @throws MorphException
    */
   private static String[] restoreDIrregular(String start, String end) throws MorphException {
     if(start==null||"".equals(start)) return null;
     
     char ch = start.charAt(start.length()-1);
     char[] jasos = MorphUtil.decompose(ch);
     if(jasos.length!=3||jasos[2]!='ã¹') return null;
     
     ch = MorphUtil.makeChar(ch, 7);
     if(start.length()>1) 
       start = Utilities.arrayToString(new String[]{start.substring(0,start.length()-1),Character.toString(ch)});
     else
       start = Character.toString(ch);
     
     WordEntry entry = DictionaryUtil.getVerb(start);
     if(entry!=null&&entry.getFeature(WordEntry.IDX_REGURA)==IRR_TYPE_DI)
       return new String[]{start,end};
     
     return null;
   }
   
   /**
    * ã ë¶ê·ì¹ ìíì ë³µìíë¤. (ê¸ë¤--ê·¸ì´)
    * @param start
    * @param end
   * @return
    * @throws MorphException
    */
   private static String[] restoreSIrregular(String start, String end) throws MorphException {
     if(start==null||"".equals(start)) return null;
     
     char ch = start.charAt(start.length()-1);
     char[] jasos = MorphUtil.decompose(ch);
     if(jasos.length!=2) return null;
     
     ch = MorphUtil.makeChar(ch, 19);
     if(start.length()>1) 
       start = start.substring(0,start.length()-1)+ch;
     else
       start = Character.toString(ch);
     
     WordEntry entry = DictionaryUtil.getVerb(start);
     if(entry!=null&&entry.getFeature(WordEntry.IDX_REGURA)==IRR_TYPE_SIUT)
       return new String[]{start,end};
 
     return null;
   }
 
   /**
    * ë¥´ ë¶ê·ì¹ ìíì ë³µìíë¤. (íë¥´ë¤-->íë¬)
    * "ë°ë¥´ë¤"ë ã¹ë¶ê·ì¹ì´ ìëì§ë§.. ì¸ ê²ì²ë¼ ì²ë¦¬íë¤.
    * @param start
    * @param end
   * @return
    * @throws MorphException
    */
   private static String[] restoreLIrregular(String start, String end) throws MorphException {
 
     if(start.length()<2) return null;
     
     char ch1 = start.charAt(start.length()-2);
     char ch2 = start.charAt(start.length()-1);
     
     char[] jasos1 = MorphUtil.decompose(ch1);
     
     if(((jasos1.length==3&&jasos1[2]=='ã¹')||jasos1.length==2)&&(ch2=='ë¬'||ch2=='ë¼')) {
   
       StringBuffer sb = new StringBuffer();
       
       ch1 = MorphUtil.makeChar(ch1, 0);
       if(start.length()>2) 
         sb.append(start.substring(0,start.length()-2)).append(ch1).append("ë¥´");
       else
         sb.append(Character.toString(ch1)).append("ë¥´");
 
       WordEntry entry = DictionaryUtil.getVerb(sb.toString());
       if(entry!=null&&entry.getFeature(WordEntry.IDX_REGURA)==IRR_TYPE_LOO)
         return new String[]{sb.toString(),end};    
     }
     
     return null;
   }
   
   /**
    * ã¹ë¶ê·ì¹ ìíì ë³µìíë¤. (ê¸¸ë¤-->ê¸´, ìë¤-->ì)
    * ì´ê°ì ëìë¦¬ì¸ âã¹âì´ âã´â, âã¹â, âãâ, âì¤â, âìâ ììì íë½íë íì©ì íì
    * @param start
    * @param end
   * @return
    * @throws MorphException
    */
   private static String[] restoreELIrregular(String start, String end) throws MorphException {
 
     if(start==null || start.length()==0 || end==null||end.length()==0) return null;
           
     if(!(end.charAt(0)=='ã´'||end.charAt(0)=='ã¹'||end.charAt(0)=='ã'||end.charAt(0)=='ì¤'||end.charAt(0)=='ì')) return null;
       
     char convEnd = MorphUtil.makeChar(start.charAt(start.length()-1), 8);
     start = start.substring(0,start.length()-1)+convEnd;
 
     WordEntry entry = DictionaryUtil.getVerb(start);
     if(entry!=null)
       return new String[]{start,end};  
     
     return null;
   }
   
   /**
    * ë¬ ë¶ê·ì¹ ìíì ë³µìíë¤. (ì´ë¥´ë¤->ì´ë¥´ë¬, í¸ë¥´ë¤->í¸ë¥´ë¬)
    * @param start
    * @param end
   * @return
    * @throws MorphException
    */
   private static String[] restoreRUIrregular(String start, String end) throws MorphException {
 
     if(start.length()<2) return null;
     
     char ch1 = start.charAt(start.length()-1);
     char ch2 = start.charAt(start.length()-2);
     
     char[] jasos1 = MorphUtil.decompose(ch1);
     char[] jasos2 = MorphUtil.decompose(ch2);
     if(jasos1[0]!='ã¹'||jasos2[0]!='ã¹') return null;
     
     ch2 = MorphUtil.makeChar(ch2, 0);
     if(start.length()>2) 
       start = start.substring(0,start.length()-1);
     else
       start = Character.toString(ch2);
 
     WordEntry entry = DictionaryUtil.getVerb(start);
     if(entry!=null&&entry.getFeature(WordEntry.IDX_REGURA)==IRR_TYPE_RU)
       return new String[]{start,end};
     
     return null;
   }
   
   /**
    * ã íë½ ìíì ë³µìíë¤. (ê¹ë§£ë¤-->ê¹ë§,ê¹ë§¤ì)
    * @param start
    * @param end
   * @return
    * @throws MorphException
    */
   private static String[] restoreHIrregular(String start, String end) throws MorphException {
     if(start==null||"".equals(start)||end==null||"".equals(end)) return null;
     char ch1 = end.charAt(0);
     char ch2 = start.charAt(start.length()-1);
     
     char[] jasos1 = MorphUtil.decompose(ch1);
     char[] jasos2 = MorphUtil.decompose(ch2);
     
     if(jasos1.length==1) {
       ch2 = MorphUtil.makeChar(ch2, 27);
     }else {
       if(jasos2.length!=2||jasos2[1]!='ã') return null;
       ch2 = MorphUtil.makeChar(ch2, 0, 27);
     }
             
     if(start.length()>1) 
       start = start.substring(0,start.length()-1)+ch2;
     else
       start = Character.toString(ch2);
 
     WordEntry entry = DictionaryUtil.getVerb(start);
     if(entry!=null&&entry.getFeature(WordEntry.IDX_REGURA)==IRR_TYPE_HIOOT)
       return new String[]{start,end};
     
     return null;
   }  
 
   /**
    * ì¼ íë½ ìíì ë³µìíë¤. (ë¨ë¤->ë, í¬ë¤-ì»¤)
    * @param start
    * @param end
   * @return
    * @throws MorphException
    */
   private static String[] restoreUIrregular(String start, String end) throws MorphException {
     if(start==null||"".equals(start)) return null;
     char ch = start.charAt(start.length()-1);    
     char[] jasos = MorphUtil.decompose(ch);
     if(!(jasos.length==2&&jasos[1]=='ã')) return null;
     
     ch = MorphUtil.makeChar(ch, 18,0);
 
     if(start.length()>1) 
       start = start.substring(0,start.length()-1)+ch;
     else
       start = Character.toString(ch);
 
     WordEntry entry = DictionaryUtil.getVerb(start);
     if(entry!=null)  return new String[]{start,end};
   
     return null;
   }  
 }
