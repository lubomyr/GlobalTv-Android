package atua.anddev.globaltv.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;

@Entity
public class Playlist {
    @Id(autoincrement = true)
    private Long id;

    private String name;
    private String url;
    private String file;
    private int type;
    private String md5;
    private String update;

    @Keep
    public Playlist(String name, String url, String file, int type, String md5, String update) {
        this.name = name;
        this.url = url;
        this.file = file;
        this.type = type;
        this.md5 = md5;
        this.update = update;
    }

    @Keep
    public Playlist(String name, String url, String file, int type) {
        this.name = name;
        this.url = url;
        this.file = file;
        this.type = type;
        this.md5 = "";
        this.update = "";
    }

    @Generated(hash = 1160175056)
    public Playlist() {
    }

    @Generated(hash = 1479442757)
    public Playlist(Long id, String name, String url, String file, int type, String md5,
                    String update) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.file = file;
        this.type = type;
        this.md5 = md5;
        this.update = update;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getMd5() {
        return md5;
    }

    public void setUpdate(String update) {
        this.update = update;
    }

    public String getUpdate() {
        return update;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public String getTypeString() {
        return String.valueOf(type);
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getFile() {
        return file;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
