package com.workfusion.academy.fe.classification;

import com.workfusion.vds.sdk.api.nlp.annotation.OnDocumentComplete;
import com.workfusion.vds.sdk.api.nlp.annotation.OnDocumentStart;
import com.workfusion.vds.sdk.api.nlp.fe.Feature;
import com.workfusion.vds.sdk.api.nlp.fe.FeatureExtractor;
import com.workfusion.vds.sdk.api.nlp.model.Document;
import com.workfusion.vds.sdk.api.nlp.model.Element;
import com.workfusion.vds.sdk.api.nlp.model.Line;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Create a feature if specific data was found in a line of the document.
 * Value of the feature depends on the line number of the document.
 *
 * @param <T>
 */
public class LinePositionOfKeywordFeatureExtractor<T extends Element> implements FeatureExtractor<T> {

    private final Collection<String> keywords;
    private static final String FEATURE_NAME = "line_position";
    private List<String> linesText;
    private transient Map<Integer, Feature> featuresMap;

    public LinePositionOfKeywordFeatureExtractor(Collection<String> keywords) {
        this.keywords = keywords
                .stream()
                .map(s -> s.trim().toLowerCase())
                .collect(Collectors.toList());
    }

    @OnDocumentStart
    public void onDocumentStart(Document document) {
        linesText = document.findAll(Line.class)
                .stream()
                .map(s -> s.getText().trim().toLowerCase())
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
        featuresMap = new HashMap<>();
        int lineTextSize = linesText.size();
        for (String keyword : keywords) {
            int linesContentLength = 0;
            for (int i = 0; i < lineTextSize; i++) {
                String lineContent = linesText.get(i);
                int firstIndex = lineContent.indexOf(keyword);

                if (firstIndex >= 0) {
                    Feature feature = new Feature(FEATURE_NAME + "_" + keyword, ((double) i + 1) / lineTextSize);
                    featuresMap.put(linesContentLength + firstIndex, feature);
                }
                linesContentLength += lineContent.length();
            }
        }
    }

    @Override
    public Collection<Feature> extract(Document document, T element) {
        int begin = element.getBegin();
        if (featuresMap.containsKey(begin)) {
            return Collections.singletonList(featuresMap.get(begin));
        }
        return Collections.emptyList();
    }

    @OnDocumentComplete
    public void documentComplete() {
        linesText.clear();
        featuresMap.clear();
    }

}
