package atua.anddev.globaltv.service;

import atua.anddev.globaltv.entity.Search;

public class SearchServiceImpl implements SearchService {

    @Override
    public void addToSearchList(String name, String url, String prov) {
        searchList.add(new Search(name, url, prov));
    }

    @Override
    public Search getSearchListById(int id) {
        return searchList.get(id);
    }

    @Override
    public void clearSearchList() {
        searchList.clear();
    }

    @Override
    public int sizeOfSearchList() {
        return searchList.size();
    }

    @Override
    public int indexNameForSearchList(String name) {
        int result = -1;
        for (int i = 0; i < searchList.size(); i++) {
            if (name.equals(searchList.get(i).getName()))
                result = i;
        }
        return result;
    }

    @Override
    public boolean containsNameForSearch(String name) {
        boolean result = false;
        for (Search search : searchList) {
            if (name.equals(search.getName()))
                result = true;
        }
        return result;
    }

}
