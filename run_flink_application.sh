#!/bin/bash

flinkApplicationDir="flink_application"
dataDir="1-data"
sinkDir="2-artifacts/stream_sinks"

# for local execution we need the flink binary to execute
flinkBinary="../dmml_java_project/flink-1.14.2/bin/flink"


# build application
mvn clean package --file $flinkApplicationDir/pom.xml

dataSet=L10k_C2 
windowSize=11
windowSlide=1
inputFile=`realpath $dataDir/$dataSet/test.csv`
runName="${dataSet}__WindowSize_${windowSize}__WindowSlide_${windowSlide}"
outputFile=`realpath $sinkDir/$runName.csv`

echo "inputFile: $inputFile"
echo "outputFile: $outputFile"
echo "runName: $runName"
echo "-----------------------------"
rm -f $outputFile

# run
$flinkBinary run \
    -c com.dmml.MainRemoteCountingWindow $flinkApplicationDir/target/ml_on_streaming-1.0-SNAPSHOT.jar \
    --inputFile  $inputFile \
    --outputFile $outputFile \
    --windowSize $windowSize \
    --windowSlide $windowSlide \
    --hasAnomalyColumn 1\
    --runName $runName \
    --url http://localhost:8000/predict 
