# What is SolrMeter? #
SolrMeter allows simple and fast configuration to run stress tests (or load tests, however you call them) to Solr installations.
It measures time of  queries, updates, commits and optimizes and show them on simple charts.

Current version of SolrMeter (0.3.0) allows you to:
  * Execute Queries to a Solr server.
  * Execute Update/Inserts to a Solr server (not necessary the same as queries).
  * Obtain queries and documents to update from a text file.
  * Execute optimize (to see Solr server configured to update/insert documents)
  * Add filter queries to the query
  * Use facets (randomly obtained from a text file with field names)
  * Select facet method.
  * Many charts and statistics (see screenshots)
  * Import/Export test configuration.
  * Extract queries from Solr's log files

See [Overview](Overview.md) for more details and some screenshots or [Tutorial](Tutorial.md) to dive into docs.

# What's next and what's new? #
**New released version 0.3.0. See [Downloads](http://code.google.com/p/solrmeter/downloads/list)**
Click [here](http://code.google.com/p/solrmeter/issues/list?can=1&q=Milestone%3DRelease-0.3.0+status%3AResolved+&colspec=ID+Type+Status+Priority+Milestone+Owner+Summary&cells=tiles) to see the new stuff.