package atua.anddev.globaltv.service;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import atua.anddev.globaltv.entity.Channel;

public class ChannelServiceImpl implements ChannelService {

    @Override
    public List<Channel> getAllChannels() {
        return channel;
    }

    @Override
    public List<String> getCategoriesList() {
        List<String> arr = new ArrayList<String>();
        boolean cat_exist = false;
        for (int i = 0; i < channel.size() - 1; i++) {
            cat_exist = false;
            for (int j = 0; j <= arr.size() - 1; j++)
                if (channel.get(i).getCategory().equalsIgnoreCase(arr.get(j)))
                    cat_exist = true;
            if (!cat_exist && !channel.get(i).getCategory().equals(""))
                arr.add(channel.get(i).getCategory());
        }
        return arr;
    }

    @Override
    public int indexNameForChannel(String name) {
        int result = -1;
        for (int i = 0; i < channel.size(); i++) {
            if (name.equals(channel.get(i).getName()))
                result = i;
        }
        return result;
    }

    @Override
    public Channel getChannelById(int id) {
        return channel.get(id);
    }

    @Override
    public void addToChannelList(String name, String url, String category) {
        channel.add(new Channel(name, url, category));
    }

    @Override
    public void clearAllChannel() {
        channel.clear();
    }

    @Override
    public int sizeOfChannelList() {
        return channel.size();
    }

    public void openChannel(String chName, Context context) {
        for (Channel chn : channel) {
            if (chName.equals(chn.getName())) {
                openURL(chn.getUrl(), context);
                return;
            }
        }
    }

    @Override
    public void openURL(final String chURL, final Context context) {
        new Thread(new Runnable() {
            public void run() {
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
        }).start();
    }
}
