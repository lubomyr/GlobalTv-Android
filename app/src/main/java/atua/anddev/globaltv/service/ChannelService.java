package atua.anddev.globaltv.service;

import android.content.Context;

import java.util.List;

import atua.anddev.globaltv.entity.Channel;

public interface ChannelService {

    Channel getChannelById(int id);

    List<Channel> getChannelsByPlist(String name);

    List<Channel> getChannelsByCategory(String plname, String catname);

    void insertAllChannels(List<Channel> channels);

    void deleteChannelbyPlist(String plist);

    void clearAllChannel();

    int sizeOfChannelList();

    List<String> getCategoriesList(String name);

    List<String> getTranslatedCategoriesList(String name);

    String getCategoryById(String name, int id);

    int getCategoriesNumber(String name);

    String getUrlByChannel(Channel channel);

    String getUpdatedUrl(Channel channel);

    void openChannel(Context context, Channel channel);

    String translate(String input);
}
