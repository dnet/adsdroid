package hu.vsza.adsapi;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Part extends HashMap<String, String> {

	public final static String NAME = "NAME", DESCRIPTION = "DESCRIPTION", HREF = "HREF";
	public final static String UA = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/536.6 (KHTML, like Gecko) Chrome/20.0.1088.0 Safari/536.6";

	public Part(Map<? extends String, ? extends String> map) {
		super(map);
	}

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

		doc = Jsoup.connect(viewPageUrl).referrer(href).userAgent(UA).get();
		Element pdfIframe = doc.getElementsByTag("iframe").get(0);
		String pdfUrl = pdfIframe.absUrl("src");

		URLConnection pdfConnection = new URL(pdfUrl).openConnection();
		pdfConnection.setRequestProperty("Referer", viewPageUrl);
		return pdfConnection;
	}
}
