package com.soma.snackexercise.repository.mission;

import com.soma.snackexercise.domain.member.Member;
import com.soma.snackexercise.domain.mission.Mission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Mission 데이터에 접근하는 데 사용되는 Repository 인터페이스
 */
public interface MissionRepository extends JpaRepository<Mission, Long> {

    /**
     * 특정 회원 (Member)의 모든 미션을 삭제합니다.
     * @param member 삭제할 미션의 회원
     */
    void deleteByMember(Member member);

    /**
     * 그룹원들 중에서 특정 기간 동안 미션을 가장 많이 수행한 사람의 미션수행완료횟수를 반환합니다.
     * @param exgroupId 그룹 ID
     * @param startDateTime 시작 일자
     * @param endDateTime 종료 일자
     * @return 특정 기간 동안, 그룹 내 최대 미션수행완료 횟수
     */
    @Query("SELECT count(*) AS cnt" +
            "    FROM Mission m" +
            "    WHERE m.exgroup.id = :exgroupId AND :startDateTime <= m.createdAt AND m.createdAt < :endDateTime AND m.endAt IS NOT NULL" +
            "    GROUP BY m.member" +
            "    ORDER BY cnt DESC" +
            "    LIMIT 1")
    Integer findCurrentFinishedRelayCountByGroupId(@Param("exgroupId") Long exgroupId, @Param("startDateTime") LocalDateTime startDateTime, @Param("endDateTime") LocalDateTime endDateTime);

    /**
     * 특정 기간 동안 하나의 그룹의 모든 그룹원들에게 할당된 미션 리스트를 반환합니다. (미수행 미션 + 수행 완료 미션 모두 포함)
     * @param exgroupId 그룹 ID
     * @param startDateTime 시작 일자
     * @param endDateTime 종료 일자
     * @return 특정 기간 동안 하나의 그룹의 모든 그룹원에게 할당된 미션 리스트
     */
    @Query("SELECT m" +
            "   FROM Mission m JOIN FETCH m.member" +
            "   WHERE m.exgroup.id = :exgroupId AND :startDateTime <= m.createdAt AND m.createdAt < :endDateTime" +
            "   ORDER BY m.createdAt")

    List<Mission> findMissionsByGroupIdWithinDateRange(@Param("exgroupId") Long exgroupId, @Param("startDateTime") LocalDateTime startDateTime, @Param("endDateTime") LocalDateTime endDateTime);


    /**
     *  특정 기간 동안 하나의 그룹의 모든 그룹원들이 수행 완료한 미션 리스트를 반환합니다.
     * @param exgroupId 그룹 ID
     * @param startDateTime 시작 일자
     * @param endDateTime 종료 일자
     * @return 특정 기간 동안 하나의 그룹의 모든 그룹원들이 수행 완료한 미션 리스트
     */
    @Query("SELECT m" +
            "   FROM Mission m JOIN FETCH m.member" +
            "   WHERE m.exgroup.id = :exgroupId AND :startDateTime <= m.createdAt AND m.createdAt < :endDateTime AND m.startAt IS NOT NULL" +
            "   ORDER BY m.createdAt")
    List<Mission> findFinishedMissionsByGroupIdWithinDateRange(@Param("exgroupId") Long exgroupId, @Param("startDateTime") LocalDateTime startDateTime, @Param("endDateTime") LocalDateTime endDateTime);
}
