package com.nibokapp.nibok.domain.mapper.bookinsertion

import com.nibokapp.nibok.data.db.Book
import com.nibokapp.nibok.data.db.Insertion
import com.nibokapp.nibok.domain.model.BookInfoModel
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

    /**
     * Build a list of Insertion given a list of domain insertions.
     *
     * @param insertions the domain insertions
     *
     * @return a list of Insertion
     */
    fun convertInsertionListFromDomain(insertions: List<BookInsertionModel>) : List<Insertion>

    /**
     * Build an Insertion given a domain insertion.
     *
     * @param insertion the domain insertion
     *
     * @return an Insertion
     */
    fun convertInsertionFromDomain(insertion: BookInsertionModel) : Insertion

    /**
     * Build a BookInfoModel given the DB Book.
     *
     * @param book the DB Book
     *
     * @return a BookInfoModel if book data was found, null otherwise
     */
    fun convertBookToDomain(book: Book?) : BookInfoModel?

    /**
     * Build a Book given a domain BookInfoModel.
     *
     * @param bookInfo the domain book info
     *
     * @return a Book
     */
    fun convertBookFromDomain(bookInfo: BookInfoModel) : Book
}