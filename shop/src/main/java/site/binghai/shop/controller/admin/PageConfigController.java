package site.binghai.shop.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import site.binghai.lib.controller.BaseController;
import site.binghai.shop.anno.Conf;
import site.binghai.shop.def.KvSupport;
import site.binghai.shop.entity.KeyValueEntity;
import site.binghai.shop.kv.*;
import site.binghai.shop.pojo.ConfObj;
import site.binghai.shop.service.KvService;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author icesea
 * @date 2020/2/28 下午9:31
 **/
@RequestMapping("manage")
@Controller
public class PageConfigController extends BaseController {
    @Autowired
    private KvService kvService;

    @GetMapping("pageConfig")
    public String pageConfig(ModelMap map, Long update) {
        Map<ConfObj, List<ConfObj>> configs = new LinkedHashMap<>();
        join(configs, AutoAcceptOrderConfig.class);
        join(configs, IndexButtonConfig.class);
        join(configs, AppConfig.class);
        join(configs, MyPageConfig.class);
        join(configs, RecommendSearchWord.class);
        join(configs, PinTuanIndexWxShareConfig.class);
        join(configs, IndexHotTopicImgWall.class);
        join(configs, PinTuanIndexImgWall.class);
        join(configs, AdImg.class);
        map.put("configs", configs);
        map.put("msg", update != null ? "更新成功" : null);
        return "manage/pageConfig";
    }

    @PostMapping(value = "ajaxUpdatePageConfig")
    @ResponseBody
    public Object updatePageConfig(@RequestBody Map map) throws Exception {
        String className = getString(map, "className");
        KeyValueEntity entity = kvService.findByKey(className);
        if(entity == null){
            entity = new KeyValueEntity();
            entity.setClassName(className);
            entity.setSvalue(toJSONString(map));
            kvService.save(entity);
        }else{
            entity.setSvalue(toJSONString(map));
            kvService.update(entity);
        }
        return success();
    }

    private void join(Map<ConfObj, List<ConfObj>> configs, Class<? extends KvSupport> clazz) {
        KvSupport v = kvService.get(clazz);
        List<ConfObj> list = emptyList();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.getAnnotation(Conf.class) != null) {
                try {
                    field.setAccessible(true);
                    Conf cf = field.getAnnotation(Conf.class);
                    ConfObj o = new ConfObj();
                    o.setName(cf.value());
                    o.setFieldName(field.getName());
                    if (v == null) {
                        o.setValue("#");
                    } else {
                        o.setValue(String.valueOf(field.get(v)));
                    }
                    o.setImg(cf.img());
                    list.add(o);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        Conf c = clazz.getAnnotation(Conf.class);
        ConfObj o = new ConfObj();
        o.setName(c.value());
        o.setClassName(clazz.getSimpleName());
        o.setImg(c.img());
        configs.put(o, list);
    }
}
