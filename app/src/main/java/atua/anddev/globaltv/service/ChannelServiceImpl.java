package atua.anddev.globaltv.service;

import android.content.*;
import android.net.*;
import android.util.*;

import atua.anddev.globaltv.entity.*;

import java.util.*;

public class ChannelServiceImpl implements ChannelService {
    @Override
    public List<String> getCategoriesList() {
        List<String> arr = new ArrayList<String>();
        boolean cat_exist = false;
        for (int i = 0; i < channel.size() - 1; i++) {
            cat_exist = false;
            for (int j = 0; j <= arr.size() - 1; j++)
                if (channel.get(i).getCategory().equalsIgnoreCase(arr.get(j)))
                    cat_exist = true;
            if (cat_exist == false && !channel.get(i).getCategory().equals(""))
                arr.add(channel.get(i).getCategory());
        }
        return arr;
    }


    @Override
    public int indexNameForChannel(String name) {
        return channelName.indexOf(name);
    }


    @Override
    public Channel getChannelById(int id) {
        return channel.get(id);
    }


    @Override
    public void addToChannelList(String name, String url, String category) {
        channel.add(new Channel(name, url, category));
        channelName.add(name);
    }

    @Override
    public void clearAllChannel() {
        channel.clear();
        channelName.clear();
    }

    @Override
    public int sizeOfChannelList() {
        return channel.size();
    }

    public void openChannel(String chName, Context context) {
        for (int i = 0; i < sizeOfChannelList(); i++) {
            if (chName.equals(getChannelById(i).getName())) {
                openURL(getChannelById(i).getUrl(), context);
                return;
            }
        }
    }

    public void openURL(final String chURL, final Context context) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(chURL));
                    browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    if (chURL.startsWith("http")) {
                        browserIntent.setDataAndType(Uri.parse(chURL), "video/*");
                    }
                    context.getApplicationContext().startActivity(browserIntent);
                } catch (Exception e) {
                    Log.i("SDL", "Error: " + e.toString());
                }
            }
        }).start();
    }
}
