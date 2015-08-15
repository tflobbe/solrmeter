# Invalid version or the data in not in 'javabin' format #

The reason for this error is that Solr has changed the javabin format on 3\_x branch and on trunk and SolrMeter uses SolrJ.
The whole trace would be something like this:

```
org.apache.solr.client.solrj.SolrServerException: Error executing query
        at org.apache.solr.client.solrj.request.QueryRequest.process(QueryRequest.java:95)
        at org.apache.solr.client.solrj.SolrServer.query(SolrServer.java:118)
        at com.linebee.solrmeter.model.task.QueryThread.executeQuery(QueryThread.java:103)
        at com.linebee.solrmeter.model.task.QueryThread.executeOperation(QueryThread.java:74)
        at com.linebee.solrmeter.model.task.AbstractOperationThread.run(AbstractOperationThread.java:53)
Caused by: java.lang.RuntimeException: Invalid version or the data in not in 'javabin' format
        at org.apache.solr.common.util.JavaBinCodec.unmarshal(JavaBinCodec.java:99)
        at org.apache.solr.client.solrj.impl.BinaryResponseParser.processResponse(BinaryResponseParser.java:39)
        at org.apache.solr.client.solrj.impl.CommonsHttpSolrServer.request(CommonsHttpSolrServer.java:466)
        at org.apache.solr.client.solrj.impl.CommonsHttpSolrServer.request(CommonsHttpSolrServer.java:243)
        at org.apache.solr.client.solrj.request.QueryRequest.process(QueryRequest.java:89)
        ... 4 more

```

On this link you can see the discussion of the change:
http://lucene.472066.n3.nabble.com/SolrJ-new-javabin-format-td1715912.html


# How to fix this problem #

If you are using the **SolrMeter version 0.2.0**, you have probably seen that here are two different versions available to download. If you are using Solr 1.4.x or previous, download the one called [solrmeter-0.2.0-jar-with-dependencies.jar](http://code.google.com/p/solrmeter/downloads/detail?name=solrmeter-0.2.0-jar-with-dependencies.jar&can=2&q=), if you are using Solr 3.x or Solr from trunk, download the one called [solrmeter-0.2.0-jar-with-dependencies\_3\_1\_4\_0.jar](http://code.google.com/p/solrmeter/downloads/detail?name=solrmeter-0.2.0-jar-with-dependencies_3_1_4_0.jar&can=2&q=).

If you are using the **SolrMeter trunk** make sure you are using the right version of SolrJ dependency:

```
<dependency>
	<artifactId>solr-solrj</artifactId>
	<groupId>org.apache.solr</groupId>
	<version>3.1.0</version>
</dependency>
```
... or higher.