package site.binghai.shop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import site.binghai.lib.controller.BaseController;
import site.binghai.shop.entity.SearchHistory;
import site.binghai.shop.kv.RecommendSearchWord;
import site.binghai.shop.service.KvService;
import site.binghai.shop.service.SearchHistoryService;

import java.net.URLEncoder;
import java.util.List;
import java.util.Random;

/**
 * @date 2020/1/31 下午8:47
 **/
@RequestMapping("shop")
@Controller
public class SearchController extends BaseController {

    @Autowired
    private KvService kvService;
    @Autowired
    private SearchHistoryService searchHistoryService;

    @GetMapping("seacher")
    public String seacher(ModelMap map) {
        RecommendSearchWord word = kvService.get(RecommendSearchWord.class);
        List<String> words = emptyList();
        if (word != null && !hasEmptyString(word.getWords())) {
            for (String w : word.getWords().split(",")) {
                words.add(w);
            }
        }
        List<SearchHistory> top20 = searchHistoryService.findTop20ByUser(getUser().getId());
        map.put("myTop20", top20);
        map.put("hot20", searchHistoryService.findHot20());
        map.put("placeholder", randomGet(words));
        return "seacher";
    }

    private String randomGet(List<String> words) {
        if (isEmptyList(words)) {
            return "";
        }
        int size = words.size();
        Random random = new Random();
        return words.get(random.nextInt(size));
    }

    @GetMapping("search")
    public String search(String content) throws Exception {
        if (hasEmptyString(content)) {
            return "redirect:index";
        }
        searchHistoryService.create(getUser().getId(), content);
        return "redirect:searchProducts?search=" + URLEncoder.encode(content, "UTF-8");
    }

    @GetMapping("cleanSearch")
    public String cleanSearch() {
        searchHistoryService.cleanByUserId(getUser().getId());
        return "redirect:seacher";
    }

    @GetMapping("fastSearch")
    public String fastSearch(@RequestParam Long id) throws Exception {
        SearchHistory history = searchHistoryService.clean(id);
        return "redirect:searchProducts?search=" + URLEncoder.encode(history.getContent(), "UTF-8");
    }
}
