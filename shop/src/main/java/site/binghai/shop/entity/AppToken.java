package site.binghai.shop.entity;

import lombok.Data;
import site.binghai.lib.entity.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @author huaishuo
 * @date 2020/3/10 下午8:02
 **/
@Entity
@Data
public class AppToken extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;
    private Long schoolId;
    private String remark;
    private String userName;
    private String passWord;
    private String token;
    private Long invalidTs;
}
