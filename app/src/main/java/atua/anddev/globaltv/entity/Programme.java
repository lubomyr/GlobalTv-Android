package atua.anddev.globaltv.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;

@Entity
public class Programme {
    @Id(autoincrement = true)
    private Long id;
    
    private String start;
    private String stop;
    private String channel;
    private String title;
    private String desc;
    private String category;

    @Keep
    public Programme(String start, String stop, String channel, String title, String desc, String category) {
        this.start = start;
        this.stop = stop;
        this.channel = channel;
        this.title = title;
        this.desc = desc;
        this.category = category;
    }

    @Generated(hash = 1762154496)
    public Programme() {
    }

    @Generated(hash = 2110306381)
    public Programme(Long id, String start, String stop, String channel, String title, String desc,
                     String category) {
        this.id = id;
        this.start = start;
        this.stop = stop;
        this.channel = channel;
        this.title = title;
        this.desc = desc;
        this.category = category;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getStop() {
        return stop;
    }

    public void setStop(String stop) {
        this.stop = stop;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
