package site.binghai.shop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import site.binghai.lib.controller.BaseController;
import site.binghai.lib.entity.WxUser;
import site.binghai.shop.entity.Product;
import site.binghai.shop.entity.ShopCollection;
import site.binghai.shop.service.ProductService;
import site.binghai.shop.service.ShopCollectionService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author icesea
 * @date 2020/3/5 下午10:28
 **/
@Controller
@RequestMapping("shop")
public class ShopCollectionController extends BaseController {
    @Autowired
    private ShopCollectionService shopCollectionService;
    @Autowired
    private ProductService productService;

    @GetMapping("collections")
    public String collections(ModelMap map) {
        WxUser user = getUser();
        List<ShopCollection> cs = shopCollectionService.findByUserId(user);
        List<Product> products = cs.stream().map(s -> productService.findById(s.getProductId()))
            .collect(Collectors.toList());
        map.put("products", products);
        return "collections";
    }
}
