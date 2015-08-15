

# Compile and Run #
First thing you need to do is download SolrMeter, either the compiled jar from the [Downloads](http://code.google.com/p/solrmeter/downloads/list) section or the source code from the [Source](http://code.google.com/p/solrmeter/source/checkout) section. [This](http://code.google.com/p/solrmeter/wiki/Usage) wiki page explains how to compile and run the source code.
Once you start SolrMeter you'll see the initial screen, probably with the “PLay” buttons and the “Optimize Now” button in red. This means that SolrMeter can't find a valid Solr instance on the specified URL (default URL for Solr is http://localhost:8983/solr, which is Solr default, so if you see this buttons in red then you are good to go).
<a href='http://solrmeter.googlecode.com/svn/wiki/imgs/greenButtons.png'><img src='http://solrmeter.googlecode.com/svn/wiki/imgs/greenButtons.png' height='250' width='450' /></a>
# Execute Queries to a Solr instance #
It's easy to tell SolrMeter to execute queries to your Solr instance. First thing you have to do is to create a text file on your PC with possible queries. Each line of this text file will be included as the “q” parameter of the request to Solr, so it will depend on how you need to test Solr. For example, if your application will use LuceneQueryParser, then a queries file could be something like:
```
text:(ipod OR ipad) AND cat:technology
text:(lucene) AND cat:books AND title:(action)
text:display AND cat:technology
….
```
on the other hand, if you are using a DismaxQueryParser in Solr, probably your text file will look like:
```
ipod ipad
lucene in action
display
technology
…
```
It's important to note that these are the queries that will be executed to Solr. [Here](http://code.google.com/p/solrmeter/wiki/queries) you can see another example of a queries file. When you are doing a real test, these should be the queries you'll be expecting from your real application (you could extract queries from the logs to create this file). It's also important to have as many different queries as possible, you don't want Solr to keep all the queries on cache because this wouldn't be a good test.
Once you created the queries file with a good number of different queries, you have to tell SolrMeter to use it. Go to the menu “Edit->Settings” and modify the “Queries File Path” property under the “Query Settings” tab. Browse to your file and select it.
<a href='http://solrmeter.googlecode.com/svn/wiki/imgs/selectQueriesFile.png'><img src='http://solrmeter.googlecode.com/svn/wiki/imgs/selectQueriesFile.png' height='250' width='450' /></a><br>
If you don't have Solr on <a href='http://localhost:8983/solr'>http://localhost:8983/solr</a> and your “Run” button was red, change the “URL Solr” property under the same tab, set the value to your URL.<br>
Press OK (I'll come back to explain what the other properties mean later)<br>
you should now see the “PLay” button in green.<br>
<a href='http://solrmeter.googlecode.com/svn/wiki/imgs/startBlack.png'><img src='http://solrmeter.googlecode.com/svn/wiki/imgs/startBlack.png' /></a><br>
Select the number of queries you want SolrMeter to execute every minute on the “Intended queries per minute” property of the query panel (on the main window).<br>
Finally, press the “Play” Button.<br>
You'll start seeing the numbers of the Query Console and the statistics changing. I'll explain the query console numbers and later the statistics.<br>
<h2>What does the Query Console numbers mean?</h2>
<a href='http://solrmeter.googlecode.com/svn/wiki/imgs/queryConsole.png'><img src='http://solrmeter.googlecode.com/svn/wiki/imgs/queryConsole.png' /></a><br>
<h3>Total Queries:</h3>
The total number of queries executed on this test.<br>
<h3>Total Query Time:</h3>
The sum of the Query time of all the executed queries in ms. This time is taken from Solr's response (QTime)<br>
<h3>Average Query Time:</h3>
The average of ms the queries take.<br>
<h3>Total Client Time:</h3>
The sum of the Query time of all the executed queries in ms, but measured from SolrMeter instead of Solr's QTime.<br>
<h3>Average Client Time:</h3>
The average of ms the queries take, considering the client time instead of Solr's QTime.<br>
<h3>Test Started At:</h3>
Indicates the moment when the test started.<br>
<h3>Total Errors:</h3>
The total number of queries that resulted in some kind of error in Solr.<br>
<h3>Intended Queries per minute:</h3>
The number of queries you want SolrMeter to execute.<br>
<h3>Actual Queries per minute:</h3>
The number of queries SolrMeter is actually executing (considering the last 10 seconds activity).<br>
<h3>Why is there a difference between “Intended” QPM and “Actual” QPM?</h3>
There are two main reasons why the Actual QPM might be different than the “Intended” QPM value. The “Random” Query executor executes queries without a defined frequency (see <a href='http://code.google.com/p/solrmeter/wiki/Executors'>this</a> page to understand why). The “Actual” QPM is calculated with the queries executed on the last 10 seconds; this is why this number will variate, sometimes it will be higher than the Intended QPM, and some times lower.<br>
The other reason why these two numbers can be different (the only reason if you are using the ConstantQueryExecutor) is that SolrMeter simply can't achieve from this machine the number of queries that you intend to. In <a href='http://code.google.com/p/solrmeter/wiki/Benchmarks'>this</a> page you'll find some benchmarks on what SolrMeter can or can't do. Of course, if you need more QPM, you could execute two SolrMeter in two different machines, the drawback here is that the statistics are not going to be complete. It might be helpful for some cases thought.<br>
<h2>What are all the other parameters of the “Query Settings” and what are they for?</h2>
<h3>Query Mode (standard / external):</h3>
This setting specify at a high level how you are going to execute your test. With the “external” mode, you have to basically give SolrMeter the exact parameters of the whole search request, including the query itself, filter queries, facets, facet mode, etc. If you select this mode, then you only have to add a text file like:<br>
<pre><code>q=Apple AND price:[0 TO 100] OR popularity:[0 TO 3]&amp;sort=popularity desc&amp;fq=weight:[10 TO *]&amp;rows=100<br>
</code></pre>
This is an excellent choice if you have a real sample of queries as they are going to be executed on your search application.<br>
The “standard” query mode, lets you execute more “random” requests, you’ll have to specify the <a href='http://code.google.com/p/solrmeter/wiki/queries'>queries</a>, the <a href='http://code.google.com/p/solrmeter/wiki/fields'>fields for faceting</a>, the <a href='http://code.google.com/p/solrmeter/wiki/filterqueries'>filter queries</a> separated, and then SolrMeter will mix it all to generate the search requests. This option is better for start, when you don’t have real logs.<br>
<h3>Solr URL:</h3>
The url where the “Query Solr” (in case you have master and slave) is located. It has to include the core if you are using multicore.<br>
<h3>Use Facets (true / false):</h3>
Tell SolrMeter if you want to set the attribute “facet=true” and add facet fields.<br>
<h3>Fields File:</h3>
The location of the file that contains the fields on which you want to facet on. You don’t need to specify this file if you are not going to facet. <a href='http://code.google.com/p/solrmeter/wiki/fields'>see the example file</a>.<br>
<h3>Use Filter Queries (true / false):</h3>
Tell SolrMeter if you want to add filter queries to the search request.<br>
<h3>Filter Query File Path:</h3>
Path to a text file with filter queries on it.<a href='http://code.google.com/p/solrmeter/wiki/filterqueries'>see the example file</a>.<br>
<h3>Queries File Path:</h3>
Path to a text file with queries on it. <a href='http://code.google.com/p/solrmeter/wiki/queries'>See the example file</a>.<br>
<h3>Query Type:</h3>
Solr’s query type to use. This is the <a href='http://wiki.apache.org/solr/CoreQueryParameters#qt'>qt</a> parameter of a regular request.<br>
<h3>Extra Parameter:</h3>
Here you can add static extra parameter to be used  on the query. For example: "hl=true,hl.fl=text,hl.fragsize=150"<br>
These parameters are going to be added in every search request. Parameters have to be separated by commas.<br>
NOTE: This has changed on trunk, now this has a better and more user-friendly interface.<br>
<h3>Query Executor:</h3>
The query executor to use. See <a href='http://code.google.com/p/solrmeter/wiki/Executors'>this</a> page for more details on Query Executors.<br>
<h3>Add Random Extra Parameter (true / false):</h3>
Tell SolrMeter if you want to add (random) extra parameters from an external text file. This is useful when you want to test for example using spatial search. You could have a text file with different locations and distances like:<br>
<pre><code>&amp;fq={!bbox}&amp;sfield=store&amp;pt=32.15,-93.85&amp;d=5<br>
&amp;fq={!bbox}&amp;sfield=store&amp;pt=17.43,-18.26&amp;d=5<br>
&amp;fq={!bbox}&amp;sfield=store&amp;pt=43.15,-92.53&amp;d=5<br>
&amp;fq={!bbox}&amp;sfield=store&amp;pt=12.45,-23.56&amp;d=5<br>
&amp;fq={!bbox}&amp;sfield=store&amp;pt=29.39,-73.65&amp;d=5<br>
</code></pre>
All those lines are going to be randomly added to the search request. Parameters must be separated by “&”.<br>
<h3>Extra Params file path:</h3>
Path to a text file with the extra parameters to be added to the search request.<br>
<br>
<h2>How to add Facets to my queries?</h2>
There are many ways of adding facets to your search depending on your needs. If your application will ALWAYS use the same facet fields, then the best thing to do is to add them as extra parameters as described in the <a href='http://code.google.com/p/solrmeter/wiki/Tutorial#Extra_Parameter:'>"Add extra parameters"</a> section of this tutorial.<br>
If you know that your application will allow facets in some fields, but those will change with each request, you can use the <a href='http://code.google.com/p/solrmeter/wiki/Tutorial#Fields_File:'>"fields"</a> file plus set the combo box <a href='http://code.google.com/p/solrmeter/wiki/Tutorial#Use_Facets_(true_/_false):'>"Use facets" to "true".</a>
If your application will have different sets of facets (in some cases will facet in field1, field2 and field3 together, but in some other cases will facet in field1, field4 and field5, then you can use the <a href='http://code.google.com/p/solrmeter/wiki/Tutorial#Add_Random_Extra_Parameter_(true_/_false):'>"random extra parameters"</a>. This is very similar than using the <a href='http://code.google.com/p/solrmeter/wiki/Tutorial#Query_Mode_(standard_/_external):'>"external" mode</a> but you just need to add the extra parameters, not all.<br>
Finally, if you are using the <a href='http://code.google.com/p/solrmeter/wiki/Tutorial#Query_Mode_(standard_/_external):'>"external" query mode</a>, you'll need to add the facet fields to each request.<br>
<h2>How to add Filter Queries?</h2>
Well, the options here are very similar to the section <a href='http://code.google.com/p/solrmeter/wiki/Tutorial#How_to_add_Facets_to_my_queries?'>"How to add Facets to my queries"</a>, the only difference is that, instead of "Use Facets" you need to set the combo <a href='http://code.google.com/p/solrmeter/wiki/Tutorial#Use_Filter_Queries_(true_/_false):'>"Use Filter Queries"</a> to true, and instead of a "fields" file you'll have to provide a <a href='http://code.google.com/p/solrmeter/wiki/Tutorial#Filter_Query_File_Path:'>"filters" file</a>. The other options (extra parameters, extra parameters file and external mode) can be used in the same way as with facets.<br>
<h2>How to add Extra parameters, like highlighting or spellchecking?</h2>
If those are static, they can be added as described in the <a href='http://code.google.com/p/solrmeter/wiki/Tutorial#Extra_Parameter:'>"Add Extra Parameters"</a> section. If those will change with each request, the best way to do it is by using the <a href='http://code.google.com/p/solrmeter/wiki/Tutorial#Add_Random_Extra_Parameter_(true_/_false):'>"Random Extra Params"</a>.<br>
<h2>How can I run the exact same queries that were executed to my production Solr Instance?</h2>
Since Version 0.3.0 (thanks to <a href='http://code.google.com/p/solrmeter/issues/detail?id=90'>this</a> issue you can extract queries from a Solr log and generate an <a href='http://code.google.com/p/solrmeter/wiki/Tutorial#Query_Mode_(standard_/_external):'>"external queries file"</a>. This means that the exact same queries (with all of the parameters) will be executed in your test, however, this DOESN'T mean that they are going to be executed in the same order or frequency!. Queries are extracted from the external file RANDOMLY and the original execution time is not maintained, those queries will be executed in a random instant and in a random order. <a href='http://code.google.com/p/solrmeter/issues/detail?id=54'>There is an open issue</a> to execute queries from the queries file in a sequential order, however, it is still not implemented. It should be very simple to implement. <a href='http://code.google.com/p/solrmeter/issues/detail?id=24'>There is also an open issue</a> to record and load tests in order to be able to execute the exact same query with different versions/configurations/servers/whatever.<br>
<h1>What does the statistics mean?</h1>
<h2>Histogram</h2>
This statistic shows the number of queries during the test that fall into the different buckets of <a href='http://code.google.com/p/solrmeter/issues/detail?id=100'>100 milliseconds</a>. SolrMeter uses Solr's QTime to build this chart.<br>
See <a href='http://code.google.com/p/solrmeter/wiki/Screenshots#Histogram'>http://code.google.com/p/solrmeter/wiki/Screenshots#Histogram</a>
<h2>Pie Chart</h2>
This statistic shows in a pie diagram the percentage of queries that fall into the different time intervals. By default the intervals are:<br>
From 0 to 500 ms<br>
From 501 to 1000 ms<br>
From 1001 to 2000 ms<br>
More than 2000 ms<br>
But those intervals can be changed by pressing the "Customize" button.<br>
See <a href='http://code.google.com/p/solrmeter/wiki/Screenshots#Pie_Chart1'>http://code.google.com/p/solrmeter/wiki/Screenshots#Pie_Chart1</a>
<h2>Query Time History</h2>
This statistic shows how the average query time evolved during the test. Every 10 seconds SolrMeter calculates the average (only considering the queries executed in the last 10 seconds) and displays it in another column of the diagram. It will show if during the test, there is something that is causing the average query time to increase (like an optimize could do, or a big segment merge).<br>
<a href='http://code.google.com/p/solrmeter/wiki/Screenshots#Query_History'>http://code.google.com/p/solrmeter/wiki/Screenshots#Query_History</a>
<h2>Operations Time Line</h2>
This Statistic shows together in one chart how long are the different operations taking. Each query is plotted into the chart, as every update, every commit and every optimize (Only those operations executed by SolrMeter are plotted, if you are using Solr's autocommit or committing from another process for example, the commit operation will not be added to the chart).<br>
This diagram is useful to see if some operations are introducing latency to others.<br>
See <a href='http://code.google.com/p/solrmeter/wiki/Screenshots#Time_Line'>http://code.google.com/p/solrmeter/wiki/Screenshots#Time_Line</a>
<h2>Query Statistic</h2>
This statistic shows in a table the last 400 executed queries, their status, QTime and result count. It also shows some statistics information about the current test like Median, Mode, Variance, Standard Deviation, Total Average, Last Minute Average, Last 10 minute average plus the last error date.<br>
See <a href='http://code.google.com/p/solrmeter/wiki/Screenshots#Query_Statistics'>http://code.google.com/p/solrmeter/wiki/Screenshots#Query_Statistics</a>
<h2>Error Log</h2>
This statistic shows displays all those request that returned to SolrMeter as an exception from Solr. It discriminates the operations in which the error occurred and if you double-click on one of them you can see the full stacktrace of the exception.<br>
See <a href='http://code.google.com/p/solrmeter/wiki/Screenshots#Error_Log'>http://code.google.com/p/solrmeter/wiki/Screenshots#Error_Log</a>
<h2>Cache Statistic</h2>
This statistic shows the Cache information obtained from Solr's request Handler. It can compare the hit ratios of the different caches all together in one diagram, but you can also select one cache in particular and see how the different attributes of it evolved through the query (lookups, hits, inserts and evictions). You can also see the cumulative value in the bottom table. This diagram extracts the information from Solr so it really doesn't matter if you are performing the operations from SolrMeter, the cache information is the one published by Solr.<br>
See <a href='http://code.google.com/p/solrmeter/wiki/Screenshots#CacheHistoryStatistic'>http://code.google.com/p/solrmeter/wiki/Screenshots#CacheHistoryStatistic</a>
<h2>Query Panel</h2>
This is actually not a statistic, it is simply a panel from which you can run regular queries to Solr in a very simple way.<br>
<h1>How to execute Updates?</h1>
Updates can be run in a very similar way than queries, however the updates file has a special format that has to be respected.<br>
First, in the “Update Settings” tab of the Settings type the URL of the “update” Solr. It can be the same Solr instance that you use for queries, but it can also use a different one (for example, if you are planning to use master/slave Solr instances).<br>
If you want SolrMeter to issue commits you should disable autocommits in Solr and select “false” in SolrMeter’s combo-box “Is Solr using autocommit?” (we should rename this field). If SolrMeter issues the commits, you’ll see information about them in the statistics, otherwise you won’t.<br>
If SolrMeter will commit, then you can select either max docs added before a commit or max time before a commit. These are similar parameters to the autocommit ones, however “max docs” only makes sense if all the documents are being added from SolrMeter (not external indexers), as we only keep track of the added/updated docs from SolrMeter.<br>
At this point, there is no way to issue soft commits from SolrMeter (you could set that up in Solr with autoSoftCommit). There is an open <a href='http://code.google.com/p/solrmeter/issues/detail?id=91'>issue</a> for that.<br>
In the “updates file path” you should select the file with the updates/adds that you want SolrMeter to send to Solr. The format of that file is described <a href='http://code.google.com/p/solrmeter/wiki/updates'>here</a>.<br>
Finally, and as with queries, you can select if you want the updates to be executed on a constant time or at a random time. See <a href='http://code.google.com/p/solrmeter/wiki/Executors'>here</a> for more details about the executors.<br>
<h1>How can I save / load the settings for my test? How can I change the default?</h1>
This is something simple to do. Set all the settings as you want to run your tests. The you can export them from the menu File->Export. This will generate a "yourname.smc.xml", it's simply a xml file with all the properties that you have configured on it. You can later use the File->Import menu to import any configuration.<br>
If you want to override the default configuration of your SolrMeter, you can open the "settings" page and click on the "OK and override defaults" button. This will export the settings in the same way as "File->Export", call the file solrmeter.smc.xml and place it in the same directory where the solrmeter executable jar file is located. SolrMeter will always try to use this file when loading the configurations.<br>
<br>
<h1>How can I use basic HTTP Authentication?</h1>
Since <a href='http://code.google.com/p/solrmeter/issues/detail?id=83'>issue#83</a> was resolved, you can use basic HTTP authentication, simply open the settings page and enter User and password there.<br>
At this point, it's not possible to use different users and passwords for the query Solr server and the Update Solr server, they'll both use the same.