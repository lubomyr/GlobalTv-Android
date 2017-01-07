package atua.anddev.globaltv.service;

import java.util.ArrayList;
import java.util.List;

import atua.anddev.globaltv.entity.Logo;

public interface LogoService {
    List<Logo> logoList = new ArrayList<>();

    void setupLogos();

    String getLogoByName(String str);
}
