package com.workfusion.academy.fe.general;

import com.workfusion.vds.sdk.api.nlp.annotation.OnDocumentStart;
import com.workfusion.vds.sdk.api.nlp.annotation.OnInit;
import com.workfusion.vds.sdk.api.nlp.fe.Feature;
import com.workfusion.vds.sdk.api.nlp.fe.FeatureExtractor;
import com.workfusion.vds.sdk.api.nlp.model.Document;
import com.workfusion.vds.sdk.api.nlp.model.Element;
import com.workfusion.vds.sdk.api.nlp.model.Line;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Create a feature if data is equal to whole line.
 *
 * @param <T>
 */
public class IsUniqueWordInLineFeatureExtractor<T extends Element> implements FeatureExtractor<T> {

    private static final String FEATURE_NAME = "is_unique_word_in_line";
    private transient List<String> lines;
    private transient List<Feature> features;

    @OnInit
    public void onInit() {
        features = Collections.singletonList(new Feature(FEATURE_NAME, 1.0));;
    }

    @OnDocumentStart
    public void onDocumentStart(Document document){
        lines = document.findAll(Line.class).stream()
                .map(s -> s.getText().toLowerCase())
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Feature> extract(Document document, T element) {
        String elementText = element.getText().toLowerCase();
        for (String line : lines) {
            if (line.equals(elementText)) {
                return features;
            }
        }
        return Collections.emptyList();
    }
}
