package site.binghai.shop.kv;

import lombok.Data;
import site.binghai.shop.def.KvSupport;
import site.binghai.shop.pojo.ImgUrl;

import java.util.List;

/**
 * 拼团首页banner
 *
 * @author huaishuo
 * @date 2020/2/23 下午6:40
 **/
@Data
public class PinIndexBanners implements KvSupport {
    private List<ImgUrl> imgs;
}
