package com.linebee.solrmeter.model.extractor;

import java.util.List;

import com.linebee.solrmeter.model.FieldExtractor;

public class FileFieldExtractor implements FieldExtractor{

	private FileStringExtractor fileExtractor;
	
	public FileFieldExtractor(String filePath) {
		fileExtractor = new FileStringExtractor(filePath);
	}

	@Override
	public List<String> getFields() {
		return fileExtractor.getAllStrings();
	}

	@Override
	public String getRandomFacetField() {
		return fileExtractor.getRandomString();
	}

	/**
	 * Considering all fields as 'facet-ables'
	 */
	@Override
	public List<String> getFacetFields() {
		return fileExtractor.getAllStrings();
	}

}
