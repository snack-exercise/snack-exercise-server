package com.soma.snackexercise.domain.mission;


import com.soma.snackexercise.domain.BaseTimeEntity;
import com.soma.snackexercise.domain.exercise.Exercise;
import com.soma.snackexercise.domain.group.Group;
import com.soma.snackexercise.domain.member.Member;
import com.soma.snackexercise.exception.CalculateMinutesDiffException;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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
    @JoinColumn(name = "groupId")
    private Group group;

    private Integer calory; // 소모 칼로리

    private LocalDateTime startAt; // 운동 시작 일시

    private LocalDateTime endAt; // 운동 종료 일시

    private Integer alarmCount; // 독촉 알람 횟수


    @Builder
    public Mission(Exercise exercise, Member member, Group group) {
        this.exercise = exercise;
        this.member = member;
        this.group = group;
        this.alarmCount = 0;
    }

    /**
     * 독촉 알람 횟수 1 증가
     */
    public void addOneAlarmCount() {
        this.alarmCount += 1;
    }


    /**
     * 미션 시작 버튼 클릭시, 미션 시작 시간 기록
     */
    public void startMission(){
        this.startAt = LocalDateTime.now();
    }

    /**
     * 미션 중단 시, startAt 초기화
     */
    public void cancelMission() {
        this.startAt = null;
    }

    /**
     * 운동 종료시, 소모 칼로리와 운동 종료 시각을 기록
     * @param calory 소모 칼로리
     * @param lengthOfVideo 운동 영상 길이
     */
    public void endMission(Integer calory, Long lengthOfVideo){
        this.endAt = this.startAt.plusMinutes(lengthOfVideo);
        this.calory = calory;
    }

    /**
     * 운동 할당 시각과 운동 시각 사이의 시각 차이를 계산
     * @return 운동 할당 시각과 운동 시작 시각의 시간 차이
     */
    public Long calculateMinutesDiffBetweenCreateAndStart() {
        if(getStartAt() == null || getCreatedAt() == null){
            throw new CalculateMinutesDiffException();
        }

        return ChronoUnit.MINUTES.between(getCreatedAt(), getStartAt());
    }

    public Boolean isValidTimeReminder(LocalDateTime now, Integer checkIntervalTime, Integer scheduledFixedRate) {
        long differenceInMinutes = ChronoUnit.MINUTES.between(getCreatedAt(), now); // (createdAt - now) % checkIntervalTime
        long reminderTime = differenceInMinutes % checkIntervalTime;

        System.out.println("[시각 차이] : differenceInMinutes = " + differenceInMinutes);

        if(differenceInMinutes < 0) {
            return false;
        }

        return reminderTime < scheduledFixedRate;
    }
}
