package atua.anddev.globaltv.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;

@Entity
public class ChannelGuide {
    @Id
    private String id;
    private String lang;
    private String displayName;

    @Keep
    public ChannelGuide(String id, String lang, String displayName) {
        this.id = id;
        this.lang = lang;
        this.displayName = displayName;
    }

    @Generated(hash = 335823160)
    public ChannelGuide() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
