package site.binghai.biz.entity.windWheel;

import lombok.Data;
import site.binghai.lib.entity.PayBizEntity;
import site.binghai.lib.enums.PayBizEnum;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @author huaishuo
 * @date 2018/12/3 下午10:56
 **/
@Data
@Entity
public class DeliveryOrder extends PayBizEntity {
    @Id
    @GeneratedValue
    private Long id;
    private String userName;
    private String openId;
    private String userPhone;
    private String userAddress;
    private String expressBrand;
    private Long expressId;
    //寄出日期
    private String expressOutDate;
    //快递联系电话
    private String expressPhone;
    private String remark;

    @Override
    public PayBizEnum getBizType() {
        return PayBizEnum.EXPRESS_DELIVERY;
    }
}
