# Updates / Adds file configuration #
A simple text file, each line representing a document that is going to be added to solr. The format is:
`fieldName:fieldValue;`
All required fields must be on the file.
## Example: ##
```
id:1;name:dog;category:animal
id:2;name:cat;category:animal
id:3;name:lettuce;category:vegetable
```

## Escaping characters ##
if you want to escape a semicolon, use a slash and then a semicolon "\;". If you want to add a slash, add two slashes "\\".


Also, you can check de [example file](http://solrmeter.googlecode.com/svn/trunk/sources/solrmeter/src/main/resources/example/updates.txt) from the sorce code, this example works just fine with the [solr example](http://lucene.apache.org/solr/tutorial.html).