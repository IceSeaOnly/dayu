package site.binghai.shop.entity;

import lombok.Data;
import site.binghai.lib.entity.PayBizEntity;
import site.binghai.lib.entity.UnifiedOrder;
import site.binghai.lib.enums.PayBizEnum;
import site.binghai.shop.enums.TuanStatus;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

/**
 * @date 2020/2/2 下午12:35
 **/
@Data
@Entity
public class ShopOrder extends PayBizEntity {
    @Id
    @GeneratedValue
    private Long id;
    private Long productId;
    private String productImgUrl;
    private String buyerName;
    private String buyerAvatar;
    private Integer size;
    private Integer price;
    private Integer totalPrice;
    private String title;
    private String standardInfo;
    private String remark;
    private String receiver;
    private String receiverAddress;
    private String receiverPhone;
    private Long expiredTime;
    private Integer starOfService;
    private Integer starOfShip;
    /**
     * 是否是拼团订单
     * */
    private Boolean ptOrder;
    /**
     * 是否拼团成功
     * */
    private TuanStatus tuanStatus;
    /**
     * 团id
     * */
    private Long tuanId;

    /**
     * 绑定起手，appToken.Id
     * */
    private Long bindRider;

    @Transient
    private UnifiedOrder unifiedOrder;
    @Transient
    private Product product;

    @Override
    public PayBizEnum getBizType() {
        return PayBizEnum.SCHOOL_SHOP;
    }
}
