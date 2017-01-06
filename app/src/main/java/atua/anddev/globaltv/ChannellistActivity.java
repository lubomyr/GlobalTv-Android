package atua.anddev.globaltv;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import atua.anddev.globaltv.adapters.ChannelHolderAdapter;
import atua.anddev.globaltv.entity.Channel;
import atua.anddev.globaltv.entity.Favorites;

public class ChannellistActivity extends AppCompatActivity implements GlobalServices,
        ChannelHolderAdapter.OnItemClickListener, SearchView.OnQueryTextListener {
    private ChannelHolderAdapter mAdapter;
    private String mSelectedCategory;
    private int mSelectedProvider;
    private boolean mFavorite;
    private boolean mSearch;
    private String mSearchString;
    private List<Channel> channelList;
    private List<Channel> favoriteList;
    private List<Channel> searchList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set sub.xml as user interface layout
        setContentView(R.layout.channellist);

        channelList = new ArrayList<>();
        favoriteList = new ArrayList<>();
        searchList = new ArrayList<>();

        getData();
        setupActionBar();
        openCategory(mSelectedCategory);
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_favorites:
                mFavorite = !mFavorite;
                if (mFavorite) {
                    item.setIcon(R.drawable.favorite_inactive);
                    showFavoriteList();
                } else {
                    item.setIcon(R.drawable.favorite_active);
                    if (mSearch)
                        searchChannel(mSearchString);
                    else {
                        mAdapter.setItems(channelList);
                        mAdapter.notifyDataSetChanged();
                    }
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_menu, menu);
        return true;
    }

    @Override
    public void onItemClick(Channel item, int viewId) {
        switch (viewId) {
            case R.id.favoriteIcon:
                changeFavorite(item);
                break;
            case R.id.title:
                guideActivity(item.getName());
                break;
            default:
                setTick(item);
                channelService.openURL(item.getUrl(), ChannellistActivity.this);
                break;
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        SearchManager searchManager = (SearchManager) this.getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                mSearch = false;
                if (mFavorite)
                    showFavoriteList();
                else
                    mAdapter.setItems(channelList);
                mAdapter.notifyDataSetChanged();
                updateInfo();
                return false;
            }
        });
        searchView.setSearchableInfo(searchManager.getSearchableInfo(this.getComponentName()));
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        mSearch = true;
        mSearchString = query;
        searchChannel(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText.isEmpty())
            return false;
        if (newText.length() > 2) {
            mSearch = true;
            mSearchString = newText;
            searchChannel(newText);
            return true;
        }
        return false;
    }

    private void getData() {
        Intent intent = getIntent();
        mSelectedCategory = intent.getStringExtra("category");
        mSelectedProvider = intent.getIntExtra("provider", -1);
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void openCategory(final String catName) {
        if (channelList.size() == 0) {
            for (Channel chn : channelService.getAllChannels()) {
                chn.setProvider(playlistService.getActivePlaylistById(mSelectedProvider).getName());
                if (catName.equals(getString(R.string.all)))
                    channelList.add(chn);
                else if (catName.equals(chn.getCategory()))
                    channelList.add(chn);
            }
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        mAdapter = new ChannelHolderAdapter(this, R.layout.item_channellist, channelList, false);
        mAdapter.setOnItemClickListener(ChannellistActivity.this);
        recyclerView.setAdapter(mAdapter);
        updateInfo();
    }

    private void updateInfo() {
        TextView textView = (TextView) findViewById(R.id.playlistTextView1);
        StringBuilder str = new StringBuilder(mSelectedCategory);
        if (mFavorite)
            str.append(" : ").append(getString(R.string.favorites));
        if (mSearch)
            str.append(" : ").append(getString(R.string.search));
        str.append(" - ").append(mAdapter.getItemCount());
        str.append(" ").append(getString(R.string.channels));
        textView.setText(str.toString());
    }

    private void setTick(Channel channel) {
        mAdapter.setSelected(channel);
        mAdapter.notifyDataSetChanged();
        updateInfo();
    }

    private void changeFavorite(Channel item) {
        if (favoriteService.indexOfFavoriteByNameAndProv(item.getName(), playlistService.getActivePlaylistById(mSelectedProvider).getName()) == -1)
            favoriteService.addToFavoriteList(item.getName(), playlistService.getActivePlaylistById(mSelectedProvider).getName());
        else
            favoriteService.deleteFromFavoritesById(favoriteService.indexNameForFavorite(item.getName()));
        if (mFavorite)
            favoriteList.remove(item);
        try {
            favoriteService.saveFavorites(ChannellistActivity.this);
        } catch (IOException ignored) {
        }
        mAdapter.notifyDataSetChanged();
        updateInfo();
    }

    private void guideActivity(String chName) {
        Intent intent = new Intent(this, GuideActivity.class);
        intent.putExtra("name", chName);
        startActivity(intent);
    }

    private void searchChannel(String str) {
        searchList.clear();
        List<Channel> list = mFavorite ? favoriteList : channelList;
        for (Channel chn : list) {
            if (chn.getName().toLowerCase().contains(str.toLowerCase()))
                searchList.add(chn);
        }
        mAdapter.setItems(searchList);
        mAdapter.notifyDataSetChanged();
        updateInfo();
    }

    private void showFavoriteList() {
        favoriteList.clear();
        List<Channel> list = mSearch ? searchList : channelList;
        for (Favorites fav : favoriteService.getFavoriteList()) {
            for (Channel chn : list) {
                if (fav.getName().equals(chn.getName()) && !favoriteList.contains(fav.getName())
                        && fav.getProv().equals(playlistService.getActivePlaylistById(mSelectedProvider).getName())) {
                    favoriteList.add(chn);
                }
            }
        }
        mAdapter.setItems(favoriteList);
        mAdapter.notifyDataSetChanged();
        updateInfo();
    }
}
