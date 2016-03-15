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
import java.util.List;

import atua.anddev.globaltv.Services;
import atua.anddev.globaltv.entity.Favorites;

public class FavoriteServiceImpl implements FavoriteService, Services {

    @Override
    public int indexOfFavoriteByNameAndProv(String name, String prov) {
        int result = -1;
        for (int i = 0; i < sizeOfFavoriteList(); i++) {
            if (name.equals(favorites.get(i).getName()) && prov.equals(favorites.get(i).getProv())) {
                result = i;
            }
        }
        return result;
    }

    @Override
    public List<String> getFavoriteListForSelProv() {
        List<String> arr = new ArrayList<String>();
        for (int i = 0; i < sizeOfFavoriteList(); i++) {
            for (int j = 0; j < channelService.sizeOfChannelList(); j++) {
                if (getFavoriteById(i).getName().equals(channelService.getChannelById(j).getName()) && !arr.contains(getFavoriteById(i).getName())) {
                    arr.add(getFavoriteById(i).getName());
                }
            }
        }
        return arr;
    }

    @Override
    public boolean containsNameForFavorite(String name) {
        return favoriteList.contains(name);
    }

    @Override
    public void deleteFromFavoritesById(int id) {
        favorites.remove(id);
        favoriteList.remove(id);
        favoriteProvList.remove(id);
    }

    @Override
    public void addToFavoriteList(String name, String prov) {
        favorites.add(new Favorites(name, prov));
        favoriteList.add(name);
        favoriteProvList.add(prov);
    }

    @Override
    public Favorites getFavoriteById(int id) {
        return favorites.get(id);
    }

    @Override
    public void clearAllFavorites() {
        favorites.clear();
        favoriteList.clear();
        favoriteProvList.clear();
    }

    @Override
    public int sizeOfFavoriteList() {
        return favorites.size();
    }

    @Override
    public int indexNameForFavorite(String name) {
        return favoriteList.indexOf(name);
    }

    @Override
    public void saveFavorites(Context context) throws FileNotFoundException, IOException {
        FileOutputStream fos;
        fos = context.getApplicationContext().openFileOutput("favorites.xml", Context.MODE_WORLD_WRITEABLE);
        XmlSerializer serializer = Xml.newSerializer();
        serializer.setOutput(fos, "UTF-8");
        serializer.startDocument(null, true);
        serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
        serializer.startTag(null, "root");

        for (int j = 0; j < sizeOfFavoriteList(); j++) {
            serializer.startTag(null, "favorites");

            serializer.startTag(null, "channel");
            serializer.text(getFavoriteById(j).getName());
            serializer.endTag(null, "channel");

            serializer.startTag(null, "playlist");
            serializer.text(getFavoriteById(j).getProv());
            serializer.endTag(null, "playlist");

            serializer.endTag(null, "favorites");
        }
        serializer.endDocument();
        serializer.flush();
        fos.close();
    }

    @Override
    public void loadFavorites(Context context) throws IOException {
        String text = null, name = null, prov = null, endTag;
        try {
            XmlPullParserFactory xppf = XmlPullParserFactory.newInstance();
            xppf.setNamespaceAware(true);
            XmlPullParser xpp = xppf.newPullParser();
            FileInputStream fis = context.getApplicationContext().openFileInput("favorites.xml");
            xpp.setInput(fis, null);
            int type = xpp.getEventType();
            while (type != XmlPullParser.END_DOCUMENT) {
                if (type == XmlPullParser.START_DOCUMENT) {
                    // nothing to do
                } else if (type == XmlPullParser.START_TAG) {
                    // nothing to do
                } else if (type == XmlPullParser.END_TAG) {
                    endTag = xpp.getName();
                    if (endTag.equals("channel"))
                        name = text;
                    if (endTag.equals("playlist"))
                        prov = text;
                    if (endTag.equals("favorites")) {
                        addToFavoriteList(name, prov);
                    }
                } else if (type == XmlPullParser.TEXT) {
                    text = xpp.getText();
                }
                type = xpp.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
