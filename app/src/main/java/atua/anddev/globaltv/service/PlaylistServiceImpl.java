package atua.anddev.globaltv.service;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import atua.anddev.globaltv.Global;
import atua.anddev.globaltv.GlobalServices;
import atua.anddev.globaltv.entity.Channel;
import atua.anddev.globaltv.entity.Playlist;

public class PlaylistServiceImpl implements PlaylistService, GlobalServices {
    /*Comparator for sorting the list by Playlist date*/
    private static Comparator<Playlist> PlstDateComparator = new Comparator<Playlist>() {

        public int compare(Playlist s1, Playlist s2) {
            String PlaylistDate1 = s1.getUpdate().toUpperCase();
            String PlaylistDate2 = s2.getUpdate().toUpperCase();

            //ascending order
            //return PlaylistDate1.compareTo(PlaylistDate2);

            //descending order
            return PlaylistDate2.compareTo(PlaylistDate1);
        }
    };

    @Override
    public List<Playlist> getSortedByDatePlaylists() {
        List<Playlist> sortedList = new ArrayList<>();
        sortedList.addAll(activePlaylist);
        Collections.sort(sortedList, PlstDateComparator);
        return sortedList;
    }

    @Override
    public void addNewActivePlaylist(Playlist plst) {
        String name = plst.getName();
        File file = new File(Global.myPath + "/" + plst.getFile());
        long fileDate = file.lastModified();
        plst.setUpdate(String.valueOf(fileDate));
        activePlaylist.add(plst);
        activePlaylistName.add(name);
    }

    @Override
    public void setMd5(int id, String md5) {
        Playlist plst = getActivePlaylistById(id);
        activePlaylist.set(id, plst).setMd5(md5);
    }

    @Override
    public void setUpdateDate(int id, Long update) {
        Playlist plst = getActivePlaylistById(id);
        String updateStr = update.toString();
        activePlaylist.set(id, plst).setUpdate(updateStr);
    }

    @Override
    public void deleteActivePlaylistById(int id) {
        activePlaylist.remove(id);
        activePlaylistName.remove(id);
    }

    @Override
    public Playlist getActivePlaylistById(int id) {
        return activePlaylist.get(id);
    }

    @Override
    public Playlist getOfferedPlaylistById(int id) {
        return offeredPlaylist.get(id);
    }

    @Override
    public void setActivePlaylistById(int id, String name, String url, int type) {
        String file = getFileName(name);
        activePlaylist.set(id, new Playlist(name, url, file, type));
        activePlaylistName.set(id, name);
    }

    @Override
    public void clearActivePlaylist() {
        activePlaylist.clear();
        activePlaylistName.clear();
    }

    @Override
    public int sizeOfActivePlaylist() {
        return activePlaylist.size();
    }

    @Override
    public int sizeOfOfferedPlaylist() {
        return offeredPlaylist.size();
    }

    @Override
    public List<String> getAllNamesOfOfferedPlaylist() {
        List<String> arr = new ArrayList<String>();
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
        Playlist plst = new Playlist(name, url, file, type, md5, update);
        activePlaylist.add(plst);
        activePlaylistName.add(name);
    }

    @Override
    public void addToOfferedPlaylist(String name, String url, int type) {
        String file = getFileName(name);
        Playlist plst = new Playlist(name, url, file, type);
        offeredPlaylist.add(plst);
    }

    public void addAllOfferedPlaylist() {
        for (Playlist plst : offeredPlaylist) {
            if (!activePlaylistName.contains(plst.getName())) {
                activePlaylist.add(plst);
                activePlaylistName.add(plst.getName());
            }
        }
    }

    @Override
    public void saveData(Context context) throws FileNotFoundException, IOException {
        FileOutputStream fos;
        fos = context.getApplicationContext().openFileOutput("userdata.xml", Context.MODE_PRIVATE);
        XmlSerializer serializer = Xml.newSerializer();
        serializer.setOutput(fos, "UTF-8");
        serializer.startDocument(null, true);
        serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
        serializer.startTag(null, "data");

//        serializer.startTag(null, "torrentkey");
//        serializer.text(Global.torrentKey);
//        serializer.endTag(null, "torrentkey");

        for (Playlist plst : activePlaylist) {
            serializer.startTag(null, "provider");

            serializer.startTag(null, "name");
            serializer.text(plst.getName());
            serializer.endTag(null, "name");

            serializer.startTag(null, "url");
            serializer.text(plst.getUrl());
            serializer.endTag(null, "url");

            serializer.startTag(null, "type");
            serializer.text(plst.getTypeString());
            serializer.endTag(null, "type");

            serializer.startTag(null, "md5");
            serializer.text(plst.getMd5());
            serializer.endTag(null, "md5");

            serializer.startTag(null, "update");
            serializer.text(plst.getUpdate());
            serializer.endTag(null, "update");

            serializer.endTag(null, "provider");
        }

        serializer.endDocument();
        serializer.flush();
        fos.close();
    }

    @Override
    public void setupProvider(String opt, Context context) {
        String name = null, url = null, type = null, md5 = "", update = String.valueOf(new Date().getTime());
        String endTag, text = null;
        if (opt.equals("default")) {
            new ParseTask().execute();
        } else {
            try {
                XmlPullParser xpp;
                //if (opt.equals("user")) {
                XmlPullParserFactory xppf = XmlPullParserFactory.newInstance();
                xppf.setNamespaceAware(true);
                xpp = xppf.newPullParser();
                FileInputStream fis = context.getApplicationContext().openFileInput("userdata.xml");
                xpp.setInput(fis, null);
                /*} else {
                 xpp = context.getResources().getXml(R.xml.data);
				 }*/
                while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                    switch (xpp.getEventType()) {
                        case XmlPullParser.START_DOCUMENT:
                            break;

                        case XmlPullParser.START_TAG:
                            break;

                        case XmlPullParser.END_TAG:
                            endTag = xpp.getName();
                            if (endTag.equals("name"))
                                name = text;
                            if (endTag.equals("url"))
                                url = text;
                            if (endTag.equals("type"))
                                type = text;
                            if (endTag.equals("md5"))
                                md5 = text;
                            if (endTag.equals("update"))
                                update = text;
                            if (endTag.equals("torrentkey"))
                                Global.torrentKey = text;
                            if (endTag.equals("provider")) {
                                if (opt.equals("user")) {
                                    addToActivePlaylist(name, url, Integer.parseInt(type), md5, update);
                                }
                            }
                            break;

                        case XmlPullParser.TEXT:
                            text = xpp.getText();
                            break;

                        default:
                            break;
                    }
                    xpp.next();
                }
            } catch (XmlPullParserException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void readPlaylist(int num) {
        String fname = getActivePlaylistById(num).getFile();
        String provName = getActivePlaylistById(num).getName();
        int type = getActivePlaylistById(num).getType();
        if (type == 2) {
            parseW3u(num);
            return;
        }
        String lineStr, chName = "", chCategory = "", chLink = "", chIcon = "";
        String groupName = "", groupName2 = "";
        channelService.clearAllChannel();
        try {
            InputStream myfile = new FileInputStream(Global.myPath + "/" + fname);
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
                    if (chName.startsWith("ALLFON.ORG")) {
                        chName = chName.substring(11, chName.length());
                    }
                    if (chName.startsWith(" ")) {
                        chName = chName.substring(1, chName.length());
                    }
                    if ((chName.length() > 0) && (chName.charAt(chName.length() - 1) == '\15')) {
                        chName = chName.substring(0, chName.length() - 1);
                    }
                    chCategory = translate(chCategory);
                    Channel channel = new Channel(chName, chLink, chCategory, chIcon, provName);
                    channelService.addToChannelList(channel);
                    chName = "";
                    chCategory = "";
                    chLink = "";
                    chIcon = "";
                    groupName = "";
                    groupName2 = "";
                }
                if ((type == 1) && lineStr.startsWith("#EXTINF:-1,") && (lineStr.indexOf("(") == lineStr.lastIndexOf("("))) {
                    chName = lineStr.substring(11, lineStr.indexOf("(") - 1);
                    chCategory = lineStr.substring(lineStr.lastIndexOf("(") + 1, lineStr.lastIndexOf(")"));
                }
                if ((type == 1) && lineStr.startsWith("#EXTINF:-1,") && (lineStr.indexOf("(") != lineStr.lastIndexOf("("))) {
                    chName = lineStr.substring(11, lineStr.lastIndexOf("(") - 1);
                    chCategory = lineStr.substring(lineStr.lastIndexOf("(") + 1, lineStr.lastIndexOf(")"));
                }
                if (lineStr.startsWith("#EXTINF:") && (type != 1)) {
                    chName = lineStr.substring(lineStr.lastIndexOf(",") + 1, lineStr.length());
                }
                if (lineStr.contains("tvg-logo=") && lineStr.contains(",")) {
                    chIcon = lineStr.substring(lineStr.indexOf("tvg-logo=") + 10, lineStr.indexOf('"', lineStr.indexOf("tvg-logo=") + 10));
                }
                if (lineStr.contains("group-title=") && lineStr.contains(",") && (lineStr.substring(lineStr.indexOf("group-title="), lineStr.indexOf("group-title=") + 12).equals("group-title="))) {
                    groupName = lineStr.substring(lineStr.indexOf("group-title=") + 13, lineStr.indexOf('"', lineStr.indexOf("group-title=") + 13));
                }
                if (lineStr.contains("#EXTGRP:") && (lineStr.substring(lineStr.indexOf("#EXTGRP:"), lineStr.indexOf("#EXTGRP:") + 8).equals("#EXTGRP:"))) {
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
        } catch (Exception e) {
            Log.i("GlobalTV", "Error: " + e.toString());
        }
    }

    private void parseW3u(int num) {
        String fname = getActivePlaylistById(num).getFile();
        String provName = getActivePlaylistById(num).getName();
        String resultJson = "";
        channelService.clearAllChannel();
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
                channelService.addToChannelList(channel);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String translate(String input) {
        String output;
        output = input;

        if (Global.origNames.length > 0) {
            for (int i = 0; i < Global.origNames.length; i++) {
                if (Global.origNames[i].equalsIgnoreCase(input)) {
                    output = Global.translatedNames[i];
                    break;
                }
            }
        }
        return output;
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
