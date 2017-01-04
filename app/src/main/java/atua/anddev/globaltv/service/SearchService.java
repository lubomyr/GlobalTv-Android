package atua.anddev.globaltv.service;

import java.util.ArrayList;
import java.util.List;

import atua.anddev.globaltv.entity.Channel;

public interface SearchService {
    List<Channel> searchList = new ArrayList<Channel>();

    List<Channel> getSearchList();

    void addToSearchList(Channel ch);

    Channel getSearchListById(int id);

    void clearSearchList();

    int sizeOfSearchList();

    int indexNameForSearchList(String name);

    boolean containsNameForSearch(String name);
}
