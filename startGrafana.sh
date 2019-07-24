#!/bin/bash
echo Starting Grafana Server...
cd Grafana/grafana-6.2.5/bin
./grafana-server
xdg-open http://localhost:3000

