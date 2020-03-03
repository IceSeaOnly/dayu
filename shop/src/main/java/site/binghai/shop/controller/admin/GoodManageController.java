package site.binghai.shop.controller.admin;

import com.alibaba.fastjson.JSONArray;
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
import site.binghai.shop.pojo.StandardObj;
import site.binghai.shop.service.ProductDetailService;
import site.binghai.shop.service.ProductService;
import site.binghai.shop.service.ShopCategoryService;

import java.util.ArrayList;
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
    public String goodsManage(Long categoryId, ModelMap map, Integer page, Boolean offline) {
        page = page == null ? 0 : page;
        page = Math.max(0, page);

        Product exp = new Product();
        exp.setCategoryId(categoryId);
        exp.setOffline(offline);
        List<Product> ps = productService.pageQuery(exp, page, 100);

        map.put("offline", String.valueOf(offline));
        map.put("page", page);
        map.put("products", ps);
        map.put("category", categoryId == null ? null : shopCategoryService.findById(categoryId));
        map.put("categoryId", categoryId == null ? "" : categoryId);
        return "manage/goodsManage";
    }

    @GetMapping("addGoods")
    public String addGoods(ModelMap map, Long categoryId) {
        Map<ShopCategory, List<ShopCategory>> list = shopCategoryService.listAll();
        Map<String, Long> options = new LinkedHashMap<>();
        list.forEach((k, v) -> {
            v.forEach(o -> options.put(k.getTitle() + "/" + o.getTitle(), o.getId()));
        });
        map.put("options", options);
        map.put("ts", now());
        map.put("categoryId", categoryId);
        return "manage/addGoods";
    }

    @GetMapping("editGoods")
    public String editGoods(@RequestParam Long id, ModelMap map) {
        Map<ShopCategory, List<ShopCategory>> list = shopCategoryService.listAll();
        Map<String, Long> options = new LinkedHashMap<>();
        list.forEach((k, v) -> {
            if (!isEmptyList(v)) {
                v.forEach(o -> options.put(k.getTitle() + "/" + o.getTitle(), o.getId()));
            }
        });

        Product product = productService.findById(id);
        map.put("options", options);
        map.put("standards", parseStandards(product.getStandards()));
        JSONObject infos = JSONObject.parseObject(product.getInfos());
        Object images = infos.remove("images");
        map.put("images", images == null ? emptyList() : images);
        map.put("blankImages", generateBlank(images));
        map.put("infos", infos);
        map.put("product", product);
        map.put("ptStartTs", product.getPtStartTs() == null ? "" : TimeTools.format(product.getPtStartTs()));
        map.put("ptEndTs", product.getPtEndTs() == null ? "" : TimeTools.format(product.getPtEndTs()));
        map.put("detail", productDetailService.findByProductId(id));
        return "manage/editGoods";
    }

    private Integer[] generateBlank(Object images) {
        int size = images == null ? 4 : 4 - ((JSONArray)images).size();
        return new Integer[size];
    }

    @PostMapping("deleteStandardInfo")
    @ResponseBody
    public Object deleteStandardInfo(@RequestParam Long productId, @RequestParam String key,
                                     String option) {

        Product product = productService.findById(productId);
        JSONObject std = JSONObject.parseObject(product.getStandards());
        if (hasEmptyString(option)) {
            std.remove(key);
        } else {
            JSONArray arr = std.getJSONArray(key);
            arr.remove(option);
            std.put(key, arr);
        }
        product.setStandards(std.toJSONString());
        productService.update(product);
        return success();
    }

    @PostMapping("addStandardInfo")
    @ResponseBody
    public Object addStandardInfo(@RequestParam Long productId, @RequestParam String key,
                                  String option) {

        Product product = productService.findById(productId);
        JSONObject std = JSONObject.parseObject(product.getStandards());
        JSONArray arr = (JSONArray)std.getOrDefault(key, new JSONArray());
        if (!hasEmptyString(option)) {
            arr.add(option);
        }
        std.put(key, arr);
        product.setStandards(std.toJSONString());
        productService.update(product);
        return success();
    }

    @PostMapping("addInfo")
    @ResponseBody
    public Object addInfo(@RequestParam Long productId, @RequestParam String info) {
        if (!info.contains(":")) {
            return fail("输入有误，请以英文冒号分隔键值");
        }
        String[] ps = info.split(":");
        if (ps.length < 2) {
            return fail("务必输入键值,请以英文冒号分隔");
        }
        Product product = productService.findById(productId);
        JSONObject infos = JSONObject.parseObject(product.getInfos());
        infos.put(ps[0], ps[1]);
        product.setInfos(infos.toJSONString());
        productService.update(product);
        return success();
    }

    @PostMapping("deleteInfo")
    @ResponseBody
    public Object deleteInfo(@RequestParam Long productId, @RequestParam String key) {
        Product product = productService.findById(productId);
        JSONObject infos = JSONObject.parseObject(product.getInfos());
        infos.remove(key);
        product.setInfos(infos.toJSONString());
        productService.update(product);
        return success();
    }

    private List<StandardObj> parseStandards(String standards) {
        List<StandardObj> ret = new ArrayList<>();
        JSONObject object = JSONObject.parseObject(standards);
        for (String k : object.keySet()) {
            StandardObj obj = new StandardObj();
            obj.setName(k);
            obj.setOptions(object.getJSONArray(k).toJavaList(String.class));
            ret.add(obj);
        }
        return ret;
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
        return success(product, null);
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
    @ResponseBody
    public Object changeOffline(@RequestParam Long id) {
        Product product = productService.findById(id);
        product.setOffline(!product.getOffline());
        productService.update(product);
        return success();
    }

    @GetMapping("recommendProduct")
    @ResponseBody
    public Object recommendProduct(@RequestParam Long id) {
        Product product = productService.findById(id);
        product.setRecommend(product.getRecommend() == null ? Boolean.TRUE : !product.getRecommend());
        productService.update(product);
        return success();
    }

    @GetMapping("removeItem")
    @ResponseBody
    public Object removeItem(@RequestParam Long id) {
        Product product = productService.findById(id);
        product.setOffline(true);
        product.setCategoryId(1000L);
        productService.update(product);
        return success();
    }
}
