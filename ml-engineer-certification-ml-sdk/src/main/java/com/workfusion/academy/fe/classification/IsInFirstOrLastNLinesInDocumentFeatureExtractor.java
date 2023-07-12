package com.workfusion.academy.fe.classification;

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
import java.util.stream.Collectors;

/**
 * Create a feature if specific data was found in first\last N rows of the document.
 *
 * @param <T>
 */
public class IsInFirstOrLastNLinesInDocumentFeatureExtractor<T extends Element> implements FeatureExtractor<T> {

    private final boolean isFirstLines;
    private int numberOfLines;
    private final String featureName;
    private transient List<String> linesText;
    private transient List<Feature> features;

    public IsInFirstOrLastNLinesInDocumentFeatureExtractor(int numberOfLines, boolean isFirstLines) {
        this.isFirstLines = isFirstLines;
        this.numberOfLines = numberOfLines;
        this.featureName = (isFirstLines ? "in_first_" : "in_last_") + this.numberOfLines + "_lines";
    }

    /**
     * Code is running one time while feature extractor initialization.
     */
    @OnInit
    public void onInit() {
        features = Collections.singletonList(new Feature(featureName, 1.0));
    }

    @OnDocumentStart
    public void onDocumentStart(Document document) {
        linesText = document.findAll(Line.class)
                .stream()
                .map(Element::getText)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Feature> extract(Document document, T element) {
        String elementText = element.getText();
        return isFirstLines ? extractFeaturesInFirstLines(elementText) : extractFeaturesInLastLines(elementText);
    }

    @OnDocumentComplete
    public void documentComplete() {
        linesText.clear();
    }

    private Collection<Feature> extractFeaturesInFirstLines(String elementText) {
        int min = Math.min(numberOfLines, linesText.size());
        for (int i = 0; i < min; i++) {
            if (linesText.get(i).contains(elementText)) {
                return features;
            }
        }
        return Collections.emptyList();
    }

    private Collection<Feature> extractFeaturesInLastLines(String elementText) {
        int linesSize = linesText.size();
        if (linesSize < numberOfLines) {
            numberOfLines = linesSize;
        }
        while (numberOfLines > 0) {
            if (linesText.get(linesSize - 1).contains(elementText)) {
                return features;
            }
            linesSize--;
            numberOfLines--;
        }
        return Collections.emptyList();
    }

}
