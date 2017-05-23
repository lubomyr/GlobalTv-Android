package atua.anddev.globaltv.service;

import java.util.ArrayList;
import java.util.List;

import atua.anddev.globaltv.entity.Channel;

public interface FavoriteService {
    List<Channel> favorites = new ArrayList<>();

    List<Channel> getAllFavorites();

    void addAll();

    void addToFavoriteList(Channel channel);

    Channel getFavoriteById(int id);

    void clearAllFavorites();

    int sizeOfFavoriteList();

    int indexNameForFavorite(String name);

    void deleteFromFavoritesById(int id);

    void deleteFromFavoritesByChannel(Channel channel);

    int indexOfFavoriteByChannel(Channel channel);

    boolean isChannelFavorite(Channel item);
}
