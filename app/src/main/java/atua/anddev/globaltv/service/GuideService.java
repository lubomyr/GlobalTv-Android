package atua.anddev.globaltv.service;

import android.app.Activity;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import atua.anddev.globaltv.entity.ChannelGuide;
import atua.anddev.globaltv.entity.GuideProv;
import atua.anddev.globaltv.entity.Programme;

public interface GuideService {
    List<GuideProv> guideProvList = new ArrayList<>();
    List<ChannelGuide> channelGuideList = new ArrayList<>();
    //List<Programme> programmeList = new ArrayList<Programme>();

    boolean checkForUpdate(Context context, int selectedGuideProv);

    void parseGuide(Activity context, int selectedGuideProv);

    String getProgramTitle(String chName);

    String getProgramDesc(String chName);

    List<Programme> getChannelGuide(String chName);

    void setupGuideProvList();

    String getTotalTimePeriod();

    int getProgramPos(String chName);

    int channelGuideListSize();

    void addAllChannelGuideList();

    String getNameOfChannelById(String id);

    List<Programme> getProgramsByStringForFullPeriod(String search);

    List<Programme> getProgramsByStringAfterMoment(String search);

    List<Programme> getProgramsByStringForCurrentMoment(String str);

    List<Programme> getProgramsByStringForToday(String str);
}
