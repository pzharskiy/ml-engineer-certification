package com.workfusion.academy.fe.general;

import com.workfusion.vds.sdk.api.nlp.annotation.OnDocumentStart;
import com.workfusion.vds.sdk.api.nlp.fe.Feature;
import com.workfusion.vds.sdk.api.nlp.fe.FeatureExtractor;
import com.workfusion.vds.sdk.api.nlp.model.Document;
import com.workfusion.vds.sdk.api.nlp.model.Element;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Create a feature if data fits provided pattern.
 *
 * @param <T>
 */
public class IsFitCustomPatternFeatureExtractor<T extends Element> implements FeatureExtractor<T> {

    private final String featureName;
    private final Pattern pattern;
    private transient List<Feature> features;

    public IsFitCustomPatternFeatureExtractor(String featureName, Pattern pattern) {
        this.pattern = pattern;
        this.featureName = featureName;
    }

    @OnDocumentStart
    public void onDocumentStart(Document document) {
        this.features = Collections.singletonList(new Feature(featureName + "_match_pattern", 1.0));
    }

    @Override
    public Collection<Feature> extract(Document document, T element) {
        String text = element.getText().trim();
        Matcher matcher = pattern.matcher(text);
        return matcher.find() ? features : Collections.emptyList();
    }
}
