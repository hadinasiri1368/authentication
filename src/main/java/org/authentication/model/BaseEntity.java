package org.authentication.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
@MappedSuperclass
public class BaseEntity {
    @Column(name = "inserted_date_time", updatable = false)
    private Date insertedDateTime;
    @Column(name = "inserted_user_id", updatable = false)
    private Long insertedUserId;
    @Column(name = "updated_date_time")
    private Date updatedDateTime;
    @Column(name = "updated_user_id")
    private Long updatedUserId;
}
