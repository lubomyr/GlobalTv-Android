package atua.anddev.globaltv.runnables;

import android.content.Context;

import java.io.IOException;

import atua.anddev.globaltv.Global;
import atua.anddev.globaltv.entity.GuideProv;

import static atua.anddev.globaltv.MainActivity.myPath;
import static atua.anddev.globaltv.MainActivity.saveUrl;
import static atua.anddev.globaltv.Services.guideService;
import static atua.anddev.globaltv.service.GuideService.guideProvList;

public class SaveGuideRunnable implements Runnable{
    private Context context;

    public SaveGuideRunnable(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        GuideProv guideProv = guideProvList.get(Global.selectedGuideProv);
        try {
            saveUrl(myPath + "/" + guideProv.getFile(), guideProv.getUrl());
        } catch (IOException e) {
            e.printStackTrace();
        }
        guideService.parseGuide(context);
    }
}
