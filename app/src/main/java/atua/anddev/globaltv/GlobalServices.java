package atua.anddev.globaltv;

import atua.anddev.globaltv.service.ChannelService;
import atua.anddev.globaltv.service.ChannelServiceImpl;
import atua.anddev.globaltv.service.FavoriteService;
import atua.anddev.globaltv.service.FavoriteServiceImpl;
import atua.anddev.globaltv.service.GuideService;
import atua.anddev.globaltv.service.GuideServiceImpl;
import atua.anddev.globaltv.service.LogoService;
import atua.anddev.globaltv.service.LogoServiceImpl;
import atua.anddev.globaltv.service.PlaylistService;
import atua.anddev.globaltv.service.PlaylistServiceImpl;
import atua.anddev.globaltv.service.SearchService;
import atua.anddev.globaltv.service.SearchServiceImpl;

public interface GlobalServices {
    PlaylistService playlistService = new PlaylistServiceImpl();
    ChannelService channelService = new ChannelServiceImpl();
    FavoriteService favoriteService = new FavoriteServiceImpl();
    SearchService searchService = new SearchServiceImpl();
    GuideService guideService = new GuideServiceImpl();
    LogoService logoService = new LogoServiceImpl();
}
