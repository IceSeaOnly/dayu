package site.binghai.shop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import site.binghai.shop.enums.BannerType;
import site.binghai.shop.kv.IndexHotTopicImgWall;
import site.binghai.shop.kv.PinTuanIndexImgWall;
import site.binghai.shop.service.BannerService;
import site.binghai.shop.service.KvService;
import site.binghai.shop.service.RecommendService;

/**
 * @date 2020/1/31 下午9:53
 **/
@Controller
@RequestMapping("shop")
public class IndexController {
    @Autowired
    private BannerService bannerService;
    @Autowired
    private RecommendService recommendService;
    @Autowired
    private KvService kvService;

    @GetMapping("index")
    public String index(ModelMap map) {
        map.put("banners", bannerService.findByType(BannerType.INDEX));
        map.put("recommends", recommendService.recommend(100));
        map.put("pinWall", kvService.get(PinTuanIndexImgWall.class));
        map.put("hot", kvService.get(IndexHotTopicImgWall.class));
        return "index";
    }
}
