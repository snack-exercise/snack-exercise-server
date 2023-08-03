package com.soma.snackexercise.dto.notification.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SendManualReminderRequest {
    private Long groupId; // 해당 회원의 그룹 ID
}
