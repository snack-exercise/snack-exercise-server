package com.soma.snackexercise.domain.joinlist;

import com.soma.snackexercise.domain.BaseEntity;
import com.soma.snackexercise.domain.group.Group;
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
    @JoinColumn(name = "groupId")
    private Group group;

    @Enumerated(EnumType.STRING)
    private JoinType joinType;

    private Integer outCount;

    private Integer executedMissionCount;

    public void addOneOutCount() {
        this.outCount += 1;
    }
    public void addOneExecutedMissionCountCount() {
        this.executedMissionCount += 1;
    }

    public void promoteToHost() {
        this.joinType = JoinType.HOST;
    }

    @Builder
    public JoinList(Member member, Group group, JoinType joinType) {
        this.member = member;
        this.group = group;
        this.joinType = joinType;
        this.outCount = 0;
        this.executedMissionCount = 0;
        active();
    }
}
