package atua.anddev.globaltv;

import android.app.Application;
import android.content.Context;

import io.realm.Realm;

public class BaseApplication extends Application {
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        Realm.init(context);
    }
}
