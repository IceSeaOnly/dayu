package site.binghai.biz.entity.third;

import lombok.Data;
import site.binghai.lib.entity.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * 第三方开放服务
 * @author huaishuo
 * @date 2018/12/16 上午10:39
 **/
@Data
@Entity
public class ThirdOpen extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private Integer price;
    private String url;
    private String ownerOpenId;
    private Boolean online;


}
