package atua.anddev.globaltv.service;

import java.util.List;

import atua.anddev.globaltv.entity.Channel;
import atua.anddev.globaltv.repository.ChannelRepository;

public class SearchServiceImpl implements SearchService {

    @Override
    public List<Channel> searchChannelsByName(String search) {
        return ChannelRepository.searchChannelsByName(search);
    }

}
