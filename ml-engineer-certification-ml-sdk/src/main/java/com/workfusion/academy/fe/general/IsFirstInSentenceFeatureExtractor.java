package com.workfusion.academy.fe.general;

import com.workfusion.vds.sdk.api.nlp.annotation.OnDocumentComplete;
import com.workfusion.vds.sdk.api.nlp.annotation.OnDocumentStart;
import com.workfusion.vds.sdk.api.nlp.fe.Feature;
import com.workfusion.vds.sdk.api.nlp.fe.FeatureExtractor;
import com.workfusion.vds.sdk.api.nlp.model.Document;
import com.workfusion.vds.sdk.api.nlp.model.Element;
import com.workfusion.vds.sdk.api.nlp.model.Sentence;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Create a feature if data is located in the beginning of the sentence.
 *
 * @param <T>
 */
public class IsFirstInSentenceFeatureExtractor<T extends Element> implements FeatureExtractor<T> {

    private static final String FEATURE_NAME = "first_in_sentence_feature";

    private transient Set<Integer> sentencesFirstElementBegins;
    private transient List<Feature> features;

    /**
     * Code is running on document load one time per document.
     *
     * @param document
     */
    @OnDocumentStart
    public void documentStart(Document document) {
        sentencesFirstElementBegins = document.findAll(Sentence.class)
                .stream()
                .map(Element::getBegin)
                .collect(Collectors.toSet());
        features = Collections.singletonList(new Feature(FEATURE_NAME, 1.0));
    }

    @Override
    public Collection<Feature> extract(Document document, T element) {
        if (sentencesFirstElementBegins.contains(element.getBegin())) {
            return features;
        }
        return Collections.emptyList();
    }

    /**
     * Code is running on document unload one time per document.
     */
    @OnDocumentComplete
    public void documentComplete() {
        sentencesFirstElementBegins.clear();
    }

}