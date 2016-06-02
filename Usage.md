# Run the latest version #
The following steps should be followed to get SolrMeter up and running.
  1. Download latest released version.
  2. run it from the command line with ```java -jar solrmeter-{version}-jar-with-dependencies.jar```
  3. create files with information of [queries](queries.md), [fields](fields.md), [updates](updates.md) and [filter queries](filterqueries.md)
  4. specify the URL of Solr for updates and queries.
  5. run the executors with the "Start" button.

# Compile from trunk #
The following steps should be followed to compile and package SolrMeter ([Maven](http://maven.apache.org/index.html) is required)
  1. Clone the repository ```git clone https://github.com/tflobbe/solrmeter.git```
  2. cd to the root of the project.
  3. run 'mvn package'
  4. The generated jar file is under "solrmeter/target" directory. The jar is named solrmeter-{version}-jar-with-dependencies.jar
  5. run it like [Run the lastest version](Usage.md#run-the-latest-version) running the generated jar instead of the downloaded one.

# Just want to see how SolrMeter works? #
If you just want to see how SolrMeter works, you can try with the default configuration. SolrMeter default configuration was thought to run with Solr example.
Just complete the [Solr tutorial](http://lucene.apache.org/solr/tutorial.html), download SolrMeter latest version and run it with 
```java -jar solrmeter-{version}-jar-with-dependencies.jar```. 
