package com.soma.snackexercise.service.notification;

import com.soma.snackexercise.domain.group.Group;
import com.soma.snackexercise.domain.member.Member;
import com.soma.snackexercise.dto.member.request.MemberUpdateFcmTokenRequest;
import com.soma.snackexercise.dto.member.response.MemberUpdateFcmTokenResponse;
import com.soma.snackexercise.dto.notification.request.SendManualReminderRequest;
import com.soma.snackexercise.exception.group.GroupNotFoundException;
import com.soma.snackexercise.exception.member.MemberNotFoundException;
import com.soma.snackexercise.repository.group.GroupRepository;
import com.soma.snackexercise.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.soma.snackexercise.domain.notification.NotificationMessage.MANUAL_REMINDER;
import static com.soma.snackexercise.util.constant.Status.ACTIVE;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class NotificationService {
    private final MemberRepository memberRepository;
    private final GroupRepository groupRepository;
    private final FirebaseCloudMessageService firebaseCloudMessageService;

    /**
     * 로그인한 회원의 fcm 토큰을 저장합니다.
     * @param request 회원의 fcm 토큰이 포함된 request
     * @param email 로그인한 회원의 email
     * @return
     */
    @Transactional
    public MemberUpdateFcmTokenResponse updateFcmToken(MemberUpdateFcmTokenRequest request, String email){
        Member member = memberRepository.findByEmailAndStatus(email, ACTIVE).orElseThrow(MemberNotFoundException::new);
        member.updateFcmToken(request.getFcmToken());
        return MemberUpdateFcmTokenResponse.toDto(member);
    }

    /**
     * 로그인한 회원이 독촉하기 버튼을 클릭한 그룹의 현재 미션 수행중인 회원에게 독촉 푸시 알림을 전송합니다.
     * @param request 독촉하기 버튼을 클릭한 그룹의 groupID가 포함된 requset
     * @param email 로그인한 회원의 email
     */
    public void sendManualReminderAlarm(SendManualReminderRequest request, String email) {
        // 그룹의 현재 미션 수행 중인 회원 조회
        Group group = groupRepository.findById(request.getGroupId()).orElseThrow(GroupNotFoundException::new);
        Member targetMember = memberRepository.findByIdAndStatus(group.getCurrentDoingMemberId(), ACTIVE).orElseThrow(MemberNotFoundException::new);

        // 해당 회원에게 알림 전송
        Member member = memberRepository.findByEmailAndStatus(email, ACTIVE).orElseThrow(MemberNotFoundException::new);
        firebaseCloudMessageService.sendByToken(targetMember.getFcmToken(), MANUAL_REMINDER.getTitleWithNickname(member.getNickname()), MANUAL_REMINDER.getBody());
    }
}
