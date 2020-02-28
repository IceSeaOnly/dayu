package site.binghai.shop.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import site.binghai.lib.controller.BaseController;
import site.binghai.shop.anno.Conf;
import site.binghai.shop.def.KvSupport;
import site.binghai.shop.kv.IndexHotTopicImgWall;
import site.binghai.shop.kv.PinIndexBanners;
import site.binghai.shop.kv.PinTuanIndexImgWall;
import site.binghai.shop.pojo.ConfObj;
import site.binghai.shop.service.KvService;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author huaishuo
 * @date 2020/2/28 下午9:31
 **/
@RequestMapping("manage")
@Controller
public class PageConfigController extends BaseController {
    @Autowired
    private KvService kvService;

    @GetMapping("pageConfig")
    public String pageConfig(ModelMap map) {
        Map<ConfObj, List<ConfObj>> configs = new LinkedHashMap<>();
        join(configs, IndexHotTopicImgWall.class);
        join(configs, PinTuanIndexImgWall.class);
        join(configs, PinIndexBanners.class);
        map.put("configs", configs);
        return "manage/pageConfig";
    }

    private void join(Map<ConfObj, List<ConfObj>> configs, Class<? extends KvSupport> clazz) {
        KvSupport v = kvService.get(clazz);
        Conf c = clazz.getAnnotation(Conf.class);
        List<ConfObj> list = emptyList();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.getAnnotation(Conf.class) != null) {
                try {
                    field.setAccessible(true);
                    Conf cf = field.getAnnotation(Conf.class);
                    ConfObj o = new ConfObj();
                    o.setName(cf.value());
                    o.setValue(cf.json() ? toJSONString(v) : String.valueOf(field.get(v)));
                    o.setNotice(cf.notice());
                    list.add(o);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        ConfObj o = new ConfObj();
        o.setName(c.value());
        o.setNotice(c.notice());
        o.setExampleImg(c.exampleImg());
        configs.put(o, list);
    }
}
