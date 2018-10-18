package site.binghai.lib.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum PayBizEnum {
    TREE_HOLE_WRITER_FEE(0,"树洞原创作品费"),
    ;

    private int code;
    private String name;
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

    PayBizEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }
}
