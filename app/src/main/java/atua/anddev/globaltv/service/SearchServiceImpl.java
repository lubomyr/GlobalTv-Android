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
    public void clearSearchList() {
        searchList.clear();
    }

    @Override
    public int sizeOfSearchList() {
        return searchList.size();
    }
}
