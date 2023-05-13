package com.lboleslavsky.nluchatbot.storage;

import com.lboleslavsky.nluchatbot.utils.Logger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Storage repository class
 * 
 * @author lboleslavsky
 */
public class Storage {
    IDbAdapter dbAdapter;
    
    List<Statement> objects = new ArrayList<>();
    public static Map<String, String> answers = new HashMap<>();

        
    public void init(IDbAdapter dbAdapter) throws Exception{
        this.dbAdapter=dbAdapter;
    }
    
    
    public void add(Statement statement) throws Exception{
        dbAdapter.add(statement);
    }
    
    public void addByCategory(String key, String text){
        key = key.trim().toLowerCase();
        
        //Logger.log("putting: "+ key +", " + text);
        
        if(answers.containsKey(key)){
            answers.put(key, answers.get(key)+";;"+text);
        } else {
            answers.put(key, text);
        }        
    }
    
    public List<String> selectAnswers(String referenceToKey) throws Exception{
        return dbAdapter.selectAnswers(referenceToKey);
    }
    
    public void loadAnswers() throws Exception{
        //dbAdapter.loadAnswers(objects);
        //Trainer.loadAndTrainByCategory(chBot, fileName, fileLoader);
    }
    
    public float getTextKeySimilarity(String[] pattern, String[] textIndex) {
        int score = 0;
        for (String token : pattern) {
            if (findPattern(textIndex, token) != -1) {
                score++;
            }
        }
        return score / (float) textIndex.length;
    }

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
    
    public List<String> findByCategory(String key){
        key = key.trim().toLowerCase();
        if(answers.containsKey(key)){
            return Arrays.asList(answers.get(key).split(";;"));
        } else {
            return new ArrayList<>();
        }
    }
    
    public List<String> find(String pattern, float treshold){
        
        String[] patternTokens = pattern.trim().split(" ");
        String[] textIndex;
        List<String> answers = new ArrayList<>();
        float x;
        float max=-1;
        //int maxIndex=-1;
        String referenceToKey; 
        
        for(int i=0;i<objects.size();i++){
            
            referenceToKey=this.objects.get(i).getReferenceToKey();
            
            if(referenceToKey==null||referenceToKey.isEmpty()){
                Logger.log("Empty refference at " + i + ", " + this.objects.get(i).getText());
                
                continue;
            }
            
            textIndex = referenceToKey.split(" ");
            
            x = this.getTextKeySimilarity(patternTokens, textIndex);
            
            if(x>=treshold){
                answers.add(this.objects.get(i).getText());
            }
            
            if (x>max){
                max=x;
                //maxIndex=i;
            }     
        }
        
        return answers;
    }
    
    public void initSchema() throws Exception{
        dbAdapter.initSchema();
    }
}
