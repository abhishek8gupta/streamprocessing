#!/bin/bash

counter=1
IPADDR=$3

while [ $COUNTER -le "$1" ] ; do
    sleep $2
    epochtime=`date +'%s'000`
    DATA="$epochtime,100,testuser,10,27,F"
    echo "sending: $DATA"
    #/kafka/bin/kafka-console-producer.sh --broker-list 192.168.53.10:9092 --topic psource < echo $DATA
    echo $DATA | /kafka/bin/kafka-console-producer.sh --broker-list $IPADDR:9092 --topic psource
    ((COUNTER+=1))
done
