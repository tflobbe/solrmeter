package com.plugtree.solrmeter.model.extractor;

import com.plugtree.solrmeter.model.FileUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LogExtractor {
    private String regularExpression;
    public static final String defaultRegularExpression = "path=/select params=\\{(.*)\\}.*";
    private boolean removeDuplicates;

    public LogExtractor(String regularExpression) {
        this(regularExpression, false);
    }

    public LogExtractor(String regularExpression, boolean removeDuplicates) {
        this.regularExpression = regularExpression;
        this.removeDuplicates = removeDuplicates;
    }

    // Todo refactor and cleanup + error handling
    public String extract(String line) {
        String extractedQuery = "";

        Pattern pattern = Pattern.compile(regularExpression);

        Matcher matcher = pattern.matcher(line);

        matcher.find();
        try{
        extractedQuery = matcher.group(1);
        }catch(IllegalStateException e){
//           e.printStackTrace();
            //todo mangage exception
        }
        return extractedQuery;
    }


    
    public Collection<String> extract(List<String> lines) {
        Collection<String> queries = aCollection();

        for (String line : lines) {
            String query = extract(line);
            if (!query.isEmpty()) {
                queries.add(query);
            }
        }
        return queries;
    }

    private Collection<String> aCollection() {
        Collection<String> queries;
        if(removeDuplicates){
            queries = new HashSet<String>();
        }else{
            queries = new ArrayList<String>();
        }
        return queries;
    }

    public Collection<String> extractFromFile(String filepath) {
        List<String> lines = FileUtils.loadStringsFromFile(filepath);
        return extract(lines);
    }


}
