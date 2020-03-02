package site.binghai.shop.enums;

/**
 * @author huaishuo
 * @date 2020/2/23 下午9:28
 **/
public enum TuanStatus {
    INIT("拼团中"),
    FULL("拼团成功"),
    FAIL("拼团失败"),
    ;

    private String desc;

    public String getDesc() {
        return desc;
    }

    TuanStatus(String desc) {
        this.desc = desc;
    }
}
