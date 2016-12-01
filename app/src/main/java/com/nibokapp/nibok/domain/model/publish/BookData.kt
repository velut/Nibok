package com.nibokapp.nibok.domain.model.publish

import android.os.Parcel
import android.os.Parcelable

/**
 * Parcelable book data used in the publishing process.
 *
 * @param id the id that the book data has on the server, if any
 * @param title the title of the book
 * @param authors the list of authors of the book
 * @param year the year in which the book was published
 * @param publisher the publisher of the book
 * @param isbn the isbn code of the book
 */
data class BookData(
        var id: String = "",
        var title: String = "",
        var authors: List<String> = emptyList(),
        var year: Int = 0,
        var publisher: String = "",
        var isbn: String = ""
) : Parcelable {
    companion object {
        @JvmField val CREATOR: Parcelable.Creator<BookData> = object : Parcelable.Creator<BookData> {
            override fun createFromParcel(source: Parcel): BookData = BookData(source)
            override fun newArray(size: Int): Array<BookData?> = arrayOfNulls(size)
        }
    }

    constructor(source: Parcel) : this(source.readString(), source.readString(), source.createStringArrayList(), source.readInt(), source.readString(), source.readString())

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(id)
        dest?.writeString(title)
        dest?.writeStringList(authors)
        dest?.writeInt(year)
        dest?.writeString(publisher)
        dest?.writeString(isbn)
    }

    /**
     * Check if data about the book contained in this BookData differs from another instance of BookData.
     *
     * @param other the other BookData object
     *
     * @return true if book data differs, false otherwise
     */
    fun differsFrom(other: BookData): Boolean =
            this.title.toLowerCase() != other.title.toLowerCase() ||
                    this.authors.map(String::toLowerCase).toSet() != other.authors.map(String::toLowerCase).toSet() ||
                    this.year != other.year ||
                    this.publisher.toLowerCase() != other.publisher.toLowerCase()
}