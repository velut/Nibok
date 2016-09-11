package com.nibokapp.nibok.domain.mapper

import com.nibokapp.nibok.data.db.Insertion
import com.nibokapp.nibok.domain.model.BookInsertionModel

/**
 * Interface for Data Mappers operating on books insertions.
 */
interface BookInsertionDataMapperInterface {

    /**
     * Build a list of BookInsertionModel given a list of insertions from the DB.
     * Insertions with invalid data are dropped from the returned list.
     *
     * @param insertions the list of insertions obtained from the DB
     *
     * @return a list of BookInsertionModel instances
     */
    fun convertInsertionListToDomain(insertions: List<Insertion>) : List<BookInsertionModel>

    /**
     * Build a BookInsertionModel from a DB insertion.
     *
     * @param insertion the considered insertion
     *
     * @return a BookInsertionModel instance containing detail data about the book's insertion
     * if insertion data is complete, null otherwise
     */
    fun convertInsertionToDomain(insertion: Insertion?) : BookInsertionModel?

}