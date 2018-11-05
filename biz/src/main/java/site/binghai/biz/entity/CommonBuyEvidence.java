package site.binghai.biz.entity;

import lombok.Data;
import site.binghai.lib.entity.PayBizEntity;
import site.binghai.lib.enums.PayBizEnum;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
public class CommonBuyEvidence extends PayBizEntity {
    @Id
    @GeneratedValue
    private Long id;
    private Long targetId;
    /**
     * @see PayBizEnum
     * */
    private Integer payBiz;

    @Override
    public PayBizEnum getBizType() {
        return PayBizEnum.COMMON_BUY_EVIDENCE;
    }
}
