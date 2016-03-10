package atua.anddev.globaltv;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;

import java.io.*;

import atua.anddev.globaltv.service.*;

public class GlobalFavoriteActivity extends MainActivity {
    private ChannelService channelService = MainActivity.channelService;
    private FavoriteService favoriteService = MainActivity.favoriteService;
    private PlaylistService playlistService = MainActivity.playlistService;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set sub.xml as user interface layout
        setContentView(R.layout.globalfavorite);
        showFavorites();
    }

    public void showFavorites() {
        TextView textview = (TextView) findViewById(R.id.globalfavoriteTextView1);
        textview.setText(getResources().getString(R.string.favorites));

        final GlobalAdapter adapter = new GlobalAdapter(this, favoriteService.favoriteList, favoriteService.favoriteProvList);
        ListView list = (ListView) findViewById(R.id.globalfavoriteListView1);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4) {
                String s = (String) p1.getItemAtPosition(p3);
                Toast.makeText(GlobalFavoriteActivity.this, s, Toast.LENGTH_SHORT).show();
                openFavorite(p3);
            }
        });
        list.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> p1, View p2, int p3, long p4) {
                final String s = (String) p1.getItemAtPosition(p3);
                final int selectedItem = p3;
                Toast.makeText(GlobalFavoriteActivity.this, s, Toast.LENGTH_SHORT).show();
                {
                    // Remove from favourite list dialog
                    runOnUiThread(new Runnable() {
                        public void run() {
                            AlertDialog.Builder builder = new AlertDialog.Builder(GlobalFavoriteActivity.this);
                            builder.setTitle(getResources().getString(R.string.request));
                            builder.setMessage(getResources().getString(R.string.doyouwant) + " " + getResources().getString(R.string.remove) + " '" + s + "' " + getResources().getString(R.string.fromfavorites));
                            builder.setPositiveButton(getResources().getString(R.string.remove), new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface p1, int p2) {
                                    favoriteService.deleteFromFavoritesById(selectedItem);
                                    try {
                                        favoriteService.saveFavorites(GlobalFavoriteActivity.this);
                                    } catch (IOException e) {
                                    }
                                    adapter.notifyDataSetChanged();
                                }
                            });
                            builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface p1, int p2) {
                                    // Nothing to do
                                }
                            });
                            AlertDialog alert = builder.create();
                            alert.setOwnerActivity(GlobalFavoriteActivity.this);
                            alert.show();
                        }
                    });
                }

                return true;
            }
        });
    }

    public void openFavorite(int itemNum) {
        String getProvFile = null;
        int getProvType = 0;
        String getProvName = favoriteService.getFavoriteById(itemNum).getProv();
        int numA = playlistService.indexNameForActivePlaylist(getProvName);
        int numD = playlistService.indexNameForOfferedPlaylist(getProvName);
        if (numA >= 0) {
            getProvFile = playlistService.getActivePlaylistById(numA).getFile();
            getProvType = playlistService.getActivePlaylistById(playlistService.indexNameForActivePlaylist(getProvName)).getType();
        } else if (numD >= 0) {
            getProvFile = playlistService.getOfferedPlaylistById(numD).getFile();
            getProvType = playlistService.getOfferedPlaylistById(playlistService.indexNameForOfferedPlaylist(getProvName)).getType();
        } else {
            Toast.makeText(GlobalFavoriteActivity.this, getResources().getString(R.string.playlistnotexist), Toast.LENGTH_SHORT).show();
            return;
        }
        readPlaylist(getProvFile, getProvType);
        for (int j = 0; j < channelService.sizeOfChannelList(); j++) {
            if (channelService.getChannelById(j).getName().equals(favoriteService.getFavoriteById(itemNum).getName())) {
                channelService.openURL(channelService.getChannelById(j).getUrl(), GlobalFavoriteActivity.this);
                break;
            }
        }
    }

}
