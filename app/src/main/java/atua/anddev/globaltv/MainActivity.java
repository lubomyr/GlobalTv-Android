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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import atua.anddev.globaltv.repository.ChannelDb;
import atua.anddev.globaltv.repository.ChannelGuideDb;
import atua.anddev.globaltv.repository.FavoriteDb;
import atua.anddev.globaltv.repository.PlaylistDb;
import atua.anddev.globaltv.repository.ProgrammeDb;
import atua.anddev.globaltv.runnables.SaveGuideRunnable;

import static atua.anddev.globaltv.service.GuideService.guideProvList;
import static atua.anddev.globaltv.service.LogoService.logoList;
import static java.util.Arrays.asList;

public class MainActivity extends Activity implements GlobalServices {
    public static PlaylistDb playlistDb;
    public static ChannelDb channelDb;
    public static FavoriteDb favoriteDb;
    public static ChannelGuideDb channelGuideDb;
    public static ProgrammeDb programmeDb;
    private ArrayAdapter provAdapter;
    private int selectedProvider;
    private int selectedGuideProv = 2;
    private String lang;
    private Configuration conf;
    private Boolean needUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Global.myPath = this.getApplicationContext().getFilesDir().toString();
        //myPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/"+context.getPackageName()+"/files";
        if (lang == null)
            lang = Locale.getDefault().getISO3Language();

        conf = getResources().getConfiguration();

        playlistDb = new PlaylistDb(this);
        channelDb = new ChannelDb(this);
        favoriteDb = new FavoriteDb(this);
        channelGuideDb = new ChannelGuideDb(this);
        programmeDb = new ProgrammeDb(this);

        if (guideProvList.size() == 0)
            guideService.setupGuideProvList();

        if (logoList.size() == 0)
            logoService.setupLogos();

        checkForUpdateGuide();

        if (playlistService.sizeOfOfferedPlaylist() == 0) {
            playlistService.setupProvider();
            favoriteService.addAll();
        }
        if (playlistService.sizeOfActivePlaylist() == 0) {
            addPlaylistDialog();
        }
        setupPlayer();
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

    private void checkForUpdateGuide() {
        SaveGuideRunnable saveGuideRunnable = new SaveGuideRunnable(this, selectedGuideProv);
        Thread thread = new Thread(saveGuideRunnable);
        thread.start();
    }

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
        globalSearchButton.setText(getString(R.string.search_channel));
        Button playlistManagerButton = (Button) findViewById(R.id.mainButton4);
        playlistManagerButton.setText(getString(R.string.playlistsManager));
        Button globalFavoriteButton = (Button) findViewById(R.id.mainButton5);
        globalFavoriteButton.setText(getString(R.string.favorites));
        Button updateAllButton = (Button) findViewById(R.id.mainButton6);
        updateAllButton.setText(getString(R.string.updateOutdatedPlaylists));
        TextView playlistView = (TextView) findViewById(R.id.mainTextView2);
        playlistView.setText(getString(R.string.playlist));
        TextView autoupdateView = (TextView) findViewById(R.id.mainTextView3);
        autoupdateView.setText(getString(R.string.use_external_player));
        Button searchProgramButton = (Button) findViewById(R.id.mainSearchProgram);
        searchProgramButton.setText(getString(R.string.search_program));
    }

    private void showLocals() {
        List<String> localsList = asList("English", "Українська", "Русский");
        Spinner spinnerView = (Spinner) findViewById(R.id.mainSpinner2);
        SpinnerAdapter adapter = new ArrayAdapter<String>(this, R.layout.spinner_row, localsList);
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
                } catch (Exception e) {
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
        provAdapter = new ArrayAdapter<>(this, R.layout.spinner_row,
                playlistService.getAllNamesOfActivePlaylist());

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

    private void setupPlayer() {
        CheckBox useInternalPlayerCb = (CheckBox) findViewById(R.id.mainCheckBox1);
        useInternalPlayerCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Global.useInternalPlayer = !b;
            }
        });
    }

    private boolean checkPlaylistFile(int num) {
        TextView textView = (TextView) findViewById(R.id.mainTextView1);
        String fname = playlistService.getActivePlaylistById(num).getFile();
        try {
            String updateDateStr = playlistService.getActivePlaylistById(num).getUpdate();
            long currDate = (new Date()).getTime();
            long diffDate, updateDate = 0;
            String tmpText;
            try {
                updateDate = Long.parseLong(updateDateStr);
                diffDate = currDate - updateDate;
            } catch (Exception e) {
                textView.setText(getString(R.string.playlistnotexist));
                Log.i("GlobalTV", "Error: " + e.toString());
                return false;
            }
            int hoursPassed = (int) TimeUnit.MILLISECONDS.toHours(diffDate);
            needUpdate = hoursPassed > 12;
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(diffDate);
            int daysPassed = cal.get(Calendar.DATE);
            switch (daysPassed) {
                case 1:
                    DateFormat format = DateFormat.getDateTimeInstance();
                    tmpText = getString(R.string.updated) + " " + format.format(new Date(updateDate));
                    break;
                case 2:
                    tmpText = getString(R.string.updated) + " 1 " + getString(R.string.dayago);
                    break;
                case 3:
                case 4:
                case 5:
                    tmpText = getString(R.string.updated) + " " + (daysPassed - 1) + " " + getString(R.string.daysago);
                    break;
                default:
                    tmpText = getString(R.string.updated) + " " + (daysPassed - 1) + " " + getString(R.string.fivedaysago);
                    break;

            }

            textView.setText(tmpText);
            InputStream myfile = new FileInputStream(Global.myPath + "/" + fname);
        } catch (FileNotFoundException e) {
            textView.setText(getResources().getString(R.string.playlistnotexist));
            needUpdate = true;
            return false;
        }
        return true;
    }

    public void openPlaylist(View view) {
        if (playlistService.sizeOfActivePlaylist() == 0) {
            Toast.makeText(MainActivity.this, getString(R.string.no_selected_playlist),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (!checkPlaylistFile(selectedProvider) || needUpdate) {
            downloadPlaylist(selectedProvider, true);
        }
        int grpcnt = channelService.getCategoriesNumber(playlistService.getActivePlaylistById(selectedProvider).getName());

        if (grpcnt > 0)
            catlistActivity();
        else
            channellistActivity();
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

    void downloadPlaylist(final int num, boolean waitforfinish) {
        DownloadPlaylist dplst = new DownloadPlaylist(num);
        Thread threadDp = new Thread(dplst);
        threadDp.start();
        if (waitforfinish) {
            try {
                threadDp.join();
            } catch (InterruptedException e) {
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
        final EditText input = new EditText(this);
        input.setSingleLine();
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(getString(R.string.request))
                .setMessage(getString(R.string.pleaseentertext))
                .setView(input)
                .setPositiveButton(getString(R.string.search), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Editable value = input.getText();
                        String searchString = value.toString();
                        globalSearchActivity(searchString);
                    }
                }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Do nothing.
            }
        }).show();
    }

    public void playlistManager(View view) {
        playlistManagerActivity();
    }

    public void catlistActivity() {
        Intent intent = new Intent(this, CatlistActivity.class);
        intent.putExtra("provider", selectedProvider);
        startActivity(intent);
    }

    public void channellistActivity() {
        String selectedCategory = getResources().getString(R.string.all);
        Intent intent = new Intent(this, ChannellistActivity.class);
        intent.putExtra("category", selectedCategory);
        intent.putExtra("provider", selectedProvider);
        startActivity(intent);
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

    public void updateInfoListActivity() {
        Intent intent = new Intent(this, UpdateInfoListActivity.class);
        startActivity(intent);
    }

    public void globalFavorite(View view) {
        globalFavoriteActivity();
    }

    public void searchProgram(View view) {
        Intent intent = new Intent(this, SearchProgramActivity.class);
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
            }
        });
        builder.setNegativeButton(getString(R.string.playlistsManager), new DialogInterface.OnClickListener() {

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
                String path = Global.myPath + "/" + playlistService.getActivePlaylistById(num).getFile();
                saveUrl(path, playlistService.getActivePlaylistById(num).getUrl());

                runOnUiThread(new Runnable() {
                    public void run() {
                        needUpdate = false;
                        String oldMd5 = playlistService.getActivePlaylistById(num).getMd5();
                        String path = Global.myPath + "/" + playlistService.getActivePlaylistById(num).getFile();
                        String newMd5 = getMd5OfFile(path);
                        if (!newMd5.equals(oldMd5)) {
                            playlistService.setMd5(num, newMd5);
                            playlistService.setUpdateDate(num, new Date().getTime());
                            Toast.makeText(MainActivity.this, getString(R.string.playlistupdated,
                                    playlistService.getActivePlaylistById(num).getName()), Toast.LENGTH_SHORT).show();
                            playlistService.readPlaylist(num);
                            checkPlaylistFile(num);
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
