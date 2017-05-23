package atua.anddev.globaltv;

import android.app.Application;
import android.content.Context;

import org.greenrobot.greendao.database.Database;

import atua.anddev.globaltv.database.greendao.DaoMaster;
import atua.anddev.globaltv.database.greendao.DaoSession;

public class BaseApplication extends Application {
    public static Context context;
    static private DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        initDB();
    }

    private void initDB() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "globaltv_db");
        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
    }

    public static DaoSession getDaoSession() {
        return daoSession;
    }

}
