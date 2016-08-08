package com.nibokapp.nibok.ui.fragment.common

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nibokapp.nibok.domain.model.Insertion
import com.nibokapp.nibok.extension.getName
import com.nibokapp.nibok.extension.inflate
import com.nibokapp.nibok.ui.adapter.common.InfiniteScrollListener
import io.realm.Case

/**
 * Base fragment for fragments representing books lists.
 */
abstract class BookFragment : BaseFragment() {

    companion object {
        private val TAG = BookFragment::class.java.simpleName
    }

    /**
     * Get the layout used by the fragment.
     *
     * @return the fragment's layout id
     */
    abstract fun getFragmentLayout() : Int

    /**
     * Get the layout manager used by the books view.
     *
     * @return the layout manager used by the books view
     */
    abstract fun getBooksViewLayoutManager() : LinearLayoutManager

    /**
     * Get the books view defined in the fragment's layout.
     *
     * @return the books view defined in the fragment's layout.
     */
    abstract fun getBooksView() : RecyclerView

    /**
     * Get the adapter used by the books view.
     *
     * @return the adapter used by the books view
     */
    abstract fun getBooksViewAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>

    /**
     * The loading function called when scrolling down the books view.
     */
    abstract fun onScrollDownLoader()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return container?.inflate(getFragmentLayout())
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupBooksView()
    }

    /**
     * Scroll back to the top of the books view.
     */
    override fun handleBackToTopAction() {
        Log.d(TAG, "Going back to top")
        getBooksView().layoutManager.scrollToPosition(0)
    }

    override fun handleOnQueryTextSubmit(query: String) = handleOnQueryTextChange(query)

    override fun handleOnQueryTextChange(query: String) {
        val results = realm
                .where(Insertion::class.java)
                .contains("book.title", query, Case.INSENSITIVE)
                .or()
                .contains("book.authors.value", query, Case.INSENSITIVE)
                .or()
                .contains("book.publisher", query, Case.INSENSITIVE)
                .or()
                .contains("book.isbn", query, Case.INSENSITIVE)
                .findAll()
        Log.d(TAG, "${results.size}")
    }

    /**
     * Initial setup of the books view.
     *
     * Assign layout manager and adapter, add infinite scroll listener.
     */
    private fun setupBooksView() {


        val lm = getBooksViewLayoutManager()
        val booksView = getBooksView()
        val booksViewName = getBooksView().getName()
        Log.d(TAG, "Setting up Books View: " + booksViewName)
        booksView.apply {
            // Performance improvement
            setHasFixedSize(true)
            // Assign layout manager
            layoutManager = lm
            // Assign adapter
            if (adapter == null) {
                adapter = getBooksViewAdapter()
            }
            // Add infinite scroll listener
            clearOnScrollListeners()
            addOnScrollListener(InfiniteScrollListener(lm) {
                // Custom loading function executed on scroll down
                onScrollDownLoader()
            })
        }
    }


}