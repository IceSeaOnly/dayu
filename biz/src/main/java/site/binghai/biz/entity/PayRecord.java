package site.binghai.biz.entity;

import lombok.Data;
import site.binghai.lib.entity.BaseEntity;
import site.binghai.lib.enums.OrderStatusEnum;
import site.binghai.lib.enums.PayBizEnum;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * 付款单
 */
@Entity
@Data
public class PayRecord extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;
    private Long externalId;
    /**
     * 业务类型
     *
     * @see PayBizEnum
     */
    private Integer bizType;
    private Integer much;
    /**
     * 支付状态
     *
     * @see OrderStatusEnum
     */
    private Integer status;
    private String openId;
    /**
     * 付款后的消息文案
     */
    private String message;
    /**
     * 支付原因
     */
    private String payReasone;

}
