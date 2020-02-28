package site.binghai.shop.kv;

import lombok.Data;
import site.binghai.shop.anno.Conf;
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
@Conf(value = "拼团头Banner", notice = "请用json格式化查看",
    exampleImg = "http://cdn.binghai.site/9d01902e-9e0b-1644-f190-6c52bf6f4194.png")
public class PinIndexBanners implements KvSupport {
    @Conf(value = "图片列表", json = true, notice = "请用json格式化查看")
    private List<ImgUrl> imgs;
}
