package site.binghai.biz.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.binghai.biz.entity.SharePage;
import site.binghai.biz.service.SharePageService;
import site.binghai.lib.controller.BaseController;
import site.binghai.lib.entity.WxUser;

/**
 * @author huaishuo
 * @date 2018/12/9 下午3:33
 **/
@RestController
@RequestMapping("/user/sharepage/")
public class SharePageController extends BaseController {
    @Autowired
    private SharePageService sharePageService;

    @GetMapping("look")
    public Object look(@RequestParam Long pid) {
        WxUser user = getUser();
        SharePage page = sharePageService.findById(pid);
        if (!page.getValid()) {
            return fail("页面已过期");
        }

        if (page.getInvalidTs() < now()) {
            sharePageService.invalid(pid);
            return fail("分享已过期");
        }

        JSONObject ret = new JSONObject();
        ret.put("isSelf", user.getOpenId().equals(page.getProducerOpenId()));
        ret.put("page", page);
        return success(ret, null);
    }
}
