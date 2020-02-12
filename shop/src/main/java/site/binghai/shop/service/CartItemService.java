package site.binghai.shop.service;

import org.springframework.stereotype.Service;
import site.binghai.lib.service.BaseService;
import site.binghai.shop.entity.CartItem;

import java.util.List;

/**
 *
 * @date 2020/2/2 下午12:55
 **/
@Service
public class CartItemService extends BaseService<CartItem> {
    public List<CartItem> findByUserId(Long userId) {
        CartItem item = new CartItem();
        item.setBuyerId(userId);
        item.setHidden(Boolean.FALSE);
        return query(item);
    }
}
