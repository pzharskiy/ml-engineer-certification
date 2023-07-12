package com.workfusion.academy.config;

import com.workfusion.academy.fe.classification.IsInFirstOrLastNLinesInDocumentFeatureExtractor;
import com.workfusion.academy.fe.general.*;
import com.workfusion.academy.fe.table.*;
import com.workfusion.academy.processing.*;
import com.workfusion.nlp.uima.api.preprocessor.IPreprocessor;
import com.workfusion.nlp.uima.preprocessor.SpaceRemovingPreprocessor;
import com.workfusion.nlp.uima.preprocessor.TextPartsPreprocessor;
import com.workfusion.vds.nlp.hypermodel.ie.generic.config.GenericIeSe30HypermodelConfiguration;
import com.workfusion.vds.nlp.uima.annotator.OcrTextBlockAnnotator;
import com.workfusion.vds.nlp.uima.resource.ClassPathResource;
import com.workfusion.vds.sdk.api.hypermodel.annotation.Import;
import com.workfusion.vds.sdk.api.hypermodel.annotation.ModelConfiguration;
import com.workfusion.vds.sdk.api.hypermodel.annotation.Named;
import com.workfusion.vds.sdk.api.nlp.annotator.Annotator;
import com.workfusion.vds.sdk.api.nlp.configuration.IeConfigurationContext;
import com.workfusion.vds.sdk.api.nlp.fe.FeatureExtractor;
import com.workfusion.vds.sdk.api.nlp.model.Document;
import com.workfusion.vds.sdk.api.nlp.model.Element;
import com.workfusion.vds.sdk.api.nlp.model.IeDocument;
import com.workfusion.vds.sdk.api.nlp.model.Token;
import com.workfusion.vds.sdk.api.nlp.processing.Processor;
import com.workfusion.vds.sdk.nlp.component.annotator.EntityBoundaryAnnotator;
import com.workfusion.vds.sdk.nlp.component.annotator.ner.AhoCorasickDictionaryNerAnnotator;
import com.workfusion.vds.sdk.nlp.component.annotator.ner.BaseRegexNerAnnotator;
import com.workfusion.vds.sdk.nlp.component.annotator.tokenizer.SplitterTokenAnnotator;
import com.workfusion.vds.sdk.nlp.component.dictionary.CsvDictionaryKeywordProvider;
import com.workfusion.vds.sdk.nlp.component.processing.DataValueNormalizationProcessor;
import com.workfusion.vds.sdk.nlp.component.processing.normalization.DecimalNumberFormatNormalizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import static com.workfusion.nlp.uima.pipeline.constants.ConfigurationConstants.PARAM_PREPROCESSORS;

/**
 * The model configuration class.
 * Here you can configure set of Feature Extractors, Annotators and Post-Processors.
 * Also you can import configuration with set of predefined components or your own configuration
 */
@ModelConfiguration
@Import(configurations = {
        @Import.Configuration(value = GenericIeSe30HypermodelConfiguration.class)
})
public class MlEngineerCertificationModelConfiguration {

    /**
     * Regex pattern to use for matching {@link Token} elements.
     */
    private final static String TOKEN_REGEX = "[\\w@.,$%’-]+";


    @Named("annotators")
    public List<Annotator<Document>> getAnnotators(IeConfigurationContext context) {
        //TODO configure annotators here.
        List<Annotator<Document>> annotators = new ArrayList<>();

        annotators.add(new SplitterTokenAnnotator(TOKEN_REGEX));
        annotators.add(new EntityBoundaryAnnotator());
        AhoCorasickDictionaryNerAnnotator supplierNameNerAnnotator = new AhoCorasickDictionaryNerAnnotator(Fields.FIELD_NAME_SUPPLIER_NAME,
                new CsvDictionaryKeywordProvider(new ClassPathResource("/dictionary/supplier_name.txt")));
        annotators.add(BaseRegexNerAnnotator.getJavaPatternRegexNerAnnotator(Fields.FIELD_NAME_EMAIL, Placeholders.EMAIL_REGEX));
        annotators.add(BaseRegexNerAnnotator.getJavaPatternRegexNerAnnotator(Fields.FIELD_NAME_SUPPLIER_NAME, Placeholders.SUPPLIER_NAME_REGEXP));
        annotators.add(BaseRegexNerAnnotator.getJavaPatternRegexNerAnnotator(Fields.FIELD_NAME_INVOICE_DATE, Placeholders.DATE_REGEXP));
        return annotators;
    }

    @Named("featureExtractors")
    public List<FeatureExtractor<Element>> getFeatureExtractors(IeConfigurationContext context) {
        //TODO configure feature extractors here.

        List<FeatureExtractor<Element>> featureExtractors = new ArrayList<>();

        String code = context.getField().getCode();
        switch (code) {
            case Fields.FIELD_NAME_SUPPLIER_NAME:
                Set<String> supplierNameKeywords = new CsvDictionaryKeywordProvider(new ClassPathResource("/dictionary/supplier_name_keywords.txt")).getDictionary();
                featureExtractors.add(new IsUpperLowerCaseFeatureExtractor<>(true));
                featureExtractors.add(new IsCoveredWithParticularNerFeatureExtractor<>(Fields.FIELD_NAME_SUPPLIER_NAME));
                featureExtractors.add(new IsPrecededWithKeywordsInLineFeatureExtractor<>(Fields.FIELD_NAME_SUPPLIER_NAME, supplierNameKeywords));
                break;
            case Fields.FIELD_NAME_EMAIL:
                Set<String> emailKeywords = new CsvDictionaryKeywordProvider(new ClassPathResource("/dictionary/email_keywords.txt")).getDictionary();

                featureExtractors.add(new IsCoveredWithParticularNerFeatureExtractor<>(Fields.FIELD_NAME_EMAIL));
                featureExtractors.add(new IsPrecededWithKeywordsInLineFeatureExtractor<>(Fields.FIELD_NAME_EMAIL, emailKeywords));
                break;
            case Fields.FIELD_NAME_INVOICE_DATE:
                Set<String> invoiceDateKeywords = new CsvDictionaryKeywordProvider(new ClassPathResource("/dictionary/invoice_date_keywords.txt")).getDictionary();

                featureExtractors.add(new IsCoveredWithParticularNerFeatureExtractor<>(Fields.FIELD_NAME_INVOICE_DATE));
                featureExtractors.add(new IsInFirstOrLastNLinesInDocumentFeatureExtractor<>(10, true));
                featureExtractors.add(new MatchFullColumnOrRowWithSpecifiedHeaderFeatureExtractor<>(Fields.FIELD_NAME_INVOICE_DATE, invoiceDateKeywords, true));
                featureExtractors.add(new MatchFullColumnOrRowWithSpecifiedHeaderFeatureExtractor<>(Fields.FIELD_NAME_INVOICE_DATE, invoiceDateKeywords, false));
                featureExtractors.add(new IsPrecededWithKeywordsInLineFeatureExtractor<>(Fields.FIELD_NAME_INVOICE_DATE, invoiceDateKeywords));
                break;
            case Fields.FIELD_NAME_INVOICE_NUMBER:
                Set<String> invoiceNumberKeywords = new CsvDictionaryKeywordProvider(new ClassPathResource("/dictionary/invoice_number_keywords.txt")).getDictionary();

                featureExtractors.add(new IsInFirstOrLastNLinesInDocumentFeatureExtractor<>(10, true));
                featureExtractors.add(new MatchFullColumnOrRowWithSpecifiedHeaderFeatureExtractor<>(Fields.FIELD_NAME_INVOICE_NUMBER, invoiceNumberKeywords, true));
                featureExtractors.add(new MatchFullColumnOrRowWithSpecifiedHeaderFeatureExtractor<>(Fields.FIELD_NAME_INVOICE_NUMBER, invoiceNumberKeywords, false));
                featureExtractors.add(new IsPrecededWithKeywordsInLineFeatureExtractor<>(Fields.FIELD_NAME_INVOICE_NUMBER, invoiceNumberKeywords));
                break;
            case Fields.FIELD_NAME_PRICE:
                Set<String> priceKeywords = new CsvDictionaryKeywordProvider(new ClassPathResource("/dictionary/price_keywords.txt")).getDictionary();
                featureExtractors.add(new MatchFullColumnOrRowWithSpecifiedHeaderFeatureExtractor<>(Fields.FIELD_NAME_PRICE, priceKeywords, true));
                break;
            case Fields.FIELD_NAME_QUANTITY:
                Set<String> quantityKeywords = new CsvDictionaryKeywordProvider(new ClassPathResource("/dictionary/quantity_keywords.txt")).getDictionary();

                featureExtractors.add(new IsCoveredWithCellFeatureExtractor<>());
                featureExtractors.add(new IsFitCustomPatternFeatureExtractor<>("is_number_quantity", Pattern.compile(Placeholders.QUANTITY_REGEXP)));
                featureExtractors.add(new MatchFullColumnOrRowWithSpecifiedHeaderFeatureExtractor<>(Fields.FIELD_NAME_QUANTITY, quantityKeywords, true));
                break;

            case Fields.FIELD_NAME_PRODUCT:
                Set<String> productKeywords = new CsvDictionaryKeywordProvider(new ClassPathResource("/dictionary/product_keywords.txt")).getDictionary();

                featureExtractors.add(new IsCoveredWithCellFeatureExtractor<>());
                featureExtractors.add(new MatchFullColumnOrRowWithSpecifiedHeaderFeatureExtractor<>(Fields.FIELD_NAME_PRODUCT, productKeywords, true));
                break;
            case Fields.FIELD_NAME_TOTAL_AMOUNT:
                Set<String> totalAmountKeywords = new CsvDictionaryKeywordProvider(new ClassPathResource("/dictionary/total_amount_keywords.txt")).getDictionary();

                featureExtractors.add(new IsPrecededWithCurrencySignFeatureExtractor<>());
                featureExtractors.add(new MatchFullColumnOrRowWithSpecifiedHeaderFeatureExtractor<>(Fields.FIELD_NAME_TOTAL_AMOUNT, totalAmountKeywords, true));
                featureExtractors.add(new MatchFullColumnOrRowWithSpecifiedHeaderFeatureExtractor<>(Fields.FIELD_NAME_TOTAL_AMOUNT, totalAmountKeywords, false));
                featureExtractors.add(new IsPrecededWithKeywordsInLineFeatureExtractor<>(Fields.FIELD_NAME_TOTAL_AMOUNT, totalAmountKeywords));
                featureExtractors.add(new IsInFirstOrLastNLinesInDocumentFeatureExtractor<>(10, false));
                featureExtractors.add(new IsLastCellInTableFeatureExtractor<>());
                break;
        }

        return featureExtractors;
    }

    @Named("processors")
    public List<Processor<IeDocument>> getProcessors() {
        //TODO configure post processors here.
        return Arrays.asList(
                new AmountNormalizationPostProcessor(Fields.FIELD_NAME_PRICE),
                new AmountNormalizationPostProcessor(Fields.FIELD_NAME_TOTAL_AMOUNT),
                new AmountNormalizationPostProcessor(Fields.FIELD_NAME_QUANTITY),
                new SupplierNamePostProcessor(Fields.FIELD_NAME_SUPPLIER_NAME),
                new ProductPostProcessor(Fields.FIELD_NAME_PRODUCT),
                new DatePostProcessing(Fields.FIELD_NAME_INVOICE_DATE),
                new EmailPostProcessor(Fields.FIELD_NAME_EMAIL, Placeholders.EMAIL_REGEX),
                new RemoveCommasFromPrice(Fields.FIELD_NAME_PRICE),
                new RemoveCommasFromPrice(Fields.FIELD_NAME_TOTAL_AMOUNT),
                new DataValueNormalizationProcessor());
    }

}