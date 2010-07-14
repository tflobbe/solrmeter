package com.linebee.stressTestScope;

import com.google.inject.AbstractModule;
/**
 * 
 * @author tflobbe
 *
 */
public class TestModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(TestObject.class);
		bind(TestObject2.class);

	}

}
