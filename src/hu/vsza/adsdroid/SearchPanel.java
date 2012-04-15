package hu.vsza.adsdroid;

import java.util.List;
import java.io.IOException;
import hu.vsza.adsapi.Search;
import hu.vsza.adsapi.Part;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.EditText;
import android.widget.Toast;

public class SearchPanel extends Activity
{
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
	}

	public void searchByPartName(View view) {
		Spinner searchModeSpinner = (Spinner)findViewById(R.id.search_mode);
		Search.Mode selectedSearchMode = (Search.Mode)searchModeSpinner.getSelectedItem();
		EditText partNameEditor = (EditText)findViewById(R.id.part_name);
		String partName = partNameEditor.getText().toString();
		try {
			List<Part> results = Search.searchByPartName(partName, selectedSearchMode);
			Toast.makeText(getBaseContext(), Integer.toString(results.size()) + " result(s) found",
					Toast.LENGTH_SHORT).show();
		} catch (IOException ioe) {
			Toast.makeText(getBaseContext(), "Error fetching results: " + ioe.getMessage(),
					Toast.LENGTH_SHORT).show();
		}
	}
}
