# bringup container stack
cd container
docker-compose up

# build
./mvnw clean install

# package
./mvnw package -Pflink-runner 

# deploy
deploy the file `.//target/PipelineDataHandler-bundled-flink.jar`


# class
com.rnd.flink.PipelineDataHandler 

## command line options
Note the ip `192.168.86.53` is local ip of the machine where docker is running.
--runner=FlinkRunner --bootstrapServers=192.168.86.53:9092

