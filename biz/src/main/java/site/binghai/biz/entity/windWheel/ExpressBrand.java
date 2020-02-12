package site.binghai.biz.entity.windWheel;

import lombok.Data;
import site.binghai.lib.entity.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * 快递品牌
 ***/
@Entity
@Data
public class ExpressBrand extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String managerName;
    private String phone;
    //启用代寄
    private Boolean enableSend;
    //启用代取
    private Boolean enableTake;
    //单位分
    private Integer price;
}
