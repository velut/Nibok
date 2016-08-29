package com.nibokapp.nibok.domain.rule

/**
 * Validator for ISBN codes.
 */
class IsbnValidator {

    companion object {
        /**
         * Values for ISBN validation.
         */
        private val ISBN_13_LENGTH = 13
        private val ISBN_PREFIXES = listOf("977", "978", "979")
        private val ISBN_PREFIX_LENGTH = 3
        private val ALL_NUMBERS_PATTERN = Regex("[0-9]+")
    }

    /**
     * Check if the isbn code length is correct according to the ISBN-13 standard.
     *
     * @param isbnCode the isbn code to check
     *
     * @return true if the length is correct, false otherwise
     */
    fun isIsbnLengthValid(isbnCode: String) =
            isbnCode.length == ISBN_13_LENGTH

    /**
     * Check if the isbn code is in the form of a valid isbn code.
     * A valid isbn-13 code must be of the right length, composed only by numbers
     * and the starting prefix must be one of the prefixes currently in use.
     *
     * @param isbnCode the isbn code to check
     *
     * @return true if the isbn code form is valid, false otherwise
     */
    fun isIsbnValid(isbnCode: String) =
            isIsbnLengthValid(isbnCode)
                    && isbnCode.matches(ALL_NUMBERS_PATTERN)
                    && isIsbnPrefixValid(isbnCode)

    private fun isIsbnPrefixValid(isbnCode: String) =
            isbnCode.substring(0, ISBN_PREFIX_LENGTH) in ISBN_PREFIXES
}
