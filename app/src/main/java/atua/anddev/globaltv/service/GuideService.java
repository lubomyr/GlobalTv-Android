package atua.anddev.globaltv.service;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import atua.anddev.globaltv.entity.ChannelGuide;
import atua.anddev.globaltv.entity.GuideProv;
import atua.anddev.globaltv.entity.Programme;

public interface GuideService {
    List<GuideProv> guideProvList = new ArrayList<>();
    List<ChannelGuide> channelGuideList = new ArrayList<>();
    List<Programme> programmeList = new ArrayList<>();

    boolean checkForUpdate(Context context, int selectedGuideProv);

    void parseGuide(Context context, int selectedGuideProv);

    String getProgramTitle(String chName);

    String getProgramDesc(String chName);

    List<Programme> getChannelGuide(String chName);

    void setupGuideProvList();

    String getTotalTimePeriod();

    int getProgramPos(String chName);

    List<Programme> searchAllPeriod(String str);

    List<Programme> searchAfterMoment(String str);

    List<Programme> searchCurrentMoment(String str);

    List<Programme> searchToday(String str);

    String getChannelNameById(String id);
}
