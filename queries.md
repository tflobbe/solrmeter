# Queries File Configuration #
A simple text file, each line should be the extact text you want to put on the "q" parameter.
This file is used by the [FileQueryExtractor](http://solrmeter.googlecode.com/svn/trunk/sources/solrmeter/src/main/java/com/linebee/solrmeter/model/extractor/FileQueryExtractor.java) class.

## Example: ##
```
car
pig
red
dog category:animal
"solr roks"
category:(animal OR vegetable)
```

Before creating this file you have to think on the search handler you are going to use.

This file is going to be referenced from the "settings" window of the UI.

Also, you can check de [example file](http://solrmeter.googlecode.com/svn/trunk/sources/solrmeter/src/main/resources/example/queries.txt) from the sorce code, this example works just fine with the [solr example](http://lucene.apache.org/solr/tutorial.html).