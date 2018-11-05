package site.binghai.biz.service.jdy;

import org.springframework.stereotype.Service;
import site.binghai.biz.entity.jdy.Log;
import site.binghai.lib.service.BaseService;

import javax.transaction.Transactional;

@Service
public class JdyLogService extends BaseService<Log> {
    @Transactional
    public void save(String content) {
        Log log = new Log();
        log.setText(content);
        save(log);
    }
}
