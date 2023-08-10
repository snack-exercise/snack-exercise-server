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
    /**
     * 지정한 멤버의 joinList를 삭제합니다.
     * @param member
     */
    void deleteByMember(Member member);

    /**
     * 주어진 그룹과 상태에 따른 joinList의 존재 여부를 확인합니다.
     * @param group
     * @param status
     * @return 존재 여부
     */
    Boolean existsByGroupAndStatus(Group group, Status status);

    /**
     * 주어진 그룹, 회원, 상태에 따른 joinList를 반환합니다.
     * @param group
     * @param member
     * @param status
     * @return 조건에 맞는 joinList
     */
    Optional<JoinList> findByGroupAndMemberAndStatus(Group group, Member member, Status status);

    /**
     * 주어진 그룹과 상태를 가진 참여 목록 중, 가장 먼저 들어온 joinList를 반환합니다.
     * @param group
     * @param status
     * @return 가장 먼저 들어온 joinList
     */
    Optional<JoinList> findFirstByGroupAndStatusOrderByCreatedAtAsc(Group group, Status status);

    /**
     * 지정된 그룹, 회원, 직책, 상태의 존재 여부를 확인합니다.
     * @param group
     * @param member
     * @param joinType
     * @param status
     * @return 그룹이 존재하면 TRUE, 그렇지 않으면 FALSE
     */
    Boolean existsByGroupAndMemberAndJoinTypeAndStatus(Group group, Member member, JoinType joinType, Status status);

    /**
     * 지정된 그룹의 현재 인원수를 조회합니다.
     * @param group
     * @return 그룹의 현재 인원수
     */
    @Query("SELECT count(*) FROM JoinList j WHERE j.group = :group AND j.outCount <= 1 AND j.status = 'ACTIVE'")
    Integer countByGroupAndOutCountLessThanOneAndStatusEqualsActive(@Param("group") Group group);

    /**
     * 주어진 그룹과 상태에 따른 joinList 목록을 반환합니다.
     * @param group
     * @param status
     * @return joinList 목록
     */
    List<JoinList> findByGroupAndStatus(Group group, Status status);

    /**
     * 미션 수행 횟수가 가장 적은 그룹원들을 조회합니다.
     * @param group 조회하려는 그룹
     * @return 미션 수행 횟수가 가장 적은 그룹원들
     */
    @Query("SELECT m FROM JoinList j " +
            "   JOIN j.member m" +
            "   WHERE j.group = :group AND j.status = 'ACTIVE'" +
            "         AND j.executedMissionCount = (SELECT MIN(j2.executedMissionCount) FROM JoinList j2 WHERE j2.group =: group)")
    List<Member> findMinExecutedMemberList(@Param("group") Group group);

    /**
     * 그룹, 회원, 상태를 기반으로 joinList의 존재 여부를 확인합니다.
     * @param group
     * @param member
     * @param status
     * @return 조건을 만족하는 joinList가 있으면 TRUE, 그렇지 않으면 FALSE
     */
    Boolean existsByGroupAndMemberAndStatus(Group group, Member member, Status status);

    /**
     * 그룹, 회원, 강퇴 횟수, 상태를 기반으로 강퇴 횟수가 지정된 숫자 이상인 joinList의 존재 여부를 확인합니다.
     * @param group
     * @param member
     * @param outCount
     * @param status
     * @return 지정된 상태와 강퇴 횟수가 지정된 숫자 이상인 joinList가 있으면 TRUE, 그렇지 않으면 FALSE
     */

    Boolean existsByGroupAndMemberAndOutCountGreaterThanEqualAndStatus(Group group, Member member, Integer outCount, Status status);


    /**
     * 회원이 현재 가입되어있으며, 종료되지 않은 그룹과 JoinList를 조회합니다.
     * @param member 회원
     * @return 회원이 가입한 JoinList와 Group
     */
    @Query("SELECT j from JoinList j JOIN FETCH j.group g WHERE j.member = :member AND j.status = 'ACTIVE'")
    List<JoinList> findAllActiveJoinGroupsByMember(@Param("member") Member member);

    /**
     * 회원이 가입했고, 종료된 그룹과 JoinList를 조회합니다.
     * @param member 회원
     * @return 회원이 가입했고, 종료된 JoinList와 Group
     */
    @Query("SELECT j from JoinList j JOIN FETCH j.group g WHERE j.member = :member AND j.status = 'INACTIVE'")
    List<JoinList> findAllInactiveJoinGroupsByMember(@Param("member") Member member);


    @Query("SELECT CASE WHEN COUNT(DISTINCT j.executedMissionCount) = 1 " +
            "THEN MAX(j.executedMissionCount) + 1 " +
            "ELSE MAX(j.executedMissionCount) END " +
            "FROM JoinList j " +
            "WHERE j.group = :group " +
            "AND j.status = :status ")
    Integer findMaxExecutedMissionCountByGroupAndStatus(@Param("group") Group group, @Param("status") Status status);

    @Query("SELECT MIN(j.executedMissionCount)" +
            "FROM JoinList j " +
            "WHERE j.group = :group " +
            "AND j.status = :status ")
    Integer findMinExecutedMissionCountByGroupAndStatus(@Param("group") Group group, @Param("status") Status status);

    @Query("SELECT COUNT(j) " +
            "FROM JoinList j " +
            "WHERE j.group = :group " +
            "AND j.status = :status " +
            "AND j.executedMissionCount = :currentFinishedRelayCount ")
    Integer findCurrentRoundPositionByGroupId(@Param("group") Group group, @Param("status") Status status, @Param("currentFinishedRelayCount") Integer currentFinishedRelayCount);

    /**
     * 그룹에서 목표 릴레이 횟수를 달성한 회원의 수를 반환합니다.
     * @param group 그룹
     * @return 그룹에서 목표 릴레이 횟수를 달성한 회원의 수
     */
    @Query("SELECT count(*) FROM JoinList j WHERE j.group = :group AND j.executedMissionCount = j.group.goalRelayNum AND j.status = 'ACTIVE'")
    Integer countGroupGoalAchievedMember(@Param("group") Group group);

    /**
     * 그룹의 회원 수를 반환합니다.
     * @param group 그룹
     * @return 그룹의 회원 수
     */
    @Query("SELECT count(*) FROM JoinList j WHERE j.group = :group AND j.status = 'ACTIVE'")
    Integer countGroupMember(@Param("group") Group group);

    @Query("SELECT j " +
            "FROM JoinList j JOIN FETCH j.group g " +
            "WHERE g.id = :groupId AND g.status = :status AND j.joinType = 'HOST'")
    Optional<JoinList> findHostJoinListByGroupIdAndStatus(@Param("groupId") Long groupId, @Param("status") Status status);
}
