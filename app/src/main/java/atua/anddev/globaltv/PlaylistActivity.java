package atua.anddev.globaltv;

import android.app.*;
import android.content.*;
import android.net.*;
import android.os.*;
import android.text.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;

import java.util.*;

import android.inputmethodservice.*;
import android.inputmethodservice.KeyboardView.*;

import java.io.*;

public class PlaylistActivity extends MainActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set sub.xml as user interface layout
        setContentView(R.layout.playlist);

        applyLocals();
        openCategory(selectedCategory);
    }

    private void applyLocals() {
        Button buttonFavorite = (Button) findViewById(R.id.playlistButton1);
        buttonFavorite.setText(getResources().getString(R.string.favorites));
        Button buttonSearch = (Button) findViewById(R.id.playlistButton2);
        buttonSearch.setText(getResources().getString(R.string.search));
    }

    private void openCategory(final String catName) {
        final ArrayList<String> playlist = new ArrayList<String>();
        final ArrayList<String> playlistUrl = new ArrayList<String>();
        for (int i = 0; i < channelService.sizeOfChannelList(); i++) {
            if (catName.equals(getResources().getString(R.string.all))) {
                playlist.add(channelService.getChannelById(i).getName());
                playlistUrl.add(channelService.getChannelById(i).getUrl());
            } else if (catName.equals(channelService.getChannelById(i).getCategory())) {
                playlist.add(channelService.getChannelById(i).getName());
                playlistUrl.add(channelService.getChannelById(i).getUrl());
            }
        }

        TextView textView = (TextView) findViewById(R.id.playlistTextView1);
        textView.setText(catName + " - " + playlist.size() + " " + getResources().getString(R.string.channels));

        ListView listView = (ListView) findViewById(R.id.playlistListView1);
        final ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, playlist);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4) {
                // TODO: Implement this method
                String s = (String) p1.getItemAtPosition(p3);
                Toast.makeText(PlaylistActivity.this, s, Toast.LENGTH_SHORT).show();
                openURL(playlistUrl.get(p3));
            }

        });
        listView.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> p1, View p2, int p3, long p4) {
                // TODO: Implement this method
                final String s = (String) p1.getItemAtPosition(p3);
                Toast.makeText(PlaylistActivity.this, s, Toast.LENGTH_SHORT).show();
                if (!(favoriteService.containsNameForFavorite(s) && playlistService.getActivePlaylistById(selectedProvider).getName().equals(favoriteService.getFavoriteById(favoriteService.indexNameForFavorite(s)).getProv()))) {
                    // Add to favourite list dialog
                    runOnUiThread(new Runnable() {
                        public void run() {
                            AlertDialog.Builder builder = new AlertDialog.Builder(PlaylistActivity.this);
                            builder.setTitle(getResources().getString(R.string.request));
                            builder.setMessage(getResources().getString(R.string.doyouwant) + " " + getResources().getString(R.string.add) + " '" + s + "' " + getResources().getString(R.string.tofavorites));
                            builder.setPositiveButton(getResources().getString(R.string.add), new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface p1, int p2) {
                                    favoriteService.addToFavoriteList(s, playlistService.getActivePlaylistById(selectedProvider).getName());
                                    try {
                                        saveFavorites();
                                    } catch (IOException e) {
                                    }
                                }
                            });
                            builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface p1, int p2) {
                                    // TODO: Implement this method
                                }
                            });
                            AlertDialog alert = builder.create();
                            alert.setOwnerActivity(PlaylistActivity.this);
                            alert.show();
                        }
                    });
                } else {
                    // Remove from favourite list dialog
                    runOnUiThread(new Runnable() {
                        public void run() {
                            AlertDialog.Builder builder = new AlertDialog.Builder(PlaylistActivity.this);
                            builder.setTitle(getResources().getString(R.string.request));
                            builder.setMessage(getResources().getString(R.string.doyouwant) + " " + getResources().getString(R.string.remove) + " '" + s + "' " + getResources().getString(R.string.fromfavorites));
                            builder.setPositiveButton(getResources().getString(R.string.remove), new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface p1, int p2) {
                                    favoriteService.deleteFromFavoritesById(favoriteService.indexNameForFavorite(s));
                                    try {
                                        saveFavorites();
                                    } catch (IOException e) {
                                    }

                                }
                            });
                            builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface p1, int p2) {
                                    // TODO: Implement this method
                                }
                            });
                            AlertDialog alert = builder.create();
                            alert.setOwnerActivity(PlaylistActivity.this);
                            alert.show();
                        }
                    });
                }

                return true;
            }
        });
    }


    public void favButton(View view) {
        favlistActivity();
    }

    public void searchDialog(View view) {
        final EditText input = new EditText(this);
        input.setSingleLine();
        new AlertDialog.Builder(PlaylistActivity.this)
                .setTitle(getResources().getString(R.string.request))
                .setMessage(getResources().getString(R.string.pleaseentertext))
                .setView(input)
                .setPositiveButton(getResources().getString(R.string.search), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Editable value = input.getText();
                        searchString = value.toString();
                        searchlistActivity();
                    }
                }).setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Do nothing.
            }
        }).show();
    }
}
