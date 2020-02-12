package site.binghai.biz.entity;

import lombok.Data;
import site.binghai.lib.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;


@Data
@Entity
public class SharePage extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;
    //JSON
    @Column(columnDefinition = "TEXT")
    private String data;
    private String title;
    //自己看时的文案
    private String selfContent;
    //其他人看时的文案
    private String otherContent;
    private String targetUrl;
    private String buttonName;
    private String producerOpenId;
    private String consumerOpenId;
    // 有效期
    private String invalidTime;
    private Long invalidTs;
    private Boolean valid;
}
