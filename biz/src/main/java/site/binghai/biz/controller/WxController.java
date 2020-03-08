package site.binghai.biz.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import site.binghai.lib.config.IceConfig;
import site.binghai.lib.controller.BaseController;
import site.binghai.lib.entity.SessionDataBundle;
import site.binghai.lib.entity.WxInfo;
import site.binghai.lib.entity.WxUser;
import site.binghai.lib.service.WxCommonService;
import site.binghai.lib.service.WxUserService;
import site.binghai.lib.utils.MD5;
import site.binghai.lib.utils.SchoolIdThreadLocal;

@Controller
@RequestMapping("/wx/")
public class WxController extends BaseController {
    @Autowired
    private IceConfig iceConfig;
    @Autowired
    private WxUserService wxUserService;
    @Autowired
    private WxCommonService wxCommonService;

    @GetMapping("wxLogin")
    public String wxLogin(@RequestParam String openId, @RequestParam String validate, String backUrl) {

        if (backUrl != null) {
            setString2Session(SessionDataBundle.BACK_URL, backUrl);
            return "redirect:" + iceConfig.getWxAuthenticationUrl() + "?backUrl=" + iceConfig.getAppRoot()
                + "/wx/wxLogin";
        }

        if (!MD5.encryption(openId + iceConfig.getWxValidateMD5Key()).equals(validate)) {
            return "redirect:" + iceConfig.getWxAuthenticationUrl();
        }

        WxUser wxUser = wxUserService.findByOpenId(openId);
        if (wxUser == null) {
            WxInfo info = wxCommonService.getUserInfo(openId);
            //if (info == null || !info.isSubscribed()) {
            //    return "redirect:" + iceConfig.getSubscribePage();
            //}
            wxUser = wxUserService.newUser(openId, info);
        }

        persistent(wxUser);

        backUrl = getStringFromSession(SessionDataBundle.BACK_URL);

        if (needCompleteInfo(wxUser)) {
            return "redirect:/shop/myInfo";
        }
        SchoolIdThreadLocal.setSchoolId(wxUser.getSchoolId());
        return backUrl == null ? "redirect:/" : "redirect:" + backUrl;
    }

    private boolean needCompleteInfo(WxUser wxUser) {
        if (hasEmptyString(wxUser.getUserName(), wxUser.getPhone(), wxUser.getSchoolId())) {
            return true;
        }
        return false;
    }
}
