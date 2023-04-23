package com.lboleslavsky.nluchatbot.storage;

import com.lboleslavsky.nluchatbot.utils.Logger;

/**
 * Class for statement
 * 
 * @author lboleslavsky
 */
public class Statement {
    private final String textKey;
    private final String text;
    private final String referenceToKey;

    public Statement(String textKey, String text, String referenceToKey){
        this.textKey=textKey;
        this.text= text;
        this.referenceToKey=referenceToKey;
        
        Logger.log("created statement: " + this.textKey + ", " + this.referenceToKey + ", " + this.text);
    }
    
    /**
     * @return the textKey
     */
    public String getTextKey() {
        return textKey;
    }

    /**
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * @return the referenceToKey
     */
    public String getReferenceToKey() {
        return referenceToKey;
    }
}
