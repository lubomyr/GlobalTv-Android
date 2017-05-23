package atua.anddev.globaltv.service;

import java.util.List;

import atua.anddev.globaltv.MainActivity;
import atua.anddev.globaltv.entity.Channel;
import atua.anddev.globaltv.repository.ChannelDb;

public class SearchServiceImpl implements SearchService {
    private ChannelDb channelDb = MainActivity.channelDb;

    @Override
    public List<Channel> searchChannelsByName(String search) {
        return channelDb.searchChannelsByName(search);
    }

}
