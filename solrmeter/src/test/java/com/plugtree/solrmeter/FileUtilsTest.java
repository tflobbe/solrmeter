package com.plugtree.solrmeter;

import java.io.UnsupportedEncodingException;
import java.util.List;

import com.plugtree.solrmeter.model.FileUtils;
import com.plugtree.solrmeter.model.SolrMeterConfiguration;

public class FileUtilsTest extends BaseTestCase {
	
	private String[] utfCodes = new String[] {"C3A2", 
	"C3AA", 
	"C3AE", 
	"C3B4", 
	"C3BB", 
	"C3A1", 
	"C3A9", 
	"C3AD", 
	"C3B3", 
	"C3BA", 
	"C3A0", 
	"C3A8", 
	"C3AC", 
	"C3B2", 
	"C3B9", 
	"C3A4", 
	"C3AB", 
	"C3AF", 
	"C3B6", 
	"C3BC", 
	"C3A3", 
	"C3B5", 
	"C3B1", 
	"C3A7", 
	"C387", 
	"C3B0", 
	"C390", 
	"C398", 
	"C3B8", 
	"C3A5", 
	"C385", 
	"C3A6", 
	"C386", 
	"C39F", 
	"C2BF", 
	"C2A1"};

	public void testLoadStringsFromFileInternationalCharactersUtf8() throws UnsupportedEncodingException {
		List<String> queries = FileUtils.loadStringsFromFile("internationalQueries.txt");
		compareAll(queries);
	}
	
	public void testLoadStringsFromFileInternationalCharactersIso88591() throws UnsupportedEncodingException {
		SolrMeterConfiguration.setProperty("files.charset", "ISO-8859-1");
		List<String> queries = FileUtils.loadStringsFromFile("internationalQueriesISO_8859_1.txt");
		compareAll(queries);
	}
	
	/*
	 * Created after http://code.google.com/p/solrmeter/issues/detail?id=103
	 */
	public void testWindowsInitialBOMIssue() throws UnsupportedEncodingException {
		SolrMeterConfiguration.setProperty("files.charset", "UTF-8");
		List<String> queries = FileUtils.loadStringsFromFile("windowsBOMProblem.txt");
		assertEquals("The file only has one query", 1, queries.size());
		assertEquals("numNights:[7 TO 14] AND numAdults:2 AND tourDate:[2012-08-09T01:00:00.000Z TO 2012-08-31T01:00:00.000Z]", queries.get(0));
		
		queries = FileUtils.loadStringsFromFile("regularUTF-8.txt");
		assertEquals("The file only has one query", 1, queries.size());
		assertEquals("numNights:[7 TO 14] AND numAdults:2 AND tourDate:[2012-08-09T01:00:00.000Z TO 2012-08-31T01:00:00.000Z]", queries.get(0));
	}

	private void compareAll(List<String> queries)
			throws UnsupportedEncodingException {
		int i = 0;
		for (String s:queries) {
			assertEquals("Failed for character " + utfCodes[i],utfCodes[i], byteArrayToHexString(s.getBytes("UTF-8")));
			i++;
		}
	}
	

	private String byteArrayToHexString(byte in[]) {
		byte ch = 0x00;
		int i = 0; 
		if (in == null || in.length <= 0)
			return null;
		String pseudo[] = {"0", "1", "2",
				"3", "4", "5", "6", "7", "8",
				"9", "A", "B", "C", "D", "E",
		"F"};

		StringBuffer out = new StringBuffer(in.length * 2);

		while (i < in.length) {
			ch = (byte) (in[i] & 0xF0); // Strip off high nibble
			ch = (byte) (ch >>> 4); // shift the bits down
			ch = (byte) (ch & 0x0F); // must do this is high order bit is on!
			out.append(pseudo[ (int) ch]); // convert the nibble to a String Character
			ch = (byte) (in[i] & 0x0F); // Strip off low nibble 
			out.append(pseudo[ (int) ch]); // convert the nibble to a String Character
			i++;

		}

		String rslt = new String(out);

		return rslt;

	}
	
	
	
}
