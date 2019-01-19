package site.binghai.biz.controller;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import site.binghai.biz.entity.ExcelConvertEntity;
import site.binghai.biz.entity.third.ThirdOpen;
import site.binghai.biz.service.ExcelConvertEntityService;
import site.binghai.biz.service.WxTplMessageService;
import site.binghai.biz.service.third.ThirdOpenSerivice;
import site.binghai.lib.controller.BaseController;
import site.binghai.lib.utils.ExcelUtils;
import site.binghai.lib.utils.GroovyEngineUtils;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author huaishuo
 * @date 2018/11/28 下午5:07
 **/
@CrossOrigin("*")
@RestController
@RequestMapping("/open/v1/")
public class OpenApiController extends BaseController {
    @Autowired
    private ExcelConvertEntityService excelConvertEntityService;
    @Autowired
    private WxTplMessageService tplMessageService;
    @Autowired
    private ThirdOpenSerivice thirdOpenSerivice;

    @GetMapping("excel2json")
    public Object excel2json(@RequestParam String token) {
        ExcelConvertEntity entity = excelConvertEntityService.findByToken(token);
        if (entity != null) {
            return fail("no such token");
        }

        return entity.getContent();
    }

    /**
     * createOrUpdateExcel2Json
     */
    @PostMapping("createOrUpdateExcel2Json")
    public Object createOrUpdateExcel2Json(String token, String sercet, @RequestParam("file") MultipartFile file) {
        ExcelConvertEntity entity = null;
        if (!hasEmptyString(token, sercet)) {
            entity = excelConvertEntityService.findByToken(token);
            if (entity == null) {
                return fail("记录不存在");
            }
        }
        // 判断文件是否为空
        if (file.isEmpty()) {
            return fail("不能读取空文件!");
        }
        try {
            List list = ExcelUtils.excel2Json(file);
            if (isEmptyList(list)) {
                return fail("不能读取空文件!");
            }
            if (entity == null) {
                entity = new ExcelConvertEntity();
                entity.setContent(toJsonObject(list).toJSONString());
                entity.setToken(UUID.randomUUID().toString());
                entity.setSecret(UUID.randomUUID().toString());
                entity = excelConvertEntityService.save(entity);
            } else {
                entity.setPreviousContent(entity.getContent());
                entity.setContent(toJsonObject(list).toJSONString());
                excelConvertEntityService.update(entity);
            }
        } catch (Exception e) {
            logger.error("readExcelData fail!", e);
            return fail(e.getMessage());
        }
        return success(entity, null);
    }

    @PostMapping("sendWxTplMessage")
    public Object sendWxTplMessage(@RequestBody Map map) {
        Long thirdId = getLong(map, "thirdId");
        String token = getString(map, "token");
        ThirdOpen api = thirdOpenSerivice.findById(thirdId);
        if (api == null) {
            return fail("非法访问");
        }

        if (api.getName().equals("WXTPL")) {
            return fail("前台服务不允许api调用");
        }

        if (!api.getUrl().equals(token)) {
            return fail("认证失败");
        }

        if (api.getOnline()) {
            return fail("服务已下线!");
        }

        try {
            String ret = GroovyEngineUtils.renderTemplate(api.getExtraInfo(), map);
            String sendRet = tplMessageService.send(JSON.parseObject(ret));
            return success("SUCCESS", sendRet);
        } catch (Exception e) {
            return fail("渲染失败:" + e.getMessage());
        }
    }

}
