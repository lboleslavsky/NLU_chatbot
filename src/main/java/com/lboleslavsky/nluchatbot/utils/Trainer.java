package com.lboleslavsky.nluchatbot.utils;

import com.lboleslavsky.nluchatbot.core.CHBot;
import com.lboleslavsky.nluchatbot.storage.IFileLoader;
import com.lboleslavsky.nluchatbot.storage.Statement;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Model trainer
 * 
 * @author lboleslavsky
 */
public class Trainer {
    
    /**
     * Constructor
     */
    public Trainer(){
        
    }
    
    /**
     * Train chatbot model
     * 
     * @param chBot
     * @param sentences
     * @throws Exception 
     */
    public void train(CHBot chBot, String[] sentences) throws Exception{
        chBot.getStorage().initSchema();
        
        String key;
        String previousKey="";
        
        for(String line : sentences){
            key = chBot.getTextKey(line);
            
            chBot.getStorage().add(new Statement(key, line, previousKey));
            
            previousKey=key;
        }
    }
    
    /**
     * Load model and train
     * 
     * @param chBot
     * @param fileName
     * @param fileLoader
     * @throws FileNotFoundException
     * @throws IOException
     * @throws Exception 
     */
    public void loadAndTrain(CHBot chBot, String fileName, IFileLoader fileLoader) throws FileNotFoundException, IOException, Exception{
        chBot.getStorage().initSchema();
        
        String key;
        String previousKey="";
        
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileLoader.load(fileName)))) {
                                   
                String line;
                while((line = bufferedReader.readLine())!=null){
                    
                    line = line.replace("-","");
                    
                    key = chBot.getTextKey(line);
                    chBot.getStorage().add(new Statement(key, line, previousKey));
                    
                    previousKey=key;
                }           
            }
        }
    
    /**
     * Load model and train by intent
     * 
     * @param chBot
     * @param fileName
     * @param fileLoader
     * @throws FileNotFoundException
     * @throws IOException
     * @throws Exception 
     */
    public static void loadAndTrainByCategory(CHBot chBot, String fileName, IFileLoader fileLoader) throws FileNotFoundException, IOException, Exception{
       
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileLoader.load(fileName)))) {
                                   
                String line;
                String[] tokens;
                while((line = bufferedReader.readLine())!=null){
                    tokens = line.split("\t");
                    
                    chBot.getStorage().addByCategory(tokens[0], tokens[1]);
                }           
            }
        }
}
