package com.soma.snackexercise.domain;

import com.soma.snackexercise.util.constant.Status;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

@Getter
@MappedSuperclass
public class BaseEntity extends BaseTimeEntity{
    @Enumerated(EnumType.STRING)
    private Status status; // 상태 (INACTIVE = 삭제)
    public void inActive() {
        this.status = Status.INACTIVE;
    }
    public void active(){
        this.status = Status.ACTIVE;
    }
}
