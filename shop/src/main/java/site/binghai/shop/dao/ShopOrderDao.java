package site.binghai.shop.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import site.binghai.shop.entity.ShopOrder;

import java.util.List;

/**
 * @author huaishuo
 * @date 2020/3/2 下午9:33
 **/
public interface ShopOrderDao extends JpaRepository<ShopOrder, Long> {
    List<ShopOrder> findAllByStatusInAndCreatedBetween(List<Integer> status, Long start, Long end);

    List<ShopOrder> findAllByStatusAndBindRiderOrderByIdDesc(Integer status, Long rider, Pageable pageable);

    List<ShopOrder> findAllByBindRiderOrderByUpdatedDesc(Long rider, Pageable pageable);
}
