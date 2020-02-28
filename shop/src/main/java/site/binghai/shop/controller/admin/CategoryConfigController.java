package site.binghai.shop.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import site.binghai.lib.controller.BaseController;
import site.binghai.shop.entity.ShopCategory;
import site.binghai.shop.service.ShopCategoryService;

/**
 * @author huaishuo
 * @date 2020/2/28 下午1:42
 **/
@Controller
@RequestMapping("manage")
public class CategoryConfigController extends BaseController {
    @Autowired
    private ShopCategoryService shopCategoryService;

    @GetMapping("categoryConfig")
    public String categoryConfig(ModelMap map) {
        map.put("categories", shopCategoryService.listAll());
        return "manage/categoryConfig";
    }

    @GetMapping("addCategory")
    public String addCategory(Long superId, ModelMap map) {
        if (superId != null) {
            ShopCategory category = shopCategoryService.findById(superId);
            map.put("sc", category);
        }
        return "manage/category";
    }

    @GetMapping("editCategory")
    public String editCategory(@RequestParam Long catId, ModelMap map) {
        ShopCategory category = shopCategoryService.findById(catId);
        map.put("sc", category);
        return "manage/editCategory";
    }

    @ResponseBody
    @PostMapping("ajaxAddCategory")
    public Object ajaxAddCategory(@RequestParam Long superId, @RequestParam String title, String url) {
        if (superId < 0 && hasEmptyString(url)) {
            return fail("图片必传!");
        }

        ShopCategory category = new ShopCategory();
        category.setHidden(Boolean.FALSE);
        category.setImgUrl(url);
        category.setSuperCategory(superId < 0);
        category.setSuperId(superId > 0 ? superId : null);
        category.setTitle(title);
        shopCategoryService.save(category);
        return success();
    }

    @ResponseBody
    @PostMapping("ajaxUpdateCategory")
    public Object ajaxUpdateCategory(@RequestParam Long catId, @RequestParam String title, String url) {
        ShopCategory category = shopCategoryService.findById(catId);
        category.setHidden(Boolean.FALSE);
        category.setTitle(title);
        if (url != null) {
            category.setImgUrl(url);
        }
        shopCategoryService.update(category);
        return success();
    }

    @GetMapping("showChange")
    public String showChange(@RequestParam Long cid) {
        ShopCategory category = shopCategoryService.findById(cid);
        category.setHidden(!category.getHidden());
        shopCategoryService.update(category);
        return "redirect:categoryConfig";
    }
}
