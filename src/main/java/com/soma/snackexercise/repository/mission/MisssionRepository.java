package com.soma.snackexercise.repository.mission;

import com.soma.snackexercise.domain.mission.Mission;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface MisssionRepository extends JpaRepository<Mission, Long> {
    @Query("SELECT count(*) AS cnt" +
            "    FROM Mission m" +
            "    WHERE m.exgroup.id = :exgroupId AND :today <= m.createdAt AND m.createdAt < :nextday AND m.endAt IS NOT NULL" +
            "    GROUP BY m.member" +
            "    ORDER BY cnt DESC" +
            "    LIMIT 1")
    Integer findCurrentFinishedRelayCountByGroupId(@Param("exgroupId") Long exgroupId, @Param("today") LocalDateTime today, @Param("nextday") LocalDateTime nextday);

    @Query("SELECT m" +
            "   FROM Mission m" +
            "   WHERE m.exgroup.id = :exgroupId AND :today <= m.createdAt AND m.createdAt < :nextday" +
            "   ORDER BY m.createdAt")
    List<Mission> findAllMissionByGroupIdAndCreatedAt(@Param("exgroupId") Long exgroupId, @Param("today") LocalDateTime today, @Param("nextday") LocalDateTime nextday);
}
