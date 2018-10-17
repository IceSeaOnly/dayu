package site.binghai.lib.entity;

import lombok.Data;
import site.binghai.lib.interfaces.SessionPersistent;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@Data
@MappedSuperclass
public class WxUser extends BaseEntity implements SessionPersistent {
    @Id
    @GeneratedValue
    private Long id;
    private String avatar;
    private String userName;
    private String phone;
    private String openId;
    /**
     * 推荐人id
     * */
    private Long refereeId;
}
