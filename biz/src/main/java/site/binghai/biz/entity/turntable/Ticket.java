package site.binghai.biz.entity.turntable;

import lombok.Data;
import site.binghai.lib.entity.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * 游戏入场券
 * */
@Entity
@Data
public class Ticket extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;
    private String relationNo; // 关联订单号
    private String openId;
    private String userName;
    private String userPhone;
    private String userAvatar;
    private Boolean played; //是否已经使用
    private Boolean win; //是否获奖
    private String prize;//奖品
    private String gameTimeString;
    private Long gameTime;

}
