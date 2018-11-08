package site.binghai.biz.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import site.binghai.biz.entity.DelayNotice;
import site.binghai.biz.enums.NoticeType;
import site.binghai.biz.service.dao.DelayNoticeDao;
import site.binghai.lib.service.BaseService;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class DelayNoticeService extends BaseService<DelayNotice> {

    @Autowired
    private DelayNoticeDao delayNoticeDao;

    @Transactional
    public void save(NoticeType noticeType, Long fireTime, String context) {
        DelayNotice notice = new DelayNotice();
        notice.setFired(Boolean.FALSE);
        notice.setContext(context);
        notice.setFireTime(fireTime);
        notice.setNoticeType(noticeType.name());
        save(notice);
    }

    public List<DelayNotice> findAvailable() {
        return delayNoticeDao.findByFiredAndFireTimeBefore(Boolean.FALSE, now());
    }

    @Override
    protected JpaRepository<DelayNotice, Long> getDao() {
        return delayNoticeDao;
    }
}
