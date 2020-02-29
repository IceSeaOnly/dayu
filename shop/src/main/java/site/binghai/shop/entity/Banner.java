package site.binghai.shop.entity;

import lombok.Data;
import site.binghai.lib.entity.BaseEntity;
import site.binghai.shop.enums.BannerType;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @date 2020/1/31 下午8:39
 **/
@Data
@Entity
public class Banner extends BaseEntity {
    @GeneratedValue
    @Id
    private Long id;
    private BannerType type;
    private String title;
    private String imgUrl;
    private String target;

}
