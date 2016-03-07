package atua.anddev.globaltv.service;

import atua.anddev.globaltv.entity.*;

public class FavoriteServiceImpl implements FavoriteService {

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

}
