package com.lboleslavsky.nluchatbot.storage;

import java.io.IOException;
import java.io.InputStream;

/**
 * File loader interface
 * 
 * @author lboleslavsky
 */
public interface IFileLoader {
    public InputStream load(String fileName) throws IOException;
}
