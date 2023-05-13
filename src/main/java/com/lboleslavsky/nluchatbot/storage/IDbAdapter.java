package com.lboleslavsky.nluchatbot.storage;

import java.util.List;

/**
 * Db adapter interface
 * @author lboleslavsky
 */
public interface IDbAdapter {
   public void add(Statement statement) throws Exception;
   public List<String> selectAnswers(String referenceToKey) throws Exception;
   public void loadAnswers(List<Statement> objects) throws Exception;      
   public void initSchema() throws Exception;
}
