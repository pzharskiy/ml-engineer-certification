package com.workfusion.academy.run;

import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import com.workfusion.vds.sdk.run.ModelRunner;
import com.workfusion.vds.sdk.run.config.LocalExecutionConfiguration;

import com.workfusion.academy.model.MlEngineerCertificationModel;

import static com.workfusion.academy.config.Placeholders.*;

/**
 *  Runner class for local model execution.
 *  Could be used for model tuning and post-processing development.
 *  Paths to input documents, trained model and output folders are required for the lauch.
 */
public class ModelExecutionRunner {

    public static void main(String[] args) {

        //TODO put correct values for the paths
        Path trainedModelPath = Paths.get(TRAINING_OUTPUT_DIR_PATH + "/" + TRAINED_MODEL_ID + "/output/model");
        Path inputDirPath = Paths.get(EXECUTION_INPUT_DIR_PATH);
        Path outputDirPath = Paths.get(EXECUTION_OUTPUT_DIR_PATH);

        //TODO add parameters if needed.
        Map<String, Object> parameters = new HashMap<>();

        LocalExecutionConfiguration configuration = LocalExecutionConfiguration.builder()
                .inputDir(inputDirPath)
                .outputDir(outputDirPath)
                .trainedModelDir(trainedModelPath)
                .parameters(parameters)
                .build();

        ModelRunner.run(MlEngineerCertificationModel.class, configuration);
    }

}
