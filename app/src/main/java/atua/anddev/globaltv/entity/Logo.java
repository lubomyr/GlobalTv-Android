package atua.anddev.globaltv.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Keep;

@Entity
public class Logo {
    private String name;
    private String icon;
    private String url;

    @Keep
    public Logo(String name, String icon, String url) {
        this.name = name;
        this.icon = icon;
        this.url = url;
    }

    @Generated(hash = 331804212)
    public Logo() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
