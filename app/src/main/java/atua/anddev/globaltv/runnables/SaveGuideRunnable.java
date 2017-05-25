package atua.anddev.globaltv.runnables;

import android.app.Activity;
import android.widget.Toast;

import java.io.IOException;

import atua.anddev.globaltv.Global;
import atua.anddev.globaltv.entity.GuideProv;

import static atua.anddev.globaltv.Global.myPath;
import static atua.anddev.globaltv.GlobalServices.guideService;
import static atua.anddev.globaltv.MainActivity.saveUrl;
import static atua.anddev.globaltv.service.GuideService.guideProvList;

public class SaveGuideRunnable implements Runnable {
    private Activity context;
    private int selectedGuideProv;

    public SaveGuideRunnable(Activity context, int selectedGuideProv) {
        this.context = context;
        this.selectedGuideProv = selectedGuideProv;
    }

    @Override
    public void run() {
        if (guideService.checkForUpdate(context, selectedGuideProv)) {
            updateGuide();
        }
        context.runOnUiThread(new Runnable() {
            public void run() {
                if (guideService.channelGuideListSize() == 0)
                    guideService.addAllChannelGuideList();
                Global.guideLoaded = true;
                Toast.makeText(context, "guide loaded", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateGuide() {
        GuideProv guideProv = guideProvList.get(selectedGuideProv);
        try {
            saveUrl(myPath + "/" + guideProv.getFile(), guideProv.getUrl());
        } catch (IOException e) {
            e.printStackTrace();
        }
        guideService.parseGuide(context, selectedGuideProv);
    }
}
