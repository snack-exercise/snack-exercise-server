package com.soma.snackexercise.repository.exgroup;

import com.soma.snackexercise.domain.exgroup.Exgroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExgroupRepository extends JpaRepository<Exgroup, Long> {
    Boolean existsByCode(String code);
}
