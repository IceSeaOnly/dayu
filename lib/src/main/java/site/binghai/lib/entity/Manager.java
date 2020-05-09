package site.binghai.lib.entity;

import lombok.Data;
import site.binghai.lib.interfaces.SessionPersistent;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

@Data
@Entity
public class Manager extends BaseEntity implements SessionPersistent {
    @Id
    @GeneratedValue
    private Long id;
    private String userName;
    private String passWord;
    private String nickName;
    private Boolean forbidden;
    private Boolean admin;

    @Transient
    private String schoolName;
}
