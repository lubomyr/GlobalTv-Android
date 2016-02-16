package atua.anddev.globaltv;

import java.util.*;

public class Playlist {
    protected ArrayList<String> name = new ArrayList<String>();
    private ArrayList<String> url = new ArrayList<String>();
    private ArrayList<String> file = new ArrayList<String>();
    private ArrayList<Integer> type = new ArrayList<Integer>();

    public int size() {
        return this.name.size();
    }

    public void clear() {
        this.name.clear();
        this.url.clear();
        this.file.clear();
        this.type.clear();
    }

    public void add(String name, String url, String file, int type) {
        this.name.add(name);
        this.url.add(url);
        this.file.add(file);
        this.type.add(type);
    }

    public void set(int pos, String name, String url, String file, int type) {
        this.name.set(pos, name);
        this.url.set(pos, url);
        this.file.set(pos, file);
        this.type.set(pos, type);
    }

    public void remove(int num) {
        this.name.remove(num);
        this.url.remove(num);
        this.file.remove(num);
        this.type.remove(num);
    }

    public String getName(int num) {
        return this.name.get(num);
    }

    public String getUrl(int num) {
        return this.url.get(num);
    }

    public String getFile(int num) {
        return this.file.get(num);
    }

    public int getType(int num) {
        return this.type.get(num);
    }

    public String getTypeString(int num) {
        return this.type.get(num).toString();
    }

    public int indexOfName(String str) {
        return this.name.indexOf(str);
    }
}
