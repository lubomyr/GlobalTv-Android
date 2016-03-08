package atua.anddev.globaltv.service;

import android.content.*;

import atua.anddev.globaltv.entity.*;

import java.util.*;
import java.io.*;

public interface FavoriteService {
    List<Favorites> favorites = new ArrayList<Favorites>();
    ArrayList<String> favoriteList = new ArrayList<String>();
    ArrayList<String> favoriteProvList = new ArrayList<String>();

    public void addToFavoriteList(String name, String prov);

    public Favorites getFavoriteById(int id);

    public void clearAllFavorites();

    public int sizeOfFavoriteList();

    public int indexNameForFavorite(String name);

    public boolean containsNameForFavorite(String name);

    public void deleteFromFavoritesById(int id);

    public List<String> getFavoriteListForSelProv();

    public int indexOfFavoriteByNameAndProv(String name, String prov);

    public void saveFavorites(Context context) throws FileNotFoundException, IOException;

    public void loadFavorites(Context context) throws IOException;
}
