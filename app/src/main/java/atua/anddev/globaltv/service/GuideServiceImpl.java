package atua.anddev.globaltv.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

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
import atua.anddev.globaltv.repository.ChannelGuideRepository;
import atua.anddev.globaltv.repository.ProgrammeRepository;

public class GuideServiceImpl implements GuideService {

    public boolean checkForUpdate(Context context, int selectedGuideProv) {
        return checkGuideDates();
    }

    public void parseGuide(Context context, int selectedGuideProv) {
        Log.d("globaltv", "parsing program guide...");
        String dispName = null, id = null, start = null, stop = null, channel = null;
        String endTag, text = null, title = null, desc = null, category = null;
        Global.guideLoaded = false;
        List<ChannelGuide> channelGuideList = new ArrayList<>();
        List<Programme> programmeList = new ArrayList<>();
        try {
            XmlPullParser xpp;
            XmlPullParserFactory xppf = XmlPullParserFactory.newInstance();
            xppf.setNamespaceAware(true);
            xpp = xppf.newPullParser();
            GuideProv guideProv = guideProvList.get(selectedGuideProv);
            FileInputStream fis = context.getApplicationContext().openFileInput(guideProv.getFile());
            GZIPInputStream gzipIs = new GZIPInputStream(fis);
            xpp.setInput(gzipIs, null);
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                switch (xpp.getEventType()) {
                    case XmlPullParser.START_DOCUMENT:
                        break;

                    case XmlPullParser.START_TAG:
                        for (int i = 0; i < xpp.getAttributeCount(); i++) {
                            String tmp = xpp.getAttributeName(i);
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
            ChannelGuideRepository.deleteAll();
            ProgrammeRepository.deleteAll();
            ChannelGuideRepository.saveAll(channelGuideList);
            ProgrammeRepository.saveAll(programmeList);
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
            Log.d("globaltv", "parsing error " + e.getMessage());
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

    @Override
    public int channelGuideListSize() {
        return channelGuideList.size();
    }

    @Override
    public void addAllChannelGuideList() {
        channelGuideList.addAll(ChannelGuideRepository.getAll());
    }

    @Override
    public String getNameOfChannelById(String id) {
        for (ChannelGuide cg : channelGuideList) {
            if (id.equals(cg.getId()))
                return cg.getDisplayName();
        }
        return null;
    }

    @Override
    public List<Programme> getProgramsByStringForFullPeriod(String search) {
        return ProgrammeRepository.getProgramsByNameFullPeriod(search);
    }

    @Override
    public List<Programme> getProgramsByStringAfterMoment(String search) {
        List<Programme> list = new ArrayList<>();
        List<Programme> searchList = ProgrammeRepository.getProgramsByNameFullPeriod(search);
        Calendar currentTime = Calendar.getInstance();
        for (Programme p : searchList) {
            Calendar stopTime = decodeDateTime(p.getStop());
            if (currentTime.before(stopTime))
                list.add(p);
        }
        return list;
    }

    @Override
    public List<Programme> getProgramsByStringForCurrentMoment(String str) {
        List<Programme> list = new ArrayList<>();
        List<Programme> searchList = ProgrammeRepository.getProgramsByNameFullPeriod(str);
        Calendar currentTime = Calendar.getInstance();
        for (Programme p : searchList) {
            if (p.getTitle().toLowerCase().contains(str.toLowerCase())) {
                Calendar startTime = decodeDateTime(p.getStart());
                Calendar stopTime = decodeDateTime(p.getStop());
                if (currentTime.after(startTime) && currentTime.before(stopTime))
                    list.add(p);
            }
        }
        return list;
    }

    public List<Programme> getProgramsByStringForToday(String str) {
        List<Programme> list = new ArrayList<>();
        List<Programme> searchList = ProgrammeRepository.getProgramsByNameFullPeriod(str);
        Calendar currentTime = Calendar.getInstance();
        for (Programme p : searchList) {
            if (p.getTitle().toLowerCase().contains(str.toLowerCase())) {
                Calendar startTime = decodeDateTime(p.getStart());
                Calendar stopTime = decodeDateTime(p.getStop());
                if ((currentTime.get(Calendar.MONTH) == startTime.get(Calendar.MONTH)) &&
                        (currentTime.get(Calendar.DAY_OF_MONTH) == startTime.get(Calendar.DAY_OF_MONTH)))
                    list.add(p);
            }
        }
        return list;
    }

    private boolean checkGuideDates() {
        boolean result;
        try {
            String chId = ChannelGuideRepository.getFirstId();
            List<Programme> programmList = ProgrammeRepository.getAllProgramsByChannel(chId);
            Calendar startTime = decodeDateTime(programmList.get(0).getStart());
            Calendar stopTime = decodeDateTime(programmList.get(programmList.size() - 1).getStart());
            Calendar currentTime = Calendar.getInstance();
            result = !(currentTime.after(startTime) && currentTime.before(stopTime));
        } catch (Exception e) {
            result = true;
        }
        return result;
    }

    private String getIdByChannelName(String chName) {
        String result = null;
        for (ChannelGuide channelGuide : channelGuideList) {
            String str = channelGuide.getDisplayName();
            if (chName.equals(str))
                result = channelGuide.getId();
        }
        return result;
    }

    private String getProgramTitlebyId(String id) {
        Programme programme = ProgrammeRepository.getCurrentProgramByChannel(id);
        return (programme != null) ? programme.getTitle() : "";
    }

    private String getProgramDescbyId(String id) {
        Programme programme = ProgrammeRepository.getCurrentProgramByChannel(id);
        return (programme != null) ? programme.getDesc() : "";
    }

    private int getProgramPositionbyId(String id) {
        return ProgrammeRepository.getCurrentPosByChannel(id);
    }

    public List<Programme> getChannelGuide(String chName) {
        String id = ChannelGuideRepository.getIdByName(chName);
        return ProgrammeRepository.getAllProgramsByChannel(id);
    }

    public void setupGuideProvList() {
        guideProvList.add(new GuideProv("Epg.in.ua", "http://epg.in.ua/epg/tvprogram_ua_ru.gz",
                "epginua.xml.gz"));
        guideProvList.add(new GuideProv("TeleGuide.info", "http://www.teleguide.info/download/new3/xmltv.xml.gz",
                "teleguide.xml.gz"));
        guideProvList.add(new GuideProv("Torrent-tv.ru", "http://api.torrent-tv.ru/ttv.xmltv.xml.gz",
                "ttvru.xml.gz"));
    }

    @SuppressLint("SimpleDateFormat")
    public String getTotalTimePeriod() {
        String result = null;
        final DateFormat totalSdf = new SimpleDateFormat("dd.MM");
        try {
            String chId = ChannelGuideRepository.getFirstId();
            List<Programme> programmList = ProgrammeRepository.getAllProgramsByChannel(chId);
            Calendar startTime = decodeDateTime(programmList.get(0).getStart());
            Calendar stopTime = decodeDateTime(programmList.get(programmList.size() - 1).getStart());
            result = totalSdf.format(startTime.getTime()) + " - " + totalSdf.format(stopTime.getTime());
        } catch (Exception ignored) {
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
}