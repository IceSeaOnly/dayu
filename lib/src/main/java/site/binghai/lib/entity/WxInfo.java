package site.binghai.lib.entity;

import lombok.Data;

@Data
public class WxInfo {
    //用户的标识，对当前公众号唯一
    private String openid;
    //用户的昵称
    private String nickname;
    //用户的性别，值为1时是男性，值为2时是女性，值为0时是未知
    private String sex;
    //用户所在城市
    private String city;
    //用户所在国家
    private String country;
    //用户所在省份
    private String province;
    //用户头像，用户没有头像时该项为空。若用户更换头像，原有头像URL将失效。
    private String headimgurl;
    //用户关注时间，为时间戳。如果用户曾多次关注，则取最后关注时间
    private String subscribe_time;
    //用户是否订阅该公众号标识，值为0时，代表此用户没有关注该公众号，拉取不到其余信息。
    private String subscribe;
    /**
     * 返回用户关注的渠道来源 ADD_SCENE_SEARCH 公众号搜索， ADD_SCENE_ACCOUNT_MIGRATION 公众号迁移， ADD_SCENE_PROFILE_CARD 名片分享，
     * ADD_SCENE_QR_CODE 扫描二维码， ADD_SCENEPROFILE LINK 图文页内名称点击， ADD_SCENE_PROFILE_ITEM 图文页右上角菜单， ADD_SCENE_PAID 支付后关注，
     * ADD_SCENE_OTHERS 其他
     */
    private String subscribe_scene;

    public boolean isSubscribed() {
        return !"0".equals(subscribe);
    }

}
