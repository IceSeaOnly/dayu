package site.binghai.biz.service;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import site.binghai.biz.consts.DiamondKey;
import site.binghai.biz.def.AuditSyncService;
import site.binghai.biz.entity.AuditRecord;
import site.binghai.biz.entity.anywish.Wish;
import site.binghai.biz.enums.AuditStatusEnum;
import site.binghai.biz.enums.AuditTypeEnum;
import site.binghai.lib.entity.WxUser;
import site.binghai.lib.service.BaseService;
import site.binghai.lib.utils.TimeTools;
import site.binghai.lib.utils.TplGenerator;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class WishService extends BaseService<Wish> implements AuditSyncService {

    @Autowired
    private DiamondService diamondService;
    @Autowired
    private WxTplMessageService wxTplMessageService;

    public List<Wish> findMy(WxUser user) {
        Wish exp = new Wish();
        exp.setOpenId(user.getOpenId());
        return sortQuery(exp, "id", true);
    }

    @Override
    @Transactional
    public void sync(AuditRecord record) {
        Wish wish = findById(record.getExternalId());
        if (wish == null || wish.getStatus().equals(record.getAuditStatus())) {
            return;
        }

        wish.setStatus(record.getAuditStatus());
        update(wish);
        notify(wish);
    }

    private void notify(Wish wish) {
        JSONObject cfg = diamondService.getConf(DiamondKey.ANY_WISH_AUDIT_NOTIFY_CONF);
        TplGenerator generator = new TplGenerator(cfg.getString("tpl"), cfg.getString("url") + wish.getId(),
            wish.getOpenId());

        generator.put("first", cfg.getString("title"))
            .put("keyword1", wish.getNickName())
            .put("keyword2", cfg.getString("productName"))
            .put("keyword4", TimeTools.now())
            .put("remark", cfg.getString("remark"));

        switch (AuditStatusEnum.valueOf(wish.getStatus())) {
            case PASS:
                generator.put("keyword3", "审核通过");
                break;
            case REJECT:
                generator.put("keyword3", "审核未通过");
                break;
            default:
                generator.put("keyword3", "延迟审核");
        }

        wxTplMessageService.send(generator.build());
    }

    @Override
    public AuditTypeEnum getAuditType() {
        return AuditTypeEnum.ANY_WISH;
    }
}
