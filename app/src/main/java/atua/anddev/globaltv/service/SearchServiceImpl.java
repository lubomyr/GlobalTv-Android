package atua.anddev.globaltv.service;

import atua.anddev.globaltv.entity.*;

public class SearchServiceImpl implements SearchService {

    @Override
    public void addToSearchList(String name, String url, String prov) {
        searchList.add(new Search(name, url, prov));
        searchNameList.add(name);
        searchProvList.add(prov);
    }

    @Override
    public Search getSearchListById(int id) {
        return searchList.get(id);
    }

    @Override
    public void clearSearchList() {
        searchList.clear();
        searchNameList.clear();
        searchProvList.clear();
    }

    @Override
    public int sizeOfSearchList() {
        return searchList.size();
    }

    @Override
    public int indexNameForSearchList(String name) {
        return searchNameList.indexOf(name);
    }

    @Override
    public boolean containsNameForSearch(String name) {
        return searchNameList.contains(name);
    }

}
