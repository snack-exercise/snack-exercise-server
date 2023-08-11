package com.soma.snackexercise.domain.notification;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationMessage {
    MANUAL_REMINDER("님이 당신의 운동을 기다리고 있어요!😃", "잠깐의 운동이 당신의 하루에 큰 변화를 가져올 거예요!💙"),
    AUTOMATIC_REMINDER_FOR_GROUPMEMBER("운동을 안하고 있어요!", "님이 지금 당장 운동할 수 있도록 독촉해보세요!👏🏻"),
    AUTOMATIC_REMINDER_TARGETMEMBER("그룹의 비타민 같은 미션 운동이 기다리고 있어요!🍊", "미션을 수행하고 다음 사람에게 미션을 넘겨보아요!😃"),
    ALLOCATE("그룹의 운동 미션 할당! 💪", "지금 당장 운동 미션을 확인하고 운동을 수행해보세요! 🔥"),
    GROUP_GOAL_ACHIEVE("그룹의 목표 달성! 🙌", "그룹의 목표 릴레이 횟수를 모두 달성했어요! 👍🏻"),
    GROUP_START("그룹이 시작되었습니다!🔥", "그룹의 목표 기간 내 목표 릴레이 횟수를 달성하기 위해 달려보아요!👏🏻"),
    GROUP_END("그룹이 종료되었어요!", "그룹의 운동 기간이 종료되었어요! 함께한 시간 동안 수고 많으셨습니다! 🙌");

    private final String title;
    private final String body;

    public String getTitleWithNickname(String nickname) {
        return nickname + title;
    }

    public String getBodyWithNickname(String nickname) {
        return nickname + body;
    }

    public String getTitleWithGroupName(String name) {
        return name + title;
    }

    public String getTitleWithGroupNameAndNickname(String groupName, String nickName) {
        return groupName + "그룹의 " + nickName + "님이" + title;
    }
}
