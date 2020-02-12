package site.binghai.shop.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import site.binghai.lib.controller.BaseController;
import site.binghai.shop.entity.Product;
import site.binghai.shop.entity.ProductComment;
import site.binghai.shop.entity.ProductDetail;
import site.binghai.shop.service.*;

import java.util.List;

/**
 *
 * @date 2020/2/1 下午3:30
 **/
@Controller
@RequestMapping("shop")
public class DetailController extends BaseController {
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductDetailService productDetailService;
    @Autowired
    private ProductCommentService productCommentService;
    @Autowired
    private RecommendService recommendService;
    @Autowired
    private ShopCollectionService shopCollectionService;
    @Autowired
    private FootHistoryService footHistoryService;

    @GetMapping("detail")
    public String detail(ModelMap map, @RequestParam Long productId) {
        Product product = productService.findById(productId);

        if (product == null || product.getOffline()) {
            return "redirect:404";
        }

        footHistoryService.footprint(getUser().getId(), productId);

        ProductDetail detail = productDetailService.findByProductId(productId);
        detail.setProduct(product);

        makeStandards(detail);
        parseInfos(detail);

        List<ProductComment> comments = productCommentService.findByProductId(productId, 1);
        detail.setComments(comments);

        long commentCnt = productCommentService.countByProductId(productId);

        map.put("recommends", recommendService.recommend(4));
        map.put("commentCnt", commentCnt);
        map.put("detail", detail);
        //map.put("couponUrl", "searcher");
        map.put("collected", shopCollectionService.isCollected(getUser().getId(), productId));
        return "detail";
    }

    @GetMapping("collect")
    public String collect(@RequestParam Long pid, @RequestParam Integer type) {
        shopCollectionService.disCollection(getUser().getId(), pid);
        if (type == 1) {
            shopCollectionService.collection(getUser().getId(), pid);
        }
        return "redirect:detail?productId=" + pid;
    }

    private void parseInfos(ProductDetail detail) {
        detail.setInfos(JSONObject.parseObject(detail.getProduct().getInfos()));
    }

    private void makeStandards(ProductDetail detail) {
        detail.setStandards(JSONObject.parseObject(detail.getProduct().getStandards()));
        JSONObject form = new JSONObject();
        for (String key : detail.getStandards().keySet()) {
            form.put(key, "");
        }
        detail.setSubmitForm(form.toJSONString());
    }
}
