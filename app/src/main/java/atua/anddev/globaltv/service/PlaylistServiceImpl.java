package atua.anddev.globaltv.service;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import atua.anddev.globaltv.Global;
import atua.anddev.globaltv.GlobalServices;
import atua.anddev.globaltv.entity.Channel;
import atua.anddev.globaltv.entity.Playlist;
import atua.anddev.globaltv.repository.PlaylistRepository;

public class PlaylistServiceImpl implements PlaylistService, GlobalServices {

    @Override
    public List<Playlist> getSortedByDatePlaylists() {
        return PlaylistRepository.getSortedByDate();
    }

    @Override
    public void addNewActivePlaylist(Playlist playlist) {
        activePlaylistName.add(playlist.getName());
        PlaylistRepository.insert(playlist);
    }

    @Override
    public void setMd5(int id, String md5) {
        List<Playlist> list = PlaylistRepository.getAll();
        Playlist playlist = list.get(id);
        PlaylistRepository.updateMd5(playlist, md5);
    }

    @Override
    public void setUpdateDate(int id, Long update) {
        List<Playlist> list = PlaylistRepository.getAll();
        Playlist playlist = list.get(id);
        PlaylistRepository.updateDate(playlist, update.toString());
    }

    @Override
    public void deleteActivePlaylistById(int id) {
        List<Playlist> list = PlaylistRepository.getAll();
        Playlist playlist = list.get(id);
        activePlaylistName.remove(playlist.getName());
        PlaylistRepository.delete(playlist);
    }

    @Override
    public Playlist getActivePlaylistById(int id) {
        List<Playlist> list = PlaylistRepository.getAll();
        return list.get(id);
    }

    @Override
    public Playlist getOfferedPlaylistById(int id) {
        return offeredPlaylist.get(id);
    }

    @Override
    public void setActivePlaylistById(int id, String name, String url, int type) {
        String file = getFileName(name);
        List<Playlist> list = PlaylistRepository.getAll();
        Playlist playlist = list.get(id);
        PlaylistRepository.update(playlist, name, url, type);
        activePlaylistName.set(id, name);
    }

    @Override
    public void clearActivePlaylist() {
        activePlaylistName.clear();
        PlaylistRepository.deleteAll();
    }

    @Override
    public int sizeOfActivePlaylist() {
        return PlaylistRepository.getAll().size();
    }

    @Override
    public int sizeOfOfferedPlaylist() {
        return offeredPlaylist.size();
    }

    @Override
    public List<String> getAllNamesOfActivePlaylist() {
        activePlaylistName.clear();
        activePlaylistName.addAll(PlaylistRepository.getNames());
        return activePlaylistName;
    }

    @Override
    public List<String> getAllNamesOfOfferedPlaylist() {
        List<String> arr = new ArrayList<>();
        for (Playlist plst : offeredPlaylist) {
            arr.add(plst.getName());
        }
        return arr;
    }

    @Override
    public int indexNameForActivePlaylist(String name) {
        return activePlaylistName.indexOf(name);
    }


    private String getFileName(String input) {
        String output = "playlist_" + input + ".m3u";
        output = output.replace(" ", "_");
        output = output.replace("(", "_");
        output = output.replace(")", "_");
        output = output.replace("@", "_");
        return output;
    }

    @Override
    public void addToActivePlaylist(String name, String url, int type, String md5, String update) {
        String file = getFileName(name);
        activePlaylistName.add(name);
        Playlist playlist = new Playlist(name, url, file, type);
        PlaylistRepository.insert(playlist);
    }

    @Override
    public void addToOfferedPlaylist(String name, String url, int type) {
        String file = getFileName(name);
        Playlist plst = new Playlist(name, url, file, type);
        offeredPlaylist.add(plst);
    }

    @Override
    public void setupProvider() {
        new ParseTask().execute();
    }

    public void addAllOfferedPlaylist() {
        for (Playlist plst : offeredPlaylist) {
            if (activePlaylistName.indexOf(plst.getName()) == -1) {
                addNewActivePlaylist(plst);
            }
        }
    }

    @Override
    public void readPlaylist(int num) {
        List<Channel> channels = new ArrayList<>();
        Playlist plst = getActivePlaylistById(num);
        int type = plst.getType();
        if (type == 2) {
            parseW3u(num);
            return;
        }
        String provName = plst.getName();
        String lineStr, chName = "", chCategory = "", chLink = "", chIcon = "";
        String groupName = "", groupName2 = "";

        try {
            InputStream myfile = new FileInputStream(Global.myPath + "/" + plst.getFile());
            Scanner myInputFile = new Scanner(myfile, "UTF8").useDelimiter("[\n]");
            while (myInputFile.hasNext()) {
                lineStr = myInputFile.next();
                if (lineStr.startsWith("acestream:") || lineStr.startsWith("http:") || lineStr.startsWith("https:")
                        || lineStr.startsWith("rtmp:") || lineStr.startsWith("rtsp:") || lineStr.startsWith("mmsh:")
                        || lineStr.startsWith("mms:") || lineStr.startsWith("rtmpt:")) {
                    chLink = lineStr;
                    if (chName.startsWith("ALLFON.TV")) {
                        chName = chName.substring(10, chName.length());
                    }
                    if (chName.startsWith(" ")) {
                        chName = chName.substring(1, chName.length());
                    }
                    if ((chName.length() > 0) && (chName.charAt(chName.length() - 1) == '\15')) {
                        chName = chName.substring(0, chName.length() - 1);
                    }
                    //chCategory = translate(chCategory);
                    Channel channel = new Channel(chName, chLink, chCategory, chIcon, provName);
                    channels.add(channel);

                    chName = "";
                    chCategory = "";
                    chLink = "";
                    chIcon = "";
                    groupName = "";
                    groupName2 = "";
                }
                if ((type == 1) && lineStr.contains(",") && (lineStr.indexOf("(") == lineStr.lastIndexOf("("))) {
                    chName = lineStr.substring(lineStr.indexOf(",") + 1, lineStr.indexOf("(") - 1);
                    chCategory = lineStr.substring(lineStr.lastIndexOf("(") + 1, lineStr.lastIndexOf(")"));
                }
                if ((type == 1) && lineStr.contains(",") && (lineStr.indexOf("(") != lineStr.lastIndexOf("("))) {
                    chName = lineStr.substring(lineStr.indexOf(",") + 1, lineStr.lastIndexOf("(") - 1);
                    chCategory = lineStr.substring(lineStr.lastIndexOf("(") + 1, lineStr.lastIndexOf(")"));
                }
                if (lineStr.startsWith("#EXTINF:") && (type != 1)) {
                    chName = lineStr.substring(lineStr.lastIndexOf(",") + 1, lineStr.length());
                }
                if (lineStr.contains("tvg-logo=") && lineStr.contains(",")) {
                    chIcon = lineStr.substring(lineStr.indexOf("tvg-logo=") + 10, lineStr.indexOf('"',
                            lineStr.indexOf("tvg-logo=") + 10));
                }
                if (lineStr.contains("group-title=") && lineStr.contains(",") &&
                        (lineStr.substring(lineStr.indexOf("group-title="),
                                lineStr.indexOf("group-title=") + 12).equals("group-title="))) {
                    groupName = lineStr.substring(lineStr.indexOf("group-title=") + 13,
                            lineStr.indexOf('"', lineStr.indexOf("group-title=") + 13));
                }
                if (lineStr.contains("#EXTGRP:") && (lineStr.substring(lineStr.indexOf("#EXTGRP:"),
                        lineStr.indexOf("#EXTGRP:") + 8).equals("#EXTGRP:"))) {
                    groupName2 = lineStr.substring(lineStr.indexOf("#EXTGRP:") + 8, lineStr.length());
                }
                if (!groupName.equals("")) {
                    chCategory = groupName;
                } else if (!groupName2.equals("")) {
                    groupName2 = groupName2.replace('\15', ' ');
                    groupName2 = groupName2.replace(" ", "");
                    chCategory = groupName2;
                }
            }
            myInputFile.close();
            channelService.deleteChannelbyPlist(plst.getName());
            channelService.insertAllChannels(channels);
        } catch (Exception e) {
            Log.i("GlobalTV", "Error: " + e.toString());
        }

    }

    private void parseW3u(int num) {
        List<Channel> channels = new ArrayList<>();
        Playlist plst = getActivePlaylistById(num);
        String fname = getActivePlaylistById(num).getFile();
        String provName = getActivePlaylistById(num).getName();
        String resultJson = "";
        try {
            InputStream myfile = new FileInputStream(Global.myPath + "/" + fname);
            Scanner myInputFile = new Scanner(myfile, "UTF8").useDelimiter("[\n]");
            StringBuffer buffer = new StringBuffer();
            while (myInputFile.hasNext()) {
                buffer.append(myInputFile.next());
            }
            resultJson = buffer.toString();
            myInputFile.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        JSONObject dataJsonObj = null;
        try {
            dataJsonObj = new JSONObject(resultJson);
            JSONArray stations = dataJsonObj.getJSONArray("stations");

            for (int i = 0; i < stations.length(); i++) {
                JSONObject station = stations.getJSONObject(i);
                String name = station.getString("name");
                String image = station.getString("image");
                String url = station.getString("url");
                Channel channel = new Channel(name, url, "", image, provName);
                channels.add(channel);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        channelService.deleteChannelbyPlist(plst.getName());
        channelService.insertAllChannels(channels);
    }

    private class ParseTask extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL("http://anddev.at.ua/globaltv/playlists.json");

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                resultJson = buffer.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultJson;
        }

        @Override
        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);
            JSONObject dataJsonObj = null;
            try {
                dataJsonObj = new JSONObject(strJson);
                JSONArray playlists = dataJsonObj.getJSONArray("playlist");
                for (int i = 0; i < playlists.length(); i++) {
                    JSONObject playlist = playlists.getJSONObject(i);

                    String name = playlist.getString("name");
                    String url = playlist.getString("url");
                    int type = playlist.getInt("type");
                    addToOfferedPlaylist(name, url, type);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
