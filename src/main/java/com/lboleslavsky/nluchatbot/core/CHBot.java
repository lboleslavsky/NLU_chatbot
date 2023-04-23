package com.lboleslavsky.nluchatbot.core;

import com.lboleslavsky.nluchatbot.utils.Logger;
import com.lboleslavsky.nluchatbot.storage.IDbAdapter;
import com.lboleslavsky.nluchatbot.storage.Storage;
import java.util.List;

/**
 * Chatbot class
 * 
 * @author lboleslavsky
 */
public class CHBot {
    private IDocumentAnalysis documentAnalysis = null;
    private Storage storage=null;
    
    /**
     * Constructor 
     * 
     * @param documentAnalysis 
     */
    public CHBot(IDocumentAnalysis documentAnalysis){
        this.documentAnalysis=documentAnalysis;
    }
    
    /**
     * Init chatbot data 
     * 
     * @param dbAdapter 
     */
    public void init(IDbAdapter dbAdapter){
        try{
            this.storage=new Storage();
            this.getStorage().init(dbAdapter);
            this.getStorage().loadAnswers();
            //this.storage.initSchema();
        } catch (Exception ex){
            Logger.log(ex);
        }
    }
    
    /**
     * Return text key
     * 
     * @param text
     * @return 
     */
    public String getTextKey(String text){        
        return getDocumentAnalysis().getTextKey(text);
    }
    
    /**
     * Choose answers
     * 
     * @param text
     * @return
     * @throws Exception 
     */
    public List<String> selectAnswers(String text) throws Exception{
        return this.storage.find(this.getTextKey(text), 0.40f);
    }
    
    /**
     * Choose answers by intent key
     * 
     * @param text
     * @return
     * @throws Exception 
     */
    public List<String> selectAnswersByCategory(String text) throws Exception{
        return this.storage.findByCategory(this.getDocumentAnalysis().getBetterTextKey(text));
    }
    
    /**
     * @return the storage
     */
    public Storage getStorage() {
        return storage;
    }

    /**
     * @return the documentAnalysis
     */
    public IDocumentAnalysis getDocumentAnalysis() {
        return documentAnalysis;
    }
}
