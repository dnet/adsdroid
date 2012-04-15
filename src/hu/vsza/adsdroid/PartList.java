package hu.vsza.adsdroid;

import java.util.List;
import java.io.IOException;
import hu.vsza.adsapi.Part;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

public class PartList extends ListActivity
{
	public final static String PARTS = "hu.vsza.adsdroid.PartList.PARTS";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		ArrayList<Part> parts = getIntent().getParcelableArrayListExtra(PARTS);
	}
}
