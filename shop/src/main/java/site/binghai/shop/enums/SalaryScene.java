package site.binghai.shop.enums;

import lombok.Getter;

/**
 * @author huaishuo
 * @date 2020/3/14 下午9:25
 **/
@Getter
public enum SalaryScene {
    DELIVERY("配送订单"),
    REWARD("绩效奖励"),

    ;
    private String desc;

    SalaryScene(String desc) {
        this.desc = desc;
    }
}
