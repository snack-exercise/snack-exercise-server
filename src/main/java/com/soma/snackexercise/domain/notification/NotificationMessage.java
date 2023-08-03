package com.soma.snackexercise.domain.notification;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationMessage {
    REMINDER("님이 운동을 안하고 있어요!", "님이 지금 당장 운동할 수 있도록 독촉해보세요!"),
    ALLOCATE("운동 미션 할당! 💪", "지금 당장 운동 미션을 확인하고 운동을 수행해보세요! 🔥"),
    GROUP_GOAL_ACHIEVE("그룹 목표 달성! 🙌", "그룹의 목표 릴레이 횟수를 모두 달성했어요! 👍🏻");

    private final String title;
    private final String body;

    public String getTitleWithNickname(String nickname) {
        return nickname + title;
    }

    public String getBodyWithNickname(String nickname) {
        return nickname + body;
    }
}
