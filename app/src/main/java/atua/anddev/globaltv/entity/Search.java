package atua.anddev.globaltv.entity;

public class Search {
    private String name;
    private String url;
    private String prov;

    public Search(String name, String url, String prov) {
        this.name = name;
        this.url = url;
        this.prov = prov;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setProv(String prov) {
        this.prov = prov;
    }

    public String getProv() {
        return prov;
    }
}
