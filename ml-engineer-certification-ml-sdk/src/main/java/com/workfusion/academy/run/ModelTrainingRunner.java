package com.workfusion.academy.run;

import com.workfusion.academy.config.Fields;
import com.workfusion.academy.model.MlEngineerCertificationModel;
import com.workfusion.nlp.uima.api.execution.ExecutionProfile;
import com.workfusion.nlp.uima.pipeline.constants.ConfigurationConstants;
import com.workfusion.nlp.uima.workflow.constant.ProcessGlobalConst;
import com.workfusion.vds.sdk.api.nlp.configuration.FieldInfo;
import com.workfusion.vds.sdk.api.nlp.configuration.FieldType;
import com.workfusion.vds.sdk.run.ModelRunner;
import com.workfusion.vds.sdk.run.config.LocalTrainingConfiguration;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.workfusion.academy.config.Placeholders.*;

/**
 *  This runner allows you to start model training on your local machine.
 *  Paths to training set and output folders, fields configuration are required for the lauch.
 */
public class ModelTrainingRunner {


    public static void main(String[] args) {

        //TODO Configure input/output
        Path inputDirPath = Paths.get(TRAINING_INPUT_DIR_PATH);
        Path outputDirPath = Paths.get(TRAINING_OUTPUT_DIR_PATH);

        //TODO Configure fields according to your use-case
        List<FieldInfo> fields = new ArrayList<>();
        fields.add(new FieldInfo.Builder("invoice")
                .type(FieldType.INFO_EXTRACTION)
                .required(true)
                .child(new FieldInfo.Builder(Fields.FIELD_NAME_SUPPLIER_NAME)
                        .type(FieldType.COMPANY_NAME)
                        .multiValue(false)
                        .required(false)
                        .build())
                .child(new FieldInfo.Builder(Fields.FIELD_NAME_EMAIL)
                        .type(FieldType.EMAIL)
                        .multiValue(false)
                        .required(false)
                        .build())
                .child(new FieldInfo.Builder(Fields.FIELD_NAME_INVOICE_DATE)
                        .type(FieldType.DATE)
                        .multiValue(false)
                        .required(true)
                        .build())
                .child(new FieldInfo.Builder(Fields.FIELD_NAME_INVOICE_NUMBER)
                        .type(FieldType.TEXT)
                        .multiValue(false)
                        .required(true)
                        .build())
                .child(new FieldInfo.Builder("group").type(FieldType.LINE_ITEM)
                        .child(new FieldInfo.Builder(Fields.FIELD_NAME_PRICE)
                                .type(FieldType.NUMBER)
                                .multiValue(false)
                                .required(true)
                                .build())
                        .child(new FieldInfo.Builder(Fields.FIELD_NAME_QUANTITY)
                                .type(FieldType.NUMBER)
                                .multiValue(false)
                                .required(true)
                                .build())
                        .child(new FieldInfo.Builder(Fields.FIELD_NAME_PRODUCT)
                                .type(FieldType.TEXT)
                                .multiValue(false)
                                .required(true)
                                .build())
                        .required(true)
                        .build())
                .child(new FieldInfo.Builder(Fields.FIELD_NAME_TOTAL_AMOUNT)
                        .type(FieldType.NUMBER)
                        .multiValue(false)
                        .required(true)
                        .build())
                .build());

        //TODO add parameters if needed.
        Map<String, Object> parameters = new HashMap<>();

        parameters.put(ProcessGlobalConst.PARAM_EXECUTION_PROFILE, ExecutionProfile.DEBUG);
        parameters.put(ConfigurationConstants.PARAM_SKIP_DOCUMENTS_WITHOUT_TAG, true);
        parameters.put(ConfigurationConstants.ENABLE_FEATURE_IMPORTANCE_EXPLANATION, true);
        parameters.put(ConfigurationConstants.GOLD_DATA_ANALYZERS,true);
        parameters.put(ConfigurationConstants.PARAM_NORMALIZE_SCORE, true);
        parameters.put(ConfigurationConstants.PARAM_IGNORE_DOCUMENT_WITH_SAME_GOLDS, true);

        LocalTrainingConfiguration configuration = LocalTrainingConfiguration.builder()
                .inputDir(inputDirPath)
                .outputDir(outputDirPath)
                .fields(fields)
                .parameters(parameters)
                .id(TRAINED_MODEL_ID)
                .build();

        ModelRunner.run(MlEngineerCertificationModel.class, configuration);
    }

}
