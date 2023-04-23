package com.lboleslavsky.nluchatbot.core;

import com.lboleslavsky.nluchatbot.storage.IFileLoader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import opennlp.tools.doccat.BagOfWordsFeatureGenerator;
import opennlp.tools.doccat.DoccatFactory;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSample;
import opennlp.tools.doccat.DocumentSampleStream;
import opennlp.tools.doccat.FeatureGenerator;
import opennlp.tools.lemmatizer.LemmatizerME;
import opennlp.tools.lemmatizer.LemmatizerModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InputStreamFactory;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;
import opennlp.tools.util.model.ModelUtil;

/**
 * Class for process pipeline
 *
 * @author lboleslavsky
 */
public class ApachePipeline {

    private static Map<String, Integer> stopWords = new HashMap<>();
    private static SentenceDetectorME myCategorizer;
    private static POSTaggerME posTaggerME;
    private static TokenizerME tokenizerME;
    private static LemmatizerME lemmatizerME;
    private static IFileLoader fileLoader;
    private static DoccatModel model;

    /**
     * Train categorizer model as per the category sample training data we
     * created.
     *
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static DoccatModel trainCategorizerModel(String fileName) throws FileNotFoundException, IOException {
        // faq-categorizer.txt is a custom training data with categories as per our chat
        // requirements.
        InputStreamFactory inputStreamFactory = new MarkableFileInputStreamFactory(new File(fileName));
        ObjectStream<String> lineStream = new PlainTextByLineStream(inputStreamFactory, StandardCharsets.UTF_8);
        ObjectStream<DocumentSample> sampleStream = new DocumentSampleStream(lineStream);

        DoccatFactory factory = new DoccatFactory(new FeatureGenerator[]{new BagOfWordsFeatureGenerator()});

        TrainingParameters params = ModelUtil.createDefaultTrainingParameters();
        params.put(TrainingParameters.CUTOFF_PARAM, 0);

        // Train a model with classifications from above file.
        DoccatModel model = DocumentCategorizerME.train("en", sampleStream, params, factory);
        return model;
    }

    /**
     * Detect category using given token. Use categorizer feature of Apache
     * OpenNLP.
     *
     * @param finalTokens
     * @return
     * @throws IOException
     */
    public static String detectCategory(String[] finalTokens) throws IOException {

        // Initialize document categorizer tool
        DocumentCategorizerME myCategorizer = new DocumentCategorizerME(model);

        // Get best possible category.
        double[] probabilitiesOfOutcomes = myCategorizer.categorize(finalTokens);
        String category = myCategorizer.getBestCategory(probabilitiesOfOutcomes);
        System.out.println("Category: " + category);

        return category;

    }

    /**
     * Loading stop words
     *
     * @param fileLoader
     * @param fileName
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void loadStopWords(IFileLoader fileLoader, String fileName) throws FileNotFoundException, IOException {
        // Better to read file once at start of program & store model in instance
        // variable. but keeping here for simplicity in understanding.
        try ( BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileLoader.load(fileName)))) {

            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                stopWords.put(line.trim().toLowerCase(), 1);
            }
        }
    }

    /**
     * Loading models
     *
     * @param fileLoader
     * @param fileNames
     * @throws Exception
     */
    public static void loadModels(IFileLoader fileLoader, String[] fileNames) throws Exception {
        ApachePipeline.fileLoader = fileLoader;
        breakSentences(fileNames[1]);
        detectPOSTags(fileNames[2]);
        tokenizeSentence(fileNames[3]);
        lemmatizeTokens(fileNames[4]);
        model = trainCategorizerModel(fileNames[5]);
    }

    /**
     * Determine if text is a stop word
     *
     * @param word
     * @return
     */
    public static boolean isStopWord(String word) {
        word = word.trim().toLowerCase();
        return stopWords.containsKey(word);
    }

    /**
     * Break data into sentences using sentence detection feature of Apache
     * OpenNLP.
     *
     * @param data
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static String[] processBreakSentences(String data) throws FileNotFoundException, IOException {
        // Better to read file once at start of program & store model in instance

        String[] sentences = myCategorizer.sentDetect(data);
        return sentences;
    }

    /**
     * Break data into sentences using sentence detection feature of Apache
     * OpenNLP.
     *
     * @param data
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    private static void breakSentences(String fileName) throws FileNotFoundException, IOException {
        // Better to read file once at start of program & store model in instance

        // variable. but keeping here for simplicity in understanding.
        try ( InputStream modelIn = fileLoader.load(fileName)) {

            myCategorizer = new SentenceDetectorME(new SentenceModel(modelIn));

        }
    }

    /**
     * Find part-of-speech or POS tags of all tokens using POS tagger feature of
     * Apache OpenNLP.
     *
     * @param tokens
     * @return
     * @throws IOException
     */
    public static String[] processDetectPOSTags(String[] tokens) throws IOException {
        // Better to read file once at start of program & store model in instance
        // variable. but keeping here for simplicity in understanding.

        String[] posTokens = posTaggerME.tag(tokens);
        return posTokens;
    }

    /**
     * Find part-of-speech or POS tags of all tokens using POS tagger feature of
     * Apache OpenNLP.
     *
     * @param tokens
     * @return
     * @throws IOException
     */
    private static void detectPOSTags(String fileName) throws IOException {
        // Better to read file once at start of program & store model in instance
        // variable. but keeping here for simplicity in understanding.
        try ( InputStream modelIn = fileLoader.load(fileName)) {
            // Initialize POS tagger tool
            posTaggerME = new POSTaggerME(new POSModel(modelIn));

        }
    }

    /**
     * Break sentence into words & punctuation marks using tokenizer feature of
     * Apache OpenNLP.
     *
     * @param sentence
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static String[] processTokenizeSentence(String sentence) throws FileNotFoundException, IOException {

        // Tokenize sentence.
        String[] tokens = tokenizerME.tokenize(sentence);
        return tokens;
    }

    /**
     * Break sentence into words & punctuation marks using tokenizer feature of
     * Apache OpenNLP.
     *
     * @param sentence
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    private static void tokenizeSentence(String fileName) throws FileNotFoundException, IOException {
        // Better to read file once at start of program & store model in instance
        // variable. but keeping here for simplicity in understanding.
        try ( InputStream modelIn = fileLoader.load(fileName)) {
            // Initialize tokenizer tool
            tokenizerME = new TokenizerME(new TokenizerModel(modelIn));
        }
    }

    /**
     * Find lemma of tokens using lemmatizer feature of Apache OpenNLP.
     *
     * @param tokens
     * @param posTags
     * @return
     * @throws InvalidFormatException
     * @throws IOException
     */
    public static String[] processLemmatizeTokens(String[] tokens, String[] posTags)
            throws InvalidFormatException, IOException {

        String[] lemmaTokens = lemmatizerME.lemmatize(tokens, posTags);
        return lemmaTokens;
    }

    /**
     * Find lemma of tokens using lemmatizer feature of Apache OpenNLP.
     *
     * @param tokens
     * @param posTags
     * @return
     * @throws InvalidFormatException
     * @throws IOException
     */
    private static void lemmatizeTokens(String fileName) throws InvalidFormatException, IOException {
        // Better to read file once at start of program & store model in instance
        // variable. but keeping here for simplicity in understanding.
        try ( InputStream modelIn = fileLoader.load(fileName)) {

            // Tag sentence.
            lemmatizerME = new LemmatizerME(new LemmatizerModel(modelIn));
        }
    }
}
