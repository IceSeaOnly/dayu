package site.binghai.biz.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import site.binghai.biz.consts.DiamondKey;
import site.binghai.biz.service.DiamondService;
import site.binghai.biz.utils.DecodeUtil;
import site.binghai.lib.controller.BaseController;
import site.binghai.lib.entity.WxUser;
import site.binghai.lib.service.WxUserService;
import site.binghai.lib.utils.GroovyEngineUtils;
import site.binghai.lib.utils.UrlUtil;

import java.util.Map;

//@RequestMapping("/user/view/")
//@RestController
public class UserViewController extends BaseController {
    @Autowired
    private DiamondService diamondService;
    @Autowired
    private DecodeUtil decodeUtil;
    @Autowired
    private WxUserService wxUserService;

    @RequestMapping("/page/{viewId}")
    public Object page(String nocache, @PathVariable String viewId) {
        String tpl = diamondService.get(viewId, nocache != null);
        if (hasEmptyString(tpl)) {
            return fail("no such page error!");
        }

        if (viewId.startsWith("dy")) {
            Map ctx = toJsonObject(getUser());
            ctx.putAll(UrlUtil.getRequestParams(getServletRequest()));
            decodeUtil.urlDecode(ctx);
            try {
                return GroovyEngineUtils.renderTemplate(String.valueOf(tpl), ctx);
            } catch (Exception e) {
                logger.error("render page error! viewId:{},ctx:{}", viewId, ctx, e);
                return diamondService.get(DiamondKey.ERROR_PAGE);
            }
        }

        return tpl;
    }

    @GetMapping("userInfo")
    public Object userInfo() {
        return success(getUser(), null);
    }

    @PostMapping("updateUserInfo")
    public Object updateUserInfo(@RequestBody Map map) {
        String userName = getString(map, "userName");
        String userPhone = getString(map, "userPhone");
        String userAddress = getString(map, "userAddress");

        if (hasEmptyString(userAddress, userName, userPhone)) {
            return fail("请填写完整");
        }

        WxUser user = wxUserService.findById(getUser().getId());
        user.setPhone(userPhone);
        user.setUserName(userName);
        user.setAddress(userAddress);
        wxUserService.update(user);
        return success();
    }
}
