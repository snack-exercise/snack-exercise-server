package com.soma.snackexercise.init;

import com.soma.snackexercise.domain.exgroup.Exgroup;
import com.soma.snackexercise.repository.exgroup.ExgroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.soma.snackexercise.factory.entity.ExgroupFactory.createExgroup;

@Component
public class TestInitDB {
    @Autowired
    private ExgroupRepository exgroupRepository;


    @Transactional
    public void initDB() {
        initExgroup();
    }

    private void initExgroup() {
        Exgroup exgroup1 = createExgroup();
        Exgroup exgroup2 = createExgroup();
        exgroupRepository.saveAll(List.of(exgroup1, exgroup2));
    }
}
