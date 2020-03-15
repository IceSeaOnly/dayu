package site.binghai.shop.enums;

import lombok.Getter;

/**
 * @author huaishuo
 * @date 2020/3/14 下午9:22
 **/
@Getter
public enum SalaryState {
    INIT("待结算"),
    CANCELED("已取消"),
    DONE("已结算"),

    ;
    private String desc;

    SalaryState(String desc) {
        this.desc = desc;
    }
}
