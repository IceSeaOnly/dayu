package site.binghai.biz.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import site.binghai.biz.consts.DiamondKey;
import site.binghai.biz.entity.AuditRecord;
import site.binghai.biz.entity.PayRecord;
import site.binghai.biz.entity.TreeHole;
import site.binghai.biz.enums.AuditTypeEnum;
import site.binghai.lib.entity.WxUser;
import site.binghai.lib.enums.PayBizEnum;
import site.binghai.lib.service.BaseService;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

import static site.binghai.biz.consts.DiamondKey.*;

@Service
public class TreeHoleService extends BaseService<TreeHole> {
    @Autowired
    private PayRecordService payRecordService;
    @Autowired
    private AuditRecordService auditRecordService;
    @Autowired
    private DiamondService diamondService;

    @Transactional
    public TreeHole create(Map map) throws Exception {
        TreeHole ins = newInstance(map);
        if (hasEmptyString(ins.getContent())) {
            throw new Exception("内容不得为空!");
        }

        ins.setAuditId(null);
        ins.setBuyerId(null);
        ins.setBuyerOpenId(null);
        ins.setConsumed(false);
        ins.setPassed(false);
        ins.setPayId(null);
        ins = save(ins);

        AuditRecord audit = auditRecordService.create(AuditTypeEnum.TREE_HOLD, ins.getId());
        ins.setAuditId(audit.getId());

        Integer much = Integer.parseInt(diamondService.get(TREE_HOLE_WRITER_FEE));
        String msg = diamondService.get(TREE_HOLE_WRITER_FEE_MSG);

        PayRecord pay = payRecordService.create(PayBizEnum.TREE_HOLE_WRITER_FEE, ins.getId(), much, ins.getOpenId(),
            msg);

        ins.setPayId(pay.getId());
        return update(ins);
    }

    public void authDelete(WxUser user, Long id) {
        TreeHole hole = findById(id);
        if (hole == null || !hole.getUserId().equals(user.getId())) {
            return;
        }
        delete(id);
    }

    public List<TreeHole> findByUser(WxUser user) {
        TreeHole exp = new TreeHole();
        exp.setUserId(user.getId());
        return sortQuery(exp, "id", true);
    }
}
