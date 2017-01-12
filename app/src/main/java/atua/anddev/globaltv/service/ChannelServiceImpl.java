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

import static atua.anddev.globaltv.GlobalServices.playlistService;

public class ChannelServiceImpl implements ChannelService {

    @Override
    public List<Channel> getAllChannels() {
        return channels;
    }

    @Override
    public List<String> getCategoriesList() {
        List<String> arr = new ArrayList<>();
        for (int i = 0; i < channels.size() - 1; i++) {
            boolean cat_exist = false;
            for (int j = 0; j <= arr.size() - 1; j++)
                if (channels.get(i).getCategory().equalsIgnoreCase(arr.get(j)))
                    cat_exist = true;
            if (!cat_exist && !channels.get(i).getCategory().equals(""))
                arr.add(channels.get(i).getCategory());
        }
        return arr;
    }

    @Override
    public void addToChannelList(Channel channel) {
        channels.add(channel);
    }

    @Override
    public void clearAllChannel() {
        channels.clear();
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
        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(chURL));
            browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (chURL.startsWith("http")) {
                String url = chURL;
                if (chURL.endsWith("\r"))
                    url = chURL.substring(0, chURL.length() - 1);
                browserIntent.setDataAndType(Uri.parse(url), "video/*");
            }
            context.startActivity(browserIntent);
        } catch (Exception e) {
            Log.i("GlobalTv", "Error: " + e.toString());
        }

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
        return getChannelsUrl(channel);
    }

    private String getChannelsUrl(Channel channel) {
        for (Channel chn : channels) {
            if (channel.getName().equals(chn.getName()))
                return chn.getUrl();
        }
        return null;
    }
}
