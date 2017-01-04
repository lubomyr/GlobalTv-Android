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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import atua.anddev.globaltv.runnables.SaveGuideRunnable;

import static atua.anddev.globaltv.service.GuideService.guideProvList;

public class MainActivity extends Activity implements GlobalServices {
    private int selectedProvider;
    private int selectedGuideProv = 2;
    private ArrayAdapter provAdapter;
    private String lang;
    private Configuration conf;
    private Boolean needUpdate;
    private String myPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Global.myPath = this.getApplicationContext().getFilesDir().toString();
        //myPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/" +
        // context.getPackageName()+"/files";
        myPath = Global.myPath;


        if (lang == null)
            lang = Locale.getDefault().getISO3Language();

        conf = getResources().getConfiguration();

        if (guideProvList.size() == 0)
            guideService.setupGuideProvList();

		if (!Global.guideLoaded) {
        Thread checkGuideUpdateThread = new Thread(checkGuideForUpdate);
        checkGuideUpdateThread.start();
		}

        if (playlistService.sizeOfOfferedPlaylist() == 0) {
            playlistService.setupProvider("default", MainActivity.this);
            if (checkFile("userdata.xml"))
                playlistService.setupProvider("user", MainActivity.this);
        }
        if (playlistService.sizeOfActivePlaylist() == 0) {
            Toast.makeText(MainActivity.this, getString(R.string.plstmanwarn), Toast.LENGTH_SHORT).show();
            addPlaylistDialog();
            needUpdate = true;
        }
        setupProviderView();
        showLocals();
    }

    @Override
    protected void onResume() {
        super.onResume();

        provAdapter.notifyDataSetChanged();
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

    Runnable checkGuideForUpdate = new Runnable() {

        @Override
        public void run() {
            if (guideService.checkForUpdate(MainActivity.this, selectedGuideProv))
                updateGuide();

            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(MainActivity.this, "guide loaded", Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

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

    private void applyLocals() {
        Button updateButton = (Button) findViewById(R.id.mainButton2);
        updateButton.setText(getString(R.string.updatePlaylistButton));
        Button openButton = (Button) findViewById(R.id.mainButton1);
        openButton.setText(getString(R.string.openPlaylistButton));
        Button globalSearchButton = (Button) findViewById(R.id.mainButton3);
        globalSearchButton.setText(getString(R.string.search));
        Button playlistManagerButton = (Button) findViewById(R.id.mainButton4);
        playlistManagerButton.setText(getString(R.string.playlistsManagerButton));
        Button globalFavoriteButton = (Button) findViewById(R.id.mainButton5);
        globalFavoriteButton.setText(getString(R.string.favorites));
        Button updateAllButton = (Button) findViewById(R.id.mainButton6);
        updateAllButton.setText(getString(R.string.updateOutdatedPlaylists));
        TextView playlistView = (TextView) findViewById(R.id.mainTextView2);
        playlistView.setText(getString(R.string.playlist));
        TextView autoupdateView = (TextView) findViewById(R.id.mainTextView3);
        autoupdateView.setText(getString(R.string.autoUpdate));
        TextView every12hView = (TextView) findViewById(R.id.mainTextView4);
        every12hView.setText(getString(R.string.every12h));
    }

    private void showLocals() {
        ArrayList<String> localsList = new ArrayList<String>();
        localsList.add("English");
        localsList.add("Українська");
        localsList.add("Русский");
        Spinner spinnerView = (Spinner) findViewById(R.id.mainSpinner2);
        SpinnerAdapter adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_expandable_list_item_1, localsList);
        spinnerView.setAdapter(adapter);

        if (lang.equals("eng"))
            spinnerView.setSelection(0);
        if (lang.equals("ukr"))
            spinnerView.setSelection(1);
        if (lang.equals("rus"))
            spinnerView.setSelection(2);

        spinnerView.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> p1, View p2, int p3, long p4) {

                String s = (String) p1.getItemAtPosition(p3);
                switch (s) {
                    case "English":
                        lang = "eng";
                        conf.locale = new Locale("en");
                        break;
                    case "Українська":
                        lang = "ukr";
                        conf.locale = new Locale("uk");
                        break;
                    case "Русский":
                        lang = "rus";
                        conf.locale = new Locale("ru");
                        break;
                }
                new Resources(getAssets(), getResources().getDisplayMetrics(), conf);
                Global.origNames = getResources().getStringArray(R.array.categories_list_orig);
                Global.translatedNames = getResources().getStringArray(R.array.categories_list_translated);
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

    private boolean checkPlaylistFile(int num) {
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
                    tmpText = getString(R.string.updated) + " " +
                            new Date(updateDate).toLocaleString();
                    break;
                case 2:
                    tmpText = getString(R.string.updated) + " 1 " +
                            getString(R.string.dayago);
                    break;
                case 3:
                case 4:
                case 5:
                    tmpText = getString(R.string.updated) + " " +
                            (daysPassed - 1) + " " + getString(R.string.daysago);
                    break;
                default:
                    tmpText = getString(R.string.updated) + " " +
                            (daysPassed - 1) + " " + getString(R.string.fivedaysago);
                    break;

            }

            textView.setText(tmpText);
            InputStream myfile = new FileInputStream(myPath + "/" + fname);
        } catch (FileNotFoundException e) {
            textView.setText(getString(R.string.playlistnotexist));
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

    public void catlistActivity() {
        Intent intent = new Intent(this, CatlistActivity.class);
        intent.putExtra("provider", selectedProvider);
        startActivity(intent);
    }

    public void channellistActivity() {
        String selectedCategory = getString(R.string.all);
        Intent intent = new Intent(this, ChannellistActivity.class);
        intent.putExtra("category", selectedCategory);
        intent.putExtra("provider", selectedProvider);
        startActivity(intent);
    }

    public void openPlaylist(View view) {
        if (playlistService.sizeOfActivePlaylist() == 0) {
            Toast.makeText(MainActivity.this, getString(R.string.no_selected_playlist),
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
            if (channelService.getCategoriesList().size() > 0)
                catlistActivity();
            else
                channellistActivity();
        }
    }

    public void downloadButton(View view) {
        if (playlistService.sizeOfActivePlaylist() == 0) {
            Toast.makeText(MainActivity.this, getString(R.string.no_selected_playlist),
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
            output = startStr + Global.torrentKey + endStr;
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
                .setTitle(getString(R.string.request))
                .setMessage(getString(R.string.pleaseentertext))
                .setView(input)
                .setPositiveButton(getString(R.string.search),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Editable value = input.getText();
                                String searchString = value.toString();
                                globalSearchActivity(searchString);
                            }
                        }).setNegativeButton(getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Do nothing.
                    }
                }).show();
    }

    public void playlistManager(View view) {
        playlistManagerActivity();
    }

    public void globalSearchActivity(String searchString) {
        Intent intent = new Intent(this, GlobalSearchActivity.class);
        intent.putExtra("search", searchString);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(getString(R.string.request));
        builder.setMessage(getString(R.string.provider_list_is_empty));
        builder.setPositiveButton(getString(R.string.playlist_add_all), new DialogInterface.OnClickListener() {
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
        builder.setNegativeButton(getString(R.string.playlistsManagerButton),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface p1, int p2) {
                        playlistManagerActivity();
                    }
                });
        AlertDialog alert = builder.create();
        alert.setOwnerActivity(MainActivity.this);
        alert.show();
    }

    private void updateGuide() {
        SaveGuideRunnable saveGuideRunnable = new SaveGuideRunnable(this, selectedGuideProv);
        Thread thread = new Thread(saveGuideRunnable);
        thread.start();
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
                            Toast.makeText(MainActivity.this, getString(R.string.playlistupdated,
                                    playlistService.getActivePlaylistById(num).getName()), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } catch (Exception e) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MainActivity.this, getString(R.string.updatefailed,
                                playlistService.getActivePlaylistById(num).getName()), Toast.LENGTH_SHORT).show();
                    }
                });
                Log.i("GlobalTV", "Error: " + e.toString());
            }
        }
    }
}
