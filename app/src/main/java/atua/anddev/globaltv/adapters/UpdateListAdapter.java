package atua.anddev.globaltv.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import atua.anddev.globaltv.R;

public class UpdateListAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final List<String> itemname1;
    private final List<String> itemname2;

    public UpdateListAdapter(Activity context, List<String> itemname1, List<String> itemname2) {
        super(context, R.layout.myglobalsearch, itemname1);

        this.context = context;
        this.itemname1 = itemname1;
        this.itemname2 = itemname2;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.myglobalsearch, null, true);

        TextView txtTitle1 = (TextView) rowView.findViewById(R.id.myglobalsearchTextView1);
        TextView txtTitle2 = (TextView) rowView.findViewById(R.id.myglobalsearchTextView2);

        txtTitle1.setText(itemname1.get(position));
        txtTitle2.setText(itemname2.get(position));

        return rowView;
    }
}
