# Run the latest version #
The following steps should be followed to get SolrMeter up and running.
  1. Download latest released version.
  1. run it from the command line with "java -jar solrmeter-{version}-jar-with-dependencies.jar"
  1. create files with information of [queries](queries.md), [fields](fields.md), [updates](updates.md) and [filter queries](filterqueries.md)
  1. specify the URL of Solr for updates and queries.
  1. run the executors with the "Start" button.

# Compile from trunk #
The following steps should be followed to compile and package SolrMeter ([Maven](http://maven.apache.org/index.html) is required)
  1. Download sources from trunk from http://solrmeter.googlecode.com/svn/trunk/sources
  1. cd to the root of the project.
  1. run 'mvn package'
  1. The generated jar file is under "solrmeter/target" directory. The jar is named solrmeter-{version}-jar-with-dependencies.jar
  1. run it like [Run the lastest version](Usage#Run_the_latest_version.md) running the generated jar instead of the downloaded one.

# Just want to see how SolrMeter works? #
If you just want to see how SolrMeter works, you can try with the default configuration. SolrMeter default configuration was thought to run with Solr example.
Just complete the [Solr tutorial](http://lucene.apache.org/solr/tutorial.html), download SolrMeter latest version and run it with "java -jar solrmeter-{version}-jar-with-dependencies.jar". Or you can run SolrMeter with [Java Web Start](http://solrmeter.googlecode.com/svn/JWS/solrmeter.jnlp).