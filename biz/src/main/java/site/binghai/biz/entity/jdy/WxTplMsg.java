package site.binghai.biz.entity.jdy;

import lombok.Data;
import site.binghai.lib.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
public class WxTplMsg extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;
    private String openId;
    private String tplId;
    @Column(columnDefinition = "TEXT")
    private String url;
    @Column(columnDefinition = "TEXT")
    private String text;
    @Column(columnDefinition = "TEXT")
    private String sendResult;
}
