package com.nuvola.tpv.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import java.util.Date;


public @Data class Auditable<U>
{
    @CreatedBy
    private U createdBy;

    @CreatedDate
    private Date createdDate;

    @LastModifiedBy
    private U lastModifiedBy;

    @LastModifiedDate
    private Date lastModifiedDate;
}
