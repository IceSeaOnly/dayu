package site.binghai.biz.entity.anywish;

import lombok.Data;
import site.binghai.biz.enums.AuditStatusEnum;
import site.binghai.lib.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
public class Wish extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;
    private String openId;
    @Column(columnDefinition = "TEXT")
    private String text;
    private String userName;
    private String phone;
    private String gender;
    private String age;
    /**
     * @see AuditStatusEnum
     */
    private Integer status;
    private Long auditId;
    private String nickName;
    private String wxAvatar;

    @Column(columnDefinition = "TEXT")
    private String reply;
}
