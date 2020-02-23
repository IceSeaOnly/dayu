package site.binghai.shop.entity;

import lombok.Data;
import site.binghai.lib.entity.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

/**
 *
 * @date 2020/2/2 下午12:28
 **/
@Entity
@Data
public class CartItem extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;
    private Long productId;
    private Long buyerId;
    private Integer size;
    private String standardInfo;
    private Boolean hidden;
    private Boolean ptOrder;
    private Long joinPtId;
    @Transient
    private String invalidReason;
    @Transient
    private Product product;

}
