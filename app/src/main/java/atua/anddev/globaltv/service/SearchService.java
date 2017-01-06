package atua.anddev.globaltv.service;

import java.util.ArrayList;
import java.util.List;

import atua.anddev.globaltv.entity.Channel;

public interface SearchService {
    List<Channel> searchList = new ArrayList<>();

    List<Channel> getSearchList();

    void addToSearchList(Channel ch);

    void clearSearchList();

    int sizeOfSearchList();

}
