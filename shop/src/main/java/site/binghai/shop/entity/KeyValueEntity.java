package site.binghai.shop.entity;

import lombok.Data;
import site.binghai.lib.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 *
 * @date 2020/2/10 上午9:51
 **/
@Data
@Entity
public class KeyValueEntity extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;
    private String className;
    @Column(columnDefinition = "TEXT")
    private String svalue;
}
