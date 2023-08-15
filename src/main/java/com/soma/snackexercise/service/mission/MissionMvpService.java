package com.soma.snackexercise.service.mission;

import com.soma.snackexercise.domain.exercise.Exercise;
import com.soma.snackexercise.domain.group.Group;
import com.soma.snackexercise.domain.mission.Mission;
import com.soma.snackexercise.dto.mission.request.MissionMvpStartRequest;
import com.soma.snackexercise.dto.mission.response.MissionStartResponse;
import com.soma.snackexercise.exception.exercise.ExerciseNotFoundException;
import com.soma.snackexercise.exception.group.InvalidGroupTimeException;
import com.soma.snackexercise.exception.mission.MissionNotFoundException;
import com.soma.snackexercise.repository.exercise.ExerciseRepository;
import com.soma.snackexercise.repository.mission.MissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
@Service
public class MissionMvpService {
    private final MissionRepository missionRepository;
    private final ExerciseRepository exerciseRepository;

    @Transactional
    public MissionStartResponse missionStart(MissionMvpStartRequest request) {
        Mission mission = missionRepository.findById(request.getMissionId()).orElseThrow(MissionNotFoundException::new);
        Exercise exercise = exerciseRepository.findById(request.getExerciseId()).orElseThrow(ExerciseNotFoundException::new);

        mission.startMission();
        mission.updateExercise(exercise);

        // 그룹의 시간 유효 시간 범위가 아니라면, 미션 시작 불가능
        Group group = mission.getGroup();
        if(!group.isCurrentTimeBetweenStartTimeAndEndTime(LocalTime.now())){
            throw new InvalidGroupTimeException();
        }
        log.info("[회원 릴레이 미션 시작시각] {}", mission.getStartAt());

        return MissionStartResponse.toDto(mission);
    }
}
