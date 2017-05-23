package atua.anddev.globaltv.service;

import java.util.ArrayList;
import java.util.List;

import atua.anddev.globaltv.entity.Playlist;

public interface PlaylistService {
    List<Playlist> offeredPlaylist = new ArrayList<>();
    List<String> activePlaylistName = new ArrayList<>();

    void addToActivePlaylist(String name, String url, int type, String md5, String update);

    void addToOfferedPlaylist(String name, String url, int type);

    void setActivePlaylistById(int id, String name, String url, int type);

    Playlist getActivePlaylistById(int id);

    Playlist getOfferedPlaylistById(int id);

    void clearActivePlaylist();

    int sizeOfActivePlaylist();

    int sizeOfOfferedPlaylist();

    List<String> getAllNamesOfActivePlaylist();

    List<String> getAllNamesOfOfferedPlaylist();

    int indexNameForActivePlaylist(String name);

    void deleteActivePlaylistById(int id);

    void addNewActivePlaylist(Playlist plst);

    void setMd5(int id, String md5);

    void setUpdateDate(int id, Long update);

    void setupProvider();

    List<Playlist> getSortedByDatePlaylists();

    void readPlaylist(int num);

    void addAllOfferedPlaylist();
}
