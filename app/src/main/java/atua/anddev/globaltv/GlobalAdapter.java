package atua.anddev.globaltv;

import android.app.*;
import android.view.*;
import android.widget.*;
import java.util.*;

public class GlobalAdapter extends ArrayAdapter<String>
{
	private final Activity context;
	private final ArrayList<String> itemname1;
	private final ArrayList<String> itemname2;
	
	public GlobalAdapter(Activity context, ArrayList<String> itemname1, ArrayList<String> itemname2)
	{
		super(context, R.layout.myglobalsearch, itemname1);
		// TODO Auto-generated constructor stub

		this.context = context;
		this.itemname1 = itemname1;
		this.itemname2 = itemname2;
	}
	
	public View getView(int position, View view, ViewGroup parent)
	{
		LayoutInflater inflater=context.getLayoutInflater();
		View rowView=inflater.inflate(R.layout.myglobalsearch, null, true);

		TextView txtTitle1 = (TextView) rowView.findViewById(R.id.myglobalsearchTextView1);
		TextView txtTitle2 = (TextView) rowView.findViewById(R.id.myglobalsearchTextView2);

		txtTitle1.setText(itemname1.get(position));
		txtTitle2.setText(itemname2.get(position));
		
		return rowView;

	};
}
