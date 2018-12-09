package site.binghai.biz.entity.windWheel;

import lombok.Data;
import site.binghai.lib.entity.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @author huaishuo
 * @date 2018/12/3 下午10:57
 **/
@Entity
@Data
public class ExpressOwner extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;
    private Long brandId;
    private Long wxUserId;
    private String phone;
    private String userName;
    private String brandName;
    private String openId;
    private Long shareFromUserId;
    private String shareFromName;
}
