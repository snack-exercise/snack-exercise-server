package com.soma.snackexercise.dto.group.response;

import com.soma.snackexercise.domain.group.Group;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GroupCreateResponse {
    private Long id;
    private String name;

    public static GroupCreateResponse toDto(Group group) {
        return new GroupCreateResponse(
                group.getId(),
                group.getName()
        );
    }
}
