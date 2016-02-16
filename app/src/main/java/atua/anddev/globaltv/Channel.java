package atua.anddev.globaltv;

import java.util.*;

public class Channel {
    private ArrayList<String> name = new ArrayList<String>();
    private ArrayList<String> category = new ArrayList<String>();
    private ArrayList<String> link = new ArrayList<String>();

    public int size() {
        return this.name.size();
    }

    public void clear() {
        this.name.clear();
        this.category.clear();
        this.link.clear();
    }

    public void add(String name, String category, String link) {
        this.name.add(name);
        this.category.add(category);
        this.link.add(link);
    }

    public String getName(int num) {
        return this.name.get(num);
    }

    public String getCategory(int num) {
        return this.category.get(num);
    }

    public String getLink(int num) {
        return this.link.get(num);
    }

}
