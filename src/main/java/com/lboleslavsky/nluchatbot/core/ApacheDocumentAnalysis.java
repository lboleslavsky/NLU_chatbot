package com.lboleslavsky.nluchatbot.core;

import com.lboleslavsky.nluchatbot.utils.Logger;
import com.lboleslavsky.nluchatbot.storage.IFileLoader;
import com.lboleslavsky.nluchatbot.storage.SentenceSnap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import opennlp.tools.ngram.NGramGenerator;

/**
 * Class for document analysis
 * @author lboleslavsky
 */
public class ApacheDocumentAnalysis implements IDocumentAnalysis {

    private static ApachePipeline pipeline = new ApachePipeline();

    @Override
    public void initPipeline(String[] fileNames, IFileLoader fileLoader) {
        try {
            
            ApachePipeline.loadStopWords(fileLoader,fileNames[0]);
            ApachePipeline.loadModels(fileLoader,fileNames);
        } catch (Exception ex) {
            Logger.log(ex);
        }
    }
    
    /**
     * Computing text similarity
     * @param pattern
     * @param textIndex
     * @return 
     */
    public float getTextKeySimilarity(String[] pattern, String[] textIndex) {
        int score = 0;
        for (String token : pattern) {
            if (findPattern(textIndex, token) != -1) {
                score++;
            }
        }
        return score / (float) textIndex.length;
    }

    /**
     * Search for pattern 
     * 
     * @param textIndex
     * @param pattern
     * @return 
     */
    private int findPattern(String[] textIndex, String pattern) {
        int i = 0;
        for (String token : textIndex) {
            if (token.equalsIgnoreCase(pattern)) {
                return i;
            }

            i++;
        }

        return -1;
    }

    /**
     * Get text key
     * @param text
     * @return 
     */
    @Override
    public String getTextKey(String text) {
    
        try {
            String userInput = text;

            // Break users chat input into sentences using sentence detection.
            String[] sentences = ApachePipeline.processBreakSentences(userInput);
            List<String> ngrams = new ArrayList<>();
            List<String> editedLemmas = new ArrayList<String>();
      
            SentenceSnap snap=new SentenceSnap();
            
            // Loop through sentences.
            for (String sentence : sentences) {

                // Separate words from each sentence using tokenizer.
                String[] tokens = ApachePipeline.processTokenizeSentence(sentence);

                // Tag separated words with POS tags to understand their gramatical structure.
                String[] posTags = ApachePipeline.processDetectPOSTags(tokens);

                // Lemmatize each word so that its easy to categorize.
                String[] lemmas = ApachePipeline.processLemmatizeTokens(tokens, posTags);
                
                snap.getLemmas().addAll(Arrays.asList(lemmas));
                snap.getPosTags().addAll(Arrays.asList(posTags));
                snap.getTokens().addAll(Arrays.asList(tokens));
            }
            
            
            for (int i = 0; i < snap.getLemmas().size(); i++) {
                    if (!pipeline.isStopWord(snap.getLemmas().get(i))) {
                        editedLemmas.add(snap.getLemmas().get(i));
                    }
            }
            
            
            if(editedLemmas.size()>2){
                Logger.log("method 1 ngrams");
                ngrams = NGramGenerator.generate(editedLemmas, 2, "_");
            } else if(editedLemmas.size()==1){
                 Logger.log("method 2 one edited lemma");
                ngrams.add(editedLemmas.get(0));
            } else {
                 Logger.log("method 3 get lemmas");
                ngrams.addAll(snap.getLemmas());
            }
            
            String r = "";

            for (String s : ngrams) {
                r += s.replace(",", "").replace("?", "").replace(";","").replace(".","").replace("!","") + " ";
            }

            Logger.log("ngram:" + r);
            return r;
        } catch (Exception ex) {
            Logger.log("Error during processing"+ex.toString());
        }

        return null;
    }
    
    /**
     * Get text key
     * 
     * @param text
     * @return 
     */
    @Override
    public String getBetterTextKey(String text) {

        try {
            String userInput = text;

            // Break users chat input into sentences using sentence detection.
            String[] sentences = ApachePipeline.processBreakSentences(userInput);
            List<String> ngrams = new ArrayList<>();
            List<String> editedLemmas = new ArrayList<String>();
      
            SentenceSnap snap=new SentenceSnap();
            
            // Loop through sentences.
            for (String sentence : sentences) {

                // Separate words from each sentence using tokenizer.
                String[] tokens = ApachePipeline.processTokenizeSentence(sentence);

                // Tag separated words with POS tags to understand their gramatical structure.
                String[] posTags = ApachePipeline.processDetectPOSTags(tokens);

                // Lemmatize each word so that its easy to categorize.
                String[] lemmas = ApachePipeline.processLemmatizeTokens(tokens, posTags);
                
                String category = ApachePipeline.detectCategory(tokens);
                
                return category;
            }
            
          
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return "not-found";
    }
   
     /**
     * @return the pipeline
     */
    public ApachePipeline getPipeline() {
        return pipeline;
    }
}
