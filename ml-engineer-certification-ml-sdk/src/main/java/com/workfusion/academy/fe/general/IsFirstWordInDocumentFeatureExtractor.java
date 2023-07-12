package com.workfusion.academy.fe.general;

import com.workfusion.vds.sdk.api.nlp.fe.Feature;
import com.workfusion.vds.sdk.api.nlp.fe.FeatureExtractor;
import com.workfusion.vds.sdk.api.nlp.model.Document;
import com.workfusion.vds.sdk.api.nlp.model.Element;

import java.util.Collection;
import java.util.Collections;

/**
 * Create a feature if data is located in the beginning of the document.
 *
 * @param <T>
 */
public class IsFirstWordInDocumentFeatureExtractor<T extends Element> implements FeatureExtractor<T> {

    private static final String FEATURE_NAME = "is_first_word_in_document";

    @Override
    public Collection<Feature> extract(Document document, T element) {
        if (element.getBegin() == 0) {
            return Collections.singletonList(new Feature(FEATURE_NAME, 2.0));
        }
        return Collections.emptyList();
    }
}
