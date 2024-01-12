# Apache Flink Stream Application with Remote ML Model

## Overview

This application showcases how to use Apache Flink for real-time stream processing, coupled with the utilization of a remote Machine Learning (ML) model for making predictions on elements within the data stream.
The idea here was to test how the two systems - stream processing and ML Model - compete for resources on the same machine or how the latency of running the model on a remote machine (but with designated hardware) affects performance.  

## Setup

1. **Environment Requirements:**
   - Ensure you have Apache Flink installed and configured. Refer to [Apache Flink Documentation](https://ci.apache.org/projects/flink/flink-docs-release-1.14/) for installation instructions.

   - Set up the remote ML model server. Ensure it is accessible from the application.

2. **Clone the Repository:**
   ```bash
   git clone https://github.com/agemcipe/in_stream_prediction.git
   cd in_stream_prediction
   ```
3. **Run Docker containing serving the ML Model**
   ```bash
   .run_time_eval.sh
   ```
4. **Run Apache Flink Streaming Process**
   ```bash
   .run_flink_application.sh
   ```  
