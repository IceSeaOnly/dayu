package site.binghai.shop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import site.binghai.lib.controller.BaseController;
import site.binghai.shop.entity.CartItem;
import site.binghai.shop.entity.Product;
import site.binghai.shop.service.CartItemService;
import site.binghai.shop.service.ProductService;
import site.binghai.shop.service.RecommendService;

import java.util.List;
import java.util.Map;

/**
 *
 * @date 2020/2/2 下午1:26
 **/
@Controller
@RequestMapping("shop")
public class CartController extends BaseController {
    @Autowired
    private CartItemService cartItemService;
    @Autowired
    private ProductService productService;
    @Autowired
    private RecommendService recommendService;

    @GetMapping("cart")
    public String cart(ModelMap map) {
        List<CartItem> all = cartItemService.findByUserId(getUser().getId());

        if (isEmptyList(all)) {
            map.put("products", recommendService.recommend(10));
            return "emptyCart";
        }

        List<CartItem> carts = emptyList();
        List<CartItem> invalidCarts = emptyList();
        enrichCarts(all, carts, invalidCarts);
        int total = carts.stream().map(c -> c.getSize() * c.getProduct().getPrice()).reduce(Integer::sum).get();
        map.put("totalFee", total);
        map.put("carts", carts);
        map.put("invalidCarts", invalidCarts);

        return "cart";
    }

    private void enrichCarts(List<CartItem> all, List<CartItem> carts, List<CartItem> invalid) {
        for (CartItem cart : all) {
            Product product = productService.findById(cart.getProductId());
            if (product == null) {
                continue;
            }
            cart.setProduct(product);
            if (product.getOffline()) {
                cart.setInvalidReason("已下架");
                invalid.add(cart);
            } else if (product.getStock() < cart.getSize()) {
                cart.setInvalidReason("库存不足");
                invalid.add(cart);
            } else {
                cart.setProduct(product);
                carts.add(cart);
            }
        }
    }

    @ResponseBody
    @GetMapping("cartResize")
    public Object cartResize(@RequestParam Long cartId, @RequestParam Integer size) {
        CartItem item = cartItemService.findById(cartId);
        if (item == null || !item.getBuyerId().equals(getUser().getId())) {
            return fail("非法请求");
        }
        Product product = productService.findById(item.getProductId());
        if (item.getSize() + size > product.getStock()) {
            return fail("库存不足");
        }
        item.setSize(item.getSize() + size);
        cartItemService.update(item);
        return success(item.getSize(), null);
    }

    @ResponseBody
    @GetMapping("cartCal")
    public Object cartCal(@RequestParam String ids) {
        if (ids.contains("ALL")) {
            return calAllCart();
        }
        int sum = 0;
        for (String id : ids.split(",")) {
            Long cartId = Long.parseLong(id);
            CartItem item = cartItemService.findById(cartId);
            if (item == null || !item.getBuyerId().equals(getUser().getId())) {
                return fail("非法请求");
            }
            Product product = productService.findById(item.getProductId());
            sum += product.getPrice() * item.getSize();
        }
        return success(sum, null);

    }

    private Object calAllCart() {
        int sum = 0;
        List<CartItem> all = cartItemService.findByUserId(getUser().getId());
        List<CartItem> carts = emptyList();
        List<CartItem> invalidCarts = emptyList();

        enrichCarts(all, carts, invalidCarts);
        for (CartItem item : carts) {
            Product product = productService.findById(item.getProductId());
            sum += product.getPrice() * item.getSize();
        }
        return success(sum, null);
    }

    @GetMapping("deleteCart")
    public String deleteCart(@RequestParam Long cartId) {
        CartItem item = cartItemService.findById(cartId);
        if (item == null || !item.getBuyerId().equals(getUser().getId())) {
            return "404";
        }

        cartItemService.delete(cartId);
        return "redirect:cart";
    }

    @ResponseBody
    @PostMapping("addCart/{productId}/{size}")
    public Object addCart(@RequestBody Map body, @PathVariable Long productId, @PathVariable Integer size) {
        Product product = productService.findById(productId);
        if (product == null || product.getOffline()) {
            return fail("商品已下架或不存在");
        }
        if (product.getStock() < size) {
            return fail("库存不足:" + product.getStock());
        }
        CartItem cart = new CartItem();
        if (body.containsKey("hidden")) {
            body.remove("hidden");
            cart.setHidden(Boolean.TRUE);
        } else {
            cart.setHidden(Boolean.FALSE);
        }
        cart.setBuyerId(getUser().getId());
        cart.setProductId(productId);
        cart.setSize(size);
        cart.setStandardInfo(extraStandardInfo(body));
        cartItemService.save(cart);
        return success(cart, null);
    }

    private String extraStandardInfo(Map body) {
        StringBuilder sb = new StringBuilder();
        body.forEach((k, v) -> {
            sb.append(k + ":" + v + " ");
        });
        return sb.toString();
    }

}
