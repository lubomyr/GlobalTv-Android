package atua.anddev.globaltv.entity;

import java.util.*;

public class Playlist {
    private String name;
    private String url;
    private String file;
    private int type;
    private String md5;
    private String update;

    public Playlist(String name, String url, String file, int type, String md5, String update) {
        this.name = name;
        this.url = url;
        this.file = file;
        this.type = type;
        this.md5 = md5;
        this.update = update;
    }

    public Playlist(String name, String url, String file, int type) {
        this.name = name;
        this.url = url;
        this.file = file;
        this.type = type;
        this.md5 = "";
        this.update = "";
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
}
