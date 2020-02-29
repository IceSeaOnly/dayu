package site.binghai.shop.enums;

/**
 * @author huaishuo
 * @date 2020/2/29 下午7:57
 **/
public enum BannerType {
    INDEX("首页"),
    PT_INDEX("拼团首页"),
    ;

    private String desc;

    public String getDesc() {
        return desc;
    }

    BannerType(String desc) {
        this.desc = desc;
    }
}
