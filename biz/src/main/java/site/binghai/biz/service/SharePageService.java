package site.binghai.biz.service;

import org.springframework.stereotype.Service;
import site.binghai.biz.entity.SharePage;
import site.binghai.lib.entity.WxUser;
import site.binghai.lib.service.BaseService;
import site.binghai.lib.utils.TimeTools;

import javax.transaction.Transactional;

/**
 * @author huaishuo
 * @date 2018/12/8 上午11:20
 **/
@Service
public class SharePageService extends BaseService<SharePage> {

    @Transactional
    public SharePage create(WxUser user, String title, String selfContent, String otherContent, String btnName,
                            String url,String data) {
        SharePage sharePage = new SharePage();
        sharePage.setButtonName(btnName);
        sharePage.setProducerOpenId(user.getOpenId());
        sharePage.setSelfContent(selfContent);
        sharePage.setOtherContent(otherContent);
        sharePage.setInvalidTs(now() + 300000);
        sharePage.setInvalidTime(TimeTools.format(sharePage.getInvalidTs()));
        sharePage.setTargetUrl(url);
        sharePage.setTitle(title);
        sharePage.setValid(true);
        sharePage.setData(data);

        return save(sharePage);
    }

    /**
     * 消费
     * */
    @Transactional
    public SharePage consume(Long sharePageId, String openId) {
        SharePage sharePage = findById(sharePageId);
        if(sharePage == null || !sharePage.getValid()){
            return null;
        }

        if(sharePage.getInvalidTs() > now()){
            sharePage.setValid(false);
            update(sharePage);
            return null;
        }

        sharePage.setValid(false);
        sharePage.setConsumerOpenId(openId);
        return update(sharePage);
    }

    @Transactional
    public void invalid(Long pid) {
        SharePage page = findById(pid);
        page.setValid(false);
        update(page);
    }
}
