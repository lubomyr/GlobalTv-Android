package atua.anddev.globaltv.service;

import java.util.*;

import atua.anddev.globaltv.entity.*;

public interface SearchService {
    List<Search> searchList = new ArrayList<Search>();
    ArrayList<String> searchNameList = new ArrayList<String>();
    ArrayList<String> searchProvList = new ArrayList<String>();

    public void addToSearchList(String name, String url, String prov);

    public Search getSearchListById(int id);

    public void clearSearchList();

    public int sizeOfSearchList();

    public int indexNameForSearchList(String name);

    public boolean containsNameForSearch(String name);
}
