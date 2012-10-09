package hu.vsza.adsapi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Search {
	protected static final Pattern jsPattern = Pattern.compile("'([^']+)'[^']*'([^']+)'[^']*'([^']+)'");

	public enum Mode {
		INCLUDED(1), START_WITH(2), END(3), MATCH(4);

		private int value;

		private Mode(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		@Override
		public String toString() {
			return super.toString().toLowerCase().replace("_", " ");
		}
	}

	public static ArrayList<Part> searchByPartName(String partName, Mode searchMode) throws IOException {
		Document doc = Jsoup.connect("http://www.alldatasheet.com/view.jsp")
			.data("sField", Integer.toString(searchMode.getValue()))
			.data("sSearchword", partName)
			.post();
		Elements tableRows = doc.select("tr.nv_td");

		ArrayList<Part> partList = new ArrayList<Part>(tableRows.size());

		for (Element tableRow : tableRows) {
			Elements rowCells = tableRow.getElementsByTag("td");
			String foundPartName = rowCells.get(1).text();
			String foundPartDesc = rowCells.get(2).text();
			String foundPartHref = rowCells.get(3).getElementsByTag("a").get(0).attributes().get("href");
			final Matcher m = jsPattern.matcher(foundPartHref);
			if (m.find()) {
				foundPartHref = new StringBuilder("http://www.alldatasheet.com/datasheet-pdf/pdf/")
					.append(m.group(1)).append('/')
					.append(m.group(2)).append('/')
					.append(m.group(3)).append(".html").toString();
			}
			partList.add(new Part(foundPartName, foundPartDesc, foundPartHref));
		}

		return partList;
	}
}
