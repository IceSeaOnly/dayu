package site.binghai.biz.service.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import site.binghai.biz.entity.DelayNotice;

import java.util.List;

public interface DelayNoticeDao extends JpaRepository<DelayNotice, Long> {

    List<DelayNotice> findByFiredAndFireTimeBefore(Boolean fired,Long fireTimeBefore);
}
