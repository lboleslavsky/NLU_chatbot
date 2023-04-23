/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lboleslavsky.nluchatbot.storage;

import com.lboleslavsky.nluchatbot.storage.IDbAdapter;
import com.lboleslavsky.nluchatbot.storage.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Database adapter
 * 
 * @author lboleslavsky
 */
public class DbAdapter implements IDbAdapter{
    private Connection connection;
    
    public DbAdapter(Connection connection){
        this.connection=connection;
    }
    
    public void add(Statement statement) throws Exception{
        PreparedStatement ps = this.connection.prepareStatement("INSERT INTO chbot(textKey,text,referenceToKey) VALUES (?,?,?);");
        ps.setString(1, statement.getTextKey());
        ps.setString(2, statement.getText());
        ps.setString(3, statement.getReferenceToKey());        
        ps.execute();
    }
    
    public List<String> selectAnswers(String referenceToKey) throws Exception{
        PreparedStatement ps = this.connection.prepareStatement("SELECT * FROM chbot WHERE referenceToKey LIKE ?");
        ps.setString(1, referenceToKey);
        
        List<String> list = new ArrayList<String>();
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            list.add(rs.getString(2));
        }
        return list;        
    }
    
    public void loadAnswers(List<Statement> objects) throws Exception{
        PreparedStatement ps = this.connection.prepareStatement("SELECT * FROM chbot");
               
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            objects.add(new Statement(rs.getString(1),rs.getString(2),rs.getString(3)));
        }          
    }
    
    public void initSchema() throws Exception{
        this.connection.createStatement().execute("CREATE TABLE chbot(textKey,text,referenceToKey);");
    }
}
