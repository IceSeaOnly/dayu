package site.binghai.lib.entity;

import lombok.Data;
import site.binghai.lib.interfaces.SessionPersistent;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@Data
@Entity
public class WxUser extends BaseEntity implements SessionPersistent {
    @Id
    @GeneratedValue
    private Long id;
    private String avatar;
    private String userName;
    private String phone;
    private Integer wallet;
    private String openId;
    private String gender;
    //用户所在城市
    private String city;
    //用户所在国家
    private String country;
    //用户所在省份
    private String province;
    //用户关注时间，为时间戳。如果用户曾多次关注，则取最后关注时间
    private String subscribeTime;
    //用户关注的渠道来源
    private String subscribeScene;
    /**
     * 余额
     */
    private Integer balance;
    /**
     * 推荐人id
     */
    private Long refereeId;
    private Boolean subscribed;

    private String address;

    private Boolean expDeliverySuperAuth = false;
}
