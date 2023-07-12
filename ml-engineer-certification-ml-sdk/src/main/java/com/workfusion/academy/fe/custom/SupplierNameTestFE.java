package com.workfusion.academy.fe.custom;

import com.workfusion.vds.sdk.api.nlp.fe.Feature;
import com.workfusion.vds.sdk.api.nlp.fe.FeatureExtractor;
import com.workfusion.vds.sdk.api.nlp.model.Document;
import com.workfusion.vds.sdk.api.nlp.model.Element;
import com.workfusion.vds.sdk.api.nlp.model.Line;
import com.workfusion.vds.sdk.api.nlp.model.NamedEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class SupplierNameTestFE<T extends Element> implements FeatureExtractor<T> {

    private static String FEATURE_NAME = "supplier_by_sentence";

    @Override
    public Collection<Feature> extract(Document document, T element) {

        List<Feature> result = new ArrayList<>();
        List<NamedEntity> namedEntities = document.findCovering(NamedEntity.class, element);
        List<Line> nextLines = document.findNext(Line.class, element, 1);
        List<Line> previousLines = document.findPrevious(Line.class, element, 1);
        for (NamedEntity namedEntity : namedEntities) {
            if (namedEntity.getType().equals("supplier") || namedEntity.getType().equals("supplier_capital")) {
                for (Line line : nextLines) {
                    if (line.getText().toLowerCase(Locale.ROOT).contains("po box")) {
                        System.out.println("\nDocument - " + document.getId());
                        System.out.println("Element - " + element.getText());
                        System.out.println("feature po box was added");
                        result.add(new Feature(FEATURE_NAME, 1.0));
                    }
                }
                for (Line line : previousLines) {
                    if (line.getText().toLowerCase(Locale.ROOT).contains("remit payment")) {
                        System.out.println("\nDocument - " + document.getId());
                        System.out.println("Element - " + element.getText());
                        System.out.println("feature remit payment to was added");
                        result.add(new Feature(FEATURE_NAME, 1.0));
                    }
                }
            }
        }
        return result;
    }
}
