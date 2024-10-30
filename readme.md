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

https://nightlies.apache.org/flink/flink-docs-release-1.17/docs/deployment/resource-providers/standalone/docker/

FLINK_PROPERTIES="jobmanager.rpc.address: jobmanager"
docker network create flink-network

 docker run \
    --rm \
    --name=jobmanager \
    --network flink-network \
    --publish 8081:8081 \
    --env FLINK_PROPERTIES="${FLINK_PROPERTIES}" \
    flink:1.17.2-scala_2.12 jobmanager

docker run \
    --rm \
    --name=taskmanager \
    --network flink-network \
    --env FLINK_PROPERTIES="${FLINK_PROPERTIES}" \
    flink:1.17.2-scala_2.12 taskmanager

# kafka
https://nightlies.apache.org/flink/flink-docs-release-1.17/docs/connectors/datastream/kafka/

docker run \
    --rm \
    --name broker \
    --network flink-network \
    apache/kafka:latest


