package com.workfusion.academy.fe;

import java.util.Collection;

import org.junit.Test;

import com.workfusion.automl.util.FeatureExtractorTestRunner;
import com.workfusion.vds.sdk.api.nlp.fe.Feature;
import com.workfusion.vds.sdk.api.nlp.model.Document;
import com.workfusion.vds.sdk.api.nlp.model.Token;
import com.workfusion.vds.sdk.nlp.component.util.DocumentFactory;

import static org.junit.Assert.assertNotNull;

public class FeatureExtractorExampleTest {

    @Test
    public void testExtract() throws Exception {
        FeatureExtractorExample<Token> featureExtractor = new FeatureExtractorExample<>();
        Document document = DocumentFactory.createIeDocument("<html>content</html>", "content");
        document.add(Token.descriptor().setBegin(0).setEnd(7));

        Collection<Feature> features = FeatureExtractorTestRunner.extractFeatures(document, featureExtractor, Token.class);

        assertNotNull(features);
    }
}
