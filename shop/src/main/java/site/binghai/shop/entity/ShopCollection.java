package site.binghai.shop.entity;

import lombok.Data;
import site.binghai.lib.entity.BaseEntity;

import javax.persistence.*;

/**
 *
 * @date 2020/2/3 下午12:19
 **/
@Data
@Entity
@Table(indexes = {@Index(columnList = "buyerId,productId", unique = true)})
public class ShopCollection extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;
    private Long buyerId;
    private Long productId;
}
