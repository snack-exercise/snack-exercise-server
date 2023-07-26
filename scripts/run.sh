#!/bin/bash
sudo service docker start
sudo docker pull ojs835/snack-exercise-hub:latest-prod
sudo docker run -d --name snack-exercise snack-exercise-hub:latest-prod