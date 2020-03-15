package site.binghai.shop.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query(value = "select count(distinct (batch_id)) from shop_order where bind_rider = :R and status = :S",
        nativeQuery = true)
    Long countByRiderAndStatus(@Param("R") Long riderId, @Param("S") Integer status);

    @Query(
        value = "select count(distinct (batch_id)) from shop_order where bind_rider = :R and status = :S and created >= :B "
            + "and created <= :E",
        nativeQuery = true)
    Long countByRiderAndStatusAndTime(@Param("B") Long beigin, @Param("E") Long end, @Param("R") Long riderId,
                                      @Param("S") Integer status);

    @Query(
        value = "select count(distinct (batch_id)) from shop_order where status in :S and created >= :B "
            + "and created <= :E",
        nativeQuery = true)
    Long countByStatusAndTime(@Param("B") Long begin, @Param("E") Long end, @Param("S") List<Integer> status);
}
