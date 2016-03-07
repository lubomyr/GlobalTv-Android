package atua.anddev.globaltv.service;

import atua.anddev.globaltv.entity.*;

import java.util.*;

public interface PlaylistService {
    List<Playlist> activePlaylist = new ArrayList<Playlist>();
    List<Playlist> offeredPlaylist = new ArrayList<Playlist>();
    List<String> activePlaylistName = new ArrayList<String>();

    public void addToActivePlaylist(String name, String url, int type);

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
}
