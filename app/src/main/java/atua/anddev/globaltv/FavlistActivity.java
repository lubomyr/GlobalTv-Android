package atua.anddev.globaltv;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

public class FavlistActivity extends Activity implements Services {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set sub.xml as user interface layout
        setContentView(R.layout.favlist);

        showFavlist();
    }

    public void showFavlist() {
        final List<String> playlist = favoriteService.getFavoriteListForSelProv();
        TextView textview = (TextView) findViewById(R.id.favlistTextView1);
        textview.setText(getResources().getString(R.string.favorites));
        ListView listView = (ListView) findViewById(R.id.favlistListView1);
        final ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, playlist);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4) {
                String s = (String) p1.getItemAtPosition(p3);
                Toast.makeText(FavlistActivity.this, s, Toast.LENGTH_SHORT).show();
                channelService.openChannel(s, FavlistActivity.this);
            }

        });
        listView.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> p1, View p2, int p3, long p4) {
                final String s = (String) p1.getItemAtPosition(p3);
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
                                    String provName = playlistService.getActivePlaylistById(MainActivity.selectedProvider).getName();
                                    int index = favoriteService.indexOfFavoriteByNameAndProv(s, provName);
                                    if (index != -1)
                                        favoriteService.deleteFromFavoritesById(index);
                                    try {
                                        favoriteService.saveFavorites(FavlistActivity.this);
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
