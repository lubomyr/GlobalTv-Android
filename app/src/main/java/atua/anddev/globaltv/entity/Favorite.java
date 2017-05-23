package atua.anddev.globaltv.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class Favorite {
    @Id(autoincrement = true)
    private Long id;

    private String name;

    private String plist;

    public Favorite(String name, String plist) {
        this.name = name;
        this.plist = plist;
    }

    @Generated(hash = 1788691815)
    public Favorite(Long id, String name, String plist) {
        this.id = id;
        this.name = name;
        this.plist = plist;
    }

    @Generated(hash = 459811785)
    public Favorite() {
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlist() {
        return this.plist;
    }

    public void setPlist(String plist) {
        this.plist = plist;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
