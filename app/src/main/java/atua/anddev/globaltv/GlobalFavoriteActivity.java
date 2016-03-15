package atua.anddev.globaltv;

import android.app.AlertDialog;
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

public class GlobalFavoriteActivity extends MainActivity implements Services {

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

    private void openFavorite(int itemNum) {
        String getProvName = favoriteService.getFavoriteById(itemNum).getProv();
        int numA = playlistService.indexNameForActivePlaylist(getProvName);
        if (numA == -1) {
            Toast.makeText(GlobalFavoriteActivity.this, getResources().getString(R.string.playlistnotexist), Toast.LENGTH_SHORT).show();
            return;
        }
        readPlaylist(numA);
        for (int j = 0; j < channelService.sizeOfChannelList(); j++) {
            if (channelService.getChannelById(j).getName().equals(favoriteService.getFavoriteById(itemNum).getName())) {
                channelService.openURL(channelService.getChannelById(j).getUrl(), GlobalFavoriteActivity.this);
                break;
            }
        }
    }

}
