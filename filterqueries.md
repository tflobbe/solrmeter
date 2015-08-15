# Filter Queries File Configuration #
A simple text file, each line should be the extact text you want to put on the "fq" parameter. This file is used by the [FileQueryExtractor](http://solrmeter.googlecode.com/svn/trunk/sources/solrmeter/src/main/java/com/linebee/solrmeter/model/extractor/FileQueryExtractor.java) class.
You can put multiple filter queries on the same line if you want them all to be used as one (on the same query).
You can also use blank lines if you want the query to execute some queries with no filter queries.

## Example ##
```
category:animal

category:vegetable
categoty:vegetable price:[0 TO 10] 
categoty:vegetable price:[10 TO *] 
```

Also, you can check de [example file](http://solrmeter.googlecode.com/svn/trunk/sources/solrmeter/src/main/resources/example/filterQueries.txt) from the sorce code, this example works just fine with the [solr example](http://lucene.apache.org/solr/tutorial.html).