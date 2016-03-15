package atua.anddev.globaltv.service;

import java.util.ArrayList;
import java.util.List;

import atua.anddev.globaltv.entity.Search;

public interface SearchService {
    List<Search> searchList = new ArrayList<Search>();
    List<String> searchNameList = new ArrayList<String>();
    List<String> searchProvList = new ArrayList<String>();

    void addToSearchList(String name, String url, String prov);

    Search getSearchListById(int id);

    void clearSearchList();

    int sizeOfSearchList();

    int indexNameForSearchList(String name);

    boolean containsNameForSearch(String name);
}
