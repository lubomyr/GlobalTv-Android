package atua.anddev.globaltv.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import atua.anddev.globaltv.GlobalServices;
import atua.anddev.globaltv.R;
import atua.anddev.globaltv.entity.Programme;

public class GuideExpListAdapter extends BaseExpandableListAdapter implements GlobalServices {
    private Context context;
    private List<Programme> itemList;
    private boolean showChannelName;

    public GuideExpListAdapter(Context context, List<Programme> itemList, boolean showChannelName) {
        this.context = context;
        this.itemList = itemList;
        this.showChannelName = showChannelName;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        List<Programme> childList = itemList;
        return childList.get(groupPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View view, ViewGroup parent) {

        Programme item = (Programme) getChild(groupPosition, childPosition);
        if (view == null) {
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = infalInflater.inflate(R.layout.item_guide_desc, null);
        }

        TextView childItem = (TextView) view.findViewById(R.id.guide_desc);
        String desc = item.getDesc();
        desc = (desc != null) ? desc : "";
        childItem.setText(Html.fromHtml(desc));
        if (desc.isEmpty())
            childItem.setVisibility(View.GONE);
        else
            childItem.setVisibility(View.VISIBLE);
        return view;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return itemList.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return itemList != null ? itemList.size() : 0;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isLastChild, View view,
                             ViewGroup parent) {

        Programme item = (Programme) getGroup(groupPosition);
        if (view == null) {
            LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inf.inflate(R.layout.item_guide_title, null);
        }

        int color = getColor(item);
        TextView timeView = (TextView) view.findViewById(R.id.guide_time);
        timeView.setText(getTimeString(item));
        timeView.setTextColor(color);
        TextView titleView = (TextView) view.findViewById(R.id.guide_title);
        String title = item.getTitle();
        title = (title != null) ? title : "";
        titleView.setText(Html.fromHtml(title));
        titleView.setTextColor(color);
        LinearLayout channelLayout = (LinearLayout) view.findViewById(R.id.channelLayout);
        if (showChannelName) {
            channelLayout.setVisibility(View.VISIBLE);
            TextView channelNameView = (TextView) view.findViewById(R.id.chName);
            String chName = guideService.getNameOfChannelById(item.getChannel());
            channelNameView.setText(chName);
            ImageView iconView = (ImageView) view.findViewById(R.id.chIcon);
            String icon = logoService.getLogoByName(chName);
            if ((icon != null) && !icon.isEmpty()) {
                Glide.with(context).load(icon).into(iconView);
            } else
                iconView.setImageDrawable(null);
        } else
            channelLayout.setVisibility(View.GONE);
        return view;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void setDataList(List<Programme> itemList) {
        this.itemList = itemList;
    }

    @SuppressLint("SimpleDateFormat")
    private String getTimeString(Programme programme) {
        final DateFormat sdfStartOutput = new SimpleDateFormat("dd.MM HH:mm");
        final DateFormat sdfEndOutput = new SimpleDateFormat("HH:mm");
        Calendar startTime = decodeDateTime(programme.getStart());
        Calendar stopTime = decodeDateTime(programme.getStop());
        return sdfStartOutput.format(startTime.getTime()) + " - " + sdfEndOutput.format(stopTime.getTime());
    }

    private int getColor(Programme programme) {
        int color;
        Calendar currentTime = Calendar.getInstance();
        Calendar startTime = decodeDateTime(programme.getStart());
        Calendar stopTime = decodeDateTime(programme.getStop());
        if (currentTime.after(startTime) && currentTime.before(stopTime)) {
            color = Color.WHITE;
        } else {
            color = Color.parseColor("#959595");
        }
        return color;
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
