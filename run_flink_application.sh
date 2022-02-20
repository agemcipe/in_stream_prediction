#!/bin/bash

flinkApplicationDir="flink_application"
dataDir="1-data"
sinkDir="2-artifacts/stream_sinks"

# for local execution we need the flink binary to execute
flinkBinary="../dmml_java_project/flink-1.14.2/bin/flink"


# build application
# mvn clean package --file $flinkApplicationDir/pom.xml

outputFile=`realpath $sinkDir/output_test.csv`
inputFile=`realpath $dataDir/L10k_C4/test.csv`
echo "inputFile: $inputFile"
echo "outputFile: $outputFile"

rm -f $outputFile

# run
$flinkBinary run \
    -c com.dmml.MainRemoteCountingWindow $flinkApplicationDir/target/ml_on_streaming-1.0-SNAPSHOT.jar \
    --inputFile  $inputFile \
    --outputFile $outputfile \
    --windowSize 11 \
    --windowSlide 1 \
    --hasAnomalyColumn 1\
    --url http://localhost:8000/predict 
