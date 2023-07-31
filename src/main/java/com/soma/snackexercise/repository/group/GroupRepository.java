package com.soma.snackexercise.repository.group;

import com.soma.snackexercise.domain.group.Group;
import com.soma.snackexercise.util.constant.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {
    Boolean existsByCodeAndStatus(String code, Status status);

    Boolean existsByIdAndStatus(Long id, Status status);

    Optional<Group> findByIdAndStatus(Long id, Status status);

    List<Group> findAllByStatus(Status status);
}
