package com.lboleslavsky.nluchatbot.utils;

/**
 * Logger
 * 
 * @author lboleslavsky
 */
public class Logger {
    
    
    public static void log(String message){
        System.out.println(message);
    }
    
    public static void log(Exception ex){
        ex.printStackTrace();
    }
}
