package atua.anddev.globaltv;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.Spinner;

import java.util.List;

import atua.anddev.globaltv.adapters.GuideExpListAdapter;
import atua.anddev.globaltv.entity.Programme;

public class SearchProgramActivity extends AppCompatActivity implements GlobalServices,
        SearchView.OnQueryTextListener {
    private GuideExpListAdapter mAdapter;
    private int searchType;
    private String searchStr;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set sub.xml as user interface layout
        setContentView(R.layout.search_program);

        setupActionBar();
        setupSearchProgramSpinner();
        initAdapter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.program_search_menu, menu);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        searchStr = query;
        searchProgram(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        SearchManager searchManager = (SearchManager) this.getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                return false;
            }
        });
        searchView.setSearchableInfo(searchManager.getSearchableInfo(this.getComponentName()));
        return super.onPrepareOptionsMenu(menu);
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

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupSearchProgramSpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.searchProgramSpinner);
        String[] testList = {getString(R.string.all_period), getString(R.string.today),
                getString(R.string.after_moment), getString(R.string.now)};
        ArrayAdapter typeAdapter = new ArrayAdapter<>(this, R.layout.item_program_search_spinner, testList);
        spinner.setAdapter(typeAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                searchType = i;
                searchProgram(searchStr);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void initAdapter() {
        ExpandableListView expandableListView = (ExpandableListView) findViewById(R.id.guideList);
        mAdapter = new GuideExpListAdapter(this, null, true);
        expandableListView.setAdapter(mAdapter);
    }

    private void searchProgram(String searchStr) {
        List<Programme> programmeList = null;
        final int ALL_PERIOD = 0;
        final int TODAY = 1;
        final int AFTER = 2;
        final int NOW = 3;
        switch (searchType) {
            case ALL_PERIOD:
                programmeList = guideService.getProgramsByStringForFullPeriod(searchStr);
                break;
            case TODAY:
                programmeList = guideService.getProgramsByStringForToday(searchStr);
                break;
            case AFTER:
                programmeList = guideService.getProgramsByStringAfterMoment(searchStr);
                break;
            case NOW:
                programmeList = guideService.getProgramsByStringForCurrentMoment(searchStr);
                break;
        }
        mAdapter.setDataList(programmeList);
        mAdapter.notifyDataSetChanged();
    }
}
