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
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SearchPanel extends Activity implements TextView.OnEditorActionListener
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

		EditText partNameEditor = (EditText)findViewById(R.id.part_name);
		Intent intent = getIntent();
		if (Intent.ACTION_SEND.equals(intent.getAction()) &&
				"text/plain".equals(intent.getType())) {
			String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
			if (sharedText != null) {
				partNameEditor.setText(sharedText);
			}
		}

		partNameEditor.setOnEditorActionListener(this);
	}

	// implements TextView.OnEditorActionListener
	// based on https://stackoverflow.com/a/3205405 by Robby Pond (@robbypond)

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (actionId == EditorInfo.IME_ACTION_SEARCH) {
			searchByPartName(v);
			return true;
		}
		return false;
	}

	public void searchByPartName(View view) {
		Spinner searchModeSpinner = (Spinner)findViewById(R.id.search_mode);
		Search.Mode selectedSearchMode = (Search.Mode)searchModeSpinner.getSelectedItem();
		EditText partNameEditor = (EditText)findViewById(R.id.part_name);
		String partName = partNameEditor.getText().toString();
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setMessage(getString(R.string.searching));
		mProgressDialog.setIndeterminate(true);
		new SearchByPartName(selectedSearchMode, partName).execute();
	}

	private class SearchByPartName extends AsyncTask<Void, Object, ArrayList<Part>> {

		protected final Search.Mode mode;
		protected final String partName;

		public SearchByPartName(Search.Mode mode, String partName) {
			super();
			this.mode = mode;
			this.partName = partName;
		}

		@Override
		protected ArrayList<Part> doInBackground(Void... params_array) {
			try {
				return Search.searchByPartName(partName, mode);
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
					Toast.makeText(getBaseContext(), R.string.no_results,
							Toast.LENGTH_SHORT).show();
				} else {
					Intent intent = new Intent(SearchPanel.this, PartList.class);
					intent.putExtra(PartList.PARTS, result);
					startActivity(intent);
				}
			} else {
				Toast.makeText(getBaseContext(), R.string.error_fetching_results,
						Toast.LENGTH_SHORT).show();
			}
		}
	}
}
