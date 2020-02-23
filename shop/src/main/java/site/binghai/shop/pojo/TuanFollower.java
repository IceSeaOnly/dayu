package site.binghai.shop.pojo;

import lombok.Data;

/**
 * @author huaishuo
 * @date 2020/2/23 下午9:26
 **/
@Data
public class TuanFollower {
    private Long orderId;
    private Long userId;
    private String openId;
    private String userName;
    private String avatar;
    private Long productId;
    private Long joinTs;
}
