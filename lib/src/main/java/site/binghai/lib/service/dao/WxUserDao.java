package site.binghai.lib.service.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import site.binghai.lib.entity.WxUser;

/**
 * @author icesea
 * @date 2020/3/2 下午2:47
 **/
public interface WxUserDao extends JpaRepository<WxUser, Long> {
    Long countBySchoolIdAndCreatedAfter(Long schoolId, Long create);
}
