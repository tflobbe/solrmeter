package com.linebee.stressTestScope;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
/**
 * 
 * @author tflobbe
 *
 */
public class StressTestScopeModule extends AbstractModule {

	 public void configure() {
		 StressTestScopeImpl batchScope = new StressTestScopeImpl();

		    // tell Guice about the scope
		    bindScope(StressTestScope.class, batchScope);

		    // make our scope instance injectable
		    bind(StressTestScopeImpl.class)
		        .annotatedWith(Names.named("StressTestScope"))
		        .toInstance(batchScope);
		  }

}
