#!/bin/bash
echo Starting Grafana Server...
xdg-open http://localhost:3000
cd Grafana/grafana-7.5.5_linux/bin
./grafana-server

