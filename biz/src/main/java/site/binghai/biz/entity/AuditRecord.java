package site.binghai.biz.entity;

import lombok.Data;
import site.binghai.biz.enums.AuditTypeEnum;
import site.binghai.lib.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
public class AuditRecord extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;
    /**
     * 外部id，根据auditItemEnum确定是哪个表里的数据
     */
    private Long externalId;

    /**
     * 审核员id
     */
    private Long managerId;
    /**
     * 审核结果
     *
     * @see site.binghai.biz.enums.AuditStatusEnum
     */
    private Integer auditStatus;
    /**
     * 审核类型
     *
     * @see AuditTypeEnum
     */
    private Integer auditType;

    /**
     * 审核员附加信息，json
     * {
     *     "2018-10-17 00:00:00":"xxxx",
     *     "2018-10-17 00:00:00":"yyyy"
     * }
     * */
    @Column(columnDefinition = "TEXT")
    private String message;

}
