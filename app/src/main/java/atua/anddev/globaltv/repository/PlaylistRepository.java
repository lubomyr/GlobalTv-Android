package atua.anddev.globaltv.repository;

import java.util.ArrayList;
import java.util.List;

import atua.anddev.globaltv.entity.Playlist;
import io.realm.Realm;
import io.realm.RealmResults;

public class PlaylistRepository {

    public static void saveAll(List<Playlist> items) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealm(items);
        realm.commitTransaction();
        realm.close();
    }

    public static void insert(Playlist item) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealm(item);
        realm.commitTransaction();
        realm.close();
    }

    public static void delete(Playlist item) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RealmResults<Playlist> result = realm.where(Playlist.class).equalTo("name", item.getName())
                .findAll();
        result.deleteAllFromRealm();
        realm.commitTransaction();
        realm.close();
    }

    public static void deleteAll() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(Playlist.class);
        realm.commitTransaction();
        realm.close();
    }

    public static List<Playlist> getAll() {
        Realm realm = Realm.getDefaultInstance();
        List<Playlist> list = new ArrayList<>();
        RealmResults<Playlist> results = realm.where(Playlist.class).findAll();
        list.addAll(results);
        return list;
    }

    public static List<Playlist> getSortedByDate() {
        Realm realm = Realm.getDefaultInstance();
        List<Playlist> list = new ArrayList<>();
        RealmResults<Playlist> results = realm.where(Playlist.class).findAllSorted("update");
        list.addAll(results);
        return list;
    }

    public static List<String> getNames() {
        Realm realm = Realm.getDefaultInstance();
        List<String> list = new ArrayList<>();
        RealmResults<Playlist> plist = realm.where(Playlist.class).findAll();
        for (Playlist p : plist)
            list.add(p.getName());
        return list;
    }

    public static void updateMd5(Playlist item, String md5) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        item.setMd5(md5);
        realm.commitTransaction();
        realm.close();
    }

    public static void updateDate(Playlist item, String update) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        item.setUpdate(update);
        realm.commitTransaction();
        realm.close();
    }

    public static void update(Playlist item, String name, String url, int type) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        item.setName(name);
        item.setUrl(url);
        item.setType(type);
        realm.commitTransaction();
        realm.close();
    }
}
