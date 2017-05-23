package atua.anddev.globaltv.service;


import java.util.List;

import atua.anddev.globaltv.GlobalServices;
import atua.anddev.globaltv.entity.Channel;
import atua.anddev.globaltv.repository.FavoriteRepository;

public class FavoriteServiceImpl implements FavoriteService, GlobalServices {

    @Override
    public List<Channel> getAllFavorites() {
        return favorites;
    }

    @Override
    public void addAll() {
        favorites.addAll(FavoriteRepository.getAll());
    }

    @Override
    public int indexOfFavoriteByChannel(Channel channel) {
        int result = -1;
        result = favorites.indexOf(channel);
        return result;
    }

    @Override
    public void deleteFromFavorites(Channel channel) {
        favorites.remove(channel);
        FavoriteRepository.delete(channel);
    }

    @Override
    public void addToFavoriteList(Channel channel) {
        favorites.add(channel);
        FavoriteRepository.insert(channel);
    }

    @Override
    public void clearAllFavorites() {
        favorites.clear();
        FavoriteRepository.deleteAll();
    }

    @Override
    public int sizeOfFavoriteList() {
        return favorites.size();
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
