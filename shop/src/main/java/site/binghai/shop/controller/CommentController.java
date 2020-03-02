package site.binghai.shop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import site.binghai.lib.controller.BaseController;
import site.binghai.lib.enums.OrderStatusEnum;
import site.binghai.lib.utils.TimeTools;
import site.binghai.shop.entity.Product;
import site.binghai.shop.entity.ProductComment;
import site.binghai.shop.entity.ShopOrder;
import site.binghai.shop.service.ProductCommentService;
import site.binghai.shop.service.ProductService;
import site.binghai.shop.service.ShopOrderService;

import java.util.List;
import java.util.Map;

/**
 * @date 2020/2/4 下午9:10
 **/
@RequestMapping("shop")
@Controller
public class CommentController extends BaseController {
    @Autowired
    private ShopOrderService shopOrderService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductCommentService productCommentService;

    @GetMapping("leaveComment")
    public String leaveComment(@RequestParam Long orderId, ModelMap map) {
        ShopOrder order = shopOrderService.findById(orderId);
        if (order == null || !order.getUserId().equals(getUser().getId())) {
            return e500("兄弟，如果你看到这个说明你来错地方了，坐下喝两杯冷静冷静");
        }

        if (order.getStatus() == OrderStatusEnum.COMPLETE.getCode()) {
            map.put("order", order);
            return "evaluation";
        } else {
            return e500("说出来你可能不信，该订单无需评价");
        }
    }

    @PostMapping("comment/{orderId}")
    @ResponseBody
    public Object comment(@RequestBody Map map, @PathVariable Long orderId) {
        ShopOrder order = shopOrderService.findById(orderId);
        if (order == null || !order.getUserId().equals(getUser().getId())) {
            return fail("兄弟不要乱来啊");
        }

        try {
            order.setStatus(OrderStatusEnum.FEED_DONE.getCode());
            Product product = productService.findById(order.getProductId());
            product.resetStarOfDesc(getDouble(map, "desc_star"));
            product.resetStarOfQuality(getDouble(map, "quality_star"));
            order.setStarOfService(getInteger(map, "service_star"));
            order.setStarOfShip(getInteger(map, "ship_star"));

            ProductComment pc = new ProductComment();
            pc.setProductId(product.getId());
            pc.setBuyerAvatarUrl(getUser().getAvatar());
            pc.setStandardInfo(order.getStandardInfo());
            pc.setBuyerName(getUser().getUserName());
            pc.setComment(getString(map, "comment"));
            pc.setDate(TimeTools.now());
            pc.setOrderId(orderId);
            productCommentService.save(pc);
            productService.update(product);
            shopOrderService.update(order);
        } catch (Exception e) {
            return fail("不要搞我啊兄弟，我就是个机器何必为难我");
        }
        return success();
    }

    @GetMapping("evaluateSuccess")
    public String evaluateSuccess() {
        return "evaluateSuccess";
    }

    @GetMapping("productComments")
    public String itemEvaluation(@RequestParam Long productId, ModelMap map) {
        List<ProductComment> comments = productCommentService.findByProductId(productId, 99);
        map.put("comments", comments);
        return "itemEvaluation";
    }
}
