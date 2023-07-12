package com.workfusion.academy.config;

public class Placeholders {

    public static final String TRAINED_MODEL_ID = "ml-engineer-certification-trained-model";
    public final static String TRAINING_INPUT_DIR_PATH = "data/train";
    public final static String TRAINING_OUTPUT_DIR_PATH = "output-training";
    public final static String EXECUTION_INPUT_DIR_PATH = "data/validation";
    public final static String EXECUTION_OUTPUT_DIR_PATH = "output-execution";
    public static final String EMAIL_REGEX = "^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$";
    public static final String DATE_FORMAT = "MM/dd/yyyy";
    public static final String SUPPLIER_NAME_REGEXP = "((?i)[a-z]+, [a-z]+ and [a-z]+)|((?i)[0-9a-z\\-\\.,' ]+(llc|sons|(i|l)nc(\\.)?|plc|group|Design|Department|travel|studios|Invoces|integration|ltd|consulting|co(\\.)?(rp(\\.)?([a-z ]+)?|mpany|nstruction|lor)?|science|works|s\\.a\\.|lw|operations|security|development|painters|gmbh|limited|s\\.p\\.a\\.|resources|center|cleaning|store))|((?i)(m\\/s|c\\/o)[0-9a-z\\-\\.,' ]+)|((?i)work[a-z]+)|((?i)[a-z]+\\-[a-z]+)|((?i)bank[a-z ]+)";
    public static final String QUANTITY_REGEXP = "\\d+";
    public static final String DATE_REGEXP = ".*(\\d{2}/\\d{2}/(?:\\d{4}|\\d{2})).*";


}
