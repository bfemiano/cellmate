package cellmate.extractor;

/**
 *
 * Enum for different the different types of CellExtractionExceptions
 * that can occur.
 *
 */
public enum ErrorType {

    CLASS_CAST,
    ILLEGAL_ACCESS,
    MISSING_FIELD,
    MISSING_ANNOTATION,
    MISSING_COLFAM_ON_WRITE,
    NULL_FIELD,
    TOO_MANY_FIELDS,
    UNSUPPORTED_TYPE,
    REJECTED_WRITE,
    UNKNOWN_ERROR;
}
