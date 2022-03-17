FROM maven:3.8.4-openjdk-11-slim
ADD . /app
WORKDIR /app
# TODO settings.xml
# RUN mvn install

# TODO entry point for running mvn app with
# ENTRYPOINT ["mvn exec:java -Dexec.mainClass=dqm.jku.dqmeerkat.demos.XXX"]
ENTRYPOINT ["sleep", "10000"]