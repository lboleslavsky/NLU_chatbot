package com.lboleslavsky.nluchatbot.storage;

import java.util.List;
import java.util.ArrayList;

/**
 * Sentence class
 * 
 * @author lbolelsavsky
 */
public class SentenceSnap {
    private List<String> tokens = new ArrayList<>();
    private List<String> posTags = new ArrayList<>();
    private List<String> lemmas = new ArrayList<>() ;

    /**
     * @return the tokens
     */
    public List<String> getTokens() {
        return tokens;
    }

    /**
     * @return the posTags
     */
    public List<String> getPosTags() {
        return posTags;
    }

    /**
     * @return the lemmas
     */
    public List<String> getLemmas() {
        return lemmas;
    }
}
