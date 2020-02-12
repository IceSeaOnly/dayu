package site.binghai.shop.entity;

import lombok.Data;
import site.binghai.lib.entity.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 *
 * 购物评价
 * @date 2020/2/1 上午10:59
 **/
@Entity
@Data
public class ProductComment extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;
    private Long orderId;
    private Long productId;
    private Long buyerId;
    private String buyerName;
    private String buyerAvatarUrl;
    private String comment;
    private String date;
    private String standardInfo;
}
