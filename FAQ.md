

# I have an error that says Invalid version or the data in not in 'ja vabin' format #
Maybe you should check out this page: http://code.google.com/p/solrmeter/wiki/SolrJVersionError

# I stop the queries and updates and I still see in the logs some queries being executed to Solr #
Do those queries like this? "_INFO: [.md](.md) webapp=/solr path=/admin/ping params={wt=javabin&version=1} hits=0 status=0 QTime=4_"
In that case, those are not regular queries, those are pings that are executed to make sure that Solr is up. If you think that these queries can interfere with your stress test, you can disable them by adding the property **solrConnectedButton.pingInterval** to 0 on the properties file or at the "Advanced" settings of the UI.