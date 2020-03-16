package site.binghai.shop.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.binghai.shop.entity.SalaryLog;

import java.util.List;

/**
 * @author icesea
 * @date 2020/3/14 下午9:29
 **/
public interface SalaryLogDao extends JpaRepository<SalaryLog, Long> {
    @Query(value = "SELECT sum(salary) FROM salary_log WHERE user_id = :UID"
        + " and state = :S and scene = :SC", nativeQuery = true)
    Integer sumByUserIdAndSceneAndState(@Param("UID") Long userId, @Param("S") Integer state, @Param("SC")
        Integer scene);

    List<SalaryLog> findAllByUserIdOrderByIdDesc(Long userId, Pageable pageable);
}
