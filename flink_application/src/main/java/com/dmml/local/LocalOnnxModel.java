package com.dmml.local;

import java.util.Collections;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import ai.onnxruntime.OrtSession.Result;

public class LocalOnnxModel {
    public static OrtEnvironment env = OrtEnvironment.getEnvironment();
    public OrtSession session;

    public LocalOnnxModel(String modelPath) {
        try {

            this.session = env.createSession(modelPath, new OrtSession.SessionOptions());
        } catch (OrtException e) {
            System.err.println(e.toString());
            // TODO: this should also raise an error
        }
    }

    public LocalOnnxModel() throws OrtException {

        // TODO: investigate why relative paths are not working within apache flink
        // program?
        // String modelPath = new
        // Resources().getClass().getClassLoader().getResource("tree_clf.onnx").toString();

        String modelPath = "/home/agemcipe/code/hpi_coursework/data_management_for_ml_systems/dmml_java_project/ml_on_stream/src/main/resources/tree_clf.onnx";
        modelPath = modelPath.replaceFirst("file:", "");

        System.out.println("Loading Onnx Model from: " + modelPath + "\n");

        try {
            this.session = env.createSession(modelPath, new OrtSession.SessionOptions());
        } catch (OrtException e) {
            System.err.println(e.toString());
            throw e;
        }
    }

    public int predict(float[][] inputs) throws OrtException {
        OnnxTensor onnxInput = OnnxTensor.createTensor(env, inputs);
        Result output = this.session.run(Collections.singletonMap("float_input", onnxInput)); // TODO: Remove Hardcoded
                                                                                              // String "float_input"
        long[] labels = (long[]) output.get(0).getValue();
        return (int) labels[0];
    }

    public static void main(String[] args) throws Exception {
        LocalOnnxModel myModel = new LocalOnnxModel();

        // Some Model inspection
        System.out.println("NumInputs: " + myModel.session.getNumInputs());
        System.out.println("InputNames: " + myModel.session.getInputNames());
        System.out.println("InputInfo: " + myModel.session.getInputInfo());
        System.out.println("OutputInfo: " + myModel.session.getOutputInfo());

        // test inputs
        float[][] modelInputs = { {
                (float) 1.0, (float) 2.0, (float) 3.0, (float) 2.0, (float) 3.0,
                (float) 1.0, (float) 2.0, (float) 3.0, (float) 2.0, (float) 3.0
        } };

        //
        int labelPredicted = myModel.predict(modelInputs);

        System.out.println("Predicted Label: " + labelPredicted);
    }

}
