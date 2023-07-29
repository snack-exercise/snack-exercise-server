package com.soma.snackexercise.domain.mission;


import com.soma.snackexercise.domain.BaseTimeEntity;
import com.soma.snackexercise.domain.exercise.Exercise;
import com.soma.snackexercise.domain.exgroup.Exgroup;
import com.soma.snackexercise.domain.member.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
public class Mission extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exerciseId")
    private Exercise exercise;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exgroupId")
    private Exgroup exgroup;

    private Integer calory; // 소모 칼로리

    private LocalDateTime startAt; // 운동 시작 일시

    private LocalDateTime endAt; // 운동 종료 일시

    private Integer alarmCount; // 독촉 알람 횟수


    @Builder
    public Mission(Exercise exercise, Member member, Exgroup exgroup, Integer alarmCount) {
        this.exercise = exercise;
        this.member = member;
        this.exgroup = exgroup;
        this.alarmCount = 0;
    }

    public void startMission(){
        this.startAt = LocalDateTime.now();
    }

    public void endMission(Integer calory){
        this.endAt = LocalDateTime.now();
        this.calory = calory;
    }
}
