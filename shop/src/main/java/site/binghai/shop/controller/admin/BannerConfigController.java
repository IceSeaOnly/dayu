package site.binghai.shop.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import site.binghai.lib.controller.BaseController;
import site.binghai.shop.entity.Banner;
import site.binghai.shop.enums.BannerType;
import site.binghai.shop.service.BannerService;

import java.util.Map;

/**
 * @author icesea
 * @date 2020/2/28 下午9:31
 **/
@RequestMapping("manage")
@Controller
public class BannerConfigController extends BaseController {
    @Autowired
    private BannerService bannerService;

    @GetMapping("bannerConfig")
    public String pageConfig(ModelMap map, Long update) {
        map.put("banners", bannerService.listAll());
        map.put("types", BannerType.values());
        map.put("msg", update != null ? "更新成功" : null);
        return "manage/bannerConfig";
    }

    @GetMapping("deleteBanner")
    public String deleteBanner(@RequestParam Long id) {
        bannerService.delete(id);
        return "redirect:bannerConfig";
    }

    @PostMapping("addBanner")
    @ResponseBody
    public Object addBanner(@RequestBody Map map) {
        Banner banner = toJsonObject(map).toJavaObject(Banner.class);
        banner.setId(null);
        if (hasEmptyString(banner.getTitle(), banner.getImgUrl(), banner.getTarget())) {
            return fail("填写不完整!");
        }

        bannerService.save(banner);
        return success();
    }

    @PostMapping("ajaxUpdateBannerConfig")
    @ResponseBody
    public Object ajaxUpdateBannerConfig(@RequestBody Map map) {
        Banner banner = toJsonObject(map).toJavaObject(Banner.class);
        if (hasEmptyString(banner.getTitle(), banner.getImgUrl(), banner.getTarget())) {
            return fail("填写不完整!");
        }
        bannerService.update(banner);
        return success();
    }
}
