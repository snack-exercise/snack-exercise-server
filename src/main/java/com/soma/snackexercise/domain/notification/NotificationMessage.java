package com.soma.snackexercise.domain.notification;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationMessage {
    ALLOCATE("운동 미션 할당! 💪", "지금 당장 운동 미션을 확인하고 운동을 수행해보세요! 🔥"),
    GROUP_GOAL_ACHIEVE("그룹 목표 달성! 🙌", "그룹의 목표 릴레이 횟수를 모두 달성했어요! 👍🏻");

    private final String title;
    private final String body;
}
