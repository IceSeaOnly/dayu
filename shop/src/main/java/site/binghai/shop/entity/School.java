package site.binghai.shop.entity;

import lombok.Data;
import site.binghai.lib.entity.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @author icesea
 * @date 2020/3/8 上午11:07
 **/
@Data
@Entity
public class School extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;
    private String schoolName;
    private String schoolImg;
    private Boolean visible;
}
