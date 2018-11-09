package site.binghai.biz.entity.turntable;

import lombok.Data;
import site.binghai.lib.entity.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * 奖池
 * */
@Entity
@Data
public class Jackpot extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String img;
    private Integer remains;
    private Integer fakeRemains;//空奖数量用来调节中奖概率
    private String msg; // 获奖提示
    // 场景
    private String jackpotScene;
}
