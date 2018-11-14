 package org.apache.lucene.analysis.kr.morph;
 
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
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 
 import org.apache.lucene.analysis.kr.utils.DictionaryUtil;
 import org.apache.lucene.analysis.kr.utils.MorphUtil;
 import org.apache.lucene.analysis.kr.utils.SyllableUtil;
 import org.apache.lucene.analysis.kr.utils.VerbUtil;
 
 public class WordSpaceAnalyzer {
 
   private MorphAnalyzer morphAnal;
   
   public WordSpaceAnalyzer() {
     morphAnal = new MorphAnalyzer();
     morphAnal.setExactCompound(false);
   }
   
   public List<AnalysisOutput> analyze(String input)  throws MorphException {
 
     List stack = new ArrayList();
     
     WSOutput output = new WSOutput();
     
     int wStart = 0;
     
     int sgCount = -9;
     
     Map<Integer, Integer> fCounter = new HashMap();
     
     for(int i=0;i<input.length();i++) {            
       
       char[] f = SyllableUtil.getFeature(input.charAt(i));
       
       String prefix = i==input.length()-1 ? "X" : input.substring(wStart,i+2);          
       Iterator iter = DictionaryUtil.findWithPrefix(prefix);
       
       List<AnalysisOutput> candidates = new ArrayList();    
       
       WordEntry entry = null;
           
       if(input.charAt(i)=='ì' || input.charAt(i)=='ì' || input.charAt(i)=='ì') {
         addSingleWord(input.substring(wStart,i), candidates);
         
                 
         // ë¤ì ìì ì´ 2ìì  ì´ì ë¨ì´ì í¬í¨ëì´ ìê³  ë§ì§ë§ ìì ì´ ìëë¼ë©´   ëìì°ê¸° ìì¹ê° ìë ê°ë¥ì±ì´ í¬ë¤.
         // ë¶ì¬, ê´íì¬, ê°íì¬ ë± ë¨ì¼ì´ì¼ ê°ë¥ì±ì¸ ê²½ì° ëìì°ê¸°ê° ê°ë¥íë, 
         // ì´ ê²½ì°ë ë¤ì ìì ì ì¡°ì¬íì¬ 
       } else if(i!= input.length()-1 && iter.hasNext()) { 
         // ìë¬´ì§ë íì§ ìì.
         sgCount = i;
       } else if(!iter.hasNext() && 
           (entry=DictionaryUtil.getBusa(input.substring(wStart,i+1)))!=null) {         
         candidates.add(buildSingleOutput(entry));
         
         // í ìì ì´ ì¡°ì¬ë ì´ë¯¸ê° ììëë ìì ì¼ ê°ë¥ì±ì´ ìë¤ë©´...  
       } else if(f[SyllableUtil.IDX_EOGAN]=='1'||f[SyllableUtil.IDX_JOSA1]=='1'){        
         if(f[SyllableUtil.IDX_JOSA1]=='1') 
           candidates.addAll(anlysisWithJosa(input.substring(wStart), i-wStart));
 
         if(f[SyllableUtil.IDX_EOGAN]=='1') 
           candidates.addAll(anlysisWithEomi(input.substring(wStart), i-wStart));
       }
   
       // í¸ë³´ê° ë  ê°ë¥ì±ì´ ëì ìì¼ë¡ ì ë ¬íë¤.
       Collections.sort(candidates, new WSOuputComparator());
       
       // ê¸¸ì´ê° ê°ì¥ ê¸´ ë¨ì´ë¥¼ ë¨ì¼ì´ë¡ ì¶ê°íë¤.
       appendSingleWord(candidates);
       
       // ë¶ìì ì¤í¨í ë¨ì´ë¥¼ 
       analysisCompouns(candidates);
       
       // í¸ë³´ê° ë  ê°ë¥ì±ì´ ëì ìì¼ë¡ ì ë ¬íë¤.
       Collections.sort(candidates, new WSOuputComparator());      
       
       int reseult = validationAndAppend(output, candidates, input);
       if(reseult==1) {
         i = output.getLastEnd()-1;
         wStart = output.getLastEnd();
       } else if(reseult==-1) {
         Integer index = fCounter.get(output.getLastEnd());
         if(index==null) index = output.getLastEnd();
         else index = index + 1;
         i = index;
         wStart = output.getLastEnd();
         fCounter.put(output.getLastEnd(), index);        
       }
 
     }
     
     // ë¶ìì ì¤í¨íìë¤ë©´ ìë ë¬¸ìì´ì ëëë ¤ ì¤ë¤.
     if(output.getLastEnd()<input.length()) {
       
       String source = input.substring(output.getLastEnd());
       int score = DictionaryUtil.getWord(source)==null ? AnalysisOutput.SCORE_ANALYSIS : AnalysisOutput.SCORE_CORRECT;
       AnalysisOutput o =new AnalysisOutput(source,null,null,PatternConstants.POS_NOUN,
           PatternConstants.PTN_N,score);
       
       o.setSource(source);
       output.getPhrases().add(o);
       morphAnal.confirmCNoun(o);
       
     }
 
     return output.getPhrases();
   }
   
   /**
    * ì¡°ì¬ë¡ ëëë ì´êµ¬ë¥¼ ë¶ìíë¤.
    * @param snipt
    * @param js
   * @return
    * @throws MorphException
    */
   private List<AnalysisOutput> anlysisWithJosa(String snipt, int js) throws MorphException {
 
     List<AnalysisOutput> candidates = new ArrayList();
     if(js<1) return candidates;
     
     int jend = findJosaEnd(snipt, js);
 
     if(jend==-1) return candidates; // íë¹í ì¡°ì¬ê° ìëë¼ë©´...
   
     String input = snipt.substring(0,jend);
 
     boolean josaFlag = true;
     
     for(int i=input.length()-1;i>0;i--) {
       
       String stem = input.substring(0,i);
       
       String josa = input.substring(i);
 
       char[] feature =  SyllableUtil.getFeature(josa.charAt(0));  
       
       if(josaFlag&&feature[SyllableUtil.IDX_JOSA1]=='1') {
         morphAnal.analysisWithJosa(stem,josa,candidates);        
       }
         
       if(josaFlag&&feature[SyllableUtil.IDX_JOSA2]=='0') josaFlag = false;
       
       if(!josaFlag) break;
       
     }
     
     if(input.length()==1) {
       AnalysisOutput o =new AnalysisOutput(input,null,null,PatternConstants.POS_NOUN,
           PatternConstants.PTN_N,AnalysisOutput.SCORE_ANALYSIS);
       candidates.add(o);
     }
     
     fillSourceString(input, candidates);
     
     return candidates;
   }
   
   /**
    * ì¡°ì¬ì ì²«ìì ë¶í° ì¡°ì¬ì 2ìì ì´ìì ì¬ì©ë  ì ìë ìì ì ì¡°ì¬íì¬
    * ê°ì¥ í° ì¡°ì¬ë¥¼ ì°¾ëë¤.
    * @param snipt
    * @param jstart
   * @return
    * @throws MorphException
    */
   private int findJosaEnd(String snipt, int jstart) throws MorphException {
     
     int jend = jstart;
 
     // [ê²ì]ì´ ëªì¬ë¥¼ ì´ë£¨ë ê²½ì°ë ìë¤.
     if(snipt.charAt(jstart-1)=='ê²'&&(snipt.charAt(jstart)=='ì')) return jstart+1;
     
     if(snipt.length()>jstart+2&&snipt.charAt(jstart+1)=='ì¤') { // ì¬ëì¤ë¬ì´, ìëì¤ë¬ì´ ê°ì ê²½ì°ë¥´ ì²ë¦¬í¨.
       char[] chrs = MorphUtil.decompose(snipt.charAt(jstart+2));
 
       if(chrs.length>=2&&chrs[0]=='ã¹'&&chrs[1]=='ã') return -1;
     }
     
     // ì¡°ì¬ì 2ìì ë¡ ì¬ì©ë  ì ë§ì§ë§ ìì ì ì°¾ëë¤.
     for(int i=jstart+1;i<snipt.length();i++) {
       char[] f = SyllableUtil.getFeature(snipt.charAt(i));
       if(f[SyllableUtil.IDX_JOSA2]=='0') break;
       jend = i;        
     }
         
     int start = jend;
     boolean hasJosa = false;
     for(int i=start;i>=jstart;i--) {
       String str = snipt.substring(jstart,i+1);
       if(DictionaryUtil.existJosa(str) && !findNounWithinStr(snipt,i,i+2) &&
           !isNounPart(snipt,jstart)) {
         jend = i;
         hasJosa = true;
         break;
       }
     }
 
     if(!hasJosa) return -1;
     
     return jend+1;
     
   }
   
   /**
    * í¥í ê³ì°ì´ë ì ë¬¸ìì´ì ë³´ì¬ì£¼ê¸° ìí´ source string ì ì ì¥íë¤.
    * @param source
    * @param candidates
    */
   private void fillSourceString(String source, List<AnalysisOutput> candidates) {
     
     for(AnalysisOutput o : candidates) {
       o.setSource(source);
     }
     
   }
   
   /**
    * ëª©ë¡ì 1ë²ì§ê° ê°ì¥ í° ê¸¸ì´ë¥¼ ê°ì§ë¤.
    * @param candidates
    */
   private void appendSingleWord(List<AnalysisOutput> candidates) throws MorphException {
   
     if(candidates.size()==0) return;
     
     String source = candidates.get(0).getSource();
     
     WordEntry entry = DictionaryUtil.getWordExceptVerb(source);
     
     if(entry!=null) {
       candidates.add(buildSingleOutput(entry));
     } else {
 
       if(candidates.get(0).getPatn()>PatternConstants.PTN_VM&&
           candidates.get(0).getPatn()<=PatternConstants.PTN_VMXMJ) return;
       
       if(source.length()<5) return;
       
       AnalysisOutput o =new AnalysisOutput(source,null,null,PatternConstants.POS_NOUN,
           PatternConstants.PTN_N,AnalysisOutput.SCORE_ANALYSIS);
       o.setSource(source);
       morphAnal.confirmCNoun(o);      
       if(o.getScore()==AnalysisOutput.SCORE_CORRECT) candidates.add(o);
     }        
   }
   
   private void addSingleWord(String source, List<AnalysisOutput> candidates) throws MorphException {
     
     WordEntry entry = DictionaryUtil.getWordExceptVerb(source);
     
     if(entry!=null) {
       candidates.add(buildSingleOutput(entry));
     } else {
       AnalysisOutput o =new AnalysisOutput(source,null,null,PatternConstants.POS_NOUN,
           PatternConstants.PTN_N,AnalysisOutput.SCORE_ANALYSIS);
       o.setSource(source);
       morphAnal.confirmCNoun(o);      
       candidates.add(o);
     }
     
 //    Collections.sort(candidates, new WSOuputComparator());
     
   }
   
   private List anlysisWithEomi(String snipt, int estart) throws MorphException {
 
     List<AnalysisOutput> candidates = new ArrayList();
     
     int eend = findEomiEnd(snipt,estart);    
 
     // ëì¬ìì ëªì¬ë¶ë¦¬
     int vstart = 0;
     for(int i=estart-1;i>=0;i--) {  
       Iterator iter = DictionaryUtil.findWithPrefix(snipt.substring(i,estart)); 
       if(iter.hasNext()) vstart=i;
       else break;
     }
       
     if(snipt.length()>eend &&
         DictionaryUtil.findWithPrefix(snipt.substring(vstart,eend+1)).hasNext()) 
       return candidates;  // ë¤ììì ê¹ì§ ë¨ì´ì ì¼ë¶ë¼ë©´.. ë¶í´ë¥¼ ìíë¤.
     
     String pvword = null;
     if(vstart!=0) pvword = snipt.substring(0,vstart);
       
     while(true) { // ã¹,ã,ã´ ì´ê¸°ëë¬¸ì ì´ë¯¸ìì¹ë¥¼ ë¤ë¡ ì¡ìëë°, ì©ì¸+ì´ë¯¸ì ííê° ìëë¼ë©´.. ì´êµ¬ ëì íë ì¤ì¸ë¤.
       String input = snipt.substring(vstart,eend);
       anlysisWithEomiDetail(input, candidates);        
       if(candidates.size()==0) break;    
       if(("ã¹".equals(candidates.get(0).getEomi()) ||
           "ã".equals(candidates.get(0).getEomi()) ||
           "ã´".equals(candidates.get(0).getEomi())) &&
           eend>estart+1 && candidates.get(0).getPatn()!=PatternConstants.PTN_VM &&
           candidates.get(0).getPatn()!=PatternConstants.PTN_NSM
           ) {
         eend--;
       }else if(pvword!=null&&candidates.get(0).getPatn()>=PatternConstants.PTN_VM&& // ëªì¬ + ì©ì¸ ì´êµ¬ ì¤ì.. ì©ì¸ì´êµ¬ë¡ ë¨ì´ë¥¼ ì´ë£¨ë ê²½ì°ë ìë¤.
           candidates.get(0).getPatn()<=PatternConstants.PTN_VMXMJ && DictionaryUtil.getWord(input)!=null){
         candidates.clear();
         break;
       }else if(pvword!=null&&VerbUtil.verbSuffix(candidates.get(0).getStem())
           &&DictionaryUtil.getNoun(pvword)!=null){ // ëªì¬ + ì©ì¸í ì ë¯¸ì¬ + ì´ë¯¸ ì²ë¦¬
         candidates.clear();
         anlysisWithEomiDetail(snipt.substring(0,eend), candidates);
         pvword=null;
         break;        
       } else {
         break;
       }
     }
             
     if(candidates.size()>0&&pvword!=null) {
       AnalysisOutput o =new AnalysisOutput(pvword,null,null,PatternConstants.POS_NOUN,
           PatternConstants.PTN_N,AnalysisOutput.SCORE_ANALYSIS);  
       morphAnal.confirmCNoun(o);
       
       List<CompoundEntry> cnouns = o.getCNounList();
       if(cnouns.size()==0) {
         boolean is = DictionaryUtil.getWordExceptVerb(pvword)!=null;
         cnouns.add(new CompoundEntry(pvword,0,is));
       } 
       
       for(AnalysisOutput candidate : candidates) {
         candidate.getCNounList().addAll(cnouns);
         candidate.getCNounList().add(new CompoundEntry(candidate.getStem(),0,true));
         candidate.setStem(pvword+candidate.getStem()); // ì´ë ê² í´ì¼ WSOutput ì ë³µí©ëªì¬ ì²ë¦¬í  ë ì ìì²ë¦¬ë¨
       }
       
     }
 
     fillSourceString(snipt.substring(0,eend), candidates);
   
     return candidates;
   }
   
   private void anlysisWithEomiDetail(String input, List<AnalysisOutput> candidates ) 
       throws MorphException {
 
     boolean eomiFlag = true;
     
     int strlen = input.length();
     
     char ch = input.charAt(strlen-1);
     char[] feature =  SyllableUtil.getFeature(ch);
     
     if(feature[SyllableUtil.IDX_YNPNA]=='1'||feature[SyllableUtil.IDX_YNPLA]=='1'||
         feature[SyllableUtil.IDX_YNPMA]=='1')
       morphAnal.analysisWithEomi(input,"",candidates);
     
     for(int i=strlen-1;i>0;i--) {
       
       String stem = input.substring(0,i);
       String eomi = input.substring(i);
 
       feature =  SyllableUtil.getFeature(eomi.charAt(0));    
       
       if(eomiFlag) {      
         morphAnal.analysisWithEomi(stem,eomi,candidates);
       }      
       
       if(eomiFlag&&feature[SyllableUtil.IDX_EOMI2]=='0') eomiFlag = false;
       
       if(!eomiFlag) break;
     }
     
   }
   
   /**
    * ì´ë¯¸ì ì²«ìì ë¶í° ì´ë¯¸ì 1ìì ì´ìì ì¬ì©ë  ì ìë ìì ì ì¡°ì¬íì¬
    * ê°ì¥ í° ì¡°ì¬ë¥¼ ì°¾ëë¤.
   * @param snipt
   * @param jstart
   * @return
   * @throws MorphException
    */
   private int findEomiEnd(String snipt, int estart) throws MorphException {
     
     int jend = 0;
     
     String tail = null;
     char[] chr = MorphUtil.decompose(snipt.charAt(estart));
     if(chr.length==3 && (chr[2]=='ã´')) {
       tail = 'ì'+snipt.substring(estart+1);
     }else if(chr.length==3 && (chr[2]=='ã¹')) {
       tail = 'ì'+snipt.substring(estart+1);      
     }else if(chr.length==3 && (chr[2]=='ã')) {
       tail = 'ìµ'+snipt.substring(estart+1);
     }else {
       tail = snipt.substring(estart);
     }        
 
     // ì¡°ì¬ì 2ìì ë¡ ì¬ì©ë  ì ë§ì§ë§ ìì ì ì°¾ëë¤.
     int start = 0;
     for(int i=1;i<tail.length();i++) {
       char[] f = SyllableUtil.getFeature(tail.charAt(i));  
       if(f[SyllableUtil.IDX_EOGAN]=='0') break;
       start = i;        
     }
           
     for(int i=start;i>0;i--) { // ì°¾ì ì ìëë¼ë 1ìì ì ë°ëì ë°íí´ì¼ íë¤.
       String str = tail.substring(0,i+1);  
       char[] chrs = MorphUtil.decompose(tail.charAt(i));  
       if(DictionaryUtil.existEomi(str) || 
           (i<2&&chrs.length==3&&(chrs[2]=='ã¹'||chrs[2]=='ã'||chrs[2]=='ã´'))) { // ã,ã¹,ã´ì´ ì°ìë ì©ì¸ì ìë¤, ì¬ì ì ë³´ê³  íì¸ì í´ë³´ì
         jend = i;
         break;
       }
     }
     
     return estart+jend+1;
     
   }
   
   /**
    * validation í íë³´ê° ë  ê°ë¥ì±ì´ ëì ìµìì ê²ì ê²°ê³¼ì ì¶ê°íë¤.
   * 
   * @param output
   * @param candidates
   * @param stack
    */
   private int validationAndAppend(WSOutput output, List<AnalysisOutput> candidates, String input)
       throws MorphException {
     
     if(candidates.size()==0) return 0;
     
     AnalysisOutput o = candidates.remove(0);    
     AnalysisOutput po = output.getPhrases().size()>0 ?  output.getPhrases().get(output.getPhrases().size()-1) : null;
     
     String ejend = o.getSource().substring(o.getStem().length());
     
     char[] chrs = po!=null&&po.getStem().length()>0 ? MorphUtil.decompose(po.getStem().charAt(po.getStem().length()-1)) : null;
     String pjend = po!=null&&po.getStem().length()>0 ? po.getSource().substring(po.getStem().length()) : null;
     
     char ja = 'x'; // ììì ë¬¸ì
     if(po!=null&&(po.getPatn()==PatternConstants.PTN_VM||po.getPatn()==PatternConstants.PTN_VMCM||po.getPatn()==PatternConstants.PTN_VMXM)) {    
       char[] chs = MorphUtil.decompose(po.getEomi().charAt(po.getEomi().length()-1));
       if(chs.length==3) ja=chs[2];
       else if(chs.length==1) ja=chs[0];      
     }
     
     int nEnd = output.getLastEnd()+o.getSource().length();
     
     char[] f = nEnd<input.length() ? SyllableUtil.getFeature(input.charAt(nEnd)) : null;      
     
     // ë°¥ë¨¹ê³  ê°ì ê²½ì°ê° ê°ë¥íë.. ë¨¹ê³ ë ëªì¬ê° ìëë¤.
     if(po!=null&&po.getPatn()==PatternConstants.PTN_N&&candidates.size()>0&&  
         o.getPatn()==PatternConstants.PTN_VM&&candidates.get(0).getPatn()==PatternConstants.PTN_N) {
       o = candidates.remove(0);       
     }else if(po!=null&&po.getPatn()>=PatternConstants.PTN_VM&&candidates.size()>0&&
         candidates.get(0).getPatn()==PatternConstants.PTN_N&&
         (ja=='ã´'||ja=='ã¹')) { // ë¤ëê°ã´, ì¬,ë(e) ë¡ ë¶í´ ë°©ì§
       o = candidates.remove(0);
     }
     
     //=============================================
     if(o.getPos()==PatternConstants.POS_NOUN && MorphUtil.hasVerbOnly(o.getStem())) {    
       output.removeLast();    
       return -1;
     }else if(nEnd<input.length() && f[SyllableUtil.IDX_JOSA1]=='1' 
         && DictionaryUtil.getNoun(o.getSource())!=null) {
       return -1;
     }else if(nEnd<input.length() && o.getScore()==AnalysisOutput.SCORE_ANALYSIS 
         && DictionaryUtil.findWithPrefix(ejend+input.charAt(nEnd)).hasNext()) { // ë£¨ì¬íã´ ê¸ííìë¶ìê¸° ë°©ì§
       return -1;  
     }else if(po!=null&&po.getPatn()==PatternConstants.PTN_VM&&"ã".equals(po.getEomi())&&
         o.getStem().equals("í")) { // ë¤ì§ í©ëë¤ ë¡ ë¶ë¦¬ëë ê² ë°©ì§
       output.removeLast();
       return -1;  
     }else if(po!=null&&po.getPatn()==PatternConstants.PTN_N&&VerbUtil.verbSuffix(o.getStem())&&
         !"ì".equals(o.getStem())) { // ì¬ëë°ë¤, ì¬ëì¤ë¬ì´ì ì²ë¦¬, ê·¸ë¬ë ìì ì ë¨ì´ì ê²°í©íì§ ìëë¤.
       output.removeLast();
       return -1;      
     } else {  
       output.addPhrase(o);        
     }
         
     return 1;
   }
   
   private AnalysisOutput buildSingleOutput(WordEntry entry) {
     
     char pos = PatternConstants.POS_NOUN;
     
     int ptn = PatternConstants.PTN_N;
     
     if(entry.getFeature(WordEntry.IDX_NOUN)=='0') {
       pos = PatternConstants.POS_AID;
       ptn = PatternConstants.PTN_AID;
     }
     
     AnalysisOutput o = new AnalysisOutput(entry.getWord(),null,null,pos,
         ptn,AnalysisOutput.SCORE_CORRECT);
     
     o.setSource(entry.getWord());
     
     return o;
   }
   
   private void analysisCompouns(List<AnalysisOutput> candidates) throws MorphException {
     
     // ë³µí©ëªì¬ ë¶í´ì¬ë¶ ê²°ì íì¬ ë¶í´
     boolean changed = false;
     boolean correct = false;
     for(AnalysisOutput o:candidates) {
       
       if(o.getScore()==AnalysisOutput.SCORE_CORRECT) {
         if(o.getPatn()!=PatternConstants.PTN_NJ) correct=true;
         // "íì±íí´"ê° [íì±í(N),í(t),ì´ì¼(e)] ë¶ìì±ê³µíìëë° [íì±/íí´]ë¶í´ëë ê²ì ë°©ì§
         if("í".equals(o.getVsfx())) break; 
         continue;
       }
 
       if(o.getPatn()<=PatternConstants.PTN_VM&&o.getStem().length()>2) {
         if(!(correct&&o.getPatn()==PatternConstants.PTN_N)) morphAnal.confirmCNoun(o);
         if(o.getScore()==AnalysisOutput.SCORE_CORRECT) changed=true;
       }
     }
     
   }
   
   /**
    * ë¬¸ìì´ì 
    * @param str  ë¶ìíê³ ì íë ì ì²´ ë¬¸ìì´
    * @param ws  ë¬¸ìì´ìì ëªì¬ë¥¼ ì°¾ë ìììì¹
    * @param es  ë¬¸ìì´ìì ëªì¬ë¥¼ ì°¾ë ë ìì¹
   * @return
    * @throws MorphException
    */
   private boolean findNounWithinStr(String str, int ws, int es) throws MorphException {
 
     if(str.length()<es) return false;
         
     for(int i=es;i<str.length();i++) {
       char[] f = SyllableUtil.getFeature(str.charAt(i));  
       if(i==str.length() || (f[SyllableUtil.IDX_JOSA1]=='1')) {        
         return (DictionaryUtil.getWord(str.substring(ws,i))!=null);
       }
     }
     
     return false;
   }
   
   private boolean isNounPart(String str, int jstart) throws MorphException  {
     
     if(true) return false;
     
     for(int i=jstart-1;i>=0;i--) {      
       if(DictionaryUtil.getWordExceptVerb(str.substring(i,jstart+1))!=null)
         return true;
     }
     
     return false;
   }
   
   private void printCandidate(WSOutput output) {
     
     List<AnalysisOutput> os = output.getPhrases();
     for(AnalysisOutput o : os) {
       System.out.print(o.toString()+"("+o.getScore()+")| ");
     }
     System.out.println("<==");
   }  
 }
