package com.workfusion.academy.fe.general;

import com.workfusion.vds.sdk.api.nlp.annotation.OnInit;
import com.workfusion.vds.sdk.api.nlp.fe.Feature;
import com.workfusion.vds.sdk.api.nlp.fe.FeatureExtractor;
import com.workfusion.vds.sdk.api.nlp.model.Document;
import com.workfusion.vds.sdk.api.nlp.model.Element;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Create a feature if is all upper\lowercase.
 *
 * @param <T>
 */
public class IsUpperLowerCaseFeatureExtractor<T extends Element> implements FeatureExtractor<T> {

    private static final String FEATURE_NAME_UPPER = "is_upper_case_feature";
    private static final String FEATURE_NAME_LOWER = "is_lower_case_feature";

    private final boolean isUpperCase;
    private transient List<Feature> features;

    public IsUpperLowerCaseFeatureExtractor(boolean isUpperCase) {
        this.isUpperCase = isUpperCase;
    }

    @OnInit
    public void onInit() {
        features = Collections.singletonList(new Feature(isUpperCase ? FEATURE_NAME_UPPER : FEATURE_NAME_LOWER, 1.0));
    }

    @Override
    public Collection<Feature> extract(Document document, T element) {
        String text = element.getText();
        if (text.equalsIgnoreCase(text)) {
            return features;
        }
        return Collections.emptyList();
    }

}
