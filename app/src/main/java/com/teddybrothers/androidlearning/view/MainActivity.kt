package com.teddybrothers.androidlearning.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.google.gson.Gson
import com.teddybrothers.androidlearning.R
import com.teddybrothers.androidlearning.adapter.PaginationScrollListener
import com.teddybrothers.androidlearning.adapter.PaginationScrollListener.Companion.PAGE_START
import com.teddybrothers.androidlearning.adapter.RecyclerViewListener
import com.teddybrothers.androidlearning.adapter.RecyclerviewAdapter
import com.teddybrothers.androidlearning.model.Movie
import com.teddybrothers.androidlearning.model.MovieListOutput
import com.teddybrothers.androidlearning.viewmodel.MovieViewModel
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(),
    RecyclerViewListener, OnRefreshListener {

    companion object {
        const val RELEASE_DATE_DESC = "release_date.desc"
    }

    private lateinit var recyclerviewAdapter: RecyclerviewAdapter
    private var listOfMovies = ArrayList<Movie>()
    lateinit var movieViewModel: MovieViewModel
    private var currentPage: Int = PAGE_START
    private var isLastPage = false
    private var isLoading = false

    var itemCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //setActionBar title
        title = "Movies App"
        init()
        enableRefresh()
    }


    private fun init() {

        swipeRefreshLayout.setOnRefreshListener(this)

        movieViewModel = ViewModelProviders.of(this).get<MovieViewModel>(
            MovieViewModel::class.java
        )

        val linearLayoutManager = LinearLayoutManager(this)
        recyclerview.layoutManager = linearLayoutManager
        recyclerviewAdapter =
            RecyclerviewAdapter(this)
        recyclerview.adapter = recyclerviewAdapter

        recyclerview.addOnScrollListener(object : PaginationScrollListener(linearLayoutManager) {
            override fun loadMoreItems() {
                isLoading = true
                currentPage++
                getMovieList()
            }

            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }
        });

    }

    private fun getMovieList() {
        val call = movieViewModel.getMoviesRepository(RELEASE_DATE_DESC, currentPage)
        val observer = Observer<MovieListOutput> { movieListResponse ->
            if (movieListResponse != null) {
                val movieListResults: List<Movie>? = movieListResponse.results
                listOfMovies.addAll(movieListResults!!.toList())
                swipeRefreshLayout.disableRefresh()

                if (currentPage != PAGE_START)
                    recyclerviewAdapter.removeLoading()

                recyclerviewAdapter.updateDataSet(listOfMovies)
                // check weather is last page or not
                if (movieListResults.isNotEmpty()) {
                    recyclerviewAdapter.addLoading()
                } else {
                    isLastPage = true
                }
                isLoading = false
            }

        }
        if (!call.hasActiveObservers()) {
            call.observe(this, observer)
        }
    }

    private fun SwipeRefreshLayout.disableRefresh() {
        postDelayed({ isRefreshing = false }, 1500)
    }

    private fun enableRefresh() {
        swipeRefreshLayout.post {
            swipeRefreshLayout.isRefreshing = true
            getMovieList()
        }
    }

    override fun onClickListener(item: Any, position: Int) {
        if (item is Movie) {
            startActivity(Intent(
                this,
                DetailMovieActivity::class.java
            ).apply {
                putExtra("movie", Gson().toJson(item))
            })
        }
    }

    override fun onRefresh() {
        itemCount = 0
        currentPage = PAGE_START
        isLastPage = false
        recyclerviewAdapter.clear()
        listOfMovies.clear()
        enableRefresh()
    }

}