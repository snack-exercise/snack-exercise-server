package com.soma.snackexercise.repository.joinlist;

import com.soma.snackexercise.domain.group.Group;
import com.soma.snackexercise.domain.joinlist.JoinList;
import com.soma.snackexercise.domain.joinlist.JoinType;
import com.soma.snackexercise.domain.member.Member;
import com.soma.snackexercise.util.constant.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JoinListRepository extends JpaRepository<JoinList, Long> {
    void deleteByMember(Member member);

    Boolean existsByGroupAndStatus(Group group, Status status);

    Optional<JoinList> findByGroupAndMemberAndStatus(Group group, Member member, Status status);

    // exgroup와 status로 JoinList를 찾고, createdAt으로 오름차순 정렬하여 가장 첫번째 요소 반환
    Optional<JoinList> findFirstByGroupAndStatusOrderByCreatedAtAsc(Group group, Status status);

    Boolean existsByGroupAndMemberAndJoinTypeAndStatus(Group group, Member member, JoinType joinType, Status status);

    @Query("SELECT count(*) FROM JoinList j WHERE j.group = :group AND j.outCount <= 1 AND j.status = 'ACTIVE'")
    Integer countByGroupAndOutCountLessThanOneAndStatusEqualsActive(@Param("group") Group group);

    List<JoinList> findByGroupAndStatus(Group group, Status status);

    // 미션 수행 횟수 가장 적은 그룹원들
    @Query("SELECT m FROM JoinList j " +
            "   JOIN j.member m" +
            "   WHERE j.group = :group AND j.status = 'ACTIVE'" +
            "         AND j.executedMissionCount = (SELECT MIN(j2.executedMissionCount) FROM JoinList j2 WHERE j2.group =: group)")
    List<Member> findMinExecutedMemberList(@Param("group") Group group);
}
