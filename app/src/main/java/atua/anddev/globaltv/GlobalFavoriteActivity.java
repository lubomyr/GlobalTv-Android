package atua.anddev.globaltv;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
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
import android.content.*;

public class GlobalFavoriteActivity extends MainActivity implements GlobalServices, ChannelHolderAdapter.OnItemClickListener {
	private List<Channel> favoriteList;
	private ChannelHolderAdapter mAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set sub.xml as user interface layout
        setContentView(R.layout.globalfavorite);
		
		favoriteList = new ArrayList<Channel>();
        showFavorites();
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
                openFavorite(item);
                break;
        }
	}
	

    public void showFavorites() {
        TextView textview = (TextView) findViewById(R.id.globalfavoriteTextView1);
        textview.setText(getResources().getString(R.string.favorites));
		
		for (int i=0; i < favoriteService.favoriteList.size(); i++) {
			Channel channel = new Channel(favoriteService.favoriteList.get(i), "", "");
			channel.setProvider(favoriteService.favoriteProvList.get(i));
			favoriteList.add(channel);
		}
		
		RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        mAdapter = new ChannelHolderAdapter(this, R.layout.item_channellist, favoriteList, true);
        mAdapter.setOnItemClickListener(GlobalFavoriteActivity.this);
        recyclerView.setAdapter(mAdapter);
		

//        final GlobalAdapter adapter = new GlobalAdapter(this, favoriteService.favoriteList, favoriteService.favoriteProvList);
//        ListView list = (ListView) findViewById(R.id.globalfavoriteListView1);
//        list.setAdapter(adapter);
//        list.setOnItemClickListener(new OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4) {
//                String s = (String) p1.getItemAtPosition(p3);
//                Toast.makeText(GlobalFavoriteActivity.this, s, Toast.LENGTH_SHORT).show();
//                openFavorite(p3);
//            }
//        });
//        list.setOnItemLongClickListener(new OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> p1, View p2, int p3, long p4) {
//                final String s = (String) p1.getItemAtPosition(p3);
//                final int selectedItem = p3;
//                Toast.makeText(GlobalFavoriteActivity.this, s, Toast.LENGTH_SHORT).show();
//                {
//                    // Remove from favourite list dialog
//                    runOnUiThread(new Runnable() {
//                        public void run() {
//                            Resources res = getResources();
//                            AlertDialog.Builder builder = new AlertDialog.Builder(GlobalFavoriteActivity.this);
//                            builder.setTitle(res.getString(R.string.request));
//                            builder.setMessage(res.getString(R.string.doyouwant) + " "
//                                    + res.getString(R.string.remove) + " '" + s + "' "
//                                    + res.getString(R.string.fromfavorites));
//                            builder.setPositiveButton(res.getString(R.string.remove), new DialogInterface.OnClickListener() {
//
//                                @Override
//                                public void onClick(DialogInterface p1, int p2) {
//                                    favoriteService.deleteFromFavoritesById(selectedItem);
//                                    try {
//                                        favoriteService.saveFavorites(GlobalFavoriteActivity.this);
//                                    } catch (IOException ignored) {
//                                    }
//                                    adapter.notifyDataSetChanged();
//                                }
//                            });
//                            builder.setNegativeButton(res.getString(R.string.cancel), new DialogInterface.OnClickListener() {
//
//                                @Override
//                                public void onClick(DialogInterface p1, int p2) {
//                                    // Nothing to do
//                                }
//                            });
//                            AlertDialog alert = builder.create();
//                            alert.setOwnerActivity(GlobalFavoriteActivity.this);
//                            alert.show();
//                        }
//                    });
//                }
//
//                return true;
//            }
//        });
    }

    private void openFavorite(Channel item) {
        String getProvName = item.getProvider();
        int numA = playlistService.indexNameForActivePlaylist(getProvName);
        if (numA == -1) {
            Toast.makeText(GlobalFavoriteActivity.this, getResources().getString(R.string.playlistnotexist), Toast.LENGTH_SHORT).show();
            return;
        }
        playlistService.readPlaylist(numA);
        for (Channel chn : channelService.getAllChannels()) {
            if (chn.getName().equals(item.getName())) {
                channelService.openURL(chn.getUrl(), GlobalFavoriteActivity.this);
                break;
            }
        }
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
            favoriteService.saveFavorites(GlobalFavoriteActivity.this);
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
