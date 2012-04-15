package hu.vsza.adsapi;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Part {

	protected String name, description, href;

	public Part(String name, String description, String href) {
		this.name = name;
		this.description = description;
		this.href = href;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getHref() {
		return href;
	}

	public URLConnection getPdfConnection() throws IOException {
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
