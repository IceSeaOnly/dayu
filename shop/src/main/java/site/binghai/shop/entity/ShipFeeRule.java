package site.binghai.shop.entity;

import lombok.Data;
import site.binghai.lib.entity.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @author huaishuo
 * @date 2020/3/15 下午10:59
 **/
@Entity
@Data
public class ShipFeeRule extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;
    /**
     * 超过多少
     * */
    private Integer much;
    /**
     * 则费用是
     * */
    private Integer fee;
}
