package site.binghai.lib.entity;

import site.binghai.lib.utils.TimeTools;

import javax.persistence.MappedSuperclass;

/**
 * 具有支付状态的实体请继承 PayBizEntity
 *
 * @see PayBizEntity
 */

@MappedSuperclass
public abstract class BaseEntity {
    private Long created;
    private Long updated;
    private Long schoolId;
    private String createdTime;
    private String updatedTime;
    private Boolean isDeleted;

    public BaseEntity() {
        created = TimeTools.currentTS();
        createdTime = TimeTools.format(created);

        updated = created;
        updatedTime = createdTime;

        isDeleted = Boolean.FALSE;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public Long getUpdated() {
        return updated;
    }

    public void setUpdated(Long updated) {
        this.updated = updated;
        this.updatedTime = TimeTools.format(updated);
    }

    public String getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(String updatedTime) {
        this.updatedTime = updatedTime;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public Long getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(Long schoolId) {
        this.schoolId = schoolId;
    }

    public abstract Long getId();

    public abstract void setId(Long id);
}

