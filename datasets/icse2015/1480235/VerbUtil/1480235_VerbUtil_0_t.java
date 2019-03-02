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
 import java.util.List;
 import java.util.Map;
 
 import org.apache.lucene.analysis.kr.morph.AnalysisOutput;
 import org.apache.lucene.analysis.kr.morph.MorphException;
 import org.apache.lucene.analysis.kr.morph.PatternConstants;
 import org.apache.lucene.analysis.kr.morph.WordEntry;
 
 public class VerbUtil {
 
   public static final Map verbSuffix = new HashMap();
   
   public static final Map XVerb = new HashMap();
   
   static {
     String[] suffixs = {
         "ì´","í","ë","ë´", "ë", "ì¤ë½","ìí¤","ì","ì","ê°","ë¹í","ë§í","ëë¦¬","ë°","ì§"};
     for(int i=0;i<suffixs.length;i++) verbSuffix.put(suffixs[i], suffixs[i]);
     
     String[] xverbs = {"ì¤","ë´","ì£¼","ë³´","ì§","ì¤ë¥´","ì¬ë¦¬"};
     for(int i=0;i<xverbs.length;i++) XVerb.put(xverbs[i], xverbs[i]);
   }
   
   /**
    * ì´ê°ì´ ì©ì¸íì ë¯¸ì¬ë¡ ëëë©´ index ë¥¼ ë°ííë¤.  ìëë©´ -1ì ë°ííë¤.
    */
   public static int endsWithVerbSuffix(String stem) {
     int len = stem.length();
     if(len<2) return -1;
     int start = 2;
     if(len==2) start = 1;      
     for(int i=start;i>0;i--) { // suffix ì ê°ì¥ ê¸´ ê¸ììê° 2ì´ë¤.
       if(verbSuffix.get(stem.substring(len-i))!=null) return (len-i);
     }    
     return -1;
   }
    
   /**
    * ì´ê°ë¶ì ë³´ì¡°ì©ì¸ [í,ë,ì¤,ë´,ì£¼,ì§]ê° ìëì§ ì¡°ì¬íë¤.
    */
   public static int endsWithXVerb(String stem) {
     int len = stem.length();
     if(len<2) return -1;
     int start = 2;
     if(len==2) start = 1;
     for(int i=start;i>0;i--) { //xverbs ì ê°ì¥ ê¸´ ê¸ììë 2ì´ë¤.
       if(XVerb.get(stem.substring(len-i))!=null) return (len-i);
     }
     return -1;
   }
    
   public static boolean verbSuffix(String stem) {
 
     return verbSuffix.get(stem)!=null;
      
   }
    
   public static boolean constraintVerb(String start, String end) {
      
     char[] schs = MorphUtil.decompose(start.charAt(start.length()-1));
     char[] echs = MorphUtil.decompose(end.charAt(0));
      
     if(schs.length==3&&schs[2]=='ã¹'&&echs[0]=='ã¹') return false;
      
     return true;
   }
    
   /**
    * 3. íêµììì´ë¤ : ì²´ì¸ + 'ìì/ë¶í°/ììë¶í°' + 'ì´' + ì´ë¯¸ (PTN_NJCM) <br>
    */
   public static boolean ananlysisNJCM(AnalysisOutput o, List candidates) throws MorphException {
  
     int strlen = o.getStem().length();
     boolean success = false;
      
     if(strlen>3&&(o.getStem().endsWith("ììì´")||o.getStem().endsWith("ë¶í°ì´"))) {
       o.addElist(o.getStem().substring(strlen-1));
       o.setJosa(o.getStem().substring(strlen-3,strlen-1));
       o.setStem(o.getStem().substring(0,strlen-3));
       success = true;
     }else if(strlen>5&&(o.getStem().endsWith("ììë¶í°ì´"))) {
       o.addElist(o.getStem().substring(strlen-1));
       o.setJosa(o.getStem().substring(strlen-5,strlen-1));
       o.setStem(o.getStem().substring(0,strlen-5));
       success = true;
     }
     if(!success) return false;
      
     if(success&&DictionaryUtil.getNoun(o.getStem())!=null) {       
       o.setScore(AnalysisOutput.SCORE_CORRECT);
 //     }else {
 //      NounUtil.confirmCNoun(o);
     }
      
     o.setPatn(PatternConstants.PTN_NJCM);
     o.setPos(PatternConstants.POS_NOUN);  
     candidates.add(o);
      
     return true;
   }
    
   /**
    * ì´ë¯¸ë¶ì ì´ê°ë¶ê° ë¶ë¦¬ë ìíìì ì©ì¸íì ë¯¸ì¬ê° ê²°í©ë  ì ìëì§ ì¡°ì¬íë¤.
    * @param o  ì´ë¯¸ë¶ì ì´ê°ë¶ê° ë¶ë¦¬ë ê²°ê³¼
    * @param candidates
    * @throws MorphException
    */
   public static boolean ananlysisNSM(AnalysisOutput o, List candidates) throws MorphException {
 
     if(o.getStem().endsWith("ì¤ë¬ì°")) o.setStem(o.getStem().substring(0,o.getStem().length()-3)+"ì¤ë½");
     int idxVbSfix = VerbUtil.endsWithVerbSuffix(o.getStem());
     if(idxVbSfix<1) return false;
   
     o.setVsfx(o.getStem().substring(idxVbSfix));
     o.setStem(o.getStem().substring(0,idxVbSfix));
     o.setPatn(PatternConstants.PTN_NSM);
     o.setPos(PatternConstants.POS_NOUN);
     
     WordEntry entry = DictionaryUtil.getWordExceptVerb(o.getStem());
         
 //    if(entry==null&&NounUtil.confirmCNoun(o)&&o.getCNounList().size()>0)  {
 //      entry = DictionaryUtil.getNoun(o.getCNounList().get(o.getCNounList().size()-1).getWord());
 //    }
 
 //    if(entry==null) return false;
 //    if(entry==null) {
 //      NounUtil.confirmDNoun(o);
 //      if(o.getScore()!=AnalysisOutput.SCORE_CORRECT) return false;
 //    }
 
     if(entry!=null) {
       if(entry.getFeature(WordEntry.IDX_NOUN)=='0') return false;
       else if(o.getVsfx().equals("í")&&entry.getFeature(WordEntry.IDX_DOV)!='1') return false;
       else if(o.getVsfx().equals("ë")&&entry.getFeature(WordEntry.IDX_BEV)!='1') return false;
       else if(o.getVsfx().equals("ë´")&&entry.getFeature(WordEntry.IDX_NE)!='1') return false;
       o.setScore(AnalysisOutput.SCORE_CORRECT); // 'ìëë¤'ì¸ ê²½ì° ì¸ëª ë± ë¯¸ë±ë¡ì´ê° ë§ì´ ë°ìëë¯ë¡ ë¶ìì±ê³µì¼ë¡ ê°ì íë¤.      
     }else {
       o.setScore(AnalysisOutput.SCORE_ANALYSIS); // 'ìëë¤'ì¸ ê²½ì° ì¸ëª ë± ë¯¸ë±ë¡ì´ê° ë§ì´ ë°ìëë¯ë¡ ë¶ìì±ê³µì¼ë¡ ê°ì íë¤.
     }
   
     candidates.add(o);
 
     return true;
 
   }
    
   public static boolean ananlysisNSMXM(AnalysisOutput o, List candidates) throws MorphException {
    
     int idxXVerb = VerbUtil.endsWithXVerb(o.getStem());
     if(idxXVerb==-1) return false;
     
     String eogan = o.getStem().substring(0,idxXVerb);
     String[] stomis = null;
 
     if((eogan.endsWith("ì")||eogan.endsWith("ì´"))&&eogan.length()>1)
       stomis = EomiUtil.splitEomi(eogan.substring(0,eogan.length()-1),eogan.substring(eogan.length()-1));
     else
       stomis = EomiUtil.splitEomi(eogan,"");
 
     if(stomis[0]==null) return false;
     
     o.addElist(stomis[1]);
     int idxVbSfix = VerbUtil.endsWithVerbSuffix(stomis[0]);
     if(idxVbSfix==-1) return false;
     
     o.setXverb(o.getStem().substring(idxXVerb));
     o.setVsfx(stomis[0].substring(idxVbSfix));
     o.setStem(stomis[0].substring(0,idxVbSfix));
     o.setPatn(PatternConstants.PTN_NSMXM);
     o.setPos(PatternConstants.POS_NOUN);
     WordEntry entry = DictionaryUtil.getNoun(o.getStem());
 //    if(entry==null&&NounUtil.confirmCNoun(o)&&o.getCNounList().size()>0)  {
 //      entry = DictionaryUtil.getNoun(o.getCNounList().get(o.getCNounList().size()-1));
 //    }
     if(entry==null) return false;  
     
     if(o.getVsfx().equals("í")&&entry.getFeature(WordEntry.IDX_DOV)!='1') return false;
     if(o.getVsfx().equals("ë")&&entry.getFeature(WordEntry.IDX_BEV)!='1') return false;        
     o.setScore(AnalysisOutput.SCORE_CORRECT);
     
     candidates.add(o);            
 
     
     return true;     
   }
    
   public static boolean analysisVMCM(AnalysisOutput o, List candidates) throws MorphException {
    
     int strlen = o.getStem().length();
      
     if(strlen<2) return false;
      
     if(!o.getStem().endsWith("ì´")) return false;
      
     char[] chrs = MorphUtil.decompose(o.getStem().charAt(strlen-2));
     boolean success = false;
   
     if(strlen>2&&o.getStem().endsWith("ê¸°ì´")) {
       o.setStem(o.getStem().substring(0,strlen-2));
       o.addElist("ê¸°");     
       success = true;       
     } else if(chrs.length>2&&chrs[2]=='ã'){
       String[] eres = EomiUtil.splitEomi(o.getStem().substring(0,strlen-1), "");
       if(eres[0]==null) return false;
       
       o.addElist(eres[1]);       
       String[] irrs = IrregularUtil.restoreIrregularVerb(eres[0], eres[1]);
        
       if(irrs!=null) o.setStem(irrs[0]);
       else o.setStem(eres[0]);
 
       success = true;
     }
      
     if(success) {    
      
       o.addElist("ì´");
       if(DictionaryUtil.getVerb(o.getStem())!=null) {
         o.setPos(PatternConstants.POS_VERB);
         o.setPatn(PatternConstants.PTN_VMCM);
         o.setScore(AnalysisOutput.SCORE_CORRECT);
         candidates.add(o);
         return true;
       }       
     }
      
     return false;
      
   }
    
   /**
    * 
    * 6. ëìì£¼ë¤ : ì©ì¸ + 'ì/ì´' + ë³´ì¡°ì©ì¸ + ì´ë¯¸ (PTN_VMXM)
    * 
    */
   public static boolean analysisVMXM(AnalysisOutput o, List candidates) throws MorphException {
 
     int idxXVerb = VerbUtil.endsWithXVerb(o.getStem());
 
     if(idxXVerb==-1) return false;
       
     o.setXverb(o.getStem().substring(idxXVerb));
     
     String eogan = o.getStem().substring(0,idxXVerb);
 
     String[] stomis = null;
     if(eogan.endsWith("ì")||eogan.endsWith("ì´")) {
       stomis = EomiUtil.splitEomi(eogan.substring(0,eogan.length()-1),eogan.substring(eogan.length()-1));
       if(stomis[0]==null) return false;
     }else {
       stomis =  EomiUtil.splitEomi(eogan, "");      
       if(stomis[0]==null||!(stomis[1].startsWith("ì")||stomis[1].startsWith("ì´"))) return false;
     }
 
     String[] irrs = IrregularUtil.restoreIrregularVerb(stomis[0], stomis[1]);
     if(irrs!=null) {
       o.setStem(irrs[0]);
       o.addElist(irrs[1]);
     } else {
       o.setStem(stomis[0]);
       o.addElist(stomis[1]);
     }
 
     if(DictionaryUtil.getVerb(o.getStem())!=null) {
       o.setPos(PatternConstants.POS_VERB);
       o.setPatn(PatternConstants.PTN_VMXM);
       o.setScore(AnalysisOutput.SCORE_CORRECT);
       candidates.add(o);
       return true;
     }  
 
     return false;     
   }
 }
