package site.binghai.biz.entity.third;

import lombok.Data;
import site.binghai.lib.entity.PayBizEntity;
import site.binghai.lib.enums.PayBizEnum;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @author huaishuo
 * @date 2018/12/16 上午11:00
 **/
@Data
@Entity
public class ThirdOpenOrder extends PayBizEntity {
    @Id
    @GeneratedValue
    private Long id;
    private Long serviceId;
    private String serviceUrl;
    private String serviceName;
    private String userName;
    //买家
    private String userOpenId;
    //卖家
    private String ownerOpenId;
    private String remark;

    @Override
    public PayBizEnum getBizType() {
        return PayBizEnum.THIRD_OPEN_SERVICE;
    }
}
