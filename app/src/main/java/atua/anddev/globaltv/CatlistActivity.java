package atua.anddev.globaltv;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class CatlistActivity extends Activity implements GlobalServices {
    private List<String> categoryList = new ArrayList<String>();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set sub.xml as user interface layout
        setContentView(R.layout.catlist);

        applyLocals();
        createCatlist();
        showCatlist();
    }

    private void applyLocals() {
        Button buttonFavorite = (Button) findViewById(R.id.catlistButton1);
        buttonFavorite.setText(getResources().getString(R.string.favorites));
        Button buttonSearch = (Button) findViewById(R.id.catlistButton2);
        buttonSearch.setText(getResources().getString(R.string.search));
    }

    private void createCatlist() {
        categoryList.add(getResources().getString(R.string.all));
        categoryList.addAll(channelService.getCategoriesList());
    }

    public void showCatlist() {
        TextView textView = (TextView) findViewById(R.id.catlistTextView1);
        textView.setText(getResources().getString(R.string.selectCategory));
        ListView listView = (ListView) findViewById(R.id.catlistListView1);
        ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, categoryList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4) {
                String s = (String) p1.getItemAtPosition(p3);
                Toast.makeText(CatlistActivity.this, s, Toast.LENGTH_SHORT).show();
                // Open category playlist
                playlistActivity(s);
            }

        });
    }

    public void playlistActivity(String selCat) {
        MainActivity.selectedCategory = selCat;
        Intent intent = new Intent(this, ChannellistActivity.class);
        startActivity(intent);
    }

    public void favlistActivity() {
        Intent intent = new Intent(this, FavlistActivity.class);
        startActivity(intent);
    }

    public void searchlistActivity() {
        Intent intent = new Intent(this, SearchlistActivity.class);
        startActivity(intent);
    }

    public void catFavButton(View view) {
        favlistActivity();
    }

    public void catSearchDialog(View view) {
        final EditText input = new EditText(this);
        input.setSingleLine();
        new AlertDialog.Builder(CatlistActivity.this)
                .setTitle(getResources().getString(R.string.request))
                .setMessage(getResources().getString(R.string.pleaseentertext))
                .setView(input)
                .setPositiveButton(getResources().getString(R.string.search), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Editable value = input.getText();
                        MainActivity.searchString = value.toString();
                        searchlistActivity();
                    }
                }).setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Do nothing.
            }
        }).show();
    }

}
