package site.binghai.lib.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import site.binghai.lib.config.IceConfig;
import site.binghai.lib.entity.WxUser;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class WxUserService extends BaseService<WxUser> {
    @Autowired
    private IceConfig iceConfig;

    public WxUser findByOpenId(String openId) {
        WxUser wxUser = new WxUser();
        wxUser.setOpenId(openId);
        return queryOne(wxUser);
    }

    @Transactional
    public WxUser newUser(String openId) {
        WxUser wxUser = new WxUser();
        wxUser.setOpenId(openId);
        wxUser.setAvatar(iceConfig.getDefaultAvatarUrl());
        return save(wxUser);
    }

    public List<WxUser> search(String search) {
        return null;
    }

}
