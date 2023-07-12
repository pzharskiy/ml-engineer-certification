package com.workfusion.academy.fe.general;

import com.workfusion.vds.sdk.api.nlp.annotation.OnInit;
import com.workfusion.vds.sdk.api.nlp.fe.Feature;
import com.workfusion.vds.sdk.api.nlp.fe.FeatureExtractor;
import com.workfusion.vds.sdk.api.nlp.model.Document;
import com.workfusion.vds.sdk.api.nlp.model.Element;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Create a feature if data preceded with currency sign.
 *
 * @param <T>
 */
public class IsPrecededWithCurrencySignFeatureExtractor<T extends Element> implements FeatureExtractor<T> {

    private static final String FEATURE_NAME = "contains_currency_sign";
    private final List<String> currencies = new ArrayList<>();
    private transient List<Feature> features;

    /**
     * Code is running one time while feature extractor initialization.
     */
    @OnInit
    public void onInit() {
        currencies.add("$");
        currencies.add("€");
        currencies.add("£");
        features = Collections.singletonList(new Feature(FEATURE_NAME, 2.0));
    }

    @Override
    public Collection<Feature> extract(Document document, T element) {
        if (element.getBegin() >= 2) {
            String textBeforeElement = document.getText().substring(element.getBegin() - 2, element.getBegin());
            for (String currency : currencies) {
                if (textBeforeElement.contains(currency)) {
                    return features;
                }
            }
        }
        return Collections.emptyList();
    }
}
