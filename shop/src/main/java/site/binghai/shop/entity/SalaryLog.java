package site.binghai.shop.entity;

import lombok.Data;
import site.binghai.lib.entity.BaseEntity;
import site.binghai.shop.enums.SalaryScene;
import site.binghai.shop.enums.SalaryState;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @author huaishuo
 * @date 2020/3/14 下午9:19
 **/
@Data
@Entity
public class SalaryLog extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;
    private Long userId;
    private Long relateId;
    private Integer salary;
    private SalaryScene scene;
    private SalaryState state;
    private String settlementDate;
}
