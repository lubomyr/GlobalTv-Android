package atua.anddev.globaltv.service;

import atua.anddev.globaltv.entity.*;

import java.util.*;

public class PlaylistServiceImpl implements PlaylistService {

    @Override
    public void deleteActivePlaylistById(int id) {
        activePlaylist.remove(id);
        activePlaylistName.remove(id);
    }


    @Override
    public Playlist getActivePlaylistById(int id) {
        return activePlaylist.get(id);
    }

    @Override
    public Playlist getOfferedPlaylistById(int id) {
        return offeredPlaylist.get(id);
    }


    @Override
    public void setActivePlaylistById(int id, String name, String url, int type) {
        String file = getFileName(name);
        activePlaylist.set(id, new Playlist(name, url, file, type));
        activePlaylistName.set(id, name);
    }

    @Override
    public void clearActivePlaylist() {
        activePlaylist.clear();
        activePlaylistName.clear();
    }

    @Override
    public void clearOfferedPlaylist() {
        offeredPlaylist.clear();
    }

    @Override
    public int sizeOfActivePlaylist() {
        return activePlaylist.size();
    }

    @Override
    public int sizeOfOfferedPlaylist() {
        return offeredPlaylist.size();
    }

    @Override
    public List<Playlist> getAllActivePlaylist() {
        return activePlaylist;
    }

    @Override
    public List<Playlist> getAllOfferedPlaylist() {
        return offeredPlaylist;
    }

    @Override
    public List<String> getAllNamesOfActivePlaylist() {
        List<String> arr = new ArrayList<String>();
        for (int i = 0; i < activePlaylist.size(); i++) {
            arr.add(activePlaylist.get(i).getName());
        }
        return arr;
    }

    @Override
    public List<String> getAllNamesOfOfferedPlaylist() {
        List<String> arr = new ArrayList<String>();
        for (int i = 0; i < offeredPlaylist.size(); i++) {
            arr.add(offeredPlaylist.get(i).getName());
        }
        return arr;
    }

    @Override
    public int indexNameForActivePlaylist(String name) {
        return activePlaylistName.indexOf(name);
    }

    @Override
    public int indexNameForOfferedPlaylist(String name) {
        int result = -1;
        for (int i = 0; i < offeredPlaylist.size(); i++) {
            if (offeredPlaylist.get(i).getName().equals(name)) {
                result = i;
            }
        }
        return result;
    }


    public String getFileName(String input) {
        String output = "playlist_" + input + ".m3u";
        output = output.replace(" ", "_");
        output = output.replace("(", "_");
        output = output.replace(")", "_");
        output = output.replace("@", "_");
        return output;
    }

    @Override
    public void addToActivePlaylist(String name, String url, int type) {
        String file = getFileName(name);
        activePlaylist.add(new Playlist(name, url, file, type));
        activePlaylistName.add(name);
    }

    @Override
    public void addToOfferedPlaylist(String name, String url, int type) {
        String file = getFileName(name);
        offeredPlaylist.add(new Playlist(name, url, file, type));
    }


}
