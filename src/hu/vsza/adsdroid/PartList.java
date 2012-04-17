package hu.vsza.adsdroid;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.io.*;
import java.net.URLConnection;
import hu.vsza.adsapi.Part;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.AsyncTask;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class PartList extends ListActivity
{
	public final static String PARTS = "hu.vsza.adsdroid.PartList.PARTS";
	ProgressDialog mProgressDialog;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		List<Map<String, String>> parts = (List<Map<String, String>>)getIntent().getSerializableExtra(PARTS);
		String[] from = new String[] {Part.NAME, Part.DESCRIPTION};
		int[] to = new int[] {R.id.result_part_name, R.id.result_part_description};
		setListAdapter(new SimpleAdapter(this, parts, R.layout.part_list_item, from, to));
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Part selectedPart = new Part((Map<String, String>)getListView().getItemAtPosition(position));
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setMessage("Starting download...");
		mProgressDialog.setIndeterminate(true);
		DownloadDatasheet dd = new DownloadDatasheet();
		dd.execute(selectedPart);
	}

	private class DownloadDatasheet extends AsyncTask<Part, String, String> {

		@Override
		protected String doInBackground(Part... parts) {
			Part selectedPart = parts[0];
			String fileName = fileNameForPart(selectedPart);
			try {
				publishProgress("Fetching PDF URL...");
				URLConnection pdfConnection = selectedPart.getPdfConnection();
				publishProgress("Connecting to PDF server...");
				pdfConnection.connect();
				InputStream input = new BufferedInputStream(pdfConnection.getInputStream());
				OutputStream output = new FileOutputStream(fileName);

				byte data[] = new byte[1024];
				long total = 0;
				int count;
				while ((count = input.read(data)) != -1) {
					total += count;
					publishProgress("Downloading datasheet... (fetched " + total + " bytes so far)");
					output.write(data, 0, count);
				}

				output.flush();
				output.close();
				input.close();
				return fileName;
			} catch (Exception e) {
				return null;
			}
		}

		protected String fileNameForPart(Part part) {
			return "/sdcard/" + part.get(Part.NAME) + DateFormat.format("-yyyyMMdd-kkmmss", new Date()) + ".pdf";
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgressDialog.show();
		}

		@Override
		protected void onProgressUpdate(String... progress) {
			super.onProgressUpdate(progress);
			mProgressDialog.setMessage(progress[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			mProgressDialog.dismiss();
			Toast.makeText(getBaseContext(), result == null ? "Error happened during download" : "Datasheet saved as " + result,
					Toast.LENGTH_SHORT).show();
			if (result != null) {
				Intent intent = new Intent();
				intent.setAction(android.content.Intent.ACTION_VIEW);
				File file = new File(result);
				intent.setData(Uri.fromFile(file));
				startActivity(intent);
			}
		}
	}
}
