package hu.vsza.adsapi;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jsoup.Jsoup;
import org.jsoup.Connection;
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

		Document doc = Jsoup.connect(href).timeout(0).get();
		Element viewPageLink = doc.select("td.blue a").get(0);
		String viewPageUrl = viewPageLink.absUrl("href");

		Connection.Response resp = Jsoup.connect(viewPageUrl).referrer(href)
			.timeout(0).userAgent(UA).header("Accept-Language", "en").execute();
		StringBuilder cookies = new StringBuilder();
		for (Map.Entry<String, String> e : resp.cookies().entrySet()) {
			if (cookies.length() != 0) cookies.append("; ");
			cookies.append(e.getKey()).append('=').append(e.getValue());
		}
		Element pdfIframe = getDatasheetIframe(resp.parse());
		String pdfUrl = pdfIframe.absUrl("src");

		URLConnection pdfConnection = new URL(pdfUrl).openConnection();
		pdfConnection.setRequestProperty("Cookie", cookies.toString());
		pdfConnection.setRequestProperty("Referer", viewPageUrl);
		pdfConnection.setRequestProperty("User-Agent", UA);
		return pdfConnection;
	}

	protected static Element getDatasheetIframe(Element doc) {
		List<Element> iframes = doc.select("iframe");
		for (Element iframe : iframes) {
			String src = iframe.attr("src");
			if (!src.startsWith("http") && !src.startsWith("//")) return iframe;
		}
		return iframes.get(0);
	}
}
