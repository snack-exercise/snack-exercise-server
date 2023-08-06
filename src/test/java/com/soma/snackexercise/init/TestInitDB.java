package com.soma.snackexercise.init;

import com.soma.snackexercise.domain.group.Group;
import com.soma.snackexercise.repository.group.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.soma.snackexercise.factory.entity.GroupFactory.createGroup;

@Component
public class TestInitDB {
    @Autowired
    private GroupRepository groupRepository;


    @Transactional
    public void initDB() {
        initExgroup();
    }

    private void initExgroup() {
        Group group1 = createGroup();
        Group group2 = createGroup();
        groupRepository.saveAll(List.of(group1, group2));
    }
}
