package com.teddybrothers.androidlearning.view

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.teddybrothers.androidlearning.R
import com.teddybrothers.androidlearning.databinding.ActivityDetailMovieBinding
import com.teddybrothers.androidlearning.model.Movie
import com.teddybrothers.androidlearning.viewmodel.MovieViewModel
import kotlinx.android.synthetic.main.activity_detail_movie.*
import org.koin.android.ext.android.inject

class MovieDetailsActivity : BaseActivity() {

    companion object {
        const val PARAM_MOVIE = "movie"
    }

    lateinit var movie: Movie
    lateinit var binding: ActivityDetailMovieBinding
    var movieViewModel = inject<MovieViewModel>().value

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this,
            R.layout.activity_detail_movie
        )
        actionBar?.setDisplayHomeAsUpEnabled(true);
        //retrieved value from list
        intent.extras?.apply {
            movie = Gson().fromJson(getString("movie"), Movie::class.java)

        }

        init()

        bookNow.setOnClickListener {
            startActivity(Intent(this,BookNowActivity::class.java))
        }
    }

    private fun init() {
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
            title = movie.title
        }

        bookNow.setOnClickListener {
            startActivity(Intent(this, BookNowActivity::class.java))
        }

        getMovieDetail(movie.id.toString())
    }

    private fun getMovieDetail(movieId : String) {
        movieViewModel.getMovieDetailRepositoryRx(movieId)
            .observe(this, Observer { movieDetailResponse ->
                binding.movieDetail = movieDetailResponse
            })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        if (itemId == android.R.id.home) // Press Back Icon
        {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }


}