package atua.anddev.globaltv.service;

import java.util.List;

import atua.anddev.globaltv.entity.Channel;

public class SearchServiceImpl implements SearchService {

    @Override
    public List<Channel> getSearchList() {
        return searchList;
    }

    @Override
    public void addToSearchList(Channel ch) {
        searchList.add(ch);
    }

    @Override
    public Channel getSearchListById(int id) {
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
        for (Channel search : searchList) {
            if (name.equals(search.getName()))
                result = true;
        }
        return result;
    }

}
