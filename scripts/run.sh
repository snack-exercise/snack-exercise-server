#!/bin/bash
sudo docker-compose -f docker-compose-prod.yml pull
sudo docker-compose -d -f docker-compose-prod.yml up
