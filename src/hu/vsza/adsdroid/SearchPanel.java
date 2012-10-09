package hu.vsza.adsdroid;

import java.util.ArrayList;
import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import hu.vsza.adsapi.Search;
import hu.vsza.adsapi.Part;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.EditText;
import android.widget.Toast;

public class SearchPanel extends Activity
{
	ProgressDialog mProgressDialog;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Spinner searchModeSpinner = (Spinner)findViewById(R.id.search_mode);
		ArrayAdapter<Search.Mode> searchModeAdapter = new ArrayAdapter<Search.Mode>(this,
				android.R.layout.simple_spinner_item, Search.Mode.values());
		searchModeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		searchModeSpinner.setAdapter(searchModeAdapter);
		CookieManager cookieManager = new CookieManager();
		CookieHandler.setDefault(cookieManager);
	}

	public void searchByPartName(View view) {
		Spinner searchModeSpinner = (Spinner)findViewById(R.id.search_mode);
		Search.Mode selectedSearchMode = (Search.Mode)searchModeSpinner.getSelectedItem();
		EditText partNameEditor = (EditText)findViewById(R.id.part_name);
		String partName = partNameEditor.getText().toString();
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setMessage("Searching...");
		mProgressDialog.setIndeterminate(true);
		SearchByPartName sbpn = new SearchByPartName();
		sbpn.execute(new PartNameSearchParams(selectedSearchMode, partName));
	}

	private class PartNameSearchParams {

		protected Search.Mode mode;
		protected String partName;

		public PartNameSearchParams(Search.Mode mode, String partName) {
			this.mode = mode;
			this.partName = partName;
		}

		public Search.Mode getMode() {
			return mode;
		}

		public String getPartName() {
			return partName;
		}
	}

	private class SearchByPartName extends AsyncTask<PartNameSearchParams, Object, ArrayList<Part>> {

		@Override
		protected ArrayList<Part> doInBackground(PartNameSearchParams... params_array) {
			PartNameSearchParams params = params_array[0];
			try {
				return Search.searchByPartName(params.getPartName(), params.getMode());
			} catch (IOException ioe) {
				return null;
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgressDialog.show();
		}

		@Override
		protected void onPostExecute(ArrayList<Part> result) {
			super.onPostExecute(result);
			mProgressDialog.dismiss();
			mProgressDialog = null;
			if (result != null) {
				if (result.isEmpty()) {
					Toast.makeText(getBaseContext(), "No results found.",
							Toast.LENGTH_SHORT).show();
				} else {
					Intent intent = new Intent(SearchPanel.this, PartList.class);
					intent.putExtra(PartList.PARTS, result);
					startActivity(intent);
				}
			} else {
				Toast.makeText(getBaseContext(), "Error fetching results.",
						Toast.LENGTH_SHORT).show();
			}
		}
	}
}
