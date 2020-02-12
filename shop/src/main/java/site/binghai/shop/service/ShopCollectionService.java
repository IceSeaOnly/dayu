package site.binghai.shop.service;

import org.springframework.stereotype.Service;
import site.binghai.lib.entity.WxUser;
import site.binghai.lib.service.BaseService;
import site.binghai.shop.entity.ShopCollection;

import javax.transaction.Transactional;

/**
 *
 * @date 2020/2/3 下午12:26
 **/
@Service
public class ShopCollectionService extends BaseService<ShopCollection> {

    @Transactional
    public void collection(Long userId, Long productId) {
        ShopCollection f = new ShopCollection();
        f.setBuyerId(userId);
        f.setProductId(productId);
        save(f);
    }

    @Transactional
    public void disCollection(Long userId, Long productId) {
        ShopCollection f = new ShopCollection();
        f.setBuyerId(userId);
        f.setProductId(productId);
        delete(f);
    }

    public boolean isCollected(Long userId, Long productId) {
        ShopCollection f = new ShopCollection();
        f.setBuyerId(userId);
        f.setProductId(productId);
        return queryOne(f) != null;
    }

    public long countByUserId(WxUser user) {
        ShopCollection f = new ShopCollection();
        f.setBuyerId(user.getId());
        return count(f);
    }
}
