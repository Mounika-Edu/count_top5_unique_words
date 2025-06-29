package com.task.count_top5_unique_words;

import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.time.LocalDateTime;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@SpringBootApplication
@Slf4j
public class CountTop5UniqueWordsApplication {
	
	private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
	        "with", "about", "against", "between", "into", "through",
	        "during", "before", "after", "above", "below",  "from",  "down", "over", "under",
	        "again", "further", "then", "once", "here", "there", "when", "where", "why", "how",
	        "all", "any", "both", "each", "few", "more", "most", "other", "some", "such",
	         "nor", "not", "only", "own", "same",  "than", "too", "very",
	        "can", "will", "just", "don", "should", "now","what","which","their","upon",
	         "she",  "they",  "you",  "him", "her",  "them",
	        "and",  "but", "if", "because",  "until", "while",
	        "the", "for","that","his","this","had","have","this","that",
	         "are", "was", "were",  "been", "being"
	    ));

    private static final Logger log = LoggerFactory.getLogger(CountTop5UniqueWordsApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(CountTop5UniqueWordsApplication.class, args);
		log.info("Start time "+LocalDateTime.now());
		try (InputStream inputStream = CountTop5UniqueWordsApplication.class.getClassLoader().getResourceAsStream("moby.txt")) {

            if (inputStream == null) {
               log.info("File not found in resources");
                return;
            }
            log.info("Finding Total Word counts in the File:");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            Map<String, Integer> wordFreq = new HashMap<>();
            long totalWordCount = 0;

            String line;
            while ((line = reader.readLine()) != null) {
                String regex = "[\\p{Punct}\\s]+"; 
                String[] words = line.split(regex); 
          
                for (String word : words) {
                    if (word.isEmpty()) continue;

                    word = word.toLowerCase(); 
                    if (word.length() <= 2 || STOP_WORDS.contains(word)) continue;

                    wordFreq.put(word, wordFreq.getOrDefault(word, 0) + 1);
                    totalWordCount++;
                }

            }
            reader.close();

            List<Map.Entry<String, Integer>> sortedByFreq = new ArrayList<>(wordFreq.entrySet());
            sortedByFreq.sort((a, b) -> b.getValue().compareTo(a.getValue()));
            log.info("Total Word Count excluding filtered words and including numbers : {} ",totalWordCount);
            long numberWordCount = wordFreq.keySet().stream()
                    .filter(word -> word.matches("\\d+")) 
                    .count();            
            long countExcludingNumbers = totalWordCount - numberWordCount;            
            log.info(" Total Word Count excluding filtered words and excluding numbers: {}  ", countExcludingNumbers);
            
            log.info(" Top 5 Most Frequent Words:");
            IntStream.range(0, Math.min(5, sortedByFreq.size()))
            .forEach(i -> {
                Map.Entry<String, Integer> entry = sortedByFreq.get(i);
                log.info((i + 1) + ". " + entry.getKey() + " - " + entry.getValue());
            });

            log.info(" Top 50 Unique Words Alphabetically:");
            List<String> uniqueWords = wordFreq.entrySet().stream()
            		 .filter(entry -> entry.getValue() == 1)
                     .map(Map.Entry::getKey)
                     .filter(word -> word.matches("[a-zA-Z]+"))
                     .sorted()
                     .limit(50)
                     .collect(Collectors.toList());

            String uniqueWordsFinalResult = String.join(", ", uniqueWords);
            log.info("words without numbers : {} ",uniqueWordsFinalResult);
            
                       
            List<String> uniqueWordsWithNumbers = wordFreq.entrySet().stream()
           		 .filter(entry -> entry.getValue() == 1)
                    .map(Map.Entry::getKey)
                    .sorted()
                    .limit(50)
                    .collect(Collectors.toList());

           String uniqueWordsWithNumbersResult = String.join(", ", uniqueWordsWithNumbers);
           log.info("words with numbers : {} ",uniqueWordsWithNumbersResult);

        } catch (Exception e) {
        	log.error("Exception Occured while processing the input {} ",e);        
        }
	log.info("end time  "+LocalDateTime.now());
	}

}
