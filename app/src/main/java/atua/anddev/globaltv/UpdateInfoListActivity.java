package atua.anddev.globaltv;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import atua.anddev.globaltv.entity.Playlist;

public class UpdateInfoListActivity extends Activity implements GlobalServices {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set sub.xml as user interface layout
        setContentView(R.layout.updateinfolist);

        showSortedDateOfPlaylist();
    }

    private void showSortedDateOfPlaylist() {
        List<Playlist> sortedList = playlistService.getSortedByDatePlaylists();
        List<String> name = new ArrayList<String>();
        List<String> date = new ArrayList<String>();

        for (Playlist plst : sortedList) {
            Long longDate;
            String daysPassed;
            name.add(plst.getName());
            try {
                longDate = Long.parseLong(plst.getUpdate());
                daysPassed = getDiffDays(longDate);
            } catch (Exception e) {
                daysPassed = getResources().getString(R.string.playlistnotexist);
            }

            date.add(daysPassed);
        }
        final GlobalAdapter adapter = new GlobalAdapter(this, name, date);
        ListView list = (ListView) findViewById(R.id.sortedPlaylistListView1);
        list.setAdapter(adapter);
    }

    private String getDiffDays(long inputDate) {
        String tmpText;
        long currDate = new Date().getTime();
        long resDate = currDate - inputDate;
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(resDate);
        int daysPassed = cal.get(Calendar.DAY_OF_YEAR);
        switch (daysPassed) {
            case 1:
                tmpText = getResources().getString(R.string.updated) + " " + new Date(inputDate).toLocaleString();
                break;
            case 2:
                tmpText = getResources().getString(R.string.updated) + " 1 " + getResources().getString(R.string.dayago);
                break;
            case 3:
            case 4:
            case 5:
                tmpText = getResources().getString(R.string.updated) + " " + (daysPassed - 1) + " " + getResources().getString(R.string.daysago);
                break;
            default:
                tmpText = getResources().getString(R.string.updated) + " " + (daysPassed - 1) + " " + getResources().getString(R.string.fivedaysago);
                break;
        }
        return tmpText;
    }

}
