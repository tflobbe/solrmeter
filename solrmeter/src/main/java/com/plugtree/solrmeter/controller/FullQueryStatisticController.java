package com.plugtree.solrmeter.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import au.com.bytecode.opencsv.CSVWriter;

import com.google.inject.Inject;
import com.plugtree.solrmeter.SolrMeterMain;
import com.plugtree.solrmeter.model.statistic.QueryLogStatistic;
import com.plugtree.solrmeter.model.statistic.QueryLogStatistic.QueryLogValue;
import com.plugtree.solrmeter.view.I18n;
import com.plugtree.stressTestScope.StressTestScope;

@StressTestScope
public class FullQueryStatisticController {
	
	private QueryLogStatistic queryLogStatistic;
	
	private JFileChooser fc;
	
	@Inject
	public FullQueryStatisticController(QueryLogStatistic queryLogStatistic) {
		this.queryLogStatistic = queryLogStatistic;
		this.fc = new JFileChooser();
	}
	
	public void onExportButtonClick() {
		int returnVal = fc.showSaveDialog(SolrMeterMain.mainFrame);
		
        if (returnVal == JFileChooser.APPROVE_OPTION) {
        	File file = fc.getSelectedFile();
        	
        	// if file exists, ask before overwriting
        	if(file.exists()) {
        		int response = JOptionPane.showConfirmDialog(
        				SolrMeterMain.mainFrame,
        				I18n.get("statistic.fullQueryStatistic.overwrite"),
        				I18n.get("statistic.fullQueryStatistic.title"),
        				JOptionPane.YES_NO_OPTION);
        		if(response==JOptionPane.NO_OPTION) {
        			return;
        		}
        	}
        	
        	try {
        		writeCSV(file);
        	} catch (IOException ex) {
    			JOptionPane.showMessageDialog(
    					SolrMeterMain.mainFrame,
    					ex.getMessage(),
    					I18n.get("statistic.fullQueryStatistic.exportExceptionTitle"),
    					JOptionPane.ERROR_MESSAGE);
    		}
        }
	}
	
	public void writeCSV(File file) throws IOException {
    	FileWriter writer = new FileWriter(file);
    	CSVWriter csvWriter = new CSVWriter(writer);
    	
		for (QueryLogValue query : queryLogStatistic.getLastQueries()) {
			String[] cols = new String[] {
					String.valueOf(query.isError()),
					query.getQueryString(),
					query.getFacetQueryString(),
					query.getFilterQueryString(),
					query.getQTime().toString(),
					query.getResults().toString()
			};
			csvWriter.writeNext(cols);
		}
		
		csvWriter.close();
	}

}
