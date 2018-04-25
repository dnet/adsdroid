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
import android.content.ActivityNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.AsyncTask;
import android.os.Environment;
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
		mProgressDialog.setMessage(getString(R.string.starting_download));
		mProgressDialog.setIndeterminate(true);
		DownloadDatasheet dd = new DownloadDatasheet();
		dd.execute(selectedPart);
	}

	private class DownloadDatasheet extends AsyncTask<Part, String, File> {

		@Override
		protected File doInBackground(Part... parts) {
			Part selectedPart = parts[0];
			try {
				File file = fileForPart(selectedPart);
				publishProgress(R.string.fetching_pdf_url);
				URLConnection pdfConnection = selectedPart.getPdfConnection();
				publishProgress(R.string.connecting_to_pdf_server);
				pdfConnection.connect();
				InputStream input = new BufferedInputStream(pdfConnection.getInputStream());
				OutputStream output = new FileOutputStream(file);

				byte data[] = new byte[1024];
				long total = 0;
				int count;
				while ((count = input.read(data)) != -1) {
					total += count;
					publishProgress(getString(R.string.download_progress, total));
					output.write(data, 0, count);
				}

				output.flush();
				output.close();
				input.close();
				return file;
			} catch (IOException e) {
				return null;
			}
		}

		protected void publishProgress(int progress) {
			publishProgress(getString(progress));
		}

		protected File fileForPart(Part part) throws IOException {
			File path = Environment.getExternalStoragePublicDirectory(
					Environment.DIRECTORY_DOWNLOADS);
			path.mkdirs();
			File file = new File(path, part.get(Part.NAME) +
					DateFormat.format("-yyyyMMdd-kkmmss", new Date()) + ".pdf");
			return file;
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
		protected void onPostExecute(File result) {
			super.onPostExecute(result);
			mProgressDialog.dismiss();
			mProgressDialog = null;
			Toast.makeText(getBaseContext(), result == null ? getString(R.string.download_error) : getString(R.string.download_done, result.getName()),
					Toast.LENGTH_SHORT).show();
			if (result != null) {
				openPDF(result);
			}
		}

		protected void openPDF(File file) {
			Intent intent = new Intent();
			intent.setAction(android.content.Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.fromFile(file), "application/pdf");
			try {
				startActivity(intent);
			} catch (ActivityNotFoundException e) {
				Toast.makeText(getBaseContext(), R.string.pdf_open_failed,
						Toast.LENGTH_SHORT).show();
			}
		}
	}
}
