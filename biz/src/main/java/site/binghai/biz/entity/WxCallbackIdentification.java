package site.binghai.biz.entity;

import lombok.Data;
import site.binghai.lib.entity.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * 微信回调认证
 *
 * @author huaishuo
 * @date 2018/12/2 下午11:30
 **/
@Data
@Entity
public class WxCallbackIdentification extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;
    private String token;
    private String openId;
    private String status;
    private String sessionKey;
    private String sessionValue;
    private String callBackUrl;
    private String clientIp;
}
