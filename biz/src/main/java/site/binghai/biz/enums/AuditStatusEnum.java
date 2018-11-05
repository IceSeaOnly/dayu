package site.binghai.biz.enums;

import java.util.HashMap;
import java.util.Map;

public enum AuditStatusEnum {
    INIT(0, "待审核"),
    PASS(1, "通过"),
    REJECT(2, "拒绝"),
    ;

    public int code;
    public String name;
    private static Map<Integer, AuditStatusEnum> maps;

    static {
        maps = new HashMap<>();
        for (AuditStatusEnum f : AuditStatusEnum.values()) {
            maps.put(f.code, f);
        }
    }

    public static AuditStatusEnum valueOf(int code) {
        return maps.get(code);
    }

    AuditStatusEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }


}
