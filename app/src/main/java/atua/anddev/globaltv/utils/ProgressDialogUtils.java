package atua.anddev.globaltv.utils;

import android.app.Activity;
import android.app.ProgressDialog;

public class ProgressDialogUtils {
    private static ProgressDialog progressDialog;

    public static ProgressDialog showProgressDialog(final Activity activity, final String msg, final int size) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog = new ProgressDialog(activity);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setMessage(msg);
                progressDialog.setCancelable(false);
                progressDialog.setMax(size);
                progressDialog.setProgress(0);
                progressDialog.show();
            }
        });
        return progressDialog;
    }

    public static void setProgress(Activity activity, final ProgressDialog progressDialog, final int value) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog != null)
                    progressDialog.setProgress(value);
            }
        });
    }

    public static void closeProgress(Activity activity, final ProgressDialog progressDialog) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog != null)
                    progressDialog.dismiss();
            }
        });
    }
}
