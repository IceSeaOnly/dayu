package site.binghai.shop.service;

import org.springframework.stereotype.Service;
import site.binghai.lib.entity.WxUser;
import site.binghai.lib.service.BaseService;
import site.binghai.shop.entity.FootHistory;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @date 2020/2/3 下午12:26
 **/
@Service
public class FootHistoryService extends BaseService<FootHistory> {

    @Transactional
    public void footprint(Long userId, Long productId) {
        FootHistory f = new FootHistory();
        f.setBuyerId(userId);
        f.setProductId(productId);
        delete(f);
        getDao().flush();
        f = new FootHistory();
        f.setBuyerId(userId);
        f.setProductId(productId);
        save(f);
    }

    public long countByUserId(WxUser user) {
        FootHistory f = new FootHistory();
        f.setBuyerId(user.getId());
        return count(f);
    }

    public List<FootHistory> findByUser(Long id) {
        FootHistory f = new FootHistory();
        f.setBuyerId(id);
        return sortQuery(f, "id", true);
    }

    @Transactional
    public void deleteAll(Long userId) {
        FootHistory f = new FootHistory();
        f.setBuyerId(userId);
        delete(f);
    }
}
