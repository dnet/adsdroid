package hu.vsza.adsapi;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import android.os.Parcelable;
import android.os.Parcel;

public class Part implements Parcelable {

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

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel out, int flags) {
		out.writeString(name);
		out.writeString(description);
		out.writeString(href);
	}

	public static final Parcelable.Creator<Part> CREATOR
		= new Parcelable.Creator<Part>() {
			public Part createFromParcel(Parcel in) {
				String name = in.readString();
				String description = in.readString();
				String href = in.readString();
				return new Part(name, description, href);
			}

			public Part[] newArray(int size) {
				return new Part[size];
			}
		};
}
