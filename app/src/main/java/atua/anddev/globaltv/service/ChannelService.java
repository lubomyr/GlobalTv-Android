package atua.anddev.globaltv.service;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import atua.anddev.globaltv.entity.Channel;

public interface ChannelService {
    List<Channel> channel = new ArrayList<Channel>();

    void addToChannelList(String name, String url, String category);

    Channel getChannelById(int id);

    List<Channel> getAllChannels();

    void clearAllChannel();

    int sizeOfChannelList();

    int indexNameForChannel(String name);

    List<String> getCategoriesList();

    void openChannel(String chName, Context context);

    void openURL(final String chURL, final Context context);
}
