package site.binghai.lib.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import site.binghai.lib.config.IceConfig;
import site.binghai.lib.entity.WxInfo;
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
    public WxUser newUser(String openId, WxInfo info) {
        WxUser wxUser = new WxUser();
        wxUser.setOpenId(openId);
        wxUser.setCity(info.getCity());
        wxUser.setProvince(info.getProvince());
        wxUser.setCountry(info.getCountry());
        wxUser.setUserName(info.getNickname());
        wxUser.setGender(getGender(info));
        wxUser.setAvatar(getAvatar(info));
        wxUser.setSubscribeTime(info.getSubscribe_time());
        wxUser.setSubscribeScene(info.getSubscribe_scene());
        return save(wxUser);
    }

    private String getAvatar(WxInfo info) {
        if (hasEmptyString(info.getHeadimgurl())) {
            return iceConfig.getDefaultAvatarUrl();
        }
        return info.getHeadimgurl();
    }

    private String getGender(WxInfo info) {
        if (hasEmptyString(info.getSex())) {
            return "未知";
        }
        switch (info.getSex()) {
            case "1":
                return "男";
            case "2":
                return "女";
            default:
                return "未知";
        }
    }

    public List<WxUser> search(String search) {
        return null;
    }

}
