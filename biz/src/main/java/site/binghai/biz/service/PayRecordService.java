package site.binghai.biz.service;

import org.springframework.stereotype.Service;
import site.binghai.biz.entity.PayRecord;
import site.binghai.lib.enums.OrderStatusEnum;
import site.binghai.lib.enums.PayBizEnum;
import site.binghai.lib.service.BaseService;

import javax.transaction.Transactional;

@Service
public class PayRecordService extends BaseService<PayRecord> {

    @Transactional
    public PayRecord create(PayBizEnum biz, Long externalId, Integer much, String openId, String msg) {
        PayRecord record = new PayRecord();
        record.setBizType(biz.getCode());
        record.setExternalId(externalId);
        record.setMuch(much);
        record.setOpenId(openId);
        record.setStatus(OrderStatusEnum.CREATED.getCode());
        record.setPayReason(biz.getName());
        record.setMessage(msg);

        return save(record);
    }
}
