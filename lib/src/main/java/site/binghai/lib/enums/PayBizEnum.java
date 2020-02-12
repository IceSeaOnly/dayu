package site.binghai.lib.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum PayBizEnum {
    TREE_HOLE_WRITER_FEE(0, "树洞原创作品费", null, true),
    COMMON_BUY_EVIDENCE(1, "通用购买凭证", null, true),
    TREE_HOLE_BUY_FEE(2, "树洞作品购买", null, true),
    VIP_CHARGE(3, "会员充值", null, false),
    EXPRESS_DELIVERY(4, "代寄快递", "http://cdn.binghai.site/o_1cu5qp3aq2fn1lae1qlc12a0bqea.jpg", true),
    THIRD_OPEN_SERVICE(5, "开放服务申请", "http://cdn.binghai.site/o_1cuqefqdvmuf179fsj2v6j1rnda.png", true),
    SCHOOL_SHOP(6, "校园商城", "http://cdn.binghai.site/o_1e0238ia21uoc1lf0dpbk2gcva.png", true),
    MERGE_PAY(7, "合并支付", "http://cdn.binghai.site/o_1e08a7v27cihv7m1l6i150hqeua.png", true),
    ;

    private int code;
    private String name;
    private String img;
    private boolean walletPay;

    private static Map<Integer, PayBizEnum> maps;

    static {
        maps = new HashMap<>();
        for (PayBizEnum f : PayBizEnum.values()) {
            maps.put(f.code, f);
        }
    }

    public static PayBizEnum valueOf(int code) {
        return maps.get(code);
    }

    PayBizEnum(int code, String name, String img, boolean walletPay) {
        this.img = img;
        this.code = code;
        this.name = name;
        this.walletPay = walletPay;
    }
}
