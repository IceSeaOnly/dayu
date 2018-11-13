package site.binghai.biz.def;

import site.binghai.biz.entity.AuditRecord;
import site.binghai.biz.enums.AuditTypeEnum;

public interface AuditSyncService {
    void sync(AuditRecord record);
    AuditTypeEnum getAuditType();
}
