package com.soma.snackexercise.repository.joinlist;

import com.soma.snackexercise.domain.exgroup.Exgroup;
import com.soma.snackexercise.domain.joinlist.JoinList;
import com.soma.snackexercise.domain.joinlist.JoinType;
import com.soma.snackexercise.domain.member.Member;
import com.soma.snackexercise.util.constant.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface JoinListRepository extends JpaRepository<JoinList, Long> {
    void deleteByMember(Member member);

    Boolean existsByExgroupAndStatus(Exgroup exgroup, Status status);

    Optional<JoinList> findByExgroupAndMemberAndStatus(Exgroup exgroup, Member member, Status status);

    // exgroup와 status로 JoinList를 찾고, createdAt으로 오름차순 정렬하여 가장 첫번째 요소 반환
    Optional<JoinList> findFirstByExgroupAndStatusOrderByCreatedAtAsc(Exgroup exgroup, Status status);

    Boolean existsByExgroupAndMemberAndJoinTypeAndStatus(Exgroup exgroup, Member member, JoinType joinType, Status status);

    @Query("SELECT count(*) FROM JoinList j WHERE j.exgroup = :exgroup AND j.outCount <= 1 AND j.status = 'ACTIVE'")
    Integer countByExgroupAndOutCountLessThanOneAndStatusEqualsActive(Exgroup exgroup);
}
