# Add your own statistics to SolrMeter #
Statistics in SolrMeter (from version 0.2.0) are pluggable. This means you can build your own statistic and see it from the SolrMeter UI.
To build your statistic, follow this steps:
## Build the **model class** ##
This class is going to observe at the operation executors (Query Executor, Update Executor or Optimize Executor). The statistic must implement the interfaces [QueryStatistic](http://solrmeter.googlecode.com/svn/trunk/sources/solrmeter/src/main/java/com/linebee/solrmeter/model/QueryStatistic.java), [UpdateStatistic](http://solrmeter.googlecode.com/svn/trunk/sources/solrmeter/src/main/java/com/linebee/solrmeter/model/UpdateStatistic.java) and/or [OptimizeStatistic](http://solrmeter.googlecode.com/svn/trunk/sources/solrmeter/src/main/java/com/linebee/solrmeter/model/OptimizeStatistic.java). You have to implement all the necessary interfaces for your purpose. It can be one, two or the three interfaces.
For Example:
```
/**
 * This is the model class.
 * This simple statistic stores the explain maps of every executed query.
 * This is just an example class. A class like this could use lots of memory on long tests.
 */
public class TestPluginStatistic implements QueryStatistic {
	
	private List<Map<String, String>> listExplains;
	
	public TestPluginStatistic() {
		super();
		listExplains= new LinkedList<Map<String,String>>();
	}

	@Override
	public void onExecutedQuery(QueryResponse response, long clientTime) {
		listExplains.add(response.getExplainMap());
	}

	@Override
	public void onFinishedTest() {
	}

	@Override
	public void onQueryError(QueryException exception) {
	}

	public List<Map<String, String>> getListExplains() {
		return new LinkedList<Map<String,String>>(listExplains);
	}

}
```
## Build the statistic **UI class** ##
You have to extend the [StatisticPanel](http://solrmeter.googlecode.com/svn/trunk/sources/solrmeter/src/main/java/com/linebee/solrmeter/view/StatisticPanel.java) and then implement it using Swing. One more thing is required in this class. If you need to use the **model class**, you will have to add it as a _Constructor Parameter_, and add the **@Inject** annotation to the constructor. This way, Guice (the dependency injection framework we are using) is going to add the correct object to it. **DO NOT CREATE A NEW INSTANCE OF THE MODEL** inside the constructor, otherwise, the instance you will have on the UI is not going to be the same instance that is observing the executors and things aren’t going to work.

```
/**
 * This is UI class
 */
public class TestPluginStatisticPanel extends StatisticPanel {
	
	private static final long serialVersionUID = 1L;

	private JLabel label;
	
	private TestPluginStatistic modelStatistic;
	
	/**
	 * This constructor is going to be invoked by Guice with an instance of TestPluginStatistic
	 * as parameter.
	 * Don't forget the @Inject annotation
	 * @param modelStatistic 
	 */
	@Inject
	public TestPluginStatisticPanel(TestPluginStatistic modelStatistic) {
		super();
		this.modelStatistic = modelStatistic;
		this.initGUI();
	}

	/**
	 * Just adds a label to show ome text
	 */
	private void initGUI() {
		label = new JLabel();
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		JScrollPane scrollPane = new JScrollPane(label);
		this.add(scrollPane);
	}

	@Override
	public String getStatisticName() {
		return "Test Plugin Statistic";
	}

	/**
	 * This method is executed every N secods to refresh the statistic view.
	 */
	@Override
	public void refreshView() {
		if(modelStatistic.getListExplains() == null || modelStatistic.getListExplains().isEmpty()) {
			label.setText("No query debug information yet");
		}else {
			StringBuffer buff = new StringBuffer();
			buff.append("<html>");
			for(Map<String, String> explain: modelStatistic.getListExplains()) {
				for(String key:explain.keySet()) {
					buff.append(key + ": &nbsp;&nbsp;" + explain.get(key).replaceAll("\n", "<br/>") + "<br/>");
				}
			}
			buff.append("</html>");
			label.setText(buff.toString());
		}
	}

}
```

## Add the classes to SolrMeter ##
You have two ways of adding the statistic to SolrMeter. The easiest way is to build the statistic code (the classes explained above) directly on SolrMeter’s project. If you do this, after creating the classes you need modify the file “statistics-config.xml” under “resources” directory adding the code:

```
	<statistic hasView="true">
		<name>PluginStatistic</name>
		<description>Statistic Added just to test the plugin architecture</description>
		<types>query</types>
		<scope>stress_test</scope>
		<model-class>com.plugtree.pluginStatistic.TestPluginStatistic</model-class>
		<view-class>com.plugtree.pluginStatistic.TestPluginStatisticPanel</view-class>
	</statistic>
```


The other way to add the statistic is with the statistic code on a different jar. You will have to add SolrMeter jar as a dependency to your project to see the interfaces you’ll need to implement. This doesn’t have to be the “with dependencies” jar and doesn’t have to be added on compile time (if you are using maven, it should be provided-scoped). Create your jar file. On the directory where you have the solrmeter jar (the executable jar) create a directory named “plugins”, and add your jar inside it. Inside that same directory, create a file named “statistics-config.xml” and add the following xml code:

```
<statistics>
	<statistic hasView="true">
		<name>PluginStatistic</name>
		<description>Statistic Added just to test the plugin architecture</description>
		<types>query</types>
		<scope>stress_test</scope>
		<model-class>com.plugtree.pluginStatistic.TestPluginStatistic</model-class>
		<view-class>com.plugtree.pluginStatistic.TestPluginStatisticPanel</view-class>
	</statistic>
</statistics>
```

In both cases this is what the xml attributes and elements mean:
  * **statistics:** The collection of all statistics. If you have more than one plugin statistic you should add it inside this element. This element is required.
  * **statisitc:** One element for each statistic you want to add to SolrMeter. This element is required.
  * **hasView:** This attribute tells that your statistic is going to be shown on the UI or not. Most of the times this attribute is going to be "true" (which is the default value) but you may build a statistic that doesn't have a view. This element is not required, default vaue is "true".
  * **name:** Of course, this is the name of the statistic. It can't be repeated and it can't have whitespaces or special characters. This name will be shown from the settings panel to select or unselect the statistic. This element is required.
  * **description:** A brief description of what the statistic can do. This text will be also shown from the settings panel. This element is not required.
  * **types:** Here you should put the type of statistic you just built. It's a comma separated list of types, wich can be "query", "update", "optimize". This types has to be related with the interfaces you implemented on the model class. For example, if you built a statistic like the one of the example, then the type can only be "query", because the class just implement QueryStatistic interface. This element is required.
  * **scope:** The scope of the statistic. The default will be stress\_test, which means that both instances will live while the same stress test goes on. Once it is finished, if a new stress test is started, the objects will not live anymore and new ones will be used. Be careful with this if you hold instances of them in other objects. You can check Guice to get a better understanding on how to use scopes. If you use “Singleton” instead, the instance will not be destroyed until the application is closed. You can also use “prototype”, which means that the instances will be new every time one is required. This is not recommended to use and will never work with statistics with view. This element is not required, the default value is "stress\_test".
  * **model-class:** The full name of the class representing the model Class. This element is required.
  * **view-class:** The full name of the class representing the view class. This is not required if the attribute “hasView” is set to false.

Run SolrMeter, you should see your statistic right away. If you don't, check the settings menu, on the "statistics" tab and make sure that yours is selected.

**If you think you have built a statistic that is usefull to de community, please share it. You can send the code to the group or upload the patch to the issues page and we will add it to the trunk.**

Download the source code or the compiled statistic of the example from the [downloads](http://code.google.com/p/solrmeter/downloads/list) page.