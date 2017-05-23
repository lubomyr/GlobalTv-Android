package atua.anddev.globaltv.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Keep;

@Entity
public class GuideProv {
    private String name;
    private String url;
    private String file;

    @Keep
    public GuideProv(String name, String url, String file) {
        this.name = name;
        this.url = url;
        this.file = file;
    }

    @Generated(hash = 1832289109)
    public GuideProv() {
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
