package atua.anddev.globaltv.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;

import java.io.Serializable;

@Entity
public class Channel implements Serializable {

    private static final long serialVersionUID = -361931352168739862L;

    @Id(autoincrement = true)
    private Long id;

    private String name;
    private String url;
    private String category;
    private String icon;
    private String provider;

    @Keep
    public Channel(String name, String url, String category, String icon, String provider) {
        this.name = name;
        this.url = url;
        this.category = category;
        this.icon = icon;
        this.provider = provider;
    }

    @Generated(hash = 459652974)
    public Channel() {
    }

    @Generated(hash = 1650387048)
    public Channel(Long id, String name, String url, String category, String icon,
                   String provider) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.category = category;
        this.icon = icon;
        this.provider = provider;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getIcon() {
        return this.icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getProvider() {
        return this.provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
