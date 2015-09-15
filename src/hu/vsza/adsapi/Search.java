package hu.vsza.adsapi;

import java.io.IOException;
import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Search {
	public enum Mode {
		INCLUDED(1), START_WITH(2), END(3), MATCH(4);

		private byte value;

		private Mode(int value) {
			this.value = (byte)value;
		}

		public byte getValue() {
			return value;
		}

		@Override
		public String toString() {
			return super.toString().toLowerCase().replace("_", " ");
		}
	}

	public static ArrayList<Part> searchByPartName(String partName, Mode searchMode) throws IOException {
		Document doc = Jsoup.connect("http://www.alldatasheet.com/view.jsp")
			.data("sField", Byte.toString(searchMode.getValue()))
			.data("sSearchword", partName)
			.header("Accept-Language", "en")
			.post();
		Elements tableRows = doc.select("tr.nv_td");

		ArrayList<Part> partList = new ArrayList<Part>(tableRows.size());

		for (Element tableRow : tableRows) {
			Elements rowCells = tableRow.getElementsByTag("td");
			if (rowCells.size() < 3) continue;
			Element firstCol = rowCells.get(0);
			Elements links = firstCol.getElementsByTag("a");
			if (links.size() == 0) continue;
			String foundPartName = firstCol.text();
			String foundPartDesc = rowCells.get(2).text();
			String foundPartHref = links.get(0).attributes().get("href");
			partList.add(new Part(foundPartName, foundPartDesc, foundPartHref));
		}

		return partList;
	}
}
