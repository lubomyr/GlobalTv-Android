package atua.anddev.globaltv;

import android.app.*;
import android.content.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;

import atua.anddev.globaltv.service.*;

import java.io.*;

import org.xmlpull.v1.*;

public class PlaylistManagerActivity extends Activity {
    protected static int editNum;
    protected static String editAction;
    protected static Boolean enable = true;
    protected PlaylistService playlistService = MainActivity.playlistService;
    private ListView selectedlistView;
    private ListView offeredlistView;
    private TextView textView;
    private ArrayAdapter selectedAdapter;
    private ArrayAdapter offeredAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set sub.xml as user interface layout
        setContentView(R.layout.playlistmanager);

        applyLocals();
        createProvlist();
        showProvlist();
    }

    private void applyLocals() {
        Button buttonAddnewPlaylist = (Button) findViewById(R.id.playlistManagerButton1);
        buttonAddnewPlaylist.setText(getResources().getString(R.string.addnewplaylist));
        Button buttonSelected = (Button) findViewById(R.id.playlistmanagerButton2);
        buttonSelected.setText(getResources().getString(R.string.selected));
        Button buttonOffered = (Button) findViewById(R.id.playlistmanagerButton3);
        buttonOffered.setText(getResources().getString(R.string.offered));
        Button buttonRestore = (Button) findViewById(R.id.playlistmanagerButton4);
        buttonRestore.setText(getResources().getString(R.string.reset));
    }

    private void createProvlist() {
        selectedlistView = (ListView) findViewById(R.id.playlistManagerListView1);
        offeredlistView = (ListView) findViewById(R.id.playlistManagerListView2);
        textView = (TextView) findViewById(R.id.playlistManagerTextView1);
        if (enable) {
            selectedlistView.setVisibility(View.VISIBLE);
            offeredlistView.setVisibility(View.GONE);
            textView.setText(getResources().getString(R.string.selected) + " - " + playlistService.sizeOfActivePlaylist() + " " + getResources().getString(R.string.pcs));
        } else {
            selectedlistView.setVisibility(View.GONE);
            offeredlistView.setVisibility(View.VISIBLE);
            textView.setText(getResources().getString(R.string.offered) + " - " + playlistService.sizeOfOfferedPlaylist() + " " + getResources().getString(R.string.pcs));
        }
    }

    private void showProvlist() {
        selectedAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, playlistService.activePlaylistName);
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
                        builder.setTitle(getResources().getString(R.string.request));
                        builder.setMessage(getResources().getString(R.string.doyouwant) + " " + getResources().getString(R.string.remove) + " '" + s + "'");
                        builder.setPositiveButton(getResources().getString(R.string.remove), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface p1, int p2) {
                                playlistService.deleteActivePlaylistById(editNum);
                                selectedAdapter.notifyDataSetChanged();
                                MainActivity.provAdapter.notifyDataSetChanged();

                                try {
                                    saveData();
                                } catch (IOException e) {
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

        offeredAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, playlistService.getAllNamesOfOfferedPlaylist());
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
                        builder.setTitle(getResources().getString(R.string.request));
                        builder.setMessage(getResources().getString(R.string.doyouwant) + " " + getResources().getString(R.string.add) + " '" + s + "'");
                        builder.setPositiveButton(getResources().getString(R.string.add), new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface p1, int p2) {
                                // check if playlist already exist in selected playlist
                                if (playlistService.indexNameForActivePlaylist(playlistService.getOfferedPlaylistById(editNum).getName()) == -1) {
                                    playlistService.addToActivePlaylist(playlistService.getOfferedPlaylistById(editNum).getName(), playlistService.getOfferedPlaylistById(editNum).getUrl(), playlistService.getOfferedPlaylistById(editNum).getType());
                                    offeredAdapter.notifyDataSetChanged();
                                    MainActivity.provAdapter.notifyDataSetChanged();
                                    try {
                                        saveData();
                                    } catch (IOException e) {
                                    }
                                } else {
                                    Toast.makeText(PlaylistManagerActivity.this, getResources().getString(R.string.playlistexist), Toast.LENGTH_SHORT).show();
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

    public void saveData() throws FileNotFoundException, IOException {
        FileOutputStream fos;
        fos = openFileOutput("userdata.xml", Context.MODE_WORLD_WRITEABLE);
        XmlSerializer serializer = Xml.newSerializer();
        serializer.setOutput(fos, "UTF-8");
        serializer.startDocument(null, Boolean.valueOf(true));
        serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
        serializer.startTag(null, "data");

        serializer.startTag(null, "torrentkey");
        serializer.text(MainActivity.torrentKey);
        serializer.endTag(null, "torrentkey");

        for (int j = 0; j < playlistService.sizeOfActivePlaylist(); j++) {
            serializer.startTag(null, "provider");

            serializer.startTag(null, "name");
            serializer.text(playlistService.getActivePlaylistById(j).getName());
            serializer.endTag(null, "name");

            serializer.startTag(null, "url");
            serializer.text(playlistService.getActivePlaylistById(j).getUrl());
            serializer.endTag(null, "url");

            serializer.startTag(null, "type");
            serializer.text(playlistService.getActivePlaylistById(j).getTypeString());
            serializer.endTag(null, "type");

            serializer.endTag(null, "provider");
        }

        serializer.endDocument();
        serializer.flush();
        fos.close();
    }

    public void addNewPlaylist(View view) {
        editAction = "addNew";
        playlistEditActivity();
    }

    public void showSelected(View view) {
        selectedlistView.setVisibility(View.VISIBLE);
        offeredlistView.setVisibility(View.GONE);
        textView.setText(getResources().getString(R.string.selected) + " - " + playlistService.sizeOfActivePlaylist() + " " + getResources().getString(R.string.pcs));
        enable = true;
    }

    public void showOffered(View view) {
        selectedlistView.setVisibility(View.GONE);
        offeredlistView.setVisibility(View.VISIBLE);
        textView.setText(getResources().getString(R.string.offered) + " - " + playlistService.sizeOfOfferedPlaylist() + " " + getResources().getString(R.string.pcs));
        enable = false;
    }

    public void playlistEditActivity() {
        Intent intent = new Intent(this, PlaylistEditActivity.class);
        startActivity(intent);
    }

    public void restoreDefault(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(PlaylistManagerActivity.this);
        builder.setTitle(getResources().getString(R.string.request));
        builder.setMessage(getResources().getString(R.string.resetwarn));
        builder.setPositiveButton(getResources().getString(R.string.reset), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface p1, int p2) {
                playlistService.clearActivePlaylist();
                selectedAdapter.notifyDataSetChanged();
                try {
                    saveData();
                } catch (IOException e) {
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

}
