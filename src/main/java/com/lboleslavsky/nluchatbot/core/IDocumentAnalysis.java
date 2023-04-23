package com.lboleslavsky.nluchatbot.core;

import com.lboleslavsky.nluchatbot.storage.IFileLoader;

/**
 * Interface for document analysis
 * 
 * @author lbolelsavsky
 */
public interface IDocumentAnalysis {
    
    /**
     * Get text key
     * @param text
     * @return 
     */
    String getTextKey(String text);
    
    /**
     * Get text key
     * 
     * @param text
     * @return 
     */
    public String getBetterTextKey(String text);

    /**
     * Initialize document processing pipeline
     * 
     * @param modelFileNames
     * @param fileLoader 
     */
    void initPipeline(String[] modelFileNames, IFileLoader fileLoader);
    
}
