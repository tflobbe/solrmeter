package com.linebee.solrmeter.mock;

import java.util.LinkedList;
import java.util.List;

import com.linebee.solrmeter.model.FieldExtractor;

public class MockFieldExtractor implements FieldExtractor {

	@Override
	public List<String> getFacetFields() {
		return new LinkedList<String>();
	}

	@Override
	public List<String> getFields() {
		return new LinkedList<String>();
	}

	@Override
	public String getRandomFacetField() {
		return "";
	}

}
