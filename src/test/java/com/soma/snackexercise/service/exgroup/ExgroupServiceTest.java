package com.soma.snackexercise.service.exgroup;

import com.soma.snackexercise.domain.exgroup.Exgroup;
import com.soma.snackexercise.dto.exgroup.request.ExgroupCreateRequest;
import com.soma.snackexercise.dto.exgroup.response.ExgroupCreateResponse;
import com.soma.snackexercise.dto.exgroup.response.ExgroupResponse;
import com.soma.snackexercise.exception.ExgroupNotFoundException;
import com.soma.snackexercise.exception.MemberNotFoundException;
import com.soma.snackexercise.repository.exgroup.ExgroupRepository;
import com.soma.snackexercise.repository.joinlist.JoinListRepository;
import com.soma.snackexercise.repository.member.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.soma.snackexercise.factory.dto.ExgroupCreateFactory.createExgroupCreateRequest;
import static com.soma.snackexercise.factory.entity.ExgroupFactory.createExgroup;
import static com.soma.snackexercise.factory.entity.MemberFactory.createMember;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("ExgroupService 비즈니스 로직 테스트")
class ExgroupServiceTest {
    @InjectMocks
    private ExgroupService exgroupService;
    @Mock
    private ExgroupRepository exgroupRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private JoinListRepository joinListRepository;

    private String email = "test@naver.com";

    @Test
    @DisplayName("운동 그룹 생성 메소드가 제대로 동작하는지 확인하는 테스트")
    void createTest() {
        //given
        ExgroupCreateRequest request = createExgroupCreateRequest();
        given(memberRepository.findByEmailAndStatus(anyString(), any())).willReturn(Optional.of(createMember()));
        given(exgroupRepository.existsByCode(anyString())).willReturn(Boolean.FALSE);

        // when
        ExgroupCreateResponse response = exgroupService.create(request, email);

        // then
        assertThat(request.getName()).isEqualTo(response.getName());
    }

    @Test
    @DisplayName("운동 그룹 생성 메소드에서 그룹 생성할 회원 조회 예외 클래스 발생 테스트")
    void createExceptionByMemberNotFoundTest() {
        // given
        given(memberRepository.findByEmailAndStatus(anyString(), any())).willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> exgroupService.create(createExgroupCreateRequest(), email)).isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    @DisplayName("운동 그룹 생성 메소드에서 그룹 코드 중복 검사 테스트")
    void createDuplicateGroupCodeTest() {
        // given
        given(memberRepository.findByEmailAndStatus(anyString(), any())).willReturn(Optional.of(createMember()));
        given(exgroupRepository.existsByCode(anyString())).willReturn(Boolean.TRUE, Boolean.TRUE, Boolean.FALSE);

        // when
        exgroupService.create(createExgroupCreateRequest(), email);

        // then
        verify(exgroupRepository, times(3)).existsByCode(anyString());
    }

    @Test
    @DisplayName("운동 그룹 조회 메소드 테스트")
    void readTest() {
        // given
        Exgroup exgroup = createExgroup();
        given(exgroupRepository.findByIdAndStatus(anyLong(), any())).willReturn(Optional.ofNullable(exgroup));

        // when
        ExgroupResponse response = exgroupService.read(1L);

        // then
        assertThat(response.getName()).isEqualTo(exgroup.getName());
    }

    @Test
    @DisplayName("운동 그룹 조회 메소드에서 찾을 수 없을 때 예외 클래스 발생 테스트")
    void readExceptionByExgroupNotFoundTest() {
        // given
        given(exgroupRepository.findByIdAndStatus(anyLong(), any())).willReturn(Optional.ofNullable(null));

        // when, then
        assertThatThrownBy(() -> exgroupService.read(1L)).isInstanceOf(ExgroupNotFoundException.class);
    }


}