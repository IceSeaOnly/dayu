package site.binghai.lib.entity;

import lombok.Data;
import site.binghai.lib.enums.PayBizEnum;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 *
 * @date 2020/2/4 下午10:37
 **/
@Data
@Entity
public class MergePayOrder extends PayBizEntity {
    @Id
    @GeneratedValue
    private Long id;
    private String unifiedIds;

    @Override
    public PayBizEnum getBizType() {
        return PayBizEnum.MERGE_PAY;
    }


}
