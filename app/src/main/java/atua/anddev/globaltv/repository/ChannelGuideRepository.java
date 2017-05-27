package atua.anddev.globaltv.repository;

import java.util.ArrayList;
import java.util.List;

import atua.anddev.globaltv.entity.ChannelGuide;
import io.realm.Realm;
import io.realm.RealmResults;

public class ChannelGuideRepository {

    public static void saveAll(List<ChannelGuide> items) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealm(items);
        realm.commitTransaction();
        realm.close();
    }

    public static void insert(ChannelGuide item) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealm(item);
        realm.commitTransaction();
        realm.close();
    }

    public static void deleteAll() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(ChannelGuide.class);
        realm.commitTransaction();
        realm.close();
    }

    public static List<ChannelGuide> getAll() {
        Realm realm = Realm.getDefaultInstance();
        List<ChannelGuide> list = new ArrayList<>();
        RealmResults<ChannelGuide> results = realm.where(ChannelGuide.class).findAll();
        list.addAll(results);
        return list;
    }

    public static String getFirstId() {
        Realm realm = Realm.getDefaultInstance();
        ChannelGuide result = realm.where(ChannelGuide.class).findFirst();
        return result.getId();
    }

    public static String getIdByName(String name) {
        Realm realm = Realm.getDefaultInstance();
        ChannelGuide result = realm.where(ChannelGuide.class).equalTo("displayName", name).findFirst();
        return result.getId();
    }

}
