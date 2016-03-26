package atua.anddev.globaltv;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class GlobalSearchActivity extends MainActivity implements Services {
    private ProgressDialog progress;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set sub.xml as user interface layout
        setContentView(R.layout.globalsearch);
        if (searchService.sizeOfSearchList() == 0)
            runProgressBar();
        else
            showSearchResults();
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
            for (int j = 0; j < channelService.sizeOfChannelList(); j++) {
                chName = channelService.getChannelById(j).getName().toLowerCase();
                if (chName.contains(searchString.toLowerCase())) {
                    searchService.addToSearchList(channelService.getChannelById(j).getName(),
                            channelService.getChannelById(j).getUrl(), playlistService.getActivePlaylistById(i).getName());
                }
            }
        }

    }

    public void showSearchResults() {
        TextView textView = (TextView) findViewById(R.id.globalsearchTextView1);
        textView.setText(getResources().getString(R.string.resultsfor) + " '" + searchString + "' - " +
                searchService.sizeOfSearchList() + " " + getResources().getString(R.string.channels));
        GlobalAdapter adapter = new GlobalAdapter(this, searchService.searchNameList, searchService.searchProvList);
        ListView list = (ListView) findViewById(R.id.globalsearchListView1);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4) {
                String s = (String) p1.getItemAtPosition(p3);
                Toast.makeText(GlobalSearchActivity.this, s, Toast.LENGTH_SHORT).show();
                channelService.openURL(searchService.getSearchListById(p3).getUrl(), GlobalSearchActivity.this);
            }

        });
        list.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> p1, View p2, int p3, long p4) {
                final String s = (String) p1.getItemAtPosition(p3);
                final int selectedItem = p3;
                Toast.makeText(GlobalSearchActivity.this, s, Toast.LENGTH_SHORT).show();
                Boolean changesAllowed = true;
                for (int i = 0; i < favoriteService.sizeOfFavoriteList(); i++) {
                    if (searchService.getSearchListById(selectedItem).getName().equals(favoriteService.getFavoriteById(i).getName())
                            && searchService.getSearchListById(selectedItem).getProv().equals(favoriteService.getFavoriteById(i).getProv()))
                        changesAllowed = false;
                }
                if (changesAllowed) {
                    // Add to favourite list dialog
                    runOnUiThread(new Runnable() {
                        public void run() {
                            AlertDialog.Builder builder = new AlertDialog.Builder(GlobalSearchActivity.this);
                            builder.setTitle(getResources().getString(R.string.request));
                            builder.setMessage(getResources().getString(R.string.doyouwant)
                                    + " " + getResources().getString(R.string.add) + " '" + s + "' to "
                                    + getResources().getString(R.string.tofavorites));
                            builder.setPositiveButton(getResources().getString(R.string.add), new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface p1, int p2) {
                                    favoriteService.addToFavoriteList(s, searchService.getSearchListById(selectedItem).getProv());
                                    try {
                                        favoriteService.saveFavorites(GlobalSearchActivity.this);
                                    } catch (IOException e) {
                                    }
                                }
                            });
                            builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface p1, int p2) {
                                    // Nothing to do
                                }
                            });
                            AlertDialog alert = builder.create();
                            alert.setOwnerActivity(GlobalSearchActivity.this);
                            alert.show();
                        }
                    });
                }

                return true;
            }
        });
    }
}
