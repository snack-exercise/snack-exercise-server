package com.soma.snackexercise.dto.exgroup.response;

import com.soma.snackexercise.domain.exgroup.Exgroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExgroupCreateResponse {
    private Long id;
    private String name;

    public static ExgroupCreateResponse toDto(Exgroup exgroup) {
        return new ExgroupCreateResponse(
                exgroup.getId(),
                exgroup.getName()
        );
    }
}
