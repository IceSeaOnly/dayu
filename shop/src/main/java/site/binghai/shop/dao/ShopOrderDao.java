package site.binghai.shop.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.binghai.shop.entity.ShopOrder;

import java.util.List;

/**
 * @author icesea
 * @date 2020/3/2 下午9:33
 **/
public interface ShopOrderDao extends JpaRepository<ShopOrder, Long> {

    List<ShopOrder> findAllBySchoolIdAndBuyerNameLikeOrReceiverLikeOrReceiverPhoneLike(Long schoolId, String buyer, String receiver, String phone);

    List<ShopOrder> findAllBySchoolIdAndStatusInAndCreatedBetween(Long schoolId, List<Integer> status, Long start,
                                                                  Long end);

    List<ShopOrder> findAllBySchoolIdAndStatusAndBindRiderOrderByIdDesc(Long schoolId, Integer status, Long rider,
                                                                        Pageable pageable);

    List<ShopOrder> findAllBySchoolIdAndBindRiderOrderByUpdatedDesc(Long schoolId, Long rider, Pageable pageable);

    @Query(
            value = "select count(distinct (batch_id)) from shop_order where bind_rider = :R and status = :S and "
                    + "school_id = :SCHOOL",
            nativeQuery = true)
    Long countByRiderAndStatus(@Param("R") Long riderId, @Param("S") Integer status, @Param("SCHOOL") Long schoolId);

    @Query(
            value =
                    "select count(distinct (batch_id)) from shop_order where bind_rider = :R and status = :S and school_id = "
                            + ":SCHOOL and created >= :B "
                            + "and created <= :E",
            nativeQuery = true)
    Long countByRiderAndStatusAndTime(@Param("SCHOOL") Long schoolId, @Param("B") Long beigin, @Param("E") Long end,
                                      @Param("R") Long riderId,
                                      @Param("S") Integer status);

    @Query(
            value =
                    "select count(distinct (batch_id)) from shop_order where status in :S and school_id = :SCHOOL and created >= :B "
                            + "and created <= :E",
            nativeQuery = true)
    Long countByStatusAndTime(@Param("SCHOOL") Long schoolId, @Param("B") Long begin, @Param("E") Long end,
                              @Param("S") List<Integer> status);
}
