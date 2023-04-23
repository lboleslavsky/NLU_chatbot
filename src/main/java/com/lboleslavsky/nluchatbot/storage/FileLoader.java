package com.lboleslavsky.nluchatbot.storage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;

/**
 * Load files
 * 
 * @author lboleslavsky
 */
public class FileLoader implements IFileLoader {

    @Override
    public InputStream load(String fileName) throws IOException{
        return new java.io.FileInputStream(Paths.get(".").normalize().toAbsolutePath()+"/"+fileName);
    }
    
}
