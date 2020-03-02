package site.binghai.lib.service.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.binghai.lib.entity.UnifiedOrder;

import java.util.List;

public interface UnifiedOrderDao extends JpaRepository<UnifiedOrder, Long> {
    List<UnifiedOrder> findAllByAppCodeOrderByCreatedDesc(Integer code, Pageable pageable);

    List<UnifiedOrder> findAllByAppCodeAndStatusOrderByCreatedDesc(Integer code, Integer status, Pageable pageable);

    Long countByAppCode(Integer code);

    Long countByAppCodeAndCreatedAfter(Integer code, Long create);

    Long countByAppCodeAndStatus(Integer code, Integer status);

    Long countByAppCodeAndStatusInAndCreatedBetween(Integer code, List<Integer> status, Long start, Long end);

    List<UnifiedOrder> findAllByUserIdOrderByIdDesc(Long userId, Pageable pageable);

    List<UnifiedOrder> findAllByStatusAndCreatedBefore(Integer status, Long before);

    @Query(value = "SELECT sum(should_pay) FROM unified_order WHERE app_code = :APPCODE"
        + " and created > :S and created < :E and status in :LIST", nativeQuery = true)
    Integer sum(@Param("APPCODE") Integer appCode, @Param("S") Long tStart, @Param("E") Long tEnd,
                @Param("LIST") List<Integer> status);

}
