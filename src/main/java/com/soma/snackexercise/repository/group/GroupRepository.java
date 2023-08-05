package com.soma.snackexercise.repository.group;

import com.soma.snackexercise.domain.group.Group;
import com.soma.snackexercise.util.constant.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {
    Boolean existsByCodeAndStatus(String code, Status status);

    Boolean existsByIdAndStatus(Long id, Status status);

    Optional<Group> findByIdAndStatus(Long id, Status status);

    List<Group> findAllByStatus(Status status);

    Optional<Group> findByCodeAndStatus(String code, Status status);

    List<Group> findAllByIsGoalAchievedAndStatus(Boolean isGoalAchieved, Status status);

    List<Group> findAllByCreatedAtNotNullAndIsGoalAchievedAndStatus(Boolean isGoalAchieved, Status status);

    /**
     * 상태가 ACTIVE 하면서 인자로 들어온 날짜보다 endAt 필드가 더 오래된 모든 그룹을 반환한다.
     * @param now
     * @param status
     * @return
     */
    List<Group> findAllByEndDateGreaterThanAndStatus(LocalDate now, Status status);
}
