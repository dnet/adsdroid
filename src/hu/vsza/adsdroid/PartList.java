package hu.vsza.adsdroid;

import java.util.List;
import hu.vsza.adsapi.Part;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.SimpleAdapter;

public class PartList extends ListActivity
{
	public final static String PARTS = "hu.vsza.adsdroid.PartList.PARTS";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		List<Part> parts = (List<Part>)getIntent().getSerializableExtra(PARTS);
		String[] from = new String[] {Part.NAME, Part.DESCRIPTION};
		int[] to = new int[] {R.id.result_part_name, R.id.result_part_description};
		setListAdapter(new SimpleAdapter(this, parts, R.layout.part_list_item, from, to));
	}
}
