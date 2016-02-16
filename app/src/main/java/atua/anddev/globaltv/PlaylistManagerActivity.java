package atua.anddev.globaltv;

import android.app.*;
import android.content.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;

import java.io.*;

import org.xmlpull.v1.*;

public class PlaylistManagerActivity extends MainActivity {
    static int editNum;
    static String editAction;
    static Boolean enable = true;
    ListView listView;
    ListView DlistView;
    TextView textView;
    ArrayAdapter adapter;
    ArrayAdapter Dadapter;

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
        Button buttonEnabled = (Button) findViewById(R.id.playlistmanagerButton2);
        buttonEnabled.setText(getResources().getString(R.string.enabled));
        Button buttonDisabled = (Button) findViewById(R.id.playlistmanagerButton3);
        buttonDisabled.setText(getResources().getString(R.string.disabled));
        Button buttonRestore = (Button) findViewById(R.id.playlistmanagerButton4);
        buttonRestore.setText(getResources().getString(R.string.restoreDefaults));
    }

    private void createProvlist() {
        listView = (ListView) findViewById(R.id.playlistManagerListView1);
        DlistView = (ListView) findViewById(R.id.playlistManagerListView2);
        textView = (TextView) findViewById(R.id.playlistManagerTextView1);
        if (enable) {
            listView.setVisibility(View.VISIBLE);
            DlistView.setVisibility(View.GONE);
            textView.setText(getResources().getString(R.string.enabled) + " - " + ActivePlaylist.size() + " " + getResources().getString(R.string.pcs));
        } else {
            listView.setVisibility(View.GONE);
            DlistView.setVisibility(View.VISIBLE);
            textView.setText(getResources().getString(R.string.disabled) + " - " + DisabledPlaylist.size() + " " + getResources().getString(R.string.pcs));
        }
    }

    public void showProvlist() {
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, ActivePlaylist.name);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4) {
                // TODO: Implement this method
                final String s = (String) p1.getItemAtPosition(p3);
                Toast.makeText(PlaylistManagerActivity.this, s, Toast.LENGTH_SHORT).show();
                editNum = p3;
                editAction = "modify";
                playlistEditActivity();
            }
        });
        listView.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> p1, View p2, int p3, long p4) {
                final String s = (String) p1.getItemAtPosition(p3);
                Toast.makeText(PlaylistManagerActivity.this, s, Toast.LENGTH_SHORT).show();
                editNum = p3;
                // disable playlist dialog
                runOnUiThread(new Runnable() {
                    public void run() {
                        AlertDialog.Builder builder = new AlertDialog.Builder(PlaylistManagerActivity.this);
                        builder.setTitle(getResources().getString(R.string.request));
                        builder.setMessage(getResources().getString(R.string.doyouwant) + " " + getResources().getString(R.string.disable) + " '" + s + "'");
                        builder.setPositiveButton(getResources().getString(R.string.disable), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface p1, int p2) {
                                DisabledPlaylist.add(ActivePlaylist.getName(editNum), ActivePlaylist.getUrl(editNum), ActivePlaylist.getFile(editNum), ActivePlaylist.getType(editNum));
                                ActivePlaylist.remove(editNum);
                                adapter.notifyDataSetChanged();
                                try {
                                    saveData();
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
                        alert.setOwnerActivity(PlaylistManagerActivity.this);
                        alert.show();
                    }
                });
                return true;
            }
        });

        Dadapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, DisabledPlaylist.name);
        DlistView.setAdapter(Dadapter);
        DlistView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4) {
                final String s = (String) p1.getItemAtPosition(p3);
                Toast.makeText(PlaylistManagerActivity.this, s, Toast.LENGTH_SHORT).show();
                editNum = p3;
                editAction = "modify";
                playlistEditActivity();
            }
        });
        DlistView.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> p1, View p2, int p3, long p4) {
                final String s = (String) p1.getItemAtPosition(p3);
                Toast.makeText(PlaylistManagerActivity.this, s, Toast.LENGTH_SHORT).show();
                editNum = p3;
                // enable playlist dialog
                runOnUiThread(new Runnable() {
                    public void run() {
                        AlertDialog.Builder builder = new AlertDialog.Builder(PlaylistManagerActivity.this);
                        builder.setTitle(getResources().getString(R.string.request));
                        builder.setMessage(getResources().getString(R.string.doyouwant) + " " + getResources().getString(R.string.enable) + " '" + s + "'");
                        builder.setPositiveButton(getResources().getString(R.string.enable), new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface p1, int p2) {
                                ActivePlaylist.add(DisabledPlaylist.getName(editNum), DisabledPlaylist.getUrl(editNum), DisabledPlaylist.getFile(editNum), DisabledPlaylist.getType(editNum));
                                DisabledPlaylist.remove(editNum);
                                Dadapter.notifyDataSetChanged();
                                try {
                                    saveData();
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
        serializer.text(torrentKey);
        serializer.endTag(null, "torrentkey");

        for (int j = 0; j < ActivePlaylist.size(); j++) {
            serializer.startTag(null, "provider");

            serializer.startTag(null, "name");
            serializer.text(ActivePlaylist.getName(j));
            serializer.endTag(null, "name");

            serializer.startTag(null, "url");
            serializer.text(ActivePlaylist.getUrl(j));
            serializer.endTag(null, "url");

//			serializer.startTag(null, "file");
//			serializer.text(ActivePlaylist.getFile(j));
//			serializer.endTag(null, "file");

            serializer.startTag(null, "type");
            serializer.text(ActivePlaylist.getTypeString(j));
            serializer.endTag(null, "type");

            serializer.startTag(null, "enable");
            serializer.text("true");
            serializer.endTag(null, "enable");

            serializer.endTag(null, "provider");
        }
        for (int j = 0; j < DisabledPlaylist.size(); j++) {
            serializer.startTag(null, "provider");

            serializer.startTag(null, "name");
            serializer.text(DisabledPlaylist.getName(j));
            serializer.endTag(null, "name");

            serializer.startTag(null, "url");
            serializer.text(DisabledPlaylist.getUrl(j));
            serializer.endTag(null, "url");

//			serializer.startTag(null, "file");
//			serializer.text(DisabledPlaylist.getFile(j));
//			serializer.endTag(null, "file");

            serializer.startTag(null, "type");
            serializer.text(DisabledPlaylist.getTypeString(j));
            serializer.endTag(null, "type");

            serializer.startTag(null, "enable");
            serializer.text("false");
            serializer.endTag(null, "enable");

            serializer.endTag(null, "provider");
        }
        serializer.endDocument();
        serializer.flush();
        fos.close();
    }

    public void addNewPlaylist(View view) {
        editAction = "add";
        playlistEditActivity();
    }

    public void showEnabled(View view) {
        listView.setVisibility(View.VISIBLE);
        DlistView.setVisibility(View.GONE);
        textView.setText(getResources().getString(R.string.enabled) + " - " + ActivePlaylist.size() + " " + getResources().getString(R.string.pcs));
        enable = true;
    }

    public void showDisabled(View view) {
        listView.setVisibility(View.GONE);
        DlistView.setVisibility(View.VISIBLE);
        textView.setText(getResources().getString(R.string.disabled) + " - " + DisabledPlaylist.size() + " " + getResources().getString(R.string.pcs));
        enable = false;
    }

    public void playlistEditActivity() {
        Intent intent = new Intent(this, PlaylistEditActivity.class);
        startActivity(intent);
    }

    public void restoreDefault(View view) {
        ActivePlaylist.clear();
        DisabledPlaylist.clear();
        try {
            setupProvider("default");
        } catch (Exception e1) {
            Toast.makeText(PlaylistManagerActivity.this, e1.toString(), Toast.LENGTH_SHORT).show();
        }
        adapter.notifyDataSetChanged();
        Dadapter.notifyDataSetChanged();
        try {
            saveData();
        } catch (IOException e) {
        }
    }

}
