package com.linebee.solrmeter.model;

import java.util.List;

public interface FieldExtractor {

	/**
	 * @return The list of all fields of schema
	 */
	List<String> getFields();
	
	/**
	 * @return The list of all fields that can be used for faceting
	 */
	List<String> getFacetFields();
	
	/**
	 * @return One random field that can be used for faceting
	 */
	String getRandomFacetField();
	
}
