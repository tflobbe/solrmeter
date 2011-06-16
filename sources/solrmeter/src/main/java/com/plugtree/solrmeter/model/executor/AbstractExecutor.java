package com.plugtree.solrmeter.model.executor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.csv.CSVUtils;
import org.apache.commons.lang.StringEscapeUtils;

public abstract class AbstractExecutor {
	
	/**
	 * Extra parameters specified to the query
	 */
	private Map<String, String> extraParameters;
	
	protected void loadExtraParameters(String property) {
			extraParameters = new HashMap<String, String>();
			
			if(property == null || "".equals(property.trim())) {
				return;
			}
			
			String[] values;
			try {
				values = CSVUtils.parseLine(property);

				for (String val : values) {
					val = StringEscapeUtils.unescapeCsv(val);
					
					int equalSignIndex = val.indexOf("=");
					if(equalSignIndex > 0) {
						extraParameters.put(val.substring(0, equalSignIndex).trim(), val.substring(equalSignIndex + 1).trim());
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	
	public Map<String, String> getExtraParameters() {
		return extraParameters;
	}

}
