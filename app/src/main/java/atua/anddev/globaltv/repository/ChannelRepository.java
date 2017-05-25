package atua.anddev.globaltv.repository;

import java.util.ArrayList;
import java.util.List;

import atua.anddev.globaltv.entity.Channel;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;

public class ChannelRepository {

    public static void saveAll(List<Channel> items) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealm(items);
        realm.commitTransaction();
        realm.close();
    }

    public static void deleteAll() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(Channel.class);
        realm.commitTransaction();
        realm.close();
    }

    public static List<Channel> getAll() {
        Realm realm = Realm.getDefaultInstance();
        List<Channel> list = new ArrayList<>();
        RealmResults<Channel> results = realm.where(Channel.class).findAll();
        list.addAll(results);
        realm.close();
        return list;
    }

    public static String getUrlByPlistAndName(String plist, String name) {
        Realm realm = Realm.getDefaultInstance();
        List<Channel> list = new ArrayList<>();
        RealmResults<Channel> results = realm.where(Channel.class).equalTo("provider", plist)
                .equalTo("name", name).findAll();
        list.addAll(results);
        return list.get(0).getUrl();
    }

    public static List<Channel> getChannelsByPlaylist(String plist) {
        Realm realm = Realm.getDefaultInstance();
        List<Channel> list = new ArrayList<>();
        RealmResults<Channel> results = realm.where(Channel.class).equalTo("provider", plist).findAll();
        list.addAll(results);
        return list;
    }

    public static void deleteChannelsByPlaylist(String plist) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RealmResults<Channel> results = realm.where(Channel.class).equalTo("provider", plist).findAll();
        results.deleteAllFromRealm();
        realm.commitTransaction();
        realm.close();
    }

    public static List<Channel> getChannelsByCategory(String plist, String catname) {
        Realm realm = Realm.getDefaultInstance();
        List<Channel> list = new ArrayList<>();
        RealmResults<Channel> results = realm.where(Channel.class).equalTo("provider", plist)
                .equalTo("category", catname).findAll();
        list.addAll(results);
        return list;
    }

    public static List<String> getCategoriesList(String plist) {
        Realm realm = Realm.getDefaultInstance();
        List<String> result = new ArrayList<>();
        RealmResults<Channel> chList = realm.where(Channel.class).equalTo("provider", plist).distinct("category");
        for (Channel c : chList)
            if (!result.contains(c.getCategory()) && !c.getCategory().isEmpty())
                result.add(c.getCategory());
        return result;
    }

    public static List<Channel> searchChannelsByName(String search) {
        Realm realm = Realm.getDefaultInstance();
        List<Channel> list = new ArrayList<>();
        RealmResults<Channel> results = realm.where(Channel.class).like("name", "*" + search + "*",
                Case.INSENSITIVE).findAll();
        list.addAll(results);
        return list;
    }

    public static Channel getChannelByUrl(String url) {
        Realm realm = Realm.getDefaultInstance();
        List<Channel> list = new ArrayList<>();
        RealmResults<Channel> results = realm.where(Channel.class).equalTo("url", url).findAll();
        list.addAll(results);
        return list.get(0);
    }
}
