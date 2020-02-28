package site.binghai.lib.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by IceSea on 2018/4/8. GitHub: https://github.com/IceSeaOnly
 */
public abstract class BaseBean extends MapUtils {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    protected String toJSONString(Object obj) {
        return JSONObject.toJSONString(obj);
    }

    protected JSONObject newJSONObject() {
        return new JSONObject();
    }

    protected JSONArray newJSONArray() {
        return new JSONArray();
    }

    protected List emptyList() {
        return new ArrayList();
    }

    public boolean isJSON(String s) {
        try {
            if (hasEmptyString(s)) {
                return false;
            }
            if (s.startsWith("{")) {
                JSONObject.parseObject(s);
                return true;
            } else if (s.startsWith("[")) {
                JSONArray.parseArray(s);
                return true;
            }
        } catch (Exception e) {}
        return false;
    }

    protected JSONObject toJsonObject(Object obj) {
        return JSONObject.parseObject(JSONObject.toJSONString(obj));
    }

    protected boolean isEmptyList(Collection list) {
        return CollectionUtils.isEmpty(list);
    }

    public <T> List<T> empty(List<T> items) {
        return isEmptyList(items) ? emptyList() : items;
    }

    public <T> List<T> subList(List<T> items, int size) {
        List<T> all = empty(items);
        if (isEmptyList(all)) {
            return emptyList();
        }
        return all.size() >= size ? all : all.subList(0, Math.min(all.size(), size));
    }

    public <T> T safeGet(List<T> list, int index) {
        if (isEmptyList(list)) { return null; }
        if (list.size() < index) { return null; }
        return list.get(index);
    }

    protected JSONArray toJSONArray(Collection collection) {
        JSONArray array = newJSONArray();
        if (!isEmptyList(collection)) {
            collection.forEach(v -> {
                array.add(toJsonObject(v));
            });
        }
        return array;
    }

    public long now() {
        return TimeTools.currentTS();
    }

    protected boolean hasEmptyString(Collection<String> strs) {
        for (String str : strs) {
            if (StringUtils.isEmpty(str)) { return false; }
        }
        return true;
    }

    protected boolean hasEmptyString(Object... strs) {
        for (Object str : strs) {
            if (str == null) { return true; }
            if (StringUtils.isEmpty(str.toString())) { return true; }
        }
        return false;
    }

}
