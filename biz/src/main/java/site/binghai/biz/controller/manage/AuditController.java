package site.binghai.biz.controller.manage;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import site.binghai.biz.entity.AuditRecord;
import site.binghai.biz.enums.AuditStatusEnum;
import site.binghai.biz.service.AuditRecordService;
import site.binghai.lib.controller.BaseController;
import site.binghai.lib.entity.Manager;
import site.binghai.lib.utils.CompareUtils;
import site.binghai.lib.utils.TimeTools;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/manage/audit/")
public class AuditController extends BaseController {
    @Autowired
    private AuditRecordService auditRecordService;

    @GetMapping("list")
    public Object list(@RequestParam Integer status) {
        Assert.isTrue(CompareUtils.inAny(status, 0, 1, 2), "status must be in 0,1,2!");
        List<AuditRecord> recordList = auditRecordService.findByAuditStatus(AuditStatusEnum.valueOf(status));
        return success(recordList, null);
    }

    @PostMapping("determine")
    public Object determine(@RequestBody Map map) {
        Manager manager = getManager();
        Long id = getLong(map, "id");
        String msg = getString(map, "msg");
        Boolean passed = getBoolean(map, "passed");

        AuditRecord record = auditRecordService.findById(id);
        if (record == null) {
            return fail("记录不存在!");
        }

        if (AuditStatusEnum.INIT != AuditStatusEnum.valueOf(record.getAuditStatus())) {
            return fail("已判决，无法更改!");
        }

        logger.warn("{} change audit record {} from status {} to {},msg:{}",
            manager, record, AuditStatusEnum.valueOf(record.getAuditStatus()),
            passed ? AuditStatusEnum.PASS : AuditStatusEnum.REJECT, msg);


        record.setAuditStatus(passed ? AuditStatusEnum.PASS.code : AuditStatusEnum.REJECT.code);
        record.setMessage(appendMessage(record.getMessage(), msg));
        record.setManagerId(manager.getId());
        record.setSynced(Boolean.FALSE);

        auditRecordService.update(record);
        return success();
    }

    private String appendMessage(String message, String msg) {
        JSONObject msgList = new JSONObject();
        if (!hasEmptyString(message)) {
            msgList.putAll(toJsonObject(message));
        }
        msgList.put(TimeTools.now(), msg);
        return msgList.toJSONString();
    }

}
