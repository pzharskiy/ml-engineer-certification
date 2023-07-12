package com.workfusion.academy.fe.classification;

import com.workfusion.vds.sdk.api.nlp.annotation.OnDocumentComplete;
import com.workfusion.vds.sdk.api.nlp.annotation.OnDocumentStart;
import com.workfusion.vds.sdk.api.nlp.fe.Feature;
import com.workfusion.vds.sdk.api.nlp.fe.FeatureExtractor;
import com.workfusion.vds.sdk.api.nlp.model.Document;
import com.workfusion.vds.sdk.api.nlp.model.Element;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Create a feature if specific data was found the the document.
 * Value of the feature depends on the absolute position of data in the document text.
 *
 * @param <T>
 */
public class PositionOfKeywordsFeatureExtractor<T extends Element> implements FeatureExtractor<T> {

    private static final String FEATURE_NAME = "document_position";

    private final Collection<String> keywords;
    private transient Map<String, Feature> featuresMap;

    public PositionOfKeywordsFeatureExtractor(Collection<String> keywords) {
        this.keywords = keywords
                .stream()
                .map(s -> s.trim().toLowerCase())
                .collect(Collectors.toSet());
    }

    @OnDocumentStart
    public void onDocumentStart(Document document) {
        String content = document.getText().toLowerCase();
        featuresMap = new HashMap<>();
        for (String keyword : keywords) {
            int firstIndex = content.indexOf(keyword);
            if (firstIndex >= 0) {
                Feature feature = new Feature(FEATURE_NAME + "_" + keyword, ((double) firstIndex) / content.length());
                featuresMap.put(keyword, feature);
            }
        }
    }

    @Override
    public Collection<Feature> extract(Document document, T element) {
        String elementText = element.getText().toLowerCase();
        if (featuresMap.containsKey(elementText)) {
            return Collections.singletonList(featuresMap.get(elementText));
        }
        return Collections.emptyList();
    }

    @OnDocumentComplete
    public void documentComplete() {
        featuresMap.clear();
    }

}
