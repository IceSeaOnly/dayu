package site.binghai.biz.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 审核分类
 * */
public enum AuditTypeEnum {
    TREE_HOLD(0,"树洞"),
    ;

    public int code;
    public String name;
    private static Map<Integer, AuditTypeEnum> maps;

    static {
        maps = new HashMap<>();
        for (AuditTypeEnum f : AuditTypeEnum.values()) {
            maps.put(f.code, f);
        }
    }

    public static AuditTypeEnum valueOf(int code) {
        return maps.get(code);
    }

    AuditTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }
}
