package com.workfusion.academy.model;

import com.workfusion.automl.hypermodel.ie.IeGenericSe30Hypermodel;
import com.workfusion.vds.sdk.api.hypermodel.ModelType;
import com.workfusion.vds.sdk.api.hypermodel.annotation.HypermodelConfiguration;
import com.workfusion.vds.sdk.api.hypermodel.annotation.ModelDescription;

import com.workfusion.academy.config.MlEngineerCertificationModelConfiguration;

/**
 * The model class. Define here your model details like code, version etc.
 */
@ModelDescription(
        code = "ml-engineer-certification",
        title = "IE SE 30 ML Engineer Certification Model",
        description = "IE SE 30 Model for ML Engineer Certification",
        version = "0.0.1",
        type = ModelType.IE
)
@HypermodelConfiguration(MlEngineerCertificationModelConfiguration.class)
public class MlEngineerCertificationModel extends IeGenericSe30Hypermodel {}