#!/bin/bash
cd /home/ubuntu/app
sudo docker-compose -f docker-compose-prod.yml pull
sudo docker-compose -f docker-compose-prod.yml up -d
