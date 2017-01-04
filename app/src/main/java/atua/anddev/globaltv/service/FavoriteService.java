package atua.anddev.globaltv.service;

import android.content.Context;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import atua.anddev.globaltv.entity.Favorites;

public interface FavoriteService {
    List<Favorites> favorites = new ArrayList<Favorites>();

    void addToFavoriteList(String name, String prov);

    Favorites getFavoriteById(int id);

    void clearAllFavorites();

    int sizeOfFavoriteList();

    int indexNameForFavorite(String name);

    boolean containsNameForFavorite(String name);

    void deleteFromFavoritesById(int id);

    void deleteFromFavoritesByNameAndProv(String name, String prov);

    List<String> getFavoriteListForProv(int provider);

    List<Favorites> getFavoriteList();

    int indexOfFavoriteByNameAndProv(String name, String prov);

    void saveFavorites(Context context) throws FileNotFoundException, IOException;

    void loadFavorites(Context context) throws IOException;
}
