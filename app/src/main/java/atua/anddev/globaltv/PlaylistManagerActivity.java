package atua.anddev.globaltv;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class PlaylistManagerActivity extends TabActivity implements GlobalServices {
    private int editNum;
    private String editAction;
    private Boolean enable = true;
    private ArrayAdapter selectedAdapter;
    final String TABS_TAG_1 = "Tag 1";
    final String TABS_TAG_2 = "Tag 2";

    private ArrayAdapter offeredAdapter;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Set sub.xml as user interface layout
        setContentView(R.layout.playlistmanager);

        applyLocals();
        setupTabs();
    }

    @Override
    protected void onResume() {
        super.onResume();

        selectedAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.playlist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.playlist_add_all) {
            playlistService.addAllOfferedPlaylist();
            try {
                playlistService.saveData(PlaylistManagerActivity.this);
                selectedAdapter.notifyDataSetChanged();
            } catch (IOException e) {
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupTabs() {
        TabHost tabHost = getTabHost();
        TabHost.TabSpec tabSpec;
        tabSpec = tabHost.newTabSpec(TABS_TAG_1);
        tabSpec.setContent(TabFactory);
        tabSpec.setIndicator(getString(R.string.selected));
        tabHost.addTab(tabSpec);
        tabSpec = tabHost.newTabSpec(TABS_TAG_2);
        tabSpec.setContent(TabFactory);
        tabSpec.setIndicator(getString(R.string.offered));
        tabHost.addTab(tabSpec);

        String selTabTag = tabHost.getCurrentTabTag();
        if (selTabTag == TABS_TAG_1)
            showSelected();
        else if (selTabTag == TABS_TAG_2)
            showOffered();

        tabHost.setOnTabChangedListener(new OnTabChangeListener() {

            @Override
            public void onTabChanged(String p1) {
                if (p1 == TABS_TAG_1)
                    showSelected();
                else if (p1 == TABS_TAG_2)
                    showOffered();
            }
        });
    }

    private void applyLocals() {
        Button buttonAddnewPlaylist = (Button) findViewById(R.id.playlistManagerButton1);
        buttonAddnewPlaylist.setText(getResources().getString(R.string.addnewplaylist));
        Button buttonRestore = (Button) findViewById(R.id.playlistmanagerButton4);
        buttonRestore.setText(getResources().getString(R.string.reset));
    }

    TabHost.TabContentFactory TabFactory = new TabHost.TabContentFactory() {

        @Override
        public View createTabContent(String tag) {
            if (tag == TABS_TAG_1) {
                return getLayoutInflater().inflate(R.layout.plman_selected, null);
                //return getLayoutInflater().inflate(R.layout.tab, null);
            } else if (tag == TABS_TAG_2) {
                return getLayoutInflater().inflate(R.layout.plman_offered, null);
            }
            return null;
        }

    };

    public void addNewPlaylist(View view) {
        editAction = "addNew";
        playlistEditActivity();
    }

    public void showSelected() {
        ListView selectedlistView = (ListView) findViewById(R.id.playlistManagerListViewF1);
        TextView textView = (TextView) findViewById(R.id.playlistManagerTextViewF1);
        textView.setText(getResources().getString(R.string.selected) + " - " + playlistService.sizeOfActivePlaylist() + " " + getResources().getString(R.string.pcs));
        enable = true;

        selectedAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1,
                playlistService.activePlaylistName);
        selectedlistView.setAdapter(selectedAdapter);
        selectedlistView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4) {
                final String s = (String) p1.getItemAtPosition(p3);
                Toast.makeText(PlaylistManagerActivity.this, s, Toast.LENGTH_SHORT).show();
                editNum = p3;
                editAction = "modify";
                playlistEditActivity();
            }
        });
        selectedlistView.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> p1, View p2, int p3, long p4) {
                final String s = (String) p1.getItemAtPosition(p3);
                Toast.makeText(PlaylistManagerActivity.this, s, Toast.LENGTH_SHORT).show();
                editNum = p3;
                // offered playlist dialog
                runOnUiThread(new Runnable() {
                    public void run() {
                        AlertDialog.Builder builder = new AlertDialog.Builder(PlaylistManagerActivity.this);
                        builder.setTitle(getString(R.string.request));
                        builder.setMessage(getString(R.string.doyouwant) + " " + getString(R.string.remove) + " '" + s + "'");
                        builder.setPositiveButton(getString(R.string.remove), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface p1, int p2) {
                                playlistService.deleteActivePlaylistById(editNum);
                                selectedAdapter.notifyDataSetChanged();
                                try {
                                    playlistService.saveData(PlaylistManagerActivity.this);
                                } catch (IOException e) {
                                }
                            }
                        });
                        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface p1, int p2) {
                                // nothing to do
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.setOwnerActivity(PlaylistManagerActivity.this);
                        alert.show();
                    }
                });
                return true;
            }
        });
    }

    public void showOffered() {
        ListView offeredlistView = (ListView) findViewById(R.id.playlistManagerListViewF2);
        TextView textView = (TextView) findViewById(R.id.playlistManagerTextViewF2);
        textView.setText(getString(R.string.offered) + " - " + playlistService.sizeOfOfferedPlaylist() +
                " " + getString(R.string.pcs));
        enable = false;

        offeredAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1,
                playlistService.getAllNamesOfOfferedPlaylist());
        offeredlistView.setAdapter(offeredAdapter);
        offeredlistView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4) {
                final String s = (String) p1.getItemAtPosition(p3);
                Toast.makeText(PlaylistManagerActivity.this, s, Toast.LENGTH_SHORT).show();
                editNum = p3;
                editAction = "modify";
                playlistEditActivity();
            }
        });
        offeredlistView.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> p1, View p2, int p3, long p4) {
                final String s = (String) p1.getItemAtPosition(p3);
                Toast.makeText(PlaylistManagerActivity.this, s, Toast.LENGTH_SHORT).show();
                editNum = p3;
                // selected playlist dialog
                runOnUiThread(new Runnable() {
                    public void run() {
                        AlertDialog.Builder builder = new AlertDialog.Builder(PlaylistManagerActivity.this);
                        builder.setTitle(getString(R.string.request));
                        builder.setMessage(getString(R.string.doyouwant) + " " + getString(R.string.add) + " '" + s + "'");
                        builder.setPositiveButton(getString(R.string.add), new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface p1, int p2) {
                                // check if playlist already exist in selected playlist
                                if (playlistService.indexNameForActivePlaylist(playlistService.getOfferedPlaylistById(editNum).getName()) == -1) {
                                    playlistService.addNewActivePlaylist(playlistService.getOfferedPlaylistById(editNum));
                                    offeredAdapter.notifyDataSetChanged();
                                    try {
                                        playlistService.saveData(PlaylistManagerActivity.this);
                                    } catch (IOException e) {
                                    }
                                } else {
                                    Toast.makeText(PlaylistManagerActivity.this, getString(R.string.playlistexist),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface p1, int p2) {
                                // nothing to do
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.setOwnerActivity(PlaylistManagerActivity.this);
                        alert.show();
                    }
                });
                return true;
            }
        });
    }

    public void playlistEditActivity() {
        Intent intent = new Intent(this, PlaylistEditActivity.class);
        intent.putExtra("num", editNum);
        intent.putExtra("action", editAction);
        intent.putExtra("enable", enable);
        startActivity(intent);
    }

    public void restoreDefault(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(PlaylistManagerActivity.this);
        builder.setTitle(getString(R.string.request));
        builder.setMessage(getString(R.string.resetwarn));
        builder.setPositiveButton(getString(R.string.reset), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface p1, int p2) {
                playlistService.clearActivePlaylist();
                selectedAdapter.notifyDataSetChanged();
                try {
                    playlistService.saveData(PlaylistManagerActivity.this);
                } catch (IOException e) {
                }
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface p1, int p2) {
                // nothing to do
            }
        });
        AlertDialog alert = builder.create();
        alert.setOwnerActivity(PlaylistManagerActivity.this);
        alert.show();
    }

}
