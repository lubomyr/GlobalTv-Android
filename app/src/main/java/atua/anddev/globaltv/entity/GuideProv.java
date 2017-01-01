package atua.anddev.globaltv.entity;

/**
 * Created by lyubomyr on 27.12.16.
 */
public class GuideProv {
    private String name;
    private String url;
    private String file;

    public GuideProv(String name, String url, String file) {
        this.name = name;
        this.url = url;
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
}
