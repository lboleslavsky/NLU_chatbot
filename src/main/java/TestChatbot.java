import com.lboleslavsky.nluchatbot.utils.Logger;
import com.lboleslavsky.nluchatbot.storage.DbAdapter;
import com.lboleslavsky.nluchatbot.storage.FileLoader;
import com.lboleslavsky.nluchatbot.utils.Trainer;
import com.lboleslavsky.nluchatbot.core.ApacheDocumentAnalysis;
import com.lboleslavsky.nluchatbot.core.CHBot;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.DriverManager;

/**
 * Test class for chatbot
 * 
 * @author lboleslavsky
 */
public class TestChatbot {
    
    /**
     * Main method for running a chatbot
     * 
     * @param args 
     */
    public static void main(String[] args){
           
        ApacheDocumentAnalysis documentAnalysis = new ApacheDocumentAnalysis();
        documentAnalysis.initPipeline(new String[]{
            "models/en_stop_words.txt",
            "models/en-sent.bin",
            "models/en-pos-maxent.bin",
            "models/en-token.bin",
            "models/en-lemmatizer.bin",
            "models/categmodel.txt"
        }, new FileLoader());       
        
       
        try{
            CHBot chBot = new CHBot(documentAnalysis);
            chBot.init(new DbAdapter(DriverManager.getConnection("jdbc:sqlite:chb.db")));
            
            Trainer trainer = new Trainer();
            trainer.loadAndTrainByCategory(chBot,"models/trainset-category.txt" , new FileLoader());
            
            System.out.println(":");
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String s;
            while((s=br.readLine())!=null){
                System.out.println("----");
                System.out.println(chBot.selectAnswersByCategory(s));
            }            
            
        } catch (Exception ex){
            Logger.log(ex);
        }
    }   
}
