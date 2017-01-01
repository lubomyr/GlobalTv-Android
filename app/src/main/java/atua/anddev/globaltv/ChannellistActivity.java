package atua.anddev.globaltv;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import atua.anddev.globaltv.adapters.ChannelHolderAdapter;
import atua.anddev.globaltv.entity.Channel;

public class ChannellistActivity extends Activity implements GlobalServices, ChannelHolderAdapter.OnItemClickListener {
    private ChannelHolderAdapter mAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set sub.xml as user interface layout
        setContentView(R.layout.playlist);

        applyLocals();
        openCategory(MainActivity.selectedCategory);
    }

    private void applyLocals() {
        Button buttonFavorite = (Button) findViewById(R.id.playlistButton1);
        buttonFavorite.setText(getResources().getString(R.string.favorites));
        Button buttonSearch = (Button) findViewById(R.id.playlistButton2);
        buttonSearch.setText(getResources().getString(R.string.search));
    }

    private void openCategory(final String catName) {
        List<Channel> channelList = new ArrayList<Channel>();
        for (Channel chn : channelService.getAllChannels()) {
            if (catName.equals(getResources().getString(R.string.all)))
                channelList.add(chn);
            else if (catName.equals(chn.getCategory()))
                channelList.add(chn);
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        mAdapter = new ChannelHolderAdapter(this, R.layout.item_channellist, channelList);
        mAdapter.setOnItemClickListener(ChannellistActivity.this);
        recyclerView.setAdapter(mAdapter);

        TextView textView = (TextView) findViewById(R.id.playlistTextView1);
        textView.setText(catName + " - " + channelList.size() + " " + getResources().getString(R.string.channels));
    }

    public void favlistActivity() {
        Intent intent = new Intent(this, FavlistActivity.class);
        startActivity(intent);
    }

    public void searchlistActivity() {
        Intent intent = new Intent(this, SearchlistActivity.class);
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
                        MainActivity.searchString = value.toString();
                        searchlistActivity();
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
                setTick(item.getUrl());
                channelService.openURL(item.getUrl(), ChannellistActivity.this);
                break;
        }
    }

    private void setTick(String strPos) {
        mAdapter.setSelected(strPos);
        mAdapter.notifyDataSetChanged();
    }

    private void changeFavorite(Channel item) {
        if (favoriteService.indexOfFavoriteByNameAndProv(item.getName(), playlistService.getActivePlaylistById(MainActivity.selectedProvider).getName()) == -1)
            favoriteService.addToFavoriteList(item.getName(), playlistService.getActivePlaylistById(MainActivity.selectedProvider).getName());
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
}
