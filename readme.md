docker-compose up


# build
mvn package -Pflink-runner 
# streamprocessing


# class
com.rnd.flink.PipelineDataHandler --runner=FlinkRunner --bootstrapServers=192.168.86.53:9092

