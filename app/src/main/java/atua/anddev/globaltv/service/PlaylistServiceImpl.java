package atua.anddev.globaltv.service;

import android.content.Context;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import atua.anddev.globaltv.MainActivity;
import atua.anddev.globaltv.R;
import atua.anddev.globaltv.entity.Playlist;

public class PlaylistServiceImpl implements PlaylistService {

    @Override
    public void addNewActivePlaylist(Playlist plst) {
        String name = plst.getName();
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
    public void clearOfferedPlaylist() {
        offeredPlaylist.clear();
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
    public List<Playlist> getAllActivePlaylist() {
        return activePlaylist;
    }

    @Override
    public List<Playlist> getAllOfferedPlaylist() {
        return offeredPlaylist;
    }

    @Override
    public List<String> getAllNamesOfActivePlaylist() {
        List<String> arr = new ArrayList<String>();
        for (int i = 0; i < activePlaylist.size(); i++) {
            arr.add(activePlaylist.get(i).getName());
        }
        return arr;
    }

    @Override
    public List<String> getAllNamesOfOfferedPlaylist() {
        List<String> arr = new ArrayList<String>();
        for (int i = 0; i < offeredPlaylist.size(); i++) {
            arr.add(offeredPlaylist.get(i).getName());
        }
        return arr;
    }

    @Override
    public int indexNameForActivePlaylist(String name) {
        return activePlaylistName.indexOf(name);
    }

    @Override
    public int indexNameForOfferedPlaylist(String name) {
        int result = -1;
        for (int i = 0; i < offeredPlaylist.size(); i++) {
            if (offeredPlaylist.get(i).getName().equals(name)) {
                result = i;
            }
        }
        return result;
    }


    public String getFileName(String input) {
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

    @Override
    public void saveData(Context context) throws FileNotFoundException, IOException {
        FileOutputStream fos;
        fos = context.getApplicationContext().openFileOutput("userdata.xml", Context.MODE_WORLD_WRITEABLE);
        XmlSerializer serializer = Xml.newSerializer();
        serializer.setOutput(fos, "UTF-8");
        serializer.startDocument(null, Boolean.valueOf(true));
        serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
        serializer.startTag(null, "data");

        serializer.startTag(null, "torrentkey");
        serializer.text(MainActivity.torrentKey);
        serializer.endTag(null, "torrentkey");

        for (int j = 0; j < sizeOfActivePlaylist(); j++) {
            Playlist plst = getActivePlaylistById(j);
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
        try {
            XmlPullParser xpp;
            if (opt.equals("user")) {
                XmlPullParserFactory xppf = XmlPullParserFactory.newInstance();
                xppf.setNamespaceAware(true);
                xpp = xppf.newPullParser();
                FileInputStream fis = context.getApplicationContext().openFileInput("userdata.xml");
                xpp.setInput(fis, null);
            } else {
                xpp = context.getResources().getXml(R.xml.data);
            }
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
                            MainActivity.torrentKey = text;
                        if (endTag.equals("provider")) {
                            if (opt.equals("user")) {
                                addToActivePlaylist(name, url, Integer.parseInt(type), md5, update);
                            }
                            if (opt.equals("default")) {
                                addToOfferedPlaylist(name, url, Integer.parseInt(type));
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
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
