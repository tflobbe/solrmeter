package com.plugtree.solrmeter.extractor;

import com.plugtree.solrmeter.BaseTestCase;
import com.plugtree.solrmeter.model.FileUtils;
import com.plugtree.solrmeter.model.extractor.LogExtractor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LogExtractorTestCase extends BaseTestCase{


    private String inputPath;
    private String regularExpression = LogExtractor.defaultRegularExpression;


    public void testExtractQueryFromAString(){

        LogExtractor logExtractor = new LogExtractor(regularExpression);
        String line = "INFO: [] webapp=/solr path=/select params={fl=*,score&debugQuery=true&indent=true&q=HTTP&wt=javabin&fq=price:[100+TO+200]&version=2} hits=0 status=0 QTime=17";
        String query = logExtractor.extract(line);
        assertEquals("fl=*,score&debugQuery=true&indent=true&q=HTTP&wt=javabin&fq=price:[100+TO+200]&version=2", query);
    }

    public void testExtractQueriesFromAListOfStrings() throws Exception {

        LogExtractor logExtractor = new LogExtractor(regularExpression);

        List<String> stringList = Arrays.asList(
                "INFO: [] webapp=/solr path=/select params={fl=*,score&debugQuery=true&indent=true&q=HTTP&wt=javabin&fq=price:[100+TO+200]&version=2} hits=0 status=0 QTime=17",
                "INFO: [] webapp=/solr path=/select params={fl=*,score&debugQuery=true&indent=true&q=Comprehensive&wt=javabin&fq=price:[200+TO+300]&version=2} hits=0 status=0 QTime=2",
                "INFO: [] webapp=/solr path=/select params={fl=*,score&debugQuery=true&indent=true&q=Apache+Software+Foundation&wt=javabin&fq=cat:electronics&version=2} hits=0 status=0 QTime=7"
        );


        List<String> queries = new ArrayList<String>(logExtractor.extract(stringList));
        assertEquals(3, queries.size());
        assertEquals("fl=*,score&debugQuery=true&indent=true&q=HTTP&wt=javabin&fq=price:[100+TO+200]&version=2", queries.get(0));
        assertEquals("fl=*,score&debugQuery=true&indent=true&q=Comprehensive&wt=javabin&fq=price:[200+TO+300]&version=2", queries.get(1));
        assertEquals("fl=*,score&debugQuery=true&indent=true&q=Apache+Software+Foundation&wt=javabin&fq=cat:electronics&version=2", queries.get(2) );

    }

    public void testExtractQueriesFromFile() throws Exception {
        inputPath = FileUtils.findFileAsString("./com/plugtree/solrmeter/logTest.txt");
        LogExtractor logExtractor = new LogExtractor(regularExpression);
        List<String> queries  = new ArrayList<String>(logExtractor.extractFromFile(inputPath));
        assertEquals(3, queries.size());
        assertEquals("fl=*,score&debugQuery=true&indent=true&q=HTTP&wt=javabin&fq=price:[100+TO+200]&version=2", queries.get(0));
        assertEquals("fl=*,score&debugQuery=true&indent=true&q=Comprehensive&wt=javabin&fq=price:[200+TO+300]&version=2", queries.get(1));
        assertEquals("fl=*,score&debugQuery=true&indent=true&q=Apache+Software+Foundation&wt=javabin&fq=cat:electronics&version=2", queries.get(2) );

    }

   
}
