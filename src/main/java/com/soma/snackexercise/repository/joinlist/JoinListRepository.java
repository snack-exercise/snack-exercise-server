package com.soma.snackexercise.repository.joinlist;

import com.soma.snackexercise.domain.exgroup.Exgroup;
import com.soma.snackexercise.domain.joinlist.JoinList;
import com.soma.snackexercise.domain.joinlist.JoinType;
import com.soma.snackexercise.domain.member.Member;
import com.soma.snackexercise.util.constant.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface JoinListRepository extends JpaRepository<JoinList, Long> {
    Boolean existsByIdAndMemberAndJoinTypeAndStatus(Long id, Member member, JoinType joinType, Status status);

    @Query("SELECT count(*) FROM JoinList j WHERE j.exgroup = :exgroup AND j.outCount <= 1 AND j.status = 'ACTIVE'")
    Integer countByExgroupAndOutCountLessThanOneAndStatusEqualsActive(Exgroup exgroup);
}
