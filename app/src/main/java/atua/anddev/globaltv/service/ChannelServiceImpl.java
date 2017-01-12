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
