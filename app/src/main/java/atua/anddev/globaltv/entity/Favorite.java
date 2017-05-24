package atua.anddev.globaltv.entity;

import io.realm.RealmObject;

public class Favorite extends RealmObject {
    private String name;

    private String plist;

    public Favorite() {
    }

    public Favorite(String name, String plist) {
        this.name = name;
        this.plist = plist;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlist() {
        return plist;
    }

    public void setPlist(String plist) {
        this.plist = plist;
    }
}
