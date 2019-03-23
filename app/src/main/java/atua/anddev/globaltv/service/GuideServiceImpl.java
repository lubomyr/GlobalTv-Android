package atua.anddev.globaltv.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.zip.GZIPInputStream;

import atua.anddev.globaltv.Global;
import atua.anddev.globaltv.entity.ChannelGuide;
import atua.anddev.globaltv.entity.GuideProv;
import atua.anddev.globaltv.entity.Programme;

public class GuideServiceImpl implements GuideService {
    private String myPath = Global.myPath;

    public boolean checkForUpdate(Context context, int selectedGuideProv) {
        boolean result;
        if (!isGuideExist(selectedGuideProv)) {
            result = true;
        } else {
            parseGuide(context, selectedGuideProv);
            result = checkGuideDates();
        }
        return result;
    }

    private boolean isGuideExist(int selectedGuideProv) {
        boolean result;
        GuideProv guideProv = guideProvList.get(selectedGuideProv);
        File file = new File(myPath + "/" +guideProv.getFile());
        result = file.exists();
        return result;
    }

    public void parseGuide(Context context, int selectedGuideProv) {
        Log.d("globaltv", "parsing program guide...");
        String dispName = null, id = null, start = null, stop = null, channel = null;
        String endTag, text = null, title = null, desc = null, category = null;
        Global.guideLoaded = false;
        channelGuideList.clear();
        programmeList.clear();
        try {
            XmlPullParser xpp;
            XmlPullParserFactory xppf = XmlPullParserFactory.newInstance();
            xppf.setNamespaceAware(true);
            xpp = xppf.newPullParser();
            GuideProv guideProv = guideProvList.get(selectedGuideProv);
            FileInputStream fis = context.getApplicationContext().openFileInput(guideProv.getFile());
            GZIPInputStream gzipIs = new GZIPInputStream(fis);
            xpp.setInput(gzipIs, null);

            String tmp = "";
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                switch (xpp.getEventType()) {
                    case XmlPullParser.START_DOCUMENT:
                        break;

                    case XmlPullParser.START_TAG:
                        for (int i = 0; i < xpp.getAttributeCount(); i++) {
                            tmp = xpp.getAttributeName(i);
                            if (tmp.equals("id"))
                                id = xpp.getAttributeValue(i);
                            if (tmp.equals("start"))
                                start = xpp.getAttributeValue(i);
                            if (tmp.equals("stop"))
                                stop = xpp.getAttributeValue(i);
                            if (tmp.equals("channel"))
                                channel = xpp.getAttributeValue(i);
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        endTag = xpp.getName();
                        if (endTag.equals("display-name"))
                            dispName = text;
                        if (endTag.equals("title"))
                            title = text;
                        if (endTag.equals("desc"))
                            desc = text;
                        if (endTag.equals("category"))
                            category = text;

                        if (endTag.equals("channel")) {
                            ChannelGuide channelGuide = new ChannelGuide(id, null, dispName);
                            channelGuideList.add(channelGuide);
                            id = null;
                            dispName = null;
                        }
                        if (endTag.equals("programme")) {
                            Programme programme = new Programme(start, stop, channel, title, desc, category);
                            programmeList.add(programme);
                            start = null;
                            stop = null;
                            channel = null;
                            title = null;
                            desc = null;
                            category = null;
                        }
                        break;
                    case XmlPullParser.TEXT:
                        text = xpp.getText();
                        break;

                    default:
                        break;
                }
                xpp.next();
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
            Log.d("globaltv", "parsing error "+e.getMessage());
        }
        Log.d("globaltv", "parsing finished");
        Global.guideLoaded = true;
    }

    @Override
    public String getProgramTitle(String chName) {
        String id = getIdByChannelName(chName);
        return getProgramTitlebyId(id);
    }

    @Override
    public String getProgramDesc(String chName) {
        String id = getIdByChannelName(chName);
        return getProgramDescbyId(id);
    }

    @Override
    public int getProgramPos(String chName) {
        String id = getIdByChannelName(chName);
        return getProgramPositionbyId(id);
    }

    private boolean checkGuideDates() {
        boolean result = false;
        List<String> dateList = new ArrayList<>();
        if (channelGuideList.size() > 0) {
            String chId = channelGuideList.get(0).getId();
            for (Programme programme : programmeList) {
                if (programme.getChannel().equals(chId)) {
                    dateList.add(programme.getStart());
                }
            }
            Calendar startDate = decodeDateTime(dateList.get(0));
            Calendar endDate = decodeDateTime(dateList.get(dateList.size() - 1));
            Calendar currentTime = Calendar.getInstance();
            result = !(currentTime.after(startDate) && currentTime.before(endDate));
        }
        return result;
    }

    private String getIdByChannelName(String chName) {
        String result = null;
        for (ChannelGuide channelGuide : channelGuideList) {
            if (chName.equals(channelGuide.getDisplayName()))
                result = channelGuide.getId();
        }
        return result;
    }

    private String getProgramTitlebyId(String id) {
        String result = null;
        for (Programme programme : programmeList) {
            if (programme.getChannel().equals(id)) {
                Calendar startTime = decodeDateTime(programme.getStart());
                Calendar stopTime = decodeDateTime(programme.getStop());
                Calendar currentTime = Calendar.getInstance();
                if (currentTime.after(startTime) && currentTime.before(stopTime)) {
                    result = programme.getTitle();
                }
            }
        }
        return result;
    }

    private String getProgramDescbyId(String id) {
        String result = null;
        for (Programme programme : programmeList) {
            if (programme.getChannel().equals(id)) {
                Calendar startTime = decodeDateTime(programme.getStart());
                Calendar stopTime = decodeDateTime(programme.getStop());
                Calendar currentTime = Calendar.getInstance();
                if (currentTime.after(startTime) && currentTime.before(stopTime)) {
                    result = programme.getDesc();
                }
            }
        }
        return result;
    }

    private int getProgramPositionbyId(String id) {
        int result = -1;
        int n = 0;
        for (Programme programme : programmeList) {
            if (programme.getChannel().equals(id)) {
                n++;
                Calendar startTime = decodeDateTime(programme.getStart());
                Calendar stopTime = decodeDateTime(programme.getStop());
                Calendar currentTime = Calendar.getInstance();
                if (currentTime.after(startTime) && currentTime.before(stopTime)) {
                    result = n;
                }
            }
        }
        return result;
    }

    public List<Programme> getChannelGuide(String chName) {
        List<Programme> result = new ArrayList<>();
        String id = getIdByChannelName(chName);
        for (Programme programme : programmeList) {
            if (programme.getChannel().equals(id)) {
                result.add(programme);
            }
        }
        return result;
    }

    public void setupGuideProvList() {
        guideProvList.add(new GuideProv("iptvx.one", "https://iptvx.one/epg/epg.xml.gz",
                "epg.xml.gz"));
        guideProvList.add(new GuideProv("TeleGuide.info", "http://www.teleguide.info/download/new3/xmltv.xml.gz",
                "xmltv.xml.gz"));
    }

    @SuppressLint("SimpleDateFormat")
    public String getTotalTimePeriod() {
        String result = null;
        final DateFormat totalSdf = new SimpleDateFormat("dd.MM");
        List<String> dateList = new ArrayList<>();
        if (channelGuideList.size() > 0) {
            String chId = channelGuideList.get(0).getId();
            for (Programme programme : programmeList) {
                if (programme.getChannel().equals(chId)) {
                    dateList.add(programme.getStart());
                }
            }
            Calendar startDate = decodeDateTime(dateList.get(0));
            Calendar endDate = decodeDateTime(dateList.get(dateList.size() - 1));
            result = totalSdf.format(startDate.getTime()) + " - " + totalSdf.format(endDate.getTime());
        }
        return result;
    }

    @SuppressLint("SimpleDateFormat")
    private Calendar decodeDateTime(String str) {
        final DateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss Z");
        Calendar result = Calendar.getInstance();
        try {
            if (!str.isEmpty())
                result.setTime(sdf.parse(str));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    public String getChannelNameById(String id) {
        for (ChannelGuide cg : channelGuideList) {
            if (id.equals(cg.getId()))
                return cg.getDisplayName();
        }
        return null;
    }

    public List<Programme> searchAllPeriod(String str) {
        List<Programme> list = new ArrayList<>();
        if (str != null) {
            for (Programme p : programmeList) {
                if (p.getTitle().toLowerCase().contains(str.toLowerCase()))
                    list.add(p);
            }
        }
        return list;
    }

    public List<Programme> searchAfterMoment(String str) {
        List<Programme> list = new ArrayList<>();
        for (Programme p : programmeList) {
            if (p.getTitle().toLowerCase().contains(str.toLowerCase())) {
                Calendar stopTime = decodeDateTime(p.getStop());
                Calendar currentTime = Calendar.getInstance();
                if (currentTime.before(stopTime))
                    list.add(p);
            }
        }
        return list;
    }

    public List<Programme> searchToday(String str) {
        List<Programme> list = new ArrayList<>();
        for (Programme p : programmeList) {
            if (p.getTitle().toLowerCase().contains(str.toLowerCase())) {
                Calendar startTime = decodeDateTime(p.getStart());
                Calendar stopTime = decodeDateTime(p.getStop());
                Calendar currentTime = Calendar.getInstance();
                if ((currentTime.get(Calendar.MONTH) == startTime.get(Calendar.MONTH)) &&
                        (currentTime.get(Calendar.DAY_OF_MONTH) == startTime.get(Calendar.DAY_OF_MONTH)))
                    list.add(p);
            }
        }
        return list;
    }

    public List<Programme> searchCurrentMoment(String str) {
        List<Programme> list = new ArrayList<>();
        for (Programme p : programmeList) {
            if (p.getTitle().toLowerCase().contains(str.toLowerCase())) {
                Calendar startTime = decodeDateTime(p.getStart());
                Calendar stopTime = decodeDateTime(p.getStop());
                Calendar currentTime = Calendar.getInstance();
                if (currentTime.after(startTime) && currentTime.before(stopTime))
                    list.add(p);
            }
        }
        return list;
    }

}
