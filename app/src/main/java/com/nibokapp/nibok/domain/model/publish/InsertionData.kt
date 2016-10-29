package com.nibokapp.nibok.domain.model.publish

import android.os.Parcel
import android.os.Parcelable

/**
 * Parcelable insertion data used in the publishing process.
 *
 * @param bookData the parcelable data about the book
 * @param bookPrice the price of the book
 * @param bookCondition the condition of the book
 * @param bookPictures the list of URIs pointing to pictures of the book taken by the user
 */
data class InsertionData(
        var bookData: BookData = BookData(),
        var bookPrice: Float = 0f,
        var bookCondition: String = "",
        var bookPictures: List<String> = emptyList()
) : Parcelable {
    companion object {
        @JvmField val CREATOR: Parcelable.Creator<InsertionData> = object : Parcelable.Creator<InsertionData> {
            override fun createFromParcel(source: Parcel): InsertionData = InsertionData(source)
            override fun newArray(size: Int): Array<InsertionData?> = arrayOfNulls(size)
        }
    }

    constructor(source: Parcel) : this(source.readParcelable<BookData>(BookData::class.java.classLoader), source.readFloat(), source.readString(), source.createStringArrayList())

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeParcelable(bookData, 0)
        dest?.writeFloat(bookPrice)
        dest?.writeString(bookCondition)
        dest?.writeStringList(bookPictures)
    }
}
