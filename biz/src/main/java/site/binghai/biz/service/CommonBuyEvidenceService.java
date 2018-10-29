package site.binghai.biz.service;

import org.springframework.stereotype.Service;
import site.binghai.biz.entity.CommonBuyEvidence;
import site.binghai.lib.def.UnifiedOrderMethods;
import site.binghai.lib.entity.UnifiedOrder;
import site.binghai.lib.enums.OrderStatusEnum;
import site.binghai.lib.enums.PayBizEnum;
import site.binghai.lib.service.BaseService;

@Service
public class CommonBuyEvidenceService extends BaseService<CommonBuyEvidence>
    implements UnifiedOrderMethods<CommonBuyEvidence> {

    public boolean hasBuy(Long targetId,Long userId,PayBizEnum biz){
        CommonBuyEvidence evidence = new CommonBuyEvidence();
        evidence.setUserId(userId);
        evidence.setPayBiz(biz.getCode());
        evidence.setTargetId(targetId);
        CommonBuyEvidence ret = queryOne(evidence);
        return ret != null && ret.getPaid();
    }

    @Override
    public CommonBuyEvidence moreInfo(UnifiedOrder order) {
        CommonBuyEvidence evidence = new CommonBuyEvidence();
        evidence.setUnifiedId(order.getId());
        return queryOne(evidence);
    }

    @Override
    public CommonBuyEvidence cancel(UnifiedOrder order) {
        CommonBuyEvidence evidence = moreInfo(order);
        evidence.setStatus(OrderStatusEnum.CANCELED_REFUNDED.getCode());
        return update(evidence);
    }

    @Override
    public void onPaid(UnifiedOrder order) {
        CommonBuyEvidence evidence = moreInfo(order);
        evidence.setStatus(OrderStatusEnum.PAIED.getCode());
        evidence.setPaid(Boolean.TRUE);
        update(evidence);
    }

    @Override
    public PayBizEnum getBizType() {
        return PayBizEnum.COMMON_BUY_EVIDENCE;
    }
}
