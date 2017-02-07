package atua.anddev.globaltv.service;

import android.content.Context;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import atua.anddev.globaltv.entity.Channel;

public interface FavoriteService {
    List<Channel> favorites = new ArrayList<>();

    void addToFavoriteList(Channel channel);

    Channel getFavoriteById(int id);

    int sizeOfFavoriteList();

    int indexNameForFavorite(String name);

    void deleteFromFavoritesById(int id);

    void deleteFromFavoritesByChannel(Channel channel);

    List<Channel> getFavoriteList();

    int indexOfFavoriteByChannel(Channel channel);

    boolean isChannelFavorite(Channel item);

    void saveFavorites(Context context) throws FileNotFoundException, IOException;

    void loadFavorites(Context context) throws IOException;
}
