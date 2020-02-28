package site.binghai.shop.controller.admin;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import site.binghai.lib.controller.BaseController;
import site.binghai.lib.utils.TimeTools;
import site.binghai.shop.entity.Product;
import site.binghai.shop.entity.ProductDetail;
import site.binghai.shop.entity.ShopCategory;
import site.binghai.shop.service.ProductDetailService;
import site.binghai.shop.service.ProductService;
import site.binghai.shop.service.ShopCategoryService;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author huaishuo
 * @date 2020/2/28 下午10:40
 **/
@RequestMapping("manage")
@Controller
public class GoodManageController extends BaseController {

    @Autowired
    private ShopCategoryService shopCategoryService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductDetailService productDetailService;

    @GetMapping("goodsManage")
    public String goodsManage(Long categoryId, ModelMap map, Integer page) {
        page = page == null ? 0 : page;
        List<Product> ps = null;
        if (categoryId != null) {
            ps = productService.searchByCategory(categoryId);
        } else {
            ps = productService.findAll(page, 100);
        }
        map.put("products", ps);
        return "manage/goodsManage";
    }

    @GetMapping("addGoods")
    public String addGoods(ModelMap map) {
        Map<ShopCategory, List<ShopCategory>> list = shopCategoryService.listAll();
        Map<String, Long> options = new LinkedHashMap<>();
        list.forEach((k, v) -> {
            v.forEach(o -> options.put(k.getTitle() + "/" + o.getTitle(), o.getId()));
        });
        map.put("options", options);
        return "manage/addGoods";
    }

    @GetMapping("editGoods")
    public String editGoods(@RequestParam Long id, ModelMap map) {
        Map<ShopCategory, List<ShopCategory>> list = shopCategoryService.listAll();
        Map<String, Long> options = new LinkedHashMap<>();
        list.forEach((k, v) -> {
            v.forEach(o -> options.put(k.getTitle() + "/" + o.getTitle(), o.getId()));
        });
        map.put("options", options);
        map.put("product", productService.findById(id));
        map.put("detail", productDetailService.findByProductId(id));
        return "manage/editGoods";
    }

    @PostMapping("ajaxAddGoods")
    @ResponseBody
    public Object ajaxAddGoods(@RequestBody Map map) {
        if (!hasEmptyString(map.get("ptSize"))) {
            map.put("ptStartTs", TimeTools.dataTime2Timestamp(getString(map, "ptStartTs")));
            map.put("ptEndTs", TimeTools.dataTime2Timestamp(getString(map, "ptEndTs")));
        }
        map.put("offline", Boolean.FALSE);
        if (!isJSON(getString(map, "infos"))) {
            return fail("详细参数不是标准json!");
        }
        if (!isJSON(getString(map, "standards"))) {
            return fail("规格字段不是标准json!");
        }
        map.remove("id");
        JSONObject obj = new JSONObject();
        obj.putAll(map);
        Product product = obj.toJavaObject(Product.class);
        if (hasEmptyString(
            product.getBrand(),
            product.getCategoryId(),
            product.getImgUrl(),
            product.getPreviousPrice(),
            product.getPrice(),
            product.getProductNo(),
            product.getSimpleDesc(),
            product.getStock(),
            product.getTitle(),
            product.getTags()
        )) {
            return fail("填写不完整!");
        }
        product = productService.save(product);
        ProductDetail detail = new ProductDetail();
        detail.setHtml(getString(map, "detail"));
        detail.setProductId(product.getId());
        productDetailService.save(detail);
        return success();
    }

    @PostMapping("ajaxEditGoods")
    @ResponseBody
    public Object ajaxEditGoods(@RequestBody Map map) {
        if (!hasEmptyString(map.get("ptSize"))) {
            map.put("ptStartTs", TimeTools.dataTime2Timestamp(getString(map, "ptStartTs")));
            map.put("ptEndTs", TimeTools.dataTime2Timestamp(getString(map, "ptEndTs")));
        }

        if (!isJSON(getString(map, "infos"))) {
            return fail("详细参数不是标准json!");
        }
        if (!isJSON(getString(map, "standards"))) {
            return fail("规格字段不是标准json!");
        }
        Long id = getLong(map, "id");
        Product product = productService.findById(id);
        JSONObject obj = toJsonObject(product);
        obj.putAll(map);

        product = obj.toJavaObject(Product.class);
        if (hasEmptyString(
            product.getBrand(),
            product.getCategoryId(),
            product.getImgUrl(),
            product.getPreviousPrice(),
            product.getPrice(),
            product.getProductNo(),
            product.getSimpleDesc(),
            product.getStock(),
            product.getTitle(),
            product.getTags()
        )) {
            return fail("填写不完整!");
        }

        productService.update(product);
        ProductDetail detail = productDetailService.findByProductId(id);
        detail.setHtml(getString(map, "detail"));
        productDetailService.update(detail);
        return success();
    }

    @GetMapping("changeOffline")
    public String changeOffline(@RequestParam Long id) {
        Product product = productService.findById(id);
        product.setOffline(!product.getOffline());
        productService.update(product);
        return "redirect:goodsManage";
    }
}
