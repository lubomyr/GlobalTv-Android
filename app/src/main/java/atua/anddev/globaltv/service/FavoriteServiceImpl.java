package atua.anddev.globaltv.service;


import java.util.List;

import atua.anddev.globaltv.GlobalServices;
import atua.anddev.globaltv.MainActivity;
import atua.anddev.globaltv.entity.Channel;
import atua.anddev.globaltv.repository.FavoriteDb;

public class FavoriteServiceImpl implements FavoriteService, GlobalServices {
    private FavoriteDb favoriteDb = MainActivity.favoriteDb;

    @Override
    public List<Channel> getAllFavorites() {
        return favorites;
    }

    @Override
    public void addAll() {
        favorites.addAll(favoriteDb.getAllFavorites());
    }

    @Override
    public int indexOfFavoriteByChannel(Channel channel) {
        int result = -1;
        for (int i = 0; i < sizeOfFavoriteList(); i++) {
            if (channel.getName().equals(getFavoriteById(i).getName()) &&
                    channel.getProvider().equals(getFavoriteById(i).getProvider())) {
                result = i;
            }
        }
        return result;
    }

    @Override
    public void deleteFromFavoritesById(int id) {
        List<Integer> idList = favoriteDb.getAllFavoritesID();
        int iddb = idList.get(id);
        favorites.remove(id);
        favoriteDb.deleteFromFavoritesById(iddb);
    }

    @Override
    public void deleteFromFavoritesByChannel(Channel channel) {
        List<Integer> idList = favoriteDb.getAllFavoritesID();
        for (int i = 0; i < sizeOfFavoriteList(); i++) {
            if (channel.getName().equals(getFavoriteById(i).getName()) &&
                    channel.getProvider().equals(getFavoriteById(i).getProvider())) {
                int iddb = idList.get(i);
                favorites.remove(i);
                favoriteDb.deleteFromFavoritesById(iddb);
            }
        }
    }

    @Override
    public void addToFavoriteList(Channel channel) {
        favorites.add(channel);
        favoriteDb.insertIntoFavorites(channel);
    }

    @Override
    public Channel getFavoriteById(int id) {
        List<Integer> idList = favoriteDb.getAllFavoritesID();
        int iddb = idList.get(id);
        return favoriteDb.getFavoriteById(iddb);
    }

    @Override
    public void clearAllFavorites() {
        favorites.clear();
        favoriteDb.deleteAllFavorites();
    }

    @Override
    public int sizeOfFavoriteList() {
        return favoriteDb.numberOfRows();
    }

    @Override
    public int indexNameForFavorite(String name) {
        int result = -1;
        for (int i = 0; i < favorites.size(); i++) {
            if (name.equals(favorites.get(i).getName()))
                result = i;
        }
        return result;
    }

    @Override
    public boolean isChannelFavorite(Channel item) {
        Boolean result = false;
        for (Channel fav : favorites) {
            if (item.getName().equals(fav.getName())
                    && item.getProvider().equals(fav.getProvider()))
                result = true;
        }
        return result;
    }

}
