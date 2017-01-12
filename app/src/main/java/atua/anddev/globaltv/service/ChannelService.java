package atua.anddev.globaltv.service;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import atua.anddev.globaltv.entity.Channel;

public interface ChannelService {
    List<Channel> channels = new ArrayList<>();

    void addToChannelList(Channel channel);

    List<Channel> getAllChannels();

    void clearAllChannel();

    List<String> getCategoriesList();

    String getUpdatedUrl(Channel channel);

    void openChannel(Context context, Channel channel);
}
