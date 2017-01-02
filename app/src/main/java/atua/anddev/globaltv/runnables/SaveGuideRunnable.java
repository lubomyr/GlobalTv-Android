package atua.anddev.globaltv.runnables;

import android.content.Context;

import java.io.IOException;

import atua.anddev.globaltv.entity.GuideProv;

import static atua.anddev.globaltv.Global.myPath;
import static atua.anddev.globaltv.MainActivity.saveUrl;
import static atua.anddev.globaltv.GlobalServices.guideService;
import static atua.anddev.globaltv.service.GuideService.guideProvList;

public class SaveGuideRunnable implements Runnable{
    private Context context;
    private int selectedGuideProv;

    public SaveGuideRunnable(Context context, int selectedGuideProv) {
        this.context = context;
        this.selectedGuideProv = selectedGuideProv;
    }

    @Override
    public void run() {
        GuideProv guideProv = guideProvList.get(selectedGuideProv);
        try {
            saveUrl(myPath + "/" + guideProv.getFile(), guideProv.getUrl());
        } catch (IOException e) {
            e.printStackTrace();
        }
        guideService.parseGuide(context, selectedGuideProv);
    }
}
