package com.soma.snackexercise.dto.group.response;


import com.soma.snackexercise.domain.group.Group;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JoinGroupResponse {
    private Long groupId;
    private String groupName;
    private Long currentMissionMemberId; // 그룹에서 현재 미션을 수행 중인 회원의 ID

    public static JoinGroupResponse toDto(Group group) {
        return new JoinGroupResponse(
                group.getId(),
                group.getName(),
                group.getCurrentDoingMemberId()
        );
    }
}
