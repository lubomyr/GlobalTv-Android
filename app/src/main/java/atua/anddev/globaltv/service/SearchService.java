package atua.anddev.globaltv.service;

import java.util.List;

import atua.anddev.globaltv.entity.Channel;

public interface SearchService {

    List<Channel> searchChannelsByName(String search);
}
