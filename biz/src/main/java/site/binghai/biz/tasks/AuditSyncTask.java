package site.binghai.biz.tasks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import site.binghai.biz.def.AuditSyncService;
import site.binghai.biz.def.ManualInvoke;
import site.binghai.biz.entity.AuditRecord;
import site.binghai.biz.enums.AuditTypeEnum;
import site.binghai.biz.service.AuditRecordService;
import site.binghai.lib.utils.BaseBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@Component
//@EnableScheduling
public class AuditSyncTask extends BaseBean implements ManualInvoke {
    @Autowired
    private Map<AuditTypeEnum, AuditSyncService> map;

    @Autowired
    private AuditRecordService auditRecordService;

    @Scheduled(cron = "0 * * * * ?")
    @Override
    public Object invoke() {
        List<AuditRecord> recordList = auditRecordService.findBySync(Boolean.FALSE);
        recordList.forEach(item -> {
            logger.info("sync audit ing... {}", item);
            map.get(AuditTypeEnum.valueOf(item.getAuditType())).sync(item);
            item.setSynced(Boolean.TRUE);
            auditRecordService.update(item);
        });
        return recordList;
    }

    @Autowired
    public void join(List<AuditSyncService> services) {
        map = new HashMap<>();
        services.forEach(v -> map.put(v.getAuditType(), v));
    }
}
