#!/bin/bash
echo Starting up BlocK-DaQ-Environment
echo Starting InfluxDB...
mate-terminal --window-with-profile=HoldOpen -e ./InfluxDB/startInflux.sh \
echo Done!
echo Starting Grafana...
mate-terminal --window-with-profile=HoldOpen -e ./Grafana/startGrafana.sh
echo Done!

