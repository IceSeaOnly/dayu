package site.binghai.shop.service;

import org.springframework.stereotype.Service;
import site.binghai.lib.service.BaseService;
import site.binghai.shop.entity.SearchHistory;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @date 2020/1/31 下午8:48
 **/
@Service
public class SearchHistoryService extends BaseService<SearchHistory> {

    public List<SearchHistory> findTop20ByUser(Long userId) {
        SearchHistory history = new SearchHistory();
        history.setUserId(userId);
        history.setCleaned(Boolean.FALSE);
        return subList(sortQuery(history, "id", true), 20);
    }

    public List<SearchHistory> findByUser(Long userId) {
        SearchHistory history = new SearchHistory();
        history.setUserId(userId);
        history.setCleaned(Boolean.FALSE);
        return empty(query(history));
    }

    @Transactional
    public void create(Long userId, String content) {
        if (content.length() > 6) {
            return;
        }
        List<SearchHistory> all = findByUser(userId);
        if (all.size() > 20) {
            all.get(0).setCleaned(Boolean.TRUE);
            update(all.get(0));
        }
        findTop20ByUser(userId);
        SearchHistory history = new SearchHistory();
        history.setUserId(userId);
        history.setCleaned(Boolean.FALSE);
        history.setContent(content);
        save(history);
    }

    @Transactional
    public void cleanByUserId(Long userId) {
        List<SearchHistory> all = findByUser(userId);
        for (SearchHistory history : all) {
            history.setCleaned(Boolean.TRUE);
            update(history);
        }
    }

    @Transactional
    public SearchHistory clean(Long id) {
        SearchHistory s = findById(id);
        s.setCleaned(Boolean.TRUE);
        update(s);
        return s;
    }

    public List<SearchHistory> findHot20() {
        return sortQuery(new SearchHistory(), "id", true);
    }
}
