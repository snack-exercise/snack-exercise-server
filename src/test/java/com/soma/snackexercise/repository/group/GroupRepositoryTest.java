package com.soma.snackexercise.repository.group;

import com.soma.snackexercise.domain.group.Group;
import com.soma.snackexercise.exception.GroupNotFoundException;
import com.soma.snackexercise.util.constant.Status;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static com.soma.snackexercise.factory.entity.GroupFactory.createExgroup;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@DisplayName("ExgroupRepository JPA 동작 테스트")
class GroupRepositoryTest {
    @Autowired
    private GroupRepository groupRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @DisplayName("운동 그룹 코드 존재 여부 확인 테스트")
    void existsByCodeTest() {
        // given
        Group group = groupRepository.save(createExgroup());
        clear();
        String code = group.getCode();

        // when
        Boolean exists = groupRepository.existsByCodeAndStatus(code, Status.ACTIVE);

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("운동 그룹 id와 status로 exgroup을 찾는 테스트")
    void findByIdAndStatusTest() {
        // given
        Group group = groupRepository.save(createExgroup());
        clear();

        // when
        Group foundGroup = groupRepository.findByIdAndStatus(group.getId(), Status.ACTIVE).orElseThrow(GroupNotFoundException::new);

        // then
        assertThat(group.getId()).isEqualTo(foundGroup.getId());
    }

    void clear() {
        entityManager.flush();
        entityManager.clear();
    }
}