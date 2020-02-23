package site.binghai.shop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import site.binghai.lib.controller.BaseController;
import site.binghai.shop.entity.CartItem;
import site.binghai.shop.entity.Product;
import site.binghai.shop.entity.Tuan;
import site.binghai.shop.enums.TuanStatus;
import site.binghai.shop.kv.PinIndexBanners;
import site.binghai.shop.kv.PinTodayRecommend;
import site.binghai.shop.pojo.TuanFollower;
import site.binghai.shop.service.KvService;
import site.binghai.shop.service.ProductDetailService;
import site.binghai.shop.service.ProductService;
import site.binghai.shop.service.TuanService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @date 2020/2/12 下午3:49
 **/
@RequestMapping("shop")
@Controller
public class PinTuanController extends BaseController {
    @Autowired
    private KvService kvService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductDetailService productDetailService;
    @Autowired
    private TuanService tuanService;
    @Autowired
    private CartController cartController;

    @GetMapping("ptIndex")
    public String ptIndex(ModelMap map) {
        map.put("banners", kvService.get(PinIndexBanners.class).getImgs());
        List<Long> items = kvService.get(PinTodayRecommend.class).getItemIds();
        List<Product> recommends = productService.findByIds(items);
        map.put("recommends", recommends);
        List<Product> products = productService.ptSearch();
        map.put("products", products);
        return "ptIndex";
    }

    @GetMapping("ptDetail")
    public String ptDetail(@RequestParam Long item, ModelMap map) {
        Product product = productService.findById(item);
        if (product == null || product.getPtSize() == null) {
            return e500("您来错地方了");
        }

        map.put("product", product);
        map.put("images", getImages(product));
        map.put("endTs", product.getPtEndTs() / 1000);
        map.put("detail", productDetailService.findByProductId(item));
        map.put("joins", tuanService.searchByProductIdAndStatus(item, TuanStatus.INIT, 3));
        return "ptDetail";
    }

    @GetMapping("tuanDetail")
    public String tuanDetail(@RequestParam Long t, ModelMap map) {
        Tuan tuan = tuanService.findByTuanId(t);
        if (tuan == null) {
            return e500("您来错地方了");
        }
        Product product = productService.findById(tuan.getProductId());
        map.put("follower", JSONObject.parseArray(tuan.getFollower(), TuanFollower.class));
        map.put("tuan", tuan);
        map.put("product", product);
        map.put("endTs", (tuan.getEndTs() - now()) / 1000);
        map.put("waits",waits(tuan.getTotalSize()-tuan.getCurrentSize()));
        List<Product> products = productService.ptSearch();
        map.put("products", products);
        return "ptInvitation";
    }

    private List<Integer> waits(int i) {
        List<Integer> ret =new ArrayList<>();
        for (int j = 0; j < i; j++) {
            ret.add(j);
        }
        return ret;
    }

    private List<String> getImages(Product product) {
        JSONObject info = JSON.parseObject(product.getInfos());
        return info.getJSONArray("images") == null ? Arrays.asList(product.getImgUrl()) :
            info.getJSONArray("images").toJavaList(String.class);
    }

    @GetMapping("createTuan")
    public String createTuan(@RequestParam Long productId) {
        Product product = productService.findById(productId);
        if (product == null || product.getPtSize() == null) {
            return e500("您来错地方了");
        }
        if (product == null || product.getOffline()) {
            return e500("商品已下架或不存在");
        }
        if (product.getStock() < 1) {
            return e500("库存不足:" + product.getStock());
        }
        CartItem cartItem = cartController.doAddCart("拼团商品", productId, 1, true, true, null);
        return "redirect:buyConfirm?cartIds=" + cartItem.getId() + "&selectedCoupon=-1&addressId=-1";
    }

    @GetMapping("joinTuan")
    public String joinTuan(@RequestParam Long tuanId) {
        Tuan tuan = tuanService.findById(tuanId);
        if (tuan == null) {
            return e500("您来错地方了");
        }
        if (tuan.getStatus() != TuanStatus.INIT) {
            return e500("团已经满了，换个团吧!");
        }
        Product product = productService.findById(tuan.getProductId());
        if (product.getStock() < 1) {
            return e500("库存不足:" + product.getStock());
        }
        CartItem cartItem = cartController.doAddCart("{}", product.getId(), 1, true, true, tuanId);
        return "redirect:buyConfirm?cartIds=" + cartItem.getId() + "&selectedCoupon=-1&addressId=-1";
    }
}
