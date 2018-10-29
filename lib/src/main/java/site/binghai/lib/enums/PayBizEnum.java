package site.binghai.lib.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum PayBizEnum {
    TREE_HOLE_WRITER_FEE(0, "树洞原创作品费", true),
    COMMON_BUY_EVIDENCE(1, "通用购买凭证", true),
    TREE_HOLE_BUY_FEE(2, "树洞作品购买", true),
    VIP_CHARGE(3, "会员充值", false),
    ;

    private int code;
    private String name;
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

    PayBizEnum(int code, String name, boolean walletPay) {
        this.code = code;
        this.name = name;
        this.walletPay = walletPay;
    }
}
