package com.rnd.flink;

import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.flink.streaming.api.CheckpointingMode;

public interface DataPipelineOptions extends PipelineOptions{
    @Description("data set")
    public String getDataSet();
    public void setDataSet(String dataSet);

    @Description("Prefix for output files")
    @Default.String("output/")
    public String getOutputPrefix();
    public void setOutputPrefix(String outputPrefix);

    @Default.String("pemit")
    public String getOutputTopic() ;
    public void setOutputTopic(String outputTopic);

    @Default.String("psource")
    public String getInputTopic();
    public void setInputTopic(String inputTopic) ;

    @Default.String("192.168.86.53:9092")
    public String getBootstrapServers();
    public void setBootstrapServers(String bootstrapServers);
    
    @Default.Long(-1l)
    public Long getCheckpointingInterval();
    public void setCheckpointingInterval(Long checkpointingInterval);
    
    @Default.Enum(value="EXACTLY_ONCE")
    public CheckpointingMode getCheckpointingMode();
    public void setCheckpointingMode(CheckpointingMode checkpointingMode);

    // public StateBackend getStateBackend();
    // public void setStateBackend(StateBackend stateBackend);
}