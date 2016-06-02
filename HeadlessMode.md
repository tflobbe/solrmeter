# Run SolrMeter in headless mode #
Until version 0.3.0, you can't run SolrMeter without UI, but since the [issue #94](../../issues/94) was contributed it is possible to run in "headless mode". There has been no release after this contribution was committed, so if you need this feature, you'll need to [build from trunk](Usage.md#Compile_from_trunk). To understand how this feature works see:

http://www.datastax.com/dev/blog/running-solrmeter-with-datastax-enterprise-in-headless-mode

(As mentioned in one of the blog's comments, you don't need to apply the patch, as it is already included in the trunk)
