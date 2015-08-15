# Fields File Configuration #
A simple text file, each line should be the a declared field of the schema. This fields are going to be used for faceting. (Only fields that you want to be used for faceting should be added to this file).

This file is going to be used by the [FileQueryExtractor](http://solrmeter.googlecode.com/svn/trunk/sources/solrmeter/src/main/java/com/linebee/solrmeter/model/extractor/FileFieldExtractor.java) class

## Example: ##

```
content
category
fileExtension
```

Also, you can check de [example file](http://solrmeter.googlecode.com/svn/trunk/sources/solrmeter/src/main/resources/example/fields.txt) from the sorce code, this example works just fine with the [solr example](http://lucene.apache.org/solr/tutorial.html).