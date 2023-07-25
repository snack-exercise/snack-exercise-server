package com.soma.snackexercise.repository.exgroup;

import com.soma.snackexercise.domain.exgroup.Exgroup;
import com.soma.snackexercise.exception.ExgroupNotFoundException;
import com.soma.snackexercise.util.constant.Status;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static com.soma.snackexercise.factory.entity.ExgroupFactory.createExgroup;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@DisplayName("ExgroupRepository JPA 동작 테스트")
class ExgroupRepositoryTest {
    @Autowired
    private ExgroupRepository exgroupRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @DisplayName("운동 그룹 코드 존재 여부 확인 테스트")
    void existsByCodeTest() {
        // given
        Exgroup exgroup = exgroupRepository.save(createExgroup());
        clear();
        String code = exgroup.getCode();

        // when
        Boolean exists = exgroupRepository.existsByCode(code);

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("운동 그룹 id와 status로 exgroup을 찾는 테스트")
    void findByIdAndStatusTest() {
        // given
        Exgroup exgroup = exgroupRepository.save(createExgroup());
        clear();

        // when
        Exgroup foundExgroup = exgroupRepository.findByIdAndStatus(exgroup.getId(), Status.ACTIVE).orElseThrow(ExgroupNotFoundException::new);

        // then
        assertThat(exgroup.getId()).isEqualTo(foundExgroup.getId());
    }

    void clear() {
        entityManager.flush();
        entityManager.clear();
    }
}