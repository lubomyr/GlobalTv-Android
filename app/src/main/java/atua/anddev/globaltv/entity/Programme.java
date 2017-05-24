package atua.anddev.globaltv.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.DaoException;
import atua.anddev.globaltv.database.greendao.DaoSession;
import atua.anddev.globaltv.database.greendao.ChannelGuideDao;
import atua.anddev.globaltv.database.greendao.ProgrammeDao;

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

    @ToOne(joinProperty = "channel")
    private ChannelGuide channelGuide;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 511568854)
    private transient ProgrammeDao myDao;

    @Generated(hash = 134692605)
    private transient String channelGuide__resolvedKey;

    @Keep
    public Programme(String start, String stop, String channel, String title, String desc, String category) {
        this.start = start;
        this.stop = stop;
        this.channel = channel;
        this.title = title;
        this.desc = desc;
        this.category = category;
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

    @Generated(hash = 1762154496)
    public Programme() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStart() {
        return this.start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getStop() {
        return this.stop;
    }

    public void setStop(String stop) {
        this.stop = stop;
    }

    public String getChannel() {
        return this.channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 2069967760)
    public ChannelGuide getChannelGuide() {
        String __key = this.channel;
        if (channelGuide__resolvedKey == null || channelGuide__resolvedKey != __key) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ChannelGuideDao targetDao = daoSession.getChannelGuideDao();
            ChannelGuide channelGuideNew = targetDao.load(__key);
            synchronized (this) {
                channelGuide = channelGuideNew;
                channelGuide__resolvedKey = __key;
            }
        }
        return channelGuide;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1825099274)
    public void setChannelGuide(ChannelGuide channelGuide) {
        synchronized (this) {
            this.channelGuide = channelGuide;
            channel = channelGuide == null ? null : channelGuide.getId();
            channelGuide__resolvedKey = channel;
        }
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1503864859)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getProgrammeDao() : null;
    }

}
