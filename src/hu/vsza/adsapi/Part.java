package hu.vsza.adsapi;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Part extends HashMap<String, String> {

	public final static String NAME = "NAME", DESCRIPTION = "DESCRIPTION", HREF = "HREF";

	public Part(String name, String description, String href) {
		super(3);
		put(NAME, name);
		put(DESCRIPTION, description);
		put(HREF, href);
	}

	public URLConnection getPdfConnection() throws IOException {
		String href = get(HREF);

		Document doc = Jsoup.connect(href).get();
		Element viewPageLink = doc.select("td.blue a").get(0);
		String viewPageUrl = viewPageLink.absUrl("href");

		doc = Jsoup.connect(viewPageUrl).referrer(href).get();
		Element pdfIframe = doc.getElementsByTag("iframe").get(0);
		String pdfUrl = pdfIframe.absUrl("src");

		URLConnection pdfConnection = new URL(pdfUrl).openConnection();
		pdfConnection.setRequestProperty("Referer", viewPageUrl);
		return pdfConnection;
	}
}
