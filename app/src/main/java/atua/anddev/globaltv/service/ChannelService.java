package atua.anddev.globaltv.service;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import atua.anddev.globaltv.entity.Channel;

public interface ChannelService {
    List<Channel> channels = new ArrayList<>();

    void addToChannelList(String name, String url, String category);

    List<Channel> getAllChannels();

    void clearAllChannel();

    List<String> getCategoriesList();

    void openURL(final String chURL, final Context context);
}
