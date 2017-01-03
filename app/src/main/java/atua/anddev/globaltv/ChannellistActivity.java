package atua.anddev.globaltv;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import atua.anddev.globaltv.adapters.ChannelHolderAdapter;
import atua.anddev.globaltv.entity.Channel;

public class ChannellistActivity extends AppCompatActivity implements GlobalServices,
        ChannelHolderAdapter.OnItemClickListener, SearchView.OnQueryTextListener {
    private ChannelHolderAdapter mAdapter;
    private String mSelectedCategory;
    private int mSelectedProvider;
    private int showFavorite = 0;
    private List<Channel> channelList = new ArrayList<Channel>();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set sub.xml as user interface layout
        setContentView(R.layout.playlist);

        getData();
        setupActionBar();
        applyLocals();
        openCategory(mSelectedCategory);
    }

    private void getData() {
        Intent intent = getIntent();
        mSelectedCategory = intent.getStringExtra("category");
        mSelectedProvider = intent.getIntExtra("provider", -1);
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void applyLocals() {
    }

    private void openCategory(final String catName) {
        if (channelList.size() == 0) {
            for (Channel chn : channelService.getAllChannels()) {
                if (catName.equals(getResources().getString(R.string.all)))
                    channelList.add(chn);
                else if (catName.equals(chn.getCategory()))
                    channelList.add(chn);
            }
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        mAdapter = new ChannelHolderAdapter(this, R.layout.item_channellist, channelList,
                mSelectedProvider);
        mAdapter.setOnItemClickListener(ChannellistActivity.this);
        recyclerView.setAdapter(mAdapter);

        TextView textView = (TextView) findViewById(R.id.playlistTextView1);
        textView.setText(catName + " - " + channelList.size() + " " + getResources().getString(R.string.channels));
    }

    public void favlistActivity() {
        Intent intent = new Intent(this, FavlistActivity.class);
        intent.putExtra("provider", mSelectedProvider);
        startActivity(intent);
    }

    public void searchlistActivity(String searchString) {
        Intent intent = new Intent(this, SearchlistActivity.class);
        intent.putExtra("search", searchString);
        intent.putExtra("provider", mSelectedProvider);
        startActivity(intent);
    }

    public void favButton(View view) {
        favlistActivity();
    }

    public void searchDialog(View view) {
        final EditText input = new EditText(this);
        input.setSingleLine();
        new AlertDialog.Builder(ChannellistActivity.this)
                .setTitle(getResources().getString(R.string.request))
                .setMessage(getResources().getString(R.string.pleaseentertext))
                .setView(input)
                .setPositiveButton(getResources().getString(R.string.search), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Editable value = input.getText();
                        String searchString = value.toString();
                        searchlistActivity(searchString);
                    }
                }).setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Do nothing.
            }
        }).show();
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

    private void setTick(Channel channel) {
        mAdapter.setSelected(channel);
        mAdapter.notifyDataSetChanged();
    }

    private void changeFavorite(Channel item) {
        if (favoriteService.indexOfFavoriteByNameAndProv(item.getName(), playlistService.getActivePlaylistById(mSelectedProvider).getName()) == -1)
            favoriteService.addToFavoriteList(item.getName(), playlistService.getActivePlaylistById(mSelectedProvider).getName());
        else
            favoriteService.deleteFromFavoritesById(favoriteService.indexNameForFavorite(item.getName()));
        try {
            favoriteService.saveFavorites(ChannellistActivity.this);
        } catch (IOException ignored) {
        }
        mAdapter.notifyDataSetChanged();
    }

    private void guideActivity(String chName) {
        Intent intent = new Intent(this, GuideActivity.class);
        intent.putExtra("name", chName);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_favorites:
                showFavorite++;
                if (showFavorite == 1)
                    showFavoriteList();
                else {
                    mAdapter.setItems(channelList);
                    mAdapter.notifyDataSetChanged();
                }
                if (showFavorite == 2)
                    showFavorite = 0;
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        SearchManager searchManager = (SearchManager) this.getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                mAdapter.setItems(channelList);
                mAdapter.notifyDataSetChanged();
                return false;
            }
        });
        searchView.setSearchableInfo(searchManager.getSearchableInfo(this.getComponentName()));
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText.isEmpty())
            return false;
        if (newText.length() > 2) {
            final String s = newText.trim().toUpperCase();
            searchChannel(s);
            return true;
        }
        return false;
    }

    private void searchChannel(String str) {
        List<Channel> searchList = new ArrayList<Channel>();
        for (Channel chn : channelList) {
            if (chn.getName().toLowerCase().contains(str.toLowerCase()))
                searchList.add(chn);
        }
        mAdapter.setItems(searchList);
        mAdapter.notifyDataSetChanged();
    }

    private void showFavoriteList() {
        List<Channel> favoriteList = new ArrayList<Channel>();
        for (int i = 0; i < favoriteService.sizeOfFavoriteList(); i++) {
            for (Channel chn : channelList) {
                if (favoriteService.getFavoriteById(i).getName().equals(chn.getName()) && !favoriteList.contains(favoriteService.getFavoriteById(i).getName())
                        && favoriteService.getFavoriteById(i).getProv().equals(playlistService.getActivePlaylistById(mSelectedProvider).getName())) {
                    favoriteList.add(chn);
                }
            }
        }
        mAdapter.setItems(favoriteList);
        mAdapter.notifyDataSetChanged();
    }
}
