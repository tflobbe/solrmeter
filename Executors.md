
# Executors #
The responsibility of _executors_ is to organize the execution of operations against a Solr server. Operations are _query_, _update_ and _optimize_.

## Built-in executors ##
Since version 0.2.0, there are two built in executors for queries and two for updates. Only one for optimize.

### Random Executor ###
This executor is available for both, queries and updates (See [QueryExecutorRandomImpl](http://solrmeter.googlecode.com/svn/trunk/sources/solrmeter/src/main/java/com/plugtree/solrmeter/model/executor/QueryExecutorRandomImpl.java) and [UpdateExecutorRandomImpl](http://solrmeter.googlecode.com/svn/trunk/sources/solrmeter/src/main/java/com/plugtree/solrmeter/model/executor/UpdateExecutorRandomImpl.java)). These were the only available executors on version 0.1.x.
For random executors, the user specify the number of operations per minute, and that number of operation is going to be executed, but with no order or constant interval. For example, if the number of operations per minute is 3, then 2 operations could be executed in the first 5 seconds of the minute (even in the same instant) and the other one on the second 58 of that same minute.
This executor tries to simulate real use where users don't execute queries on an exact interval.
To select this executor, write "random" the Executor field in the settings panel (for queries or updates)<br />
This is an example of how the random executor could behave with 3 QPM:<br />
<img src='http://solrmeter.googlecode.com/svn/wiki/imgs/RandomExecutor.jpg' /><br />
Each horizontal line represents a thread during one minute, and the red rows represent queries or updates.

### Constant Executor ###
This executor is available for both, queries and updates (See [QueryExecutorConstantImpl](http://solrmeter.googlecode.com/svn/trunk/sources/solrmeter/src/main/java/com/plugtree/solrmeter/model/executor/QueryExecutorConstantImpl.java) and [UpdateExecutorConstantImpl](http://solrmeter.googlecode.com/svn/trunk/sources/solrmeter/src/main/java/com/plugtree/solrmeter/model/executor/UpdateExecutorConstantImpl.java)).
For Constant executors, the user specifies the number of operations to be executed in a minute, and an interval between operations is calculated to be equal and to complete the specified number of operation in a minute.
For example, if the user specifies 60 operations per minute, then, one operation is going to be executed every second. If the user specify 30 operations, then one operation every 2 seconds, and so on.
To select this executor, write "constant" the Executor field in the settings panel (for queries or updates)<br />
This is an example of how the constant executor will behave with 3 QPM:<br />
<img src='http://solrmeter.googlecode.com/svn/wiki/imgs/ConstantExecutor.jpg' /><br />
The horizontal line represents a thread during a minute, and the red rows represent queries or updates

### On Demand Executor ###
This is the only executor available for optimize operations, and it's only available for this operation (See [OnDemandOptimizeExecutor](http://solrmeter.googlecode.com/svn/trunk/sources/solrmeter/src/main/java/com/plugtree/solrmeter/model/executor/OnDemandOptimizeExecutor.java)).
With On Demand Executor the operation will only be executed when the user specifies so, just once (for example, hitting the "Optimize Now" button from the UI).

## Custom Executors ##
Since version 0.2.0 it is also possible to build custom executors and add them to SolrMeter as plug-in.
This is an example on how to do it:

### Create a Java Project ###
First create a java project and add SolrMeter as a dependency. You will also need Guice2.0 as dependency.

### Create the Executor ###
Then, create a class that implements QueryExecutor, UpdateExecutor or OptimizeExecutor, or you can extend any of the existing ones if you want. For example:

```
@StressTestScope
public class ExampleExecutor extends QueryExecutorConstantImpl {
	
	private int operationNumber = 20;
	
	@Inject
	public ExampleExecutor(FieldExtractor facetFieldExtractor,
			@Named("filterQueryExtractor") QueryExtractor filterQueryExtractor,
			@Named("queryExtractor") QueryExtractor queryExtractor) {
		super(facetFieldExtractor, filterQueryExtractor, queryExtractor);
		operationNumber = 20;
	}
	
	@Override
	public void notifyQueryExecuted(QueryResponse response, long clientTime) {
		super.notifyQueryExecuted(response, clientTime);
		if(operationNumber-- <= 0) {
			this.stop();
		}
		
	}

}

```

### Create the new Guice Module ###
Then, create a class that extends com.plugtree.solrmeter.ModelModule and override the methods getQueryExecutorsMap(),getUpdateExecutorsMap() or getOptimizeExecutorsMap(). This methods returns a map with all the existing query executors, update executors and optimize executors. Add your new executor to that map, for example:

```
public class ExtendedModelModule extends ModelModule {
	
	@Override
	protected Map<String, Class<? extends QueryExecutor>> getQueryExecutorsMap() {
		Map<String, Class<? extends QueryExecutor>> map = super.getQueryExecutorsMap();
		map.put("example", ExampleExecutor.class);
		return map;
	}
}

```

### Add your class to SolrMeter classpath ###
To do this, generate de jar file, and add it to a “plugins” directory located on the same place as the SolrMeter executable jar file.

### Tell SolrMeter to use your module ###
To do this, edit the “solrmeter.properties” file inside the executable jar file of  solrmeter and replace the line guice.modelModule=com.plugtree.example.ExtendedModelModule with guice.modelModule=yourPackage.YourNewModule.
For Example:
```
...
#Guice Modules
guice.statisticsModule=com.plugtree.solrmeter.StatisticsModule
guice.modelModule=com.plugtree.example.ExtendedModelModule
guice.standalonePresentationModule=com.plugtree.solrmeter.StandalonePresentationModule
...
```

(I know, modify the solrmeter.properties file is an ugly thing to do inside the jar file, this way of changing properties will be eliminated when issues [#38](http://code.google.com/p/solrmeter/issues/detail?id=38) and [#39](http://code.google.com/p/solrmeter/issues/detail?id=39) are done :) )


Run solrmeter and you can select this new executor by typing "example" on the Query Executor Field of the Query Settings. The example will run 20 queries like the QueryExecutorConstantImpl and then stops itself.

The full example code can be downloaded from the [downloads](http://code.google.com/p/solrmeter/downloads/list) page.

**If you think you have built an executorthat is usefull to de community, please share it. You can send the code to the group or upload the patch to the issues page and we will add it to the trunk.**