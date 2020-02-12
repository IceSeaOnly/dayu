package site.binghai.lib.entity;

import lombok.Data;
import site.binghai.lib.enums.CouponStatus;
import site.binghai.lib.enums.CouponType;
import site.binghai.lib.enums.PayBizEnum;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

/**
 *
 * @date 2020/2/4 下午12:39
 **/
@Entity
@Data
public class Coupon extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;
    private Long userId;
    private Long couponId;
    private CouponStatus couponStatus;

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

    @Transient
    private boolean available;
    @Transient
    private String disableReason;
    @Transient
    private String validTime;
}
