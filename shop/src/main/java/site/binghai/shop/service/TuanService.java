package site.binghai.shop.service;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import site.binghai.lib.def.WxEventHandler;
import site.binghai.lib.entity.WxUser;
import site.binghai.lib.service.BaseService;
import site.binghai.shop.entity.ShopOrder;
import site.binghai.shop.entity.Tuan;
import site.binghai.shop.enums.TuanStatus;
import site.binghai.shop.pojo.TuanFollower;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author huaishuo
 * @date 2020/2/23 下午9:27
 **/
@Service
public class TuanService extends BaseService<Tuan> {
    @Autowired
    private WxEventHandler wxEventHandler;

    @Transactional
    public static void cancel(Tuan t) {

    }

    public List<Tuan> searchByProductIdAndStatus(Long productId, TuanStatus status, int size) {
        Tuan tuan = new Tuan();
        tuan.setProductId(productId);
        tuan.setStatus(status);
        List<Tuan> ts = query(tuan);
        if (isEmptyList(ts)) {
            return null;
        }
        ts.forEach(this::enrich);
        ts.sort((a, b) -> b.getId() > a.getId() ? 1 : -1);
        return isEmptyList(ts) ? emptyList() : ts.subList(0, Math.min(size, ts.size()));
    }

    private void enrich(Tuan tuan) {
        long last = (tuan.getEndTs() - now()) / 1000;
        long hour = last / 3600;
        long mins = (last - hour * 3600) / 60;
        long secs = last % 60;
        tuan.setLast(String.format("%02d:%02d:%02d", hour, mins, secs));
    }

    @Transactional
    public Tuan create(WxUser user, String tag, ShopOrder order, int size) {
        Tuan tuan = new Tuan();
        tuan.setProductId(order.getProductId());
        tuan.setTotalSize(size);
        tuan.setTag(tag);
        tuan.setTitle(order.getTitle());
        tuan.setLeaderName(user.getUserName());
        tuan.setLeaderOrderId(order.getId());
        tuan.setLeaderId(user.getId());
        tuan.setLeaderAvatar(user.getAvatar());
        tuan.setEndTs(now() + 86400000L);
        tuan.setFollower("[]");
        tuan.setCurrentSize(1);
        tuan.setStatus(TuanStatus.INIT);
        tuan.setLeaderOpenId(user.getOpenId());
        return save(tuan);
    }

    @Transactional
    public Tuan join(WxUser user, ShopOrder order) {
        Tuan tuan = findById(order.getTuanId());
        tuan.setCurrentSize(tuan.getCurrentSize() + 1);
        tuan.setStatus(tuan.getCurrentSize() >= tuan.getTotalSize() ? TuanStatus.FULL : TuanStatus.INIT);
        List<TuanFollower> followers = JSONObject.parseArray(tuan.getFollower(), TuanFollower.class);
        TuanFollower follower = new TuanFollower();
        follower.setOpenId(user.getOpenId());
        follower.setOrderId(order.getId());
        follower.setAvatar(user.getAvatar());
        follower.setJoinTs(now());
        follower.setUserId(user.getId());
        follower.setUserName(user.getUserName());
        followers.add(follower);
        tuan.setFollower(toJSONString(followers));
        tuan = update(tuan);

        if (tuan.getStatus() == TuanStatus.FULL) {
            wxEventHandler.onTuanFull(tuan.getId(), order.getTitle(), order.getPrice(), tuan.getLeaderOpenId());
            for (TuanFollower f : followers) {
                wxEventHandler.onTuanFull(tuan.getId(), order.getTitle(), order.getPrice(), f.getOpenId());
            }
        }
        return tuan;
    }

    public List<Tuan> scanTimeOut() {
        List<Tuan> all = searchByProductIdAndStatus(null, TuanStatus.INIT, 999);
        return isEmptyList(all) ? null : all.stream().filter(p -> p.getCreated() < now() - 86400000L).collect(
            Collectors.toList());
    }

    public Tuan findByTuanId(Long t) {
        Tuan tuan = findById(t);
        enrich(tuan);
        return tuan;
    }
}
