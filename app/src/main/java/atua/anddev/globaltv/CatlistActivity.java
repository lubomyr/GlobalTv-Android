package atua.anddev.globaltv;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class CatlistActivity extends AppCompatActivity implements GlobalServices {
    private List<String> categoryList = new ArrayList<String>();
    private int mSelectedProvider;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set sub.xml as user interface layout
        setContentView(R.layout.catlist);

        getData();
        setupActionBar();
        createCatlist();
        showCatlist();
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getData() {
        Intent intent = getIntent();
        mSelectedProvider = intent.getIntExtra("provider", -1);
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
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
                channellistActivity(s);
            }

        });
    }

    public void channellistActivity(String selCat) {
        Intent intent = new Intent(this, ChannellistActivity.class);
        intent.putExtra("category", selCat);
        intent.putExtra("provider", mSelectedProvider);
        startActivity(intent);
    }

}
