package atua.anddev.globaltv.service;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import atua.anddev.globaltv.Global;
import atua.anddev.globaltv.MainActivity;
import atua.anddev.globaltv.entity.Channel;
import atua.anddev.globaltv.entity.Playlist;
import atua.anddev.globaltv.repository.ChannelRepository;

import static atua.anddev.globaltv.GlobalServices.playlistService;

public class ChannelServiceImpl implements ChannelService {

    @Override
    public String getCategoryById(String name, int id) {
        return getCategoriesList(name).get(id);
    }

    @Override
    public String getUrlByChannel(Channel channel) {
        return ChannelRepository.getUrlByPlistAndName(channel.getProvider(), channel.getName());
    }

    @Override
    public List<Channel> getChannelsByCategory(String plname, String catname) {
        return ChannelRepository.getChannelsByCategory(plname, catname);
    }

    @Override
    public void insertAllChannels(List<Channel> channels) {
        ChannelRepository.saveAll(channels);
    }

    @Override
    public void deleteChannelbyPlist(String plist) {
        ChannelRepository.deleteChannelsByPlaylist(plist);
    }

    @Override
    public List<Channel> getChannelsByPlist(String name) {
        return ChannelRepository.getChannelsByPlaylist(name);
    }

    @Override
    public int getCategoriesNumber(String name) {
        return getCategoriesList(name).size();
    }

    @Override
    public List<String> getCategoriesList(String name) {
        return ChannelRepository.getCategoriesList(name);
    }

    @Override
    public List<String> getTranslatedCategoriesList(String name) {
        List<String> res = new ArrayList<>();
        List<String> catList = ChannelRepository.getCategoriesList(name);
        for (String str : catList) {
            res.add(translate(str));
        }
        return res;
    }

    @Override
    public void clearAllChannel() {
        ChannelRepository.deleteAll();
    }

    @Override
    public int sizeOfChannelList() {
        return ChannelRepository.getAll().size();
    }

    @Override
    public void openChannel(final Context context, final Channel channel) {
        new Thread(new Runnable() {
            public void run() {
                String url;
                if (channel.getProvider().equals("Kineskop.tv"))
                    url = getUpdatedUrl(channel);
                else
                    url = channel.getUrl();
                openURL(context, url);
            }
        }).start();
    }

    private void openURL(final Context context, final String chURL) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(chURL));
                    browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    if (chURL.startsWith("http"))
                        browserIntent.setDataAndType(Uri.parse(chURL.trim()), "video/*");
                    context.getApplicationContext().startActivity(browserIntent);
                } catch (Exception e) {
                    Log.i("SDL", "Error: " + e.toString());
                }
            }
        }).start();
    }

    public String getUpdatedUrl(Channel channel) {
        int provId = playlistService.indexNameForActivePlaylist(channel.getProvider());
        Playlist plst = playlistService.getActivePlaylistById(provId);
        String path = Global.myPath + "/" + plst.getFile();
        try {
            MainActivity.saveUrl(path, plst.getUrl());
        } catch (IOException e) {
            e.printStackTrace();
        }
        playlistService.readPlaylist(provId);
        return getUrlByChannel(channel);
    }

    public String translate(String input) {
        String output;
        output = input;

        if (Global.origNames.length > 0) {
            for (int i = 0; i < Global.origNames.length; i++) {
                if (Global.origNames[i].equalsIgnoreCase(input)) {
                    output = Global.translatedNames[i];
                    break;
                }
            }
        }
        return output;
    }
}
