package site.binghai.lib.entity;

import lombok.Data;
import site.binghai.lib.enums.CouponType;
import site.binghai.lib.enums.PayBizEnum;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

/**
 *
 * @date 2020/2/4 下午12:32
 **/
@Entity
@Data
public class CouponPlan extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;
    private String title;
    private String detail;
    private PayBizEnum bizType;
    private CouponType couponType;
    private Integer totalSize;
    /**
     * 满多少门槛
     */
    private Integer minLimit;
    /**
     * 满减值
     */
    private Integer couponValue;
    private Long validTs;
    private Long invalidTs;
    /**
     * 反复领取
     */
    private Boolean repeatTake;

    @Transient
    private String validTime;
}
