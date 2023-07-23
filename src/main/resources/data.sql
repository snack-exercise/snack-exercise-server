-- INSERT MEMBER
INSERT INTO Member (id, name, profileImage, nickname, gender, socialType, email, fcmToken, status, createdAt, updatedAt, birthYear) VALUES (1, '오진서', null, '진서', 'MALE', 'KAKAO', 'jinseo@gmail.com', null, 'ACTIVE', '2023-07-21 14:49:54', '2023-07-21 14:49:54', '1999');
INSERT INTO Member (id, name, profileImage, nickname, gender, socialType, email, fcmToken, status, createdAt, updatedAt, birthYear) VALUES (2, '정희수', null, '희수', 'FEMALE', 'KAKAO', 'heesu@gmail.com', null, 'ACTIVE', '2023-07-21 14:53:29', '2023-07-21 14:53:29', '2001');
INSERT INTO Member (id, name, profileImage, nickname, gender, socialType, email, fcmToken, status, createdAt, updatedAt, birthYear) VALUES (3, '김민정', null, '민정', 'FEMALE', 'KAKAO', 'minjung@gmail.com', null, 'ACTIVE', '2023-07-21 14:53:29', '2023-07-21 14:53:29', '2000');
INSERT INTO Member (id, name, profileImage, nickname, gender, socialType, email, fcmToken, status, createdAt, updatedAt, birthYear) VALUES (4, '오해원', null, '해원', 'FEMALE', 'KAKAO', 'haewon@gmail.com', null, 'ACTIVE', '2023-07-21 14:53:29', '2023-07-21 14:53:29', '2004');
INSERT INTO Member (id, name, profileImage, nickname, gender, socialType, email, fcmToken, status, createdAt, updatedAt, birthYear) VALUES (5, '설윤아', null, '설윤', 'FEMALE', 'KAKAO', 'yoona@gmail.com', null, 'ACTIVE', '2023-07-21 14:53:29', '2023-07-21 14:53:29', '2004');
INSERT INTO Member (id, name, profileImage, nickname, gender, socialType, email, fcmToken, status, createdAt, updatedAt, birthYear) VALUES (6, '이영지', null, 'youngji2002', 'FEMALE', 'KAKAO', 'youngzi@gmail.com', null, 'ACTIVE', '2023-07-21 14:53:29', '2023-07-21 14:53:29', '2002');
INSERT INTO Member (id, name, profileImage, nickname, gender, socialType, email, fcmToken, status, createdAt, updatedAt, birthYear) VALUES (7, '박보검', null, 'park', 'MALE', 'KAKAO', 'park@gmail.com', null, 'ACTIVE', '2023-07-21 14:53:29', '2023-07-21 14:53:29', '1993');

-- INSERT EXGROUP
INSERT INTO Exgroup (id, name, emozi, color, maxMemberNum, startTime, endTime, description, penalty, code, createdAt, updatedAt, missionIntervalTime, currentDoingMemberId, checkIntervalTime, checkMaxNum, startDate, endDate, goalRelayNum) VALUES (2, '운동하자', '', '#101010', 6, '09:00:00', '18:00:00', '저희 그룹은 2주동안 매일매일 운동하는 것을 목표로합니다.', '꼴등이 1등한테 스벅 깊티 쏘기
', '105236', '2023-07-21 23:55:26', '2023-07-21 23:55:26', 30, null, 10, 2, '2023-07-21', '2023-08-04', 14);
INSERT INTO Exgroup (id, name, emozi, color, maxMemberNum, startTime, endTime, description, penalty, code, createdAt, updatedAt, missionIntervalTime, currentDoingMemberId, checkIntervalTime, checkMaxNum, startDate, endDate, goalRelayNum) VALUES (3, '짧고굵게', '', '#638391', 4, '10:00:00', '16:00:00', '짧고 굵게 딱 1주일 동안 운동하자!
', '꼴등 스쿼트 50개 영상찍어 올리기
', '156398', '2023-07-21 23:58:14', '2023-07-21 23:55:26', 20, null, 10, 3, '2023-07-21', '2023-07-29', 14);

-- INSERT JOINLIST
INSERT INTO JoinList (id, memberId, exgroupId, createdAt, joinType, updatedAt, outCount) VALUES (1, 1, 2, '2023-07-21 15:05:14', 'HOST', '2023-07-21 15:05:14', 0);
INSERT INTO JoinList (id, memberId, exgroupId, createdAt, joinType, updatedAt, outCount) VALUES (2, 1, 3, '2023-07-21 15:05:14', 'MEMBER', '2023-07-21 15:05:14', 0);
INSERT INTO JoinList (id, memberId, exgroupId, createdAt, joinType, updatedAt, outCount) VALUES (3, 2, 2, '2023-07-21 15:05:14', 'MEMBER', '2023-07-21 15:05:14', 0);
INSERT INTO JoinList (id, memberId, exgroupId, createdAt, joinType, updatedAt, outCount) VALUES (4, 3, 2, '2023-07-21 15:05:14', 'MEMBER', '2023-07-21 15:05:14', 0);
INSERT INTO JoinList (id, memberId, exgroupId, createdAt, joinType, updatedAt, outCount) VALUES (5, 4, 3, '2023-07-21 15:05:14', 'HOST', '2023-07-21 15:05:14', 0);
INSERT INTO JoinList (id, memberId, exgroupId, createdAt, joinType, updatedAt, outCount) VALUES (6, 5, 2, '2023-07-21 15:05:54', 'MEMBER', '2023-07-21 15:05:54', 0);
INSERT INTO JoinList (id, memberId, exgroupId, createdAt, joinType, updatedAt, outCount) VALUES (7, 6, 2, '2023-07-21 15:05:54', 'MEMBER', '2023-07-21 15:05:54', 0);
INSERT INTO JoinList (id, memberId, exgroupId, createdAt, joinType, updatedAt, outCount) VALUES (8, 7, 2, '2023-07-21 15:06:18', 'MEMBER', '2023-07-21 15:06:18', 0);

-- INSERT EXERCISE
INSERT INTO Exercise (minPerKcal, createdAt, id, updatedAt, description, exerciseCategory, name, status, videoLink) VALUES (10, '2023-07-23 11:16:04.000000', 1, '2023-07-23 11:16:03.000000', '어디서나 할 수 있는 전신 운동 입니다
', 'EXERCISE', '전신 운동', 'ACTIVE', 'https://www.youtube.com/shorts/GA2DFir8fbo');
INSERT INTO Exercise (minPerKcal, createdAt, id, updatedAt, description, exerciseCategory, name, status, videoLink) VALUES (15, '2023-07-23 14:44:15.000000', 2, '2023-07-23 14:44:22.000000', '서서하는 복근 운동
', 'EXERCISE', '서서하는 복근 운동
', 'ACTIVE', 'https://www.youtube.com/shorts/m-kAmwkanoI');
INSERT INTO Exercise (minPerKcal, createdAt, id, updatedAt, description, exerciseCategory, name, status, videoLink) VALUES (20, '2023-07-23 14:44:17.000000', 3, '2023-07-23 14:44:20.000000', '하체 운동
', 'EXERCISE', '하체 운동
', 'ACTIVE', 'https://www.youtube.com/shorts/l1CEorIKDcE');
INSERT INTO Exercise (minPerKcal, createdAt, id, updatedAt, description, exerciseCategory, name, status, videoLink) VALUES (10, '2023-07-23 14:44:18.000000', 4, '2023-07-23 14:44:23.000000', '구석구석 유산소
', 'EXERCISE', '구석구석 유산소', 'ACTIVE', 'https://www.youtube.com/shorts/yDwnyQeLB8c');
INSERT INTO Exercise (minPerKcal, createdAt, id, updatedAt, description, exerciseCategory, name, status, videoLink) VALUES (10, '2023-07-23 14:44:19.000000', 5, '2023-07-23 14:44:24.000000', '복근 운동', 'EXERCISE', '복근 운동', 'ACTIVE', 'https://www.youtube.com/shorts/1K0Ono8vo1o');

-- INSERT MISSION
INSERT INTO Mission (alarmCount, calory, createdAt, endAt, exerciseId, exgroupId, id, memberId, startAt, updatedAt, status) VALUES (0, 10, '2023-07-23 16:30:44.000000', '2023-07-23 16:40:42.000000', 1, 2, 1, 1, '2023-07-23 16:35:35.000000', '2023-07-23 16:30:44.000000', 'ACTIVE');
INSERT INTO Mission (alarmCount, calory, createdAt, endAt, exerciseId, exgroupId, id, memberId, startAt, updatedAt, status) VALUES (0, 15, '2023-07-23 16:40:42.000000', '2023-07-23 16:57:35.000000', 3, 2, 2, 3, '2023-07-23 16:50:42.000000', '2023-07-23 16:40:42.000000', 'ACTIVE');
INSERT INTO Mission (alarmCount, calory, createdAt, endAt, exerciseId, exgroupId, id, memberId, startAt, updatedAt, status) VALUES (2, 25, '2023-07-23 16:57:35.000000', '2023-07-23 18:08:29.000000', 4, 2, 3, 5, '2023-07-23 18:00:35.000000', '2023-07-23 16:57:35.000000', 'ACTIVE');
INSERT INTO Mission (alarmCount, calory, createdAt, endAt, exerciseId, exgroupId, id, memberId, startAt, updatedAt, status) VALUES (0, 20, '2023-07-23 18:08:29.000000', '2023-07-23 18:20:29.000000', 5, 2, 4, 6, '2023-07-23 18:11:29.000000', '2023-07-23 18:08:29.000000', 'ACTIVE');
INSERT INTO Mission (alarmCount, calory, createdAt, endAt, exerciseId, exgroupId, id, memberId, startAt, updatedAt, status) VALUES (0, null, '2023-07-23 18:20:29.000000', null, 3, 2, 5, 2, null, '2023-07-23 18:20:29.000000', 'ACTIVE');
INSERT INTO Mission (alarmCount, calory, createdAt, endAt, exerciseId, exgroupId, id, memberId, startAt, updatedAt, status) VALUES (0, null, '2023-07-23 09:00:48.000000', null, 2, 3, 6, 1, null, '2023-07-23 09:00:48.000000', 'ACTIVE');

