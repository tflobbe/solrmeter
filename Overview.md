This is a Screenshot overview of SolrMeter. This needs to be updated, SolrMeter's UI changed a little bit.

While you run the test, watch the updated data on the charts, like the Pie chart:

<a href='http://code.google.com/p/solrmeter/wiki/Screenshots#Pie_Chart1'><img src='http://solrmeter.googlecode.com/svn/wiki/Screenshots/pie-chart1.PNG' height='250' width='300' /></a> <a href='http://code.google.com/p/solrmeter/wiki/Screenshots#Pie_Chart2'><img src='http://solrmeter.googlecode.com/svn/wiki/Screenshots/pie-chart2.PNG' height='250' width='300' /></a>

You can also watch the Query time histogram, that shows the distribution of obtained query times:

<a href='http://code.google.com/p/solrmeter/wiki/Screenshots#Histogram'><img src='http://solrmeter.googlecode.com/svn/wiki/Screenshots/histogram-chart.PNG' align='middle' height='250' width='300' /></a>

See how the query time average has changed during the current test:

<a href='http://code.google.com/p/solrmeter/wiki/Screenshots#Query_History'><img src='http://solrmeter.googlecode.com/svn/wiki/Screenshots/query-history-chart.PNG' align='middle' height='250' width='300' /></a>

See the log of ocurred errors when trying to query, add, commit or optimize. (Double click the row to see the full stack trace):

<a href='http://code.google.com/p/solrmeter/wiki/Screenshots#Error_Log'><img src='http://solrmeter.googlecode.com/svn/wiki/Screenshots/error-log-and-stacktrace.PNG' align='middle' height='250' width='300' /></a>

See also the times queries, updates, commits and optimize take during the test. You can see them all together (usefull to see when one operation affects another one) or you can filter to see each one separately:

<a href='http://code.google.com/p/solrmeter/wiki/Screenshots#Time_Line'><img src='http://solrmeter.googlecode.com/svn/wiki/Screenshots/timeline-chart.PNG' align='middle' height='250' width='300' /></a>

You can also see last executed queries of the test, the time they took, how many results they brougth or if it result in an error. Also Statistics like median, mode, variance, last minute average, last ten minutes average and some more:

<a href='http://code.google.com/p/solrmeter/wiki/Screenshots#Query_Statistics'><img src='http://solrmeter.googlecode.com/svn/wiki/Screenshots/query-statistics.PNG' height='250' width='300' /></a> <a href='http://code.google.com/p/solrmeter/wiki/Screenshots#Query_Statistics2'><img src='http://solrmeter.googlecode.com/svn/wiki/Screenshots/query-statistics2.PNG' height='250' width='300' /></a>

From version 0.2.0, you have also a Cache History Statistic, in which you can see the usage of every cache:

<a href='http://code.google.com/p/solrmeter/wiki/Screenshots#CacheHistoryStatistic'><img src='http://solrmeter.googlecode.com/svn/wiki/Screenshots/CacheHistoryChart.jpg' height='250' width='300' /></a>

... Or hit ratio comparison of all Solr caches:

<a href='http://code.google.com/p/solrmeter/wiki/Screenshots#HitRatioStatistic'><img src='http://solrmeter.googlecode.com/svn/wiki/Screenshots/CacheHitRatio.jpg' height='250' width='300' /></a>

See the [Usage](Usage.md) page for details on how to download/use/contribute SolrMeter.