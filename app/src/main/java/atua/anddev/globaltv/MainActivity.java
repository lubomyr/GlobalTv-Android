package atua.anddev.globaltv;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity implements GlobalServices {
    public static String torrentKey;
    public static String origNames[];
    public static String translatedNames[];
    public static ArrayAdapter provAdapter;
    public static Boolean playlistWithGroup;
    public static String myPath;
    public static int selectedProvider;
    static String selectedCategory;
    static String lang;
    static String searchString;
    Configuration conf;
    private Boolean needUpdate;

    public static void saveUrl(final String filename, final String urlString)
            throws MalformedURLException, IOException {
        BufferedInputStream in = null;
        FileOutputStream fout = null;
        try {
            in = new BufferedInputStream(new URL(urlString).openStream());
            fout = new FileOutputStream(filename);

            final byte data[] = new byte[1024];
            int count;
            while ((count = in.read(data, 0, 1024)) != -1) {
                fout.write(data, 0, count);
            }
        } finally {
            if (in != null) {
                in.close();
            }
            if (fout != null) {
                fout.close();
            }
        }
    }

    public static String getMd5OfFile(String filePath) {
        String returnVal = "";
        try {
            InputStream input = new FileInputStream(filePath);
            byte[] buffer = new byte[1024];
            MessageDigest md5Hash = MessageDigest.getInstance("MD5");
            int numRead = 0;
            while (numRead != -1) {
                numRead = input.read(buffer);
                if (numRead > 0) {
                    md5Hash.update(buffer, 0, numRead);
                }
            }
            input.close();

            byte[] md5Bytes = md5Hash.digest();
            for (byte md5Byte : md5Bytes) {
                returnVal += Integer.toString((md5Byte & 0xff) + 0x100, 16).substring(1);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return returnVal.toUpperCase();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        myPath = this.getApplicationContext().getFilesDir().toString();
        //myPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/"+context.getPackageName()+"/files";
        if (lang == null)
            lang = Locale.getDefault().getISO3Language();

        conf = getResources().getConfiguration();

        if (playlistService.sizeOfOfferedPlaylist() == 0) {
            playlistService.setupProvider("default", MainActivity.this);
            if (checkFile("userdata.xml"))
                playlistService.setupProvider("user", MainActivity.this);
        }
        if (playlistService.sizeOfActivePlaylist() == 0) {
            Toast.makeText(MainActivity.this, getResources().getString(R.string.plstmanwarn), Toast.LENGTH_SHORT).show();
            addPlaylistDialog();
            needUpdate = true;
        }
        setupProviderView();
        showLocals();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.playlist_update_info) {
            updateInfoListActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    private void applyLocals() {
        Resources res = getResources();
        Button updateButton = (Button) findViewById(R.id.mainButton2);
        updateButton.setText(res.getString(R.string.updatePlaylistButton));
        Button openButton = (Button) findViewById(R.id.mainButton1);
        openButton.setText(res.getString(R.string.openPlaylistButton));
        Button globalSearchButton = (Button) findViewById(R.id.mainButton3);
        globalSearchButton.setText(res.getString(R.string.search));
        Button playlistManagerButton = (Button) findViewById(R.id.mainButton4);
        playlistManagerButton.setText(res.getString(R.string.playlistsManagerButton));
        Button globalFavoriteButton = (Button) findViewById(R.id.mainButton5);
        globalFavoriteButton.setText(res.getString(R.string.favorites));
        Button updateAllButton = (Button) findViewById(R.id.mainButton6);
        updateAllButton.setText(res.getString(R.string.updateOutdatedPlaylists));
        TextView playlistView = (TextView) findViewById(R.id.mainTextView2);
        playlistView.setText(res.getString(R.string.playlist));
        TextView autoupdateView = (TextView) findViewById(R.id.mainTextView3);
        autoupdateView.setText(res.getString(R.string.autoUpdate));
        TextView every12hView = (TextView) findViewById(R.id.mainTextView4);
        every12hView.setText(res.getString(R.string.every12h));
    }

    private void showLocals() {
        List<String> localsList = Arrays.asList("English", "Українська", "Русский");
        Spinner spinnerView = (Spinner) findViewById(R.id.mainSpinner2);
        SpinnerAdapter adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_expandable_list_item_1, localsList);
        spinnerView.setAdapter(adapter);
        int eng = 0;
        int ukr = 1;
        int rus = 2;

        if (lang.equals("eng"))
            spinnerView.setSelection(eng);
        if (lang.equals("ukr"))
            spinnerView.setSelection(ukr);
        if (lang.equals("rus"))
            spinnerView.setSelection(rus);

        spinnerView.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> p1, View p2, int p3, long p4) {

                String s = (String) p1.getItemAtPosition(p3);
                switch (s) {
                    case "English":
                        MainActivity.lang = "eng";
                        conf.locale = new Locale("en");
                        break;
                    case "Українська":
                        MainActivity.lang = "ukr";
                        conf.locale = new Locale("uk");
                        break;
                    case "Русский":
                        MainActivity.lang = "rus";
                        conf.locale = new Locale("ru");
                        break;
                }
                new Resources(getAssets(), getResources().getDisplayMetrics(), conf);
                origNames = getResources().getStringArray(R.array.categories_list_orig);
                translatedNames = getResources().getStringArray(R.array.categories_list_translated);
                applyLocals();
                try {
                    checkPlaylistFile(selectedProvider);
                } catch (Exception ignored) {
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> p1) {
                // nothing to do
            }
        });
    }

    private void setupProviderView() {
        Spinner spinnerView = (Spinner) findViewById(R.id.mainSpinner1);
        provAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1,
                playlistService.activePlaylistName);

        spinnerView.setAdapter(provAdapter);
        spinnerView.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> p1, View p2, int p3, long p4) {

                String s = (String) p1.getItemAtPosition(p3);
                selectedProvider = p3;
                checkPlaylistFile(p3);
            }

            @Override
            public void onNothingSelected(AdapterView<?> p1) {
                // nothing to do
            }
        });
    }

    public void catlistActivity() {
        Intent intent = new Intent(this, CatlistActivity.class);
        startActivity(intent);
    }

    private boolean checkPlaylistFile(int num) {
        Resources res = getResources();
        TextView textView = (TextView) findViewById(R.id.mainTextView1);
        String fname = playlistService.getActivePlaylistById(num).getFile();
        try {
            String updateDateStr = playlistService.getActivePlaylistById(num).getUpdate();
            File file = new File(myPath + "/" + fname);
            long fileDate = file.lastModified();
            long currDate = new Date().getTime();
            long diffDate, updateDate = 0;
            try {
                updateDate = Long.parseLong(updateDateStr);
                diffDate = currDate - updateDate;
            } catch (Exception e) {
                diffDate = currDate - fileDate;
                updateDate = fileDate;
                Log.i("GlobalTV", "Error: " + e.toString());
            }
            String tmpText;
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(diffDate);
            int daysPassed = cal.get(Calendar.DAY_OF_YEAR);
            int hoursPassed = (int) TimeUnit.MILLISECONDS.toHours(diffDate);
            if (hoursPassed > 12)
                needUpdate = true;
            else
                needUpdate = false;

            switch (daysPassed) {
                case 1:
                    tmpText = res.getString(R.string.updated) + " " + new Date(updateDate).toLocaleString();
                    break;
                case 2:
                    tmpText = res.getString(R.string.updated) + " 1 " + res.getString(R.string.dayago);
                    break;
                case 3:
                case 4:
                case 5:
                    tmpText = res.getString(R.string.updated) + " " + (daysPassed - 1) + " " + res.getString(R.string.daysago);
                    break;
                default:
                    tmpText = res.getString(R.string.updated) + " " + (daysPassed - 1) + " " + res.getString(R.string.fivedaysago);
                    break;

            }

            textView.setText(tmpText);
            InputStream myfile = new FileInputStream(myPath + "/" + fname);
        } catch (FileNotFoundException e) {
            textView.setText(res.getString(R.string.playlistnotexist));
            needUpdate = true;
            return false;
        }
        return true;
    }

    private boolean checkFile(String fname) {
        try {
            InputStream myfile = new FileInputStream(myPath + "/" + fname);
        } catch (FileNotFoundException e) {
            return false;
        }
        return true;
    }

    public void playlistActivity() {
        selectedCategory = getResources().getString(R.string.all);
        Intent intent = new Intent(this, PlaylistActivity.class);
        startActivity(intent);
    }

    public void openPlaylist(View view) {
        if (playlistService.sizeOfActivePlaylist() == 0) {
            Toast.makeText(MainActivity.this, getResources().getString(R.string.no_selected_playlist),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            if (needUpdate) {
                downloadPlaylist(selectedProvider, true);
            }
        } finally {
            playlistService.readPlaylist(selectedProvider);
            try {
                if (favoriteService.sizeOfFavoriteList() == 0)
                    favoriteService.loadFavorites(MainActivity.this);
            } catch (IOException e) {
                Log.i("GlobalTV", "Error: " + e);
            }
            if (playlistWithGroup)
                catlistActivity();
            else
                playlistActivity();
        }
    }

    public void downloadButton(View view) {
        if (playlistService.sizeOfActivePlaylist() == 0) {
            Toast.makeText(MainActivity.this, getResources().getString(R.string.no_selected_playlist),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        downloadPlaylist(selectedProvider, false);
    }

    public void updateAll(View view) {
        downloadAllPlaylist();
    }

    private void downloadPlaylist(final int num, boolean waitforfinish) {
        DownloadPlaylist dplst = new DownloadPlaylist(num);
        Thread threadDp = new Thread(dplst);
        threadDp.start();
        if (waitforfinish) {
            try {
                threadDp.join();
            } catch (InterruptedException ignored) {
            }
        }
    }

    private String fixKey(String str) {
        String output;
        output = str;
        String startStr = "http://content.torrent-tv.ru/";
        String endStr;
        if (str.startsWith(startStr) && str.contains("/cdn/")) {
            endStr = str.substring(str.indexOf("/cdn/"), str.length());
            output = startStr + torrentKey + endStr;
        }
        return output;
    }

    private void downloadAllPlaylist() {
        for (int i = 0; i < playlistService.sizeOfActivePlaylist(); i++) {
            if (!checkPlaylistFile(i) || needUpdate) {
                downloadPlaylist(i, false);
            }
        }
    }

    public void globalSearch(View view) {
        searchService.clearSearchList();
        final EditText input = new EditText(this);
        input.setSingleLine();
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(getResources().getString(R.string.request))
                .setMessage(getResources().getString(R.string.pleaseentertext))
                .setView(input)
                .setPositiveButton(getResources().getString(R.string.search), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Editable value = input.getText();
                        searchString = value.toString();
                        globalSearchActivity();
                    }
                }).setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Do nothing.
            }
        }).show();
    }

    public void playlistManager(View view) {
        playlistManagerActivity();
    }

    public void globalSearchActivity() {
        Intent intent = new Intent(this, GlobalSearchActivity.class);
        startActivity(intent);
    }

    public void playlistManagerActivity() {
        Intent intent = new Intent(this, PlaylistManagerActivity.class);
        startActivity(intent);
    }

    public void globalFavoriteActivity() {
        Intent intent = new Intent(this, GlobalFavoriteActivity.class);
        startActivity(intent);
    }

    public void playerActivity() {
        Intent intent = new Intent(this, PlayerActivity.class);
        startActivity(intent);
    }

    public void globalFavorite(View view) {
        try {
            if (favoriteService.sizeOfFavoriteList() == 0)
                favoriteService.loadFavorites(MainActivity.this);
        } catch (IOException e) {
            Log.i("GlobalTV", "Error: " + e.toString());
        }
        globalFavoriteActivity();
    }

    public void updateInfoListActivity() {
        Intent intent = new Intent(this, UpdateInfoListActivity.class);
        startActivity(intent);
    }

    public void addPlaylistDialog() {
        Resources res = getResources();
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(res.getString(R.string.request));
        builder.setMessage(res.getString(R.string.provider_list_is_empty));
        builder.setPositiveButton(res.getString(R.string.playlist_add_all), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface p1, int p2) {
                playlistService.addAllOfferedPlaylist();
                provAdapter.notifyDataSetChanged();
                try {
                    playlistService.saveData(MainActivity.this);
                } catch (IOException ignored) {
                }
            }
        });
        builder.setNegativeButton(res.getString(R.string.playlistsManagerButton), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface p1, int p2) {
                playlistManagerActivity();
            }
        });
        AlertDialog alert = builder.create();
        alert.setOwnerActivity(MainActivity.this);
        alert.show();
    }

    private class DownloadPlaylist implements Runnable {
        private int num;

        DownloadPlaylist(int num) {
            this.num = num;
        }

        public void run() {
            try {
                String path = myPath + "/" + playlistService.getActivePlaylistById(num).getFile();
                saveUrl(path, playlistService.getActivePlaylistById(num).getUrl());

                runOnUiThread(new Runnable() {
                    public void run() {
                        needUpdate = false;
                        String oldMd5 = playlistService.getActivePlaylistById(num).getMd5();
                        String path = myPath + "/" + playlistService.getActivePlaylistById(num).getFile();
                        String newMd5 = getMd5OfFile(path);
                        if (!newMd5.equals(oldMd5)) {
                            playlistService.setMd5(num, newMd5);
                            playlistService.setUpdateDate(num, new Date().getTime());
                            try {
                                playlistService.saveData(MainActivity.this);
                            } catch (IOException e) {
                                Log.i("GlobalTV", "Error: " + e.toString());
                            }
                            checkPlaylistFile(selectedProvider);
                            Toast.makeText(MainActivity.this,
                                    getResources().getString(R.string.playlistupdated,
                                    playlistService.getActivePlaylistById(num).getName()), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } catch (Exception e) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MainActivity.this,
                                getResources().getString(R.string.updatefailed,
                                playlistService.getActivePlaylistById(num).getName()), Toast.LENGTH_SHORT).show();
                    }
                });
                Log.i("GlobalTV", "Error: " + e.toString());
            }
        }
    }
}
