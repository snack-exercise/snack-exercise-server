#!/bin/bash
# nginx가 가리키지 않는 port로 컨테이너 변경
echo "> 현재 구동중인 profile 확인"
CURRENT_PROFILE=$(curl -s http://localhost/profiles)

echo "> CURRENT_PROFILE은 $CURRENT_PROFILE"

if [ "$CURRENT_PROFILE" = "blue" ]
then
        IDLE_PROFILE=green
        IDLE_PORT=8081
elif [ "$CURRENT_PROFILE" = "green" ]
then
        IDLE_PROFILE=blue
        IDLE_PORT=8080
else
        echo "> 일치하는 Profile이 없습니다. Profile: $CURRENT_PROFILE"
        echo "> blue을 할당합니다. IDLE_PROFILE: blue"
        IDLE_PROFILE="blue"
        IDLE_PORT=8080
fi

CONTAINER_ID=$(docker container ls -f "name=spring-${IDLE_PROFILE}" -q)

sudo docker-compose -f docker-compose-prod.yml down
sudo docker-compose -f docker-compose-prod.yml pull spring-${IDLE_PROFILE}
sudo docker rm -f snack-redis
sudo docker rm -f spring-${IDLE_PROFILE}
sudo docker image rm ojs835/snack-exercise-hub:latest-prod

echo "> spring-$IDLE_PROFILE 컨테이너 배포"
sudo docker-compose -f docker-compose-prod.yml up -d spring-${IDLE_PROFILE}

echo "> $IDLE_PROFILE 10초 후 Health check 시작"
echo "> curl -s http://localhost:$IDLE_PORT/actuator/health "
sleep 10

for retry_count in {1..10};
do
        response=$(curl -s http://localhost:$IDLE_PORT/actuator/health)
        up_count=$(echo $response | grep 'UP' | wc -l)
        echo "up_count : $up_count"

        if [ $up_count -ge 1 ]
        then
                echo "> Health check 성공"
                break
        else
                echo "> Health check의 응답을 알 수 없거나 혹은 status가 UP이 아닙니다."
                echo "> Health check: ${response}"
        fi

        if [ $retry_count -eq 10 ]
        then
                echo "> Health check 실패. "
                echo "> Nginx에 연결하지 않고 배포를 종료합니다."
                exit 1
        fi

        echo "> Health check 연결 실패. 재시도..."
        sleep 10
done

echo "> 스위칭을 시도합니다..."
sleep 10

sudo sh /home/ubuntu/snackpotApp/scripts/switch.sh
