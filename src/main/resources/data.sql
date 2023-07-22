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
