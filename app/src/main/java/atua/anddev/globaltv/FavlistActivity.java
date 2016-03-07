package atua.anddev.globaltv;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;

import java.io.*;
import java.util.*;

public class FavlistActivity extends CatlistActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set sub.xml as user interface layout
        setContentView(R.layout.favlist);

        showFavlist();
    }

    public void showFavlist() {
        final ArrayList<String> playlist = new ArrayList<String>();
        for (int i = 0; i < favoriteService.sizeOfFavoriteList(); i++) {
            for (int j = 0; j < channelService.sizeOfChannelList(); j++) {
                if (favoriteService.getFavoriteById(i).getName().equals(channelService.getChannelById(j).getName()) && !playlist.contains(favoriteService.getFavoriteById(i).getName())) {
                    playlist.add(favoriteService.getFavoriteById(i).getName());
                }
            }
        }
        TextView textview = (TextView) findViewById(R.id.favlistTextView1);
        textview.setText(getResources().getString(R.string.favorites));
        ListView listView = (ListView) findViewById(R.id.favlistListView1);
        final ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, playlist);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4) {
                // TODO: Implement this method
                String s = (String) p1.getItemAtPosition(p3);
                Toast.makeText(FavlistActivity.this, s, Toast.LENGTH_SHORT).show();
                openChannel(s);
            }

        });
        listView.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> p1, View p2, int p3, long p4) {
                // TODO: Implement this method
                final String s = (String) p1.getItemAtPosition(p3);
                final int selectedItem = p3;
                Toast.makeText(FavlistActivity.this, s, Toast.LENGTH_SHORT).show();
                {

                    // Remove from favourite list dialog
                    runOnUiThread(new Runnable() {
                        public void run() {
                            AlertDialog.Builder builder = new AlertDialog.Builder(FavlistActivity.this);
                            builder.setTitle(getResources().getString(R.string.request));
                            builder.setMessage(getResources().getString(R.string.doyouwant) + " " + getResources().getString(R.string.remove) + " '" + s + "' " + getResources().getString(R.string.fromfavorites));
                            builder.setPositiveButton(getResources().getString(R.string.remove), new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface p1, int p2) {
                                    playlist.remove(s);
                                    favoriteService.deleteFromFavoritesById(favoriteService.indexNameForFavorite(s));
                                    try {
                                        saveFavorites();
                                    } catch (IOException e) {
                                    }
                                    adapter.notifyDataSetChanged();

                                }
                            });
                            builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface p1, int p2) {
                                    // TODO: Implement this method
                                }
                            });
                            AlertDialog alert = builder.create();
                            alert.setOwnerActivity(FavlistActivity.this);
                            alert.show();
                        }
                    });
                }

                return true;
            }
        });

    }

}
