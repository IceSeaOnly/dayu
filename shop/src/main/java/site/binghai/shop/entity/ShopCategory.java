package site.binghai.shop.entity;

import lombok.Data;
import site.binghai.lib.entity.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 *
 * @date 2020/2/1 上午9:03
 **/
@Entity
@Data
public class ShopCategory extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;
    private Boolean superCategory;
    private Boolean hidden;
    private Boolean recommend;
    private String title;
    private String imgUrl;
    private Long superId;
}
