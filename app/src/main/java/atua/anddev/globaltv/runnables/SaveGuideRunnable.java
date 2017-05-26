package atua.anddev.globaltv.runnables;

import android.app.Activity;
import android.app.ProgressDialog;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import atua.anddev.globaltv.Global;
import atua.anddev.globaltv.entity.GuideProv;
import atua.anddev.globaltv.utils.FileUtils;
import atua.anddev.globaltv.utils.ProgressDialogUtils;

import static atua.anddev.globaltv.Global.myPath;
import static atua.anddev.globaltv.GlobalServices.guideService;
import static atua.anddev.globaltv.service.GuideService.guideProvList;

public class SaveGuideRunnable implements Runnable {
    private Activity activity;
    private int selectedGuideProv;
    private ProgressDialog progressDialog;

    public SaveGuideRunnable(Activity activity, int selectedGuideProv) {
        this.activity = activity;
        this.selectedGuideProv = selectedGuideProv;
    }

    @Override
    public void run() {
        if (guideService.checkForUpdate(activity, selectedGuideProv)) {
            updateGuide();
        }
        if (guideService.channelGuideListSize() == 0)
            guideService.addAllChannelGuideList();
        Global.guideLoaded = true;
        activity.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(activity, "guide loaded", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateGuide() {
        GuideProv guideProv = guideProvList.get(selectedGuideProv);
        try {
            final int size = FileUtils.getFileSize(new URL(guideProv.getUrl()));
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog = ProgressDialogUtils.showProgressDialog(activity,
                            "Downloading ChannelGuide...", size);
                }
            });
            saveUrl(activity, myPath + "/" + guideProv.getFile(), guideProv.getUrl());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            ProgressDialogUtils.closeProgress(activity, progressDialog);
        }
        guideService.parseGuide(activity, selectedGuideProv);
    }

    private void saveUrl(Activity activity, final String filename, final String urlString)
            throws MalformedURLException, IOException {
        BufferedInputStream in = null;
        FileOutputStream fout = null;
        try {
            in = new BufferedInputStream(new URL(urlString).openStream());
            fout = new FileOutputStream(filename);

            final byte data[] = new byte[1024];
            int count;
            int cnt = 0;
            while ((count = in.read(data, 0, 1024)) != -1) {
                fout.write(data, 0, count);
                cnt += count;
                ProgressDialogUtils.setProgress(activity, progressDialog, cnt);
            }
        } finally {
            if (in != null) {
                in.close();
            }
            if (fout != null) {
                fout.close();
            }
        }
    }
}
