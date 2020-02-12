package site.binghai.shop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import site.binghai.lib.controller.BaseController;
import site.binghai.lib.utils.TimeTools;
import site.binghai.shop.service.ShopCategoryService;

/**
 *
 * @date 2020/2/1 上午8:58
 **/
@Controller
@RequestMapping("shop")
public class CategoryController extends BaseController {

    @Autowired
    private ShopCategoryService shopCategoryService;

    @GetMapping("category")
    public String category(ModelMap map) {
        map.put("searchPlaceholder", TimeTools.now());
        map.put("ad","http://cdn.binghai.site/o_1dvv48jd01cp174r1li91q3jfkha.jpg");
        map.put("categories",shopCategoryService.list());
        map.put("hotCategories",shopCategoryService.hotList());
        return "category";
    }
}
