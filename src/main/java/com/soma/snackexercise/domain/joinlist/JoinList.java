package com.soma.snackexercise.domain.joinlist;

import com.soma.snackexercise.domain.BaseEntity;
import com.soma.snackexercise.domain.exgroup.Exgroup;
import com.soma.snackexercise.domain.member.Member;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
public class JoinList extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exgroupId")
    private Exgroup exgroup;

    @Enumerated(EnumType.STRING)
    private JoinType joinType;

    private Integer outCount;


    @Builder
    public JoinList(Member member, Exgroup exgroup, JoinType joinType) {
        this.member = member;
        this.exgroup = exgroup;
        this.joinType = joinType;
        this.outCount = 0;
        active();
    }
}
