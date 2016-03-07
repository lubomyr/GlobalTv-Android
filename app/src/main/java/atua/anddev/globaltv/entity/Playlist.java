package atua.anddev.globaltv.entity;

public class Playlist {
    private String name;
    private String url;
    private String file;
    private int type;

    public Playlist(String name, String url, String file, int type) {
        this.name = name;
        this.url = url;
        this.file = file;
        this.type = type;
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
