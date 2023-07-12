package com.workfusion.academy.fe.custom;

import com.workfusion.academy.config.Fields;
import com.workfusion.vds.sdk.api.nlp.fe.Feature;
import com.workfusion.vds.sdk.api.nlp.fe.FeatureExtractor;
import com.workfusion.vds.sdk.api.nlp.fe.annotation.FeatureName;
import com.workfusion.vds.sdk.api.nlp.model.Document;
import com.workfusion.vds.sdk.api.nlp.model.Element;
import com.workfusion.vds.sdk.api.nlp.model.Line;
import com.workfusion.vds.sdk.api.nlp.model.NamedEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

@FeatureName(SupplierNameFE.FEATURE_NAME)
public class SupplierNameFE<T extends Element> implements FeatureExtractor<T> {
    public static final String FEATURE_NAME = "supplier_name";


    @Override
    public Collection<Feature> extract(Document document, T element) {
        List<Feature> result = new ArrayList<>();
        List<NamedEntity> namedEntities = document.findCovering(NamedEntity.class, element);
        List<Line> previousLine = document.findPrevious(Line.class, element, 1);
        for (NamedEntity namedEntity : namedEntities) {
            if (namedEntity.getType().equals(Fields.FIELD_NAME_SUPPLIER_NAME)) {
                if (!element.getText().toLowerCase(Locale.ROOT).contains("invoice")) {
                    if (previousLine.isEmpty()) {
                        result.add(new Feature(FEATURE_NAME, 3.0));
                    }
                }
            }
        }
        return result;
    }
}