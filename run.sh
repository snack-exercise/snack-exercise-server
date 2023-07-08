#!/bin/bash
sudo service docker start
docker pull ojs835/snack-exercise-hub:latest-prod
docker run -d --name snack-exercise snack-exercise-hub:latest-prod