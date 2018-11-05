package site.binghai.lib.utils;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by IceSea on 2018/4/30.
 * GitHub: https://github.com/IceSeaOnly
 * 模板消息构造器
 */
public class TplGenerator {
    private String url;
    private String toUser;
    private String tplId;
    private JSONObject data;

    private TplGenerator() {
        this.data = data = new JSONObject();
    }

    public TplGenerator(String tplId,String url, String toUser) {
        this.url = url;
        this.toUser = toUser;
        this.tplId = tplId;
    }

    public JSONObject build(){
        JSONObject msg = new JSONObject();
        msg.put("touser",toUser);
        msg.put("template_id",tplId);
        msg.put("url",url);
        msg.put("data",data);
        return msg;
    }

    public TplGenerator put(String key, String value) {
        JSONObject v = new JSONObject();
        v.put("value", value);
        data.put(key, v);
        return this;
    }

    public JSONObject getAll() {
        return data;
    }
}
