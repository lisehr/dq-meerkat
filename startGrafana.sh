#!/bin/bash
echo Starting Grafana Server...
xdg-open http://localhost:3000
cd Grafana/grafana-6.2.5/bin
./grafana-server

