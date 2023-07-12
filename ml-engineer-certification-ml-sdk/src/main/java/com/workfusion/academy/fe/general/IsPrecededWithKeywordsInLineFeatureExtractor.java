package com.workfusion.academy.fe.general;

import com.workfusion.vds.sdk.api.nlp.annotation.OnDocumentComplete;
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
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Create a feature if data is preceded in line with any keyword from provided list.
 *
 * @param <T>
 */
public class IsPrecededWithKeywordsInLineFeatureExtractor<T extends Element> implements FeatureExtractor<T> {

    private final String featureName;
    private final Set<String> keywords;
    private transient List<String> lines;
    private transient List<Feature> features;

    public IsPrecededWithKeywordsInLineFeatureExtractor(String featureName, Set<String> keywords) {
        this.featureName = featureName + "_keywords_preceded";
        this.keywords = keywords;
    }

    @OnInit
    public void onInit() {
        features = Collections.singletonList(new Feature(featureName, 2.5));
    }

    @OnDocumentStart
    public void onDocumentStart(Document document) {
        lines = document.findAll(Line.class).stream()
                .map(s -> s.getText().toLowerCase())
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Feature> extract(Document document, T element) {
        String elementText = element.getText().toLowerCase();

        for (String line : lines) {
            if (!line.contains(elementText)) {
                continue;
            }
            for (String keyword : keywords) {
                if (line.contains(keyword) && line.indexOf(keyword) < line.indexOf(elementText)) {
                    return features;
                }
            }
        }
        return Collections.emptyList();
    }

    @OnDocumentComplete
    public void onDocumentComplete() {
        lines.clear();
    }
}

