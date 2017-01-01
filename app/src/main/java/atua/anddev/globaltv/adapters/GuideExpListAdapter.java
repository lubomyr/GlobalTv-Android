package atua.anddev.globaltv.adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import atua.anddev.globaltv.Global;
import atua.anddev.globaltv.R;
import atua.anddev.globaltv.GlobalServices;
import atua.anddev.globaltv.entity.Programme;

public class GuideExpListAdapter extends BaseExpandableListAdapter implements GlobalServices {
    private Context context;
    private List<Programme> itemList;

    public GuideExpListAdapter(Context context, List<Programme> itemList) {
        this.context = context;
        this.itemList = itemList;
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

    private String getTimeString(Programme programme) {
        final DateFormat sdfInput = new SimpleDateFormat("yyyyMMddHHmmss Z");
        final DateFormat sdfStartOutput = new SimpleDateFormat("dd.MM HH:mm");
        final DateFormat sdfEndOutput = new SimpleDateFormat("HH:mm");
        String startDateInput = programme.getStart();
        String endDateInput = programme.getStop();
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        try {
            if (!startDateInput.isEmpty())
                startDate.setTime(sdfInput.parse(startDateInput));
            if (!endDateInput.isEmpty())
                endDate.setTime(sdfInput.parse(endDateInput));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String timeOutput = sdfStartOutput.format(startDate.getTime()) + " - " + sdfEndOutput.format(endDate.getTime());
        return timeOutput;
    }

    private int getColor(Programme programme) {
        int color;
        Calendar currentTime = Calendar.getInstance();
        final DateFormat sdfInput = new SimpleDateFormat("yyyyMMddHHmmss Z");
        String startDateInput = programme.getStart();
        String endDateInput = programme.getStop();
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        try {
            if (!startDateInput.isEmpty())
                startDate.setTime(sdfInput.parse(startDateInput));
            if (!endDateInput.isEmpty())
                endDate.setTime(sdfInput.parse(endDateInput));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (currentTime.after(startDate) && currentTime.before(endDate)) {
            color = Color.WHITE;
        } else {
            color = Color.parseColor("#959595");
        }
        return color;
    }

}
