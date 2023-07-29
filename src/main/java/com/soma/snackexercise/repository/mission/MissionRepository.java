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
     * 특정 그룹 내에서 주어진 날짜 범위 안에 완료된 릴레이의 개수를 반환합니다.
     * @param exgroupId 그룹 ID
     * @param startDateTime 시작 날짜의 시간
     * @param endDateTime 종료 날짜의 시간
     * @return 완료된 릴레이의 횟수
     */
    @Query("SELECT count(*) AS cnt" +
            "    FROM Mission m" +
            "    WHERE m.exgroup.id = :exgroupId AND :startDateTime <= m.createdAt AND m.createdAt < :endDateTime AND m.endAt IS NOT NULL" +
            "    GROUP BY m.member" +
            "    ORDER BY cnt DESC" +
            "    LIMIT 1")
    Integer findCurrentFinishedRelayCountByGroupId(@Param("exgroupId") Long exgroupId, @Param("today") LocalDateTime startDateTime, @Param("nextday") LocalDateTime endDateTime);

    /**
     * 특정 그룹 내에서 주어진 날짜 범위에 생성된 모든 미션을 조회합니다.
     * @param exgroupId 그룹 ID
     * @param startDateTime 시작 날짜의 시간
     * @param endDateTime 종료 날짜의 시간
     * @return 조회된 미션 목록
     */
    @Query("SELECT m" +
            "   FROM Mission m JOIN FETCH m.member" +
            "   WHERE m.exgroup.id = :exgroupId AND :startDateTime <= m.createdAt AND m.createdAt < :endDateTime" +
            "   ORDER BY m.createdAt")
    List<Mission> findMissionsByGroupIdWithinDateRange(@Param("exgroupId") Long exgroupId, @Param("today") LocalDateTime startDateTime, @Param("nextday") LocalDateTime endDateTime);

    /**
     * 특정 그룹 내에서 주어진 날짜 범위 안에 완료된 모든 미션을 조회합니다.
     * @param exgroupId 그룹 ID
     * @param startDateTime 시작 날짜의 시간
     * @param endDateTime 종료 날짜의 시간
     * @return 조회된 미션 목록
     */
    @Query("SELECT m" +
            "   FROM Mission m JOIN FETCH m.member" +
            "   WHERE m.exgroup.id = :exgroupId AND :startDateTime <= m.createdAt AND m.createdAt < :endDateTime AND m.startAt IS NOT NULL" +
            "   ORDER BY m.createdAt")
    List<Mission> findExecutedMissionsByGroupIdWithinDateRange(@Param("exgroupId") Long exgroupId, @Param("today") LocalDateTime startDateTime, @Param("nextday") LocalDateTime endDateTime);
}
