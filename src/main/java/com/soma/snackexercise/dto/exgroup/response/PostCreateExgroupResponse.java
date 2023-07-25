package com.soma.snackexercise.dto.exgroup.response;

import com.soma.snackexercise.domain.exgroup.Exgroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostCreateExgroupResponse {
    private Long id;
    private String name;

    public static PostCreateExgroupResponse toDto(Exgroup exgroup) {
        return new PostCreateExgroupResponse(
                exgroup.getId(),
                exgroup.getName()
        );
    }
}
