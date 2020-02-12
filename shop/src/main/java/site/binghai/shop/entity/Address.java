package site.binghai.shop.entity;

import lombok.Data;
import site.binghai.lib.entity.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 *
 * @date 2020/2/5 下午8:23
 **/
@Data
@Entity
public class Address extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;
    private Long userId;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddr;
}
