ALTER TABLE Exgroup CONVERT TO CHARACTER SET utf8mb4;
ALTER TABLE Member CONVERT TO CHARACTER SET utf8mb4;

-- INSERT MEMBER
INSERT INTO Member (id, name, profileImage, nickname, gender, socialType, email, fcmToken, status, createdAt, updatedAt, birthYear) VALUES (1, '오진서', null, '진서', 'MALE', 'KAKAO', 'jinseo@gmail.com', null, 'ACTIVE', now(), now(), '1999');
INSERT INTO Member (id, name, profileImage, nickname, gender, socialType, email, fcmToken, status, createdAt, updatedAt, birthYear) VALUES (2, '정희수', null, '희수', 'FEMALE', 'KAKAO', 'heesu@gmail.com', null, 'ACTIVE', now(), now(), '2001');
INSERT INTO Member (id, name, profileImage, nickname, gender, socialType, email, fcmToken, status, createdAt, updatedAt, birthYear) VALUES (3, '김민정', null, '민정', 'FEMALE', 'KAKAO', 'minjung@gmail.com', null, 'ACTIVE', now(), now(), '2000');
INSERT INTO Member (id, name, profileImage, nickname, gender, socialType, email, fcmToken, status, createdAt, updatedAt, birthYear) VALUES (4, '오해원', null, '해원', 'FEMALE', 'KAKAO', 'haewon@gmail.com', null, 'ACTIVE', now(), now(), '2004');
INSERT INTO Member (id, name, profileImage, nickname, gender, socialType, email, fcmToken, status, createdAt, updatedAt, birthYear) VALUES (5, '설윤아', null, '설윤', 'FEMALE', 'KAKAO', 'yoona@gmail.com', null, 'ACTIVE', now(), now(), '2004');
INSERT INTO Member (id, name, profileImage, nickname, gender, socialType, email, fcmToken, status, createdAt, updatedAt, birthYear) VALUES (6, '이영지', null, 'youngji2002', 'FEMALE', 'KAKAO', 'youngzi@gmail.com', null, 'ACTIVE', now(), now(), '2002');
INSERT INTO Member (id, name, profileImage, nickname, gender, socialType, email, fcmToken, status, createdAt, updatedAt, birthYear) VALUES (7, '박보검', null, 'park', 'MALE', 'KAKAO', 'park@gmail.com', null, 'ACTIVE', now(), now(), '1993');

-- INSERT EXGROUP
INSERT INTO Exgroup (id, name, emozi, color, maxMemberNum, startTime, endTime, description, penalty, code, createdAt, updatedAt, currentDoingMemberId, checkIntervalTime, checkMaxNum, startDate, endDate, goalRelayNum, existDays, status) VALUES (1, '운동하자', '🍉', '#101010', 6, '09:00:00', '18:00:00', '저희 그룹은 2주동안 매일매일 운동하는 것을 목표로합니다.', '꼴등이 1등한테 스벅 깊티 쏘기
', '105236', now(), now(), null, 10, 2, NULL, NULL, 14, 14, 'ACTIVE');
INSERT INTO Exgroup (id, name, emozi, color, maxMemberNum, startTime, endTime, description, penalty, code, createdAt, updatedAt, currentDoingMemberId, checkIntervalTime, checkMaxNum, startDate, endDate, goalRelayNum, existDays, status) VALUES (2, '짧고굵게', '☘️', '#638391', 4, '10:00:00', '16:00:00', '짧고 굵게 딱 1주일 동안 운동하자!
', '꼴등 스쿼트 50개 영상찍어 올리기', '156398', now(), now(), null, 10, 3, NULL, NULL, 14, 8, 'ACTIVE');

-- INSERT JOINLIST
INSERT INTO JoinList (id, memberId, groupId, createdAt, joinType, updatedAt, outCount, status, executedMissionCount) VALUES (1, 1, 1, now(), 'HOST', now(), 0, 'ACTIVE', 0);
INSERT INTO JoinList (id, memberId, groupId, createdAt, joinType, updatedAt, outCount, status, executedMissionCount) VALUES (2, 1, 2, now(), 'MEMBER', now(), 0, 'ACTIVE', 1);
INSERT INTO JoinList (id, memberId, groupId, createdAt, joinType, updatedAt, outCount, status, executedMissionCount) VALUES (3, 2, 1, now(), 'MEMBER', now(), 0, 'ACTIVE', 1);
INSERT INTO JoinList (id, memberId, groupId, createdAt, joinType, updatedAt, outCount, status, executedMissionCount) VALUES (4, 3, 1, now(), 'MEMBER', now(), 0, 'ACTIVE', 1);
INSERT INTO JoinList (id, memberId, groupId, createdAt, joinType, updatedAt, outCount, status, executedMissionCount) VALUES (5, 4, 2, now(), 'HOST', now(), 0, 'ACTIVE', 0);
INSERT INTO JoinList (id, memberId, groupId, createdAt, joinType, updatedAt, outCount, status, executedMissionCount) VALUES (6, 5, 1, now(), 'MEMBER', now(), 0, 'ACTIVE', 1);
INSERT INTO JoinList (id, memberId, groupId, createdAt, joinType, updatedAt, outCount, status, executedMissionCount) VALUES (7, 6, 1, now(), 'MEMBER', now(), 0, 'ACTIVE', 0);
INSERT INTO JoinList (id, memberId, groupId, createdAt, joinType, updatedAt, outCount, status, executedMissionCount) VALUES (8, 7, 1, now(), 'MEMBER', now(), 0, 'ACTIVE', 0);

-- INSERT EXERCISE
INSERT INTO Exercise (minPerKcal, createdAt, id, updatedAt, description, exerciseCategory, name, status, videoLink) VALUES (10, now(), 1, now(), '어디서나 할 수 있는 전신 운동 입니다
', 'EXERCISE', '전신 운동', 'ACTIVE', 'https://www.youtube.com/shorts/GA2DFir8fbo');
INSERT INTO Exercise (minPerKcal, createdAt, id, updatedAt, description, exerciseCategory, name, status, videoLink) VALUES (15, now(), 2, now(), '서서하는 복근 운동
', 'EXERCISE', '서서하는 복근 운동
', 'ACTIVE', 'https://www.youtube.com/shorts/m-kAmwkanoI');
INSERT INTO Exercise (minPerKcal, createdAt, id, updatedAt, description, exerciseCategory, name, status, videoLink) VALUES (20, now(), 3, now(), '하체 운동
', 'EXERCISE', '하체 운동
', 'ACTIVE', 'https://www.youtube.com/shorts/l1CEorIKDcE');
INSERT INTO Exercise (minPerKcal, createdAt, id, updatedAt, description, exerciseCategory, name, status, videoLink) VALUES (10, now(), 4, now(), '구석구석 유산소
', 'EXERCISE', '구석구석 유산소', 'ACTIVE', 'https://www.youtube.com/shorts/yDwnyQeLB8c');
INSERT INTO Exercise (minPerKcal, createdAt, id, updatedAt, description, exerciseCategory, name, status, videoLink) VALUES (10, now(), 5, now(), '복근 운동', 'EXERCISE', '복근 운동', 'ACTIVE', 'https://www.youtube.com/shorts/1K0Ono8vo1o');

-- INSERT MISSION
INSERT INTO Mission (alarmCount, calory, createdAt, endAt, exerciseId, groupId, id, memberId, startAt, updatedAt) VALUES (0, 10, now(), TIMESTAMPADD(MINUTE, 15, now()), 1, 1, 1, 1, TIMESTAMPADD(MINUTE, 10, now()), now());
INSERT INTO Mission (alarmCount, calory, createdAt, endAt, exerciseId, groupId, id, memberId, startAt, updatedAt) VALUES (0, 15, TIMESTAMPADD(MINUTE, 15, now()), TIMESTAMPADD(MINUTE, 45, now()), 3, 1, 2, 3, TIMESTAMPADD(MINUTE, 43, now()), TIMESTAMPADD(MINUTE, 15, now()));
INSERT INTO Mission (alarmCount, calory, createdAt, endAt, exerciseId, groupId, id, memberId, startAt, updatedAt) VALUES (2, 25, TIMESTAMPADD(MINUTE, 45, now()), TIMESTAMPADD(MINUTE, 60, now()), 4, 1, 3, 5, TIMESTAMPADD(MINUTE, 53, now()), TIMESTAMPADD(MINUTE, 45, now()));
INSERT INTO Mission (alarmCount, calory, createdAt, endAt, exerciseId, groupId, id, memberId, startAt, updatedAt) VALUES (0, 20, TIMESTAMPADD(MINUTE, 60, now()), TIMESTAMPADD(MINUTE, 78, now()), 5, 1, 4, 6, TIMESTAMPADD(MINUTE, 72, now()), TIMESTAMPADD(MINUTE, 60, now()));
INSERT INTO Mission (alarmCount, calory, createdAt, endAt, exerciseId, groupId, id, memberId, startAt, updatedAt) VALUES (0, null, TIMESTAMPADD(MINUTE, 78, now()), null, 3, 1, 5, 2, null, TIMESTAMPADD(MINUTE, 78, now()));
INSERT INTO Mission (alarmCount, calory, createdAt, endAt, exerciseId, groupId, id, memberId, startAt, updatedAt) VALUES (0, null, TIMESTAMPADD(MINUTE, 10, now()), null, 2, 2, 6, 1, null, TIMESTAMPADD(MINUTE, 10, now()));

