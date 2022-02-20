dataInputDir="1-data"

windowSize=10
model="lstm_ad"
dataInput=`ls $dataInputDir | head -n 1`
executionType="train"

while getopts 'd:s:m:t:' flag; do
  case "${flag}" in
    d) dataInput="${OPTARG}" ;;
    s) windowSize="${OPTARG}" ;;
    m) model="${OPTARG}" ;;
    t) executionType="${OPTARG}" ;;
    *) exit 1 ;;
  esac
done

echo "dataInput: $dataInput"
echo "windowSize: $windowSize"
echo "model: $model"
echo "executionType: $executionType"
echo "-------------------"

algorithmConfig="{
    \"executionType\": \"$executionType\", 
    \"input_size\": 2, 
    \"dataInput\": \"/data/$dataInput/train_no_anomaly.csv\", 
    \"dataOutput\": \"/results/anomaly_scores.ts\", 
    \"modelInput\": \"/results/model_${model}_${dataInput}_windowSize_$windowSize.pkl\", 
    \"modelOutput\": \"/results/model_${model}_${dataInput}_windowSize_$windowSize.pkl\", 
    \"customParameters\": {
	\"window_size\": $windowSize,
	\"epochs\": 10
			} 
  }"

echo $algorithmConfig
echo "-------------------"


# Build new image
docker build -t mut:5000/akita/$model:latest time_eval_algorithms/$model/ --no-cache

docker run --rm \
    -v $(pwd)/1-data:/data:ro \
    -v $(pwd)/2-artifacts/models:/results:rw \
    -e LOCAL_UID=1000 \
    -e LOCAL_GID=1000 \
    -p 8000:8000 \
  mut:5000/akita/$model:latest execute-algorithm "$algorithmConfig"