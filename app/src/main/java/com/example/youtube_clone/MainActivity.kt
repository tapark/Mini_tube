package com.example.youtube_clone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.youtube_clone.Dto.VideoDto
import com.example.youtube_clone.adapter.MainVideoAdapter
import com.example.youtube_clone.databinding.ActivityMainBinding
import com.example.youtube_clone.service.VideoService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var mainVideoAdapter: MainVideoAdapter

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, PlayerFragment())
            .commit()

        mainVideoAdapter = MainVideoAdapter {url, title ->
            supportFragmentManager.fragments.find {
                it is PlayerFragment
            }?.let {
                (it as PlayerFragment).setPlayer(url, title)
            }
        }
        binding.mainRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.mainRecyclerView.adapter = mainVideoAdapter

        getVideoList()

    }

    private fun getVideoList() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://run.mocky.io/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(VideoService::class.java).also {
            it.getVideoList().enqueue(object : Callback<VideoDto> {
                override fun onResponse(call: Call<VideoDto>, response: Response<VideoDto>) {
                    if (response.isSuccessful.not()) {
                        Toast.makeText(this@MainActivity, "서버 연결에 실패했습니다. 잠시 후 다시 시도해주세요", Toast.LENGTH_SHORT)
                        return
                    }
                    response.body()?.let { videoDto ->

                        mainVideoAdapter.submitList(videoDto.videos)
                        mainVideoAdapter.notifyDataSetChanged()
                    }
                }

                override fun onFailure(call: Call<VideoDto>, t: Throwable) {
                    Toast.makeText(this@MainActivity, "서버 연결에 실패했습니다. 잠시 후 다시 시도해주세요", Toast.LENGTH_SHORT)
                }

            })
        }
    }
}