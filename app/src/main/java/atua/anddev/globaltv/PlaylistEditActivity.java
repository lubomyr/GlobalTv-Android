package atua.anddev.globaltv;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class PlaylistEditActivity extends Activity implements GlobalServices {
    private int selectedType;
    private Editable name, url;
    private String editAction;
    private int editNum;
    private boolean enable;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set sub.xml as user interface layout
        setContentView(R.layout.playlistedit);

        Intent intent = getIntent();
        editAction = intent.getStringExtra("action");
        editNum = intent.getIntExtra("num", -1);
        enable = intent.getBooleanExtra("enable", false);
        applyLocalsEdit();
        showEdit();
    }

    public void applyLocalsEdit() {
        TextView textViewName = (TextView) findViewById(R.id.playlisteditTextView1);
        textViewName.setText(getString(R.string.name));
        TextView textViewUrl = (TextView) findViewById(R.id.playlisteditTextView2);
        textViewUrl.setText(getString(R.string.url));
        TextView textViewType = (TextView) findViewById(R.id.playlisteditTextView4);
        textViewType.setText(getString(R.string.type));
        Button deleteButton = (Button) findViewById(R.id.playlisteditButton1);
        deleteButton.setText(getString(R.string.delete));
        Button addEditButton = (Button) findViewById(R.id.playlisteditButton2);
        if (editAction.equals("addNew")) {
            addEditButton.setText(getString(R.string.add));
            deleteButton.setVisibility(View.GONE);
        }
        if (editAction.equals("modify")) {
            if (enable) {
                addEditButton.setText(getString(R.string.modify));
                deleteButton.setVisibility(View.VISIBLE);
            } else {
                addEditButton.setText(getString(R.string.add));
                deleteButton.setVisibility(View.GONE);
            }
        }

    }

    public void showEdit() {
        ArrayList<String> typeList = new ArrayList<String>();
        typeList.add(getString(R.string.standardplaylist));
        typeList.add(getString(R.string.torrenttvplaylist));
        typeList.add(getString(R.string.w3u_playlist));
        Spinner spinnerView = (Spinner) findViewById(R.id.playlisteditSpinner1);
        SpinnerAdapter adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_expandable_list_item_1, typeList);
        spinnerView.setAdapter(adapter);

        EditText editTextName = (EditText) findViewById(R.id.playlisteditEditText1);
        EditText editTextUrl = (EditText) findViewById(R.id.playlisteditEditText2);
        if (editAction.equals("modify")) {
            if (enable) {
                editTextName.setText(playlistService.getActivePlaylistById(editNum).getName());
                editTextUrl.setText(playlistService.getActivePlaylistById(editNum).getUrl());
                selectedType = playlistService.getActivePlaylistById(editNum).getType();
                spinnerView.setSelection(playlistService.getActivePlaylistById(editNum).getType());
            } else {
                editTextName.setText(playlistService.getOfferedPlaylistById(editNum).getName());
                editTextUrl.setText(playlistService.getOfferedPlaylistById(editNum).getUrl());
                selectedType = playlistService.getOfferedPlaylistById(editNum).getType();
                spinnerView.setSelection(playlistService.getOfferedPlaylistById(editNum).getType());
            }
        }
        spinnerView.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onNothingSelected(AdapterView<?> p1) {
                // nothing to do
            }

            @Override
            public void onItemSelected(AdapterView<?> p1, View p2, int p3, long p4) {

                String s = (String) p1.getItemAtPosition(p3);
                selectedType = p3;
            }
        });
        name = editTextName.getText();
        url = editTextUrl.getText();
    }

    public void addEdit(View view) throws IOException {
        Boolean success = false;
        if (editAction.equals("modify")) {
            if (name.toString().length() == 0 || url.toString().length() == 0) {
                Toast.makeText(PlaylistEditActivity.this, getString(R.string.pleasefillallfields),
                        Toast.LENGTH_SHORT).show();
            } else {
                success = true;
                if (enable) {
                    playlistService.setActivePlaylistById(editNum, name.toString(), url.toString(),
                            selectedType);
                } else {
                    // check if playlist already exist in selected playlist
                    if (playlistService.indexNameForActivePlaylist(name.toString()) == -1)
                        playlistService.addToActivePlaylist(name.toString(), url.toString(),
                                selectedType, "", "");
                    else
                        Toast.makeText(PlaylistEditActivity.this, getString(R.string.playlistexist),
                                Toast.LENGTH_SHORT).show();
                }
            }
        }
        if (editAction.equals("add")) {
            if (name.toString().length() == 0 || url.toString().length() == 0) {
                Toast.makeText(PlaylistEditActivity.this,
                        getString(R.string.pleasefillallfields), Toast.LENGTH_SHORT).show();
            } else {
                success = true;
                // check if playlist already exist in selected playlist
                if (playlistService.indexNameForActivePlaylist(name.toString()) == -1)
                    playlistService.addToActivePlaylist(name.toString(), url.toString(),
                            selectedType, "", "");
                else
                    Toast.makeText(PlaylistEditActivity.this,
                            getString(R.string.playlistexist), Toast.LENGTH_SHORT).show();
            }
        }
        try {
            if (success)
                playlistService.saveData(PlaylistEditActivity.this);
        } catch (IOException ignored) {
        }
        if (success) {
            playlistService.saveData(PlaylistEditActivity.this);
            super.onBackPressed();
        }
    }

    public void deletePlaylist(View view) throws IOException {
        if (enable) {
            playlistService.deleteActivePlaylistById(editNum);
            playlistService.saveData(PlaylistEditActivity.this);
        }
        super.onBackPressed();
    }

}
