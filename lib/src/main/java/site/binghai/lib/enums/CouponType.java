package site.binghai.lib.enums;

/**
 *
 * @date 2020/2/4 下午12:33
 **/
public enum CouponType {
    CUT_OFF("满减券"),

    ;

    private String name;

    public String getName() {
        return name;
    }

    CouponType(String name) {
        this.name = name;
    }
}
