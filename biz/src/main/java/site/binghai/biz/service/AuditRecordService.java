package site.binghai.biz.service;

import org.springframework.stereotype.Service;
import site.binghai.biz.entity.AuditRecord;
import site.binghai.biz.enums.AuditStatusEnum;
import site.binghai.biz.enums.AuditTypeEnum;
import site.binghai.lib.service.BaseService;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class AuditRecordService extends BaseService<AuditRecord> {

    @Transactional
    public AuditRecord create(AuditTypeEnum type, Long externalId) {
        AuditRecord record = new AuditRecord();
        record.setAuditStatus(AuditStatusEnum.INIT.code);
        record.setAuditType(type.code);
        record.setExternalId(externalId);
        record.setSynced(Boolean.TRUE);
        return save(record);
    }

    public List<AuditRecord> findByAuditStatus(AuditStatusEnum status) {
        AuditRecord exp = new AuditRecord();
        exp.setAuditStatus(status.code);
        return sortQuery(exp, "id", true);
    }

    public List<AuditRecord> findBySync(Boolean synced) {
        AuditRecord exp = new AuditRecord();
        exp.setSynced(synced);
        return query(exp);
    }
}
