package com.example.youtube_clone

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.youtube_clone.Dto.VideoDto
import com.example.youtube_clone.adapter.MainVideoAdapter
import com.example.youtube_clone.databinding.FragmentPlayerBinding
import com.example.youtube_clone.service.VideoService
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.abs

class PlayerFragment: Fragment(R.layout.fragment_player) {

    private  var binding: FragmentPlayerBinding? = null

    private lateinit var mainVideoAdapter: MainVideoAdapter

    private var player: SimpleExoPlayer? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentPlayerBinding = FragmentPlayerBinding.bind(view)
        binding = fragmentPlayerBinding

        initMotionLayoutEvent(fragmentPlayerBinding)

        initRecyclerView(fragmentPlayerBinding)

        initPlayer(fragmentPlayerBinding)

        initControlButton(fragmentPlayerBinding)

        getVideoList()

    }

    private fun initControlButton(fragmentPlayerBinding: FragmentPlayerBinding) {
        fragmentPlayerBinding.bottomPlayerControlButton.setOnClickListener {
            player?.let {
                if (it.isPlaying) {
                    it.pause()
                } else {
                    it.play()
                }
            }
        }
    }

    private fun initPlayer(fragmentPlayerBinding: FragmentPlayerBinding) {

        context?.let {
            player = SimpleExoPlayer.Builder(it).build()
        }
        binding?.let {
            fragmentPlayerBinding.playerView.player = player
            player?.addListener(object: Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)

                    if (isPlaying) {
                        it.bottomPlayerControlButton
                            .setImageResource(R.drawable.ic_baseline_pause_24)
                    } else {
                        it.bottomPlayerControlButton
                            .setImageResource(R.drawable.ic_baseline_play_arrow_24)
                    }

                }
            })
        }
    }

    private fun initRecyclerView(fragmentPlayerBinding: FragmentPlayerBinding) {
        mainVideoAdapter = MainVideoAdapter {url, title ->
            setPlayer(url, title)
        }

        fragmentPlayerBinding.fragmentRecyclerView.apply {
            adapter = mainVideoAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun getVideoList() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://run.mocky.io/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(VideoService::class.java).also {
            it.getVideoList().enqueue(object: Callback<VideoDto> {
                override fun onResponse(call: Call<VideoDto>, response: Response<VideoDto>) {
                    if(response.isSuccessful.not()) {
                        Toast.makeText(context, "서버 연결에 실패했습니다. 잠시 후 다시 시도해주세요", Toast.LENGTH_SHORT)
                        return
                    }
                    response.body()?.let { videoDto ->
                        mainVideoAdapter.submitList(videoDto.videos)
                        mainVideoAdapter.notifyDataSetChanged()
                    }
                }

                override fun onFailure(call: Call<VideoDto>, t: Throwable) {
                    Toast.makeText(context, "서버 연결에 실패했습니다. 잠시 후 다시 시도해주세요", Toast.LENGTH_SHORT)
                }

            })
        }
    }

    private fun initMotionLayoutEvent(fragmentPlayerBinding: FragmentPlayerBinding) {
        fragmentPlayerBinding.playerMotionLayout.setTransitionListener(object: MotionLayout.TransitionListener {
            override fun onTransitionStarted(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int
            ) {

            }

            override fun onTransitionChange(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int,
                progress: Float
            ) {
                binding?.let {
                    (activity as MainActivity).also { mainActivity ->
                        mainActivity.findViewById<MotionLayout>(R.id.mainMotionLayout).progress = progress
                    }
                }
            }

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {

            }

            override fun onTransitionTrigger(
                motionLayout: MotionLayout?,
                triggerId: Int,
                positive: Boolean,
                progress: Float
            ) {

            }

        })

    }

    fun setPlayer(url: String, title: String) {

        context?.let {
            val dataSourceFactory = DefaultDataSourceFactory(it)
            val uri = Uri.parse(url)
            val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(uri))
            player?.setMediaSource(mediaSource)
            player?.prepare()
            player?.play()
        }

        binding?.let {
            it.playerMotionLayout.transitionToEnd()
            it.bottomTitleTextView.text = title
        }
    }

    override fun onStop() {
        super.onStop()
        player?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
        player?.release()
    }
}