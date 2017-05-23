package atua.anddev.globaltv;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import java.util.List;

import atua.anddev.globaltv.adapters.ChannelHolderAdapter;
import atua.anddev.globaltv.entity.Channel;

public class GlobalSearchActivity extends AppCompatActivity implements GlobalServices, ChannelHolderAdapter.OnItemClickListener {
    private String searchString;
    private ChannelHolderAdapter mAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set sub.xml as user interface layout
        setContentView(R.layout.globalsearch);

        getData();
        setupActionBar();
        showSearchResults();
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

    @Override
    public void onItemClick(Channel item, int viewId) {
        switch (viewId) {
            case R.id.favoriteIcon:
                changeFavorite(item);
                break;
            case R.id.title:
                guideActivity(item);
                break;
            default:
                setTick(item);
                openChannel(item);
                break;
        }
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void getData() {
        Intent intent = getIntent();
        searchString = intent.getStringExtra("search");
    }

    public void showSearchResults() {
        List<Channel> searchList = searchService.searchChannelsByName(searchString);
        TextView textView = (TextView) findViewById(R.id.globalsearchTextView1);
        textView.setText(getResources().getString(R.string.resultsfor) + " '" + searchString + "' - " +
                searchList.size() + " " + getResources().getString(R.string.channels));

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        mAdapter = new ChannelHolderAdapter(this, R.layout.item_channellist, searchList, true);
        mAdapter.setOnItemClickListener(GlobalSearchActivity.this);
        recyclerView.setAdapter(mAdapter);
    }

    private void setTick(Channel channel) {
        mAdapter.setSelected(channel);
        mAdapter.notifyDataSetChanged();
    }

    private void changeFavorite(Channel item) {
        Boolean changesAllowed = true;
        for (Channel fav : favoriteService.getAllFavorites()) {
            if (item.getName().equals(fav.getName())
                    && item.getProvider().equals(fav.getProvider()))
                changesAllowed = false;
        }
        if (changesAllowed)
            favoriteService.addToFavoriteList(item);
        else
            favoriteService.deleteFromFavorites(item);
        mAdapter.notifyDataSetChanged();
    }

    private void guideActivity(Channel channel) {
        Intent intent = new Intent(this, GuideActivity.class);
        intent.putExtra("channel", channel);
        startActivity(intent);
    }

    private void openChannel(Channel channel) {
        if (Global.useInternalPlayer) {
            Intent intent = new Intent(this, PlayerActivity.class);
            intent.putExtra("channel", channel);
            startActivity(intent);
        } else
            channelService.openChannel(this, channel);
    }
}
