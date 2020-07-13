#!/bin/bash
echo Starting up BlocK-DaQ-Environment
echo Starting InfluxDB...
echo Starting Grafana...
mate-terminal --window-with-profile=HoldOpen -e ./InfluxDB/startInflux.sh 
sleep 1
mate-terminal --window-with-profile=HoldOpen -e ./Grafana/startGrafana.sh
echo Done!

