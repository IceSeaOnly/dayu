package site.binghai.shop.pojo;

import lombok.Data;

/**
 * @author icesea
 * @date 2020/2/28 下午10:04
 **/
@Data
public class ConfObj {
    private String name;
    private Object value;
    private String className;
    private String fieldName;
    private boolean img;
}
