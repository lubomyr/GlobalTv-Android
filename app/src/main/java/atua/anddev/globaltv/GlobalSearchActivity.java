package atua.anddev.globaltv;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import atua.anddev.globaltv.entity.Channel;
import java.util.*;
import android.support.v7.widget.*;
import atua.anddev.globaltv.adapters.*;

public class GlobalSearchActivity extends MainActivity implements GlobalServices, ChannelHolderAdapter.OnItemClickListener
{
    private ProgressDialog progress;
    private String searchString;
	private ChannelHolderAdapter mAdapter;
	private List<Channel> searchList;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set sub.xml as user interface layout
        setContentView(R.layout.globalsearch);

		searchList = new ArrayList<Channel>();
        getData();
        if (searchList.size() == 0)
            runProgressBar();
        else
            showSearchResults();
    }
	
	@Override
	public void onItemClick(Channel item, int viewId)
	{
		switch (viewId) {
            case R.id.favoriteIcon:
                changeFavorite(item);
                break;
            case R.id.title:
                guideActivity(item.getName());
                break;
            default:
                setTick(item);
                channelService.openURL(item.getUrl(), GlobalSearchActivity.this);
                break;
        }
	}

    private void getData() {
        Intent intent = getIntent();
        searchString = intent.getStringExtra("search");
    }

    private void runProgressBar() {
        progress = new ProgressDialog(this);
        progress.setMessage(getResources().getString(R.string.searching));
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progress.setMax(playlistService.sizeOfActivePlaylist());
        progress.setProgress(0);
        progress.show();

        Thread t = new Thread() {
            @Override
            public void run() {
                prepare_globalSearch();
                progress.dismiss();
                runOnUiThread(new Runnable() {
                    public void run() {
                        showSearchResults();
                    }
                });
            }
        };
        t.start();
    }

    private void prepare_globalSearch() {
        for (int i = 0; i < playlistService.sizeOfActivePlaylist(); i++) {
            progress.setProgress(i);
            playlistService.readPlaylist(i);

            String chName;
            for (Channel chn : channelService.channel) {
                chName = chn.getName().toLowerCase();
                if (chName.contains(searchString.toLowerCase())) {
					chn.setProvider(playlistService.getActivePlaylistById(i).getName());
                    searchList.add(chn);
                }
            }
        }

    }

    public void showSearchResults() {
        TextView textView = (TextView) findViewById(R.id.globalsearchTextView1);
        textView.setText(getResources().getString(R.string.resultsfor) + " '" + searchString + "' - " +
                searchList.size() + " " + getResources().getString(R.string.channels));
				
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        mAdapter = new ChannelHolderAdapter(this, R.layout.item_channellist, searchList, true);
        mAdapter.setOnItemClickListener(GlobalSearchActivity.this);
		
        recyclerView.setAdapter(mAdapter);
		
//        GlobalAdapter adapter = new GlobalAdapter(this, searchService.searchNameList, searchService.searchProvList);
//        ListView list = (ListView) findViewById(R.id.globalsearchListView1);
//        list.setAdapter(adapter);
//        list.setOnItemClickListener(new OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4) {
//                String s = (String) p1.getItemAtPosition(p3);
//                Toast.makeText(GlobalSearchActivity.this, s, Toast.LENGTH_SHORT).show();
//                channelService.openURL(searchService.getSearchListById(p3).getUrl(), GlobalSearchActivity.this);
//            }
//
//        });
//        list.setOnItemLongClickListener(new OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> p1, View p2, int p3, long p4) {
//                final String s = (String) p1.getItemAtPosition(p3);
//                final int selectedItem = p3;
//                Toast.makeText(GlobalSearchActivity.this, s, Toast.LENGTH_SHORT).show();
//                Boolean changesAllowed = true;
//                for (int i = 0; i < favoriteService.sizeOfFavoriteList(); i++) {
//                    if (searchService.getSearchListById(selectedItem).getName().equals(favoriteService.getFavoriteById(i).getName())
//                            && searchService.getSearchListById(selectedItem).getProv().equals(favoriteService.getFavoriteById(i).getProv()))
//                        changesAllowed = false;
//                }
//                if (changesAllowed) {
//                    // Add to favourite list dialog
//                    runOnUiThread(new Runnable() {
//                        public void run() {
//                            AlertDialog.Builder builder = new AlertDialog.Builder(GlobalSearchActivity.this);
//                            builder.setTitle(getResources().getString(R.string.request));
//                            builder.setMessage(getResources().getString(R.string.doyouwant)
//                                    + " " + getResources().getString(R.string.add) + " '" + s + "' to "
//                                    + getResources().getString(R.string.tofavorites));
//                            builder.setPositiveButton(getResources().getString(R.string.add), new DialogInterface.OnClickListener() {
//
//                                @Override
//                                public void onClick(DialogInterface p1, int p2) {
//                                    favoriteService.addToFavoriteList(s, searchService.getSearchListById(selectedItem).getProv());
//                                    try {
//                                        favoriteService.saveFavorites(GlobalSearchActivity.this);
//                                    } catch (IOException e) {
//                                    }
//                                }
//                            });
//                            builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
//
//                                @Override
//                                public void onClick(DialogInterface p1, int p2) {
//                                    // Nothing to do
//                                }
//                            });
//                            AlertDialog alert = builder.create();
//                            alert.setOwnerActivity(GlobalSearchActivity.this);
//                            alert.show();
//                        }
//                    });
//                }
//
//                return true;
//            }
//        });
    }
	
	private void setTick(Channel channel) {
        mAdapter.setSelected(channel);
        mAdapter.notifyDataSetChanged();
    }

    private void changeFavorite(Channel item) {
		Boolean changesAllowed = true;
                for (int i = 0; i < favoriteService.sizeOfFavoriteList(); i++) {
                    if (item.getName().equals(favoriteService.getFavoriteById(i).getName())
                            && item.getProvider().equals(favoriteService.getFavoriteById(i).getProv()))
                        changesAllowed = false;
                }
        if (changesAllowed)
            favoriteService.addToFavoriteList(item.getName(), item.getProvider());
        else
            favoriteService.deleteFromFavoritesById(favoriteService.indexNameForFavorite(item.getName()));
        try {
            favoriteService.saveFavorites(GlobalSearchActivity.this);
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
