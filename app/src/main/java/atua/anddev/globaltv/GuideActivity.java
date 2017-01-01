package atua.anddev.globaltv;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ExpandableListView;

import java.util.List;

import atua.anddev.globaltv.adapters.GuideExpListAdapter;
import atua.anddev.globaltv.entity.Programme;

public class GuideActivity extends Activity implements GlobalServices {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set sub.xml as user interface layout
        setContentView(R.layout.guidelist);

        String chName = getData();
        showData(chName);
    }

    private String getData() {
        Intent intent = this.getIntent();
        return intent.getStringExtra("name");
    }

    private void showData(String chName) {
        List<Programme> guideList = guideService.getChannelGuide(chName);

        ExpandableListView expandableListView = (ExpandableListView) findViewById(R.id.guideList);
        GuideExpListAdapter adapter = new GuideExpListAdapter(this, guideList);
        expandableListView.setAdapter(adapter);
        int pos = guideService.getProgramPos(chName);
        if (pos > 2)
            pos -=-2;
        expandableListView.setSelection(pos);
    }
}
