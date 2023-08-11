#!/bin/bash
# nginx가 가리키는 스프링 port 수정
echo "> 현재 구동중인 Port 확인"
CURRENT_PROFILE=$(curl -s http://localhost/profiles)

if [ "$CURRENT_PROFILE" = "blue" ]
then
  IDLE_PORT=8081
elif [ "$CURRENT_PROFILE" = "green" ]
then
  IDLE_PORT=8080
else
  echo "> 일치하는 Profile이 없습니다. Profile:$CURRENT_PROFILE"
  echo "> 8080을 할당합니다."
  IDLE_PORT=8080
fi

PROXY_PORT=$(curl -s http://localhost/profiles)
echo "> 현재 구동중인 Port: $PROXY_PORT"

echo "> 전환할 Port : $IDLE_PORT"
echo "> Port 전환"
echo "set \$service_url http://127.0.0.1:${IDLE_PORT};" | sudo tee /etc/nginx/conf.d/service-url.inc

echo "> Nginx Reload"
sudo service nginx reload