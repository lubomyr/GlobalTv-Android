package atua.anddev.globaltv.service;

import android.content.*;

import atua.anddev.globaltv.entity.*;

import java.io.*;
import java.util.*;

public interface PlaylistService {
    List<Playlist> activePlaylist = new ArrayList<Playlist>();
    List<Playlist> offeredPlaylist = new ArrayList<Playlist>();
    List<String> activePlaylistName = new ArrayList<String>();

    public void addToActivePlaylist(String name, String url, int type, String md5, String update);

    public void addToOfferedPlaylist(String name, String url, int type);

    public void setActivePlaylistById(int id, String name, String url, int type);

    public Playlist getActivePlaylistById(int id);

    public Playlist getOfferedPlaylistById(int id);

    public void clearActivePlaylist();

    public void clearOfferedPlaylist();

    public int sizeOfActivePlaylist();

    public int sizeOfOfferedPlaylist();

    public List<Playlist> getAllActivePlaylist();

    public List<Playlist> getAllOfferedPlaylist();

    public List<String> getAllNamesOfActivePlaylist();

    public List<String> getAllNamesOfOfferedPlaylist();

    public int indexNameForActivePlaylist(String name);

    public int indexNameForOfferedPlaylist(String name);

    public void deleteActivePlaylistById(int id);

    public void addNewActivePlaylist(Playlist plst);

    public void setMd5(int id, String md5);

    public void setUpdateDate(int id, Long update);

    public void saveData(Context context) throws FileNotFoundException, IOException;

    public void setupProvider(String opt, Context context);
}
