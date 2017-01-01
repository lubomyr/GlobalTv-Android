package atua.anddev.globaltv.service;

import android.content.Context;

import atua.anddev.globaltv.entity.ChannelGuide;
import atua.anddev.globaltv.entity.GuideProv;
import atua.anddev.globaltv.entity.Programme;

import java.util.ArrayList;
import java.util.List;

public interface GuideService {
    List<GuideProv> guideProvList = new ArrayList<GuideProv>();
    List<ChannelGuide> channelGuideList = new ArrayList<ChannelGuide>();
    List<Programme> programmeList = new ArrayList<Programme>();

    boolean checkForUpdate(Context context);

    void parseGuide(Context context);

    String getProgramTitle(String chName);

    String getProgramDesc(String chName);

    List<Programme> getChannelGuide(String chName);

    void setupGuideProvList();

    String getTotalTimePeriod();

    public int getProgramPos(String chName);
}
