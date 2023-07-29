package com.soma.snackexercise.repository.exgroup;

import com.soma.snackexercise.domain.exgroup.Exgroup;
import com.soma.snackexercise.util.constant.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExgroupRepository extends JpaRepository<Exgroup, Long> {
    Boolean existsByCode(String code);

    Optional<Exgroup> findByIdAndStatus(Long id, Status status);

    List<Exgroup> findAllByStatus(Status status);
}
