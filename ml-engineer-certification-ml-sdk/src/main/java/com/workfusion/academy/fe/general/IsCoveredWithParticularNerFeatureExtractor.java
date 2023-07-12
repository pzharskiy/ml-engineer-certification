package com.workfusion.academy.fe.general;

import com.workfusion.vds.sdk.api.nlp.annotation.OnDocumentComplete;
import com.workfusion.vds.sdk.api.nlp.annotation.OnDocumentStart;
import com.workfusion.vds.sdk.api.nlp.annotation.OnInit;
import com.workfusion.vds.sdk.api.nlp.fe.Feature;
import com.workfusion.vds.sdk.api.nlp.fe.FeatureExtractor;
import com.workfusion.vds.sdk.api.nlp.model.Document;
import com.workfusion.vds.sdk.api.nlp.model.Element;
import com.workfusion.vds.sdk.api.nlp.model.NamedEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Create a feature if data is covered with particular Ner.
 *
 * @param <T>
 */
public class IsCoveredWithParticularNerFeatureExtractor<T extends Element> implements FeatureExtractor<T> {

    private final String nerType;
    private transient List<NamedEntity> filteredNamedEntities;
    private transient List<Feature> features;

    public IsCoveredWithParticularNerFeatureExtractor(String nerType) {
        this.nerType = nerType;
    }

    @OnInit
    public void onInit() {
        features = Collections.singletonList(new Feature(nerType + "_feature", 7.0));
    }

    @OnDocumentStart
    public void onDocumentStart(Document document) {
        try {
            Collection<NamedEntity> namedEntities = document.findAll(NamedEntity.class);
            filteredNamedEntities = namedEntities.stream()
                    .filter(n -> n.getType().equals(nerType))
                    .collect(Collectors.toList());
        }
        catch(Exception e){
            filteredNamedEntities = new ArrayList<>();
        }
    }

    @Override
    public Collection<Feature> extract(Document document, T element) {
        for (NamedEntity entity : filteredNamedEntities) {
            if (entity.getBegin() <= element.getBegin() && entity.getEnd() >= element.getEnd()) {
                return features;
            }
        }
        return Collections.emptyList();
    }

    @OnDocumentComplete
    public void documentComplete() {
        filteredNamedEntities.clear();
    }
}