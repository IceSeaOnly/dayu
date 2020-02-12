package site.binghai.shop.entity;

import lombok.Data;
import site.binghai.lib.entity.PayBizEntity;
import site.binghai.lib.enums.PayBizEnum;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

/**
 *
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
    private Integer startOfService;
    private Integer startOfShip;
    @Transient
    private Product product;

    @Override
    public PayBizEnum getBizType() {
        return PayBizEnum.SCHOOL_SHOP;
    }
}
