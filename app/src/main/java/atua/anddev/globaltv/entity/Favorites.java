package atua.anddev.globaltv.entity;

public class Favorites {
    private String name;
    private String prov;

    public Favorites(String name, String prov) {
        this.name = name;
        this.prov = prov;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setProv(String prov) {
        this.prov = prov;
    }

    public String getProv() {
        return prov;
    }
}
