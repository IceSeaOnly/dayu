package site.binghai.shop.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.binghai.shop.entity.SalaryLog;
import site.binghai.shop.enums.SalaryScene;
import site.binghai.shop.enums.SalaryState;

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

    @Query(value = "SELECT sum(salary) FROM salary_log WHERE user_id = :UID"
            + " and state = :S and scene = :SC and created > :START and created < :END", nativeQuery = true)
    Integer sumByUserIdAndSceneAndStateAndDate(@Param("UID") Long userId, @Param("S") Integer state, @Param("SC")
            Integer scene, @Param("START") Long start, @Param("END") Long end);

    List<SalaryLog> findAllBySchoolIdAndUserIdAndSceneAndStateAndCreatedGreaterThanAndCreatedLessThan(Long schoolId, Long userId, SalaryScene scene
            , SalaryState salaryState, Long start, Long end);

    List<SalaryLog> findAllByUserIdOrderByIdDesc(Long userId, Pageable pageable);
}
