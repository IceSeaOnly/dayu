package site.binghai.shop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import site.binghai.lib.controller.BaseController;
import site.binghai.shop.entity.Product;
import site.binghai.shop.service.ProductService;

import java.util.List;

/**
 *
 * @date 2020/2/1 上午11:02
 **/
@Controller
@RequestMapping("shop")
public class ProductController extends BaseController {

    @Autowired
    private ProductService productService;

    @GetMapping("searchProducts")
    public String searchProducts(ModelMap map, @RequestParam String search, String sort,
                                 String filter) {
        List<Product> products = productService.searchBy(search);

        if (!hasEmptyString(sort)) {
            sortBy(sort, products);
        }
        map.put("products", products);
        map.put("href", "searchProducts?search=" + search);
        map.put("sort", sort);
        map.put("search", search);
        return "itemList";
    }

    @GetMapping("productCategoryList")
    public String productCategoryList(ModelMap map, @RequestParam Long category, String sort,
                                      String filter) {
        List<Product> products = productService.searchByCategory(category);

        if (!hasEmptyString(sort)) {
            sortBy(sort, products);
        }

        map.put("products", products);
        map.put("href", "productCategoryList?category=" + category);
        map.put("sort", sort);
        map.put("search", "");
        return "itemList";
    }

    private void sortBy(String sort, List<Product> products) {
        if ("priceUp".equals(sort)) {
            products.sort((a, b) -> a.getPrice() < b.getPrice() ? -1 : 1);
        } else if ("priceDown".equals(sort)) {
            products.sort((a, b) -> a.getPrice() > b.getPrice() ? -1 : 1);
        } else if ("sold".equals(sort)) {
            products.sort((a, b) -> a.getSold() > b.getSold() ? -1 : 1);
        }
    }

}
