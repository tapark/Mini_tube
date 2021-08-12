# 동영상 스트리밍


### Retrofit2
데이터 모델 지정
~~~kotlin
//수신 api (.json)
"videos": [
	{
		"description": "",
		"sources": "",
		"subtitle": "",
		"thumb": "",
		"title": ""
	}
]
~~~
~~~kotlin
// json key 값에 일치하는 VideoModel.kt 생성
data class VideoModel(
    val title: String,
    val subtitle: String,
    val description: String,
    val sources: String,
    val thumb: String
)
~~~
~~~kotlin
// .json 리스트(vidos)의 하위모델(VideoModel) 리스트 생성
data class VideoDto(
    val videos: List<VideoModel>
)
~~~
http 통신 interface
~~~kotlin
// VideoService.kt 생성
interface VideoService {

    @GET("/v3/15a561e3-b44d-4f44-b50e-519735e8cbac")
    fun getVideoList(): Call<VideoDto>
}
~~~
Json data 수신
~~~kotlin
// in MainActivity.kt
// retrofit 빌드
val retrofit = Retrofit.Builder()
	.baseUrl("https://run.mocky.io/")
	.addConverterFactory(GsonConverterFactory.create())
	.build()

// 인터페이스를 수신하는 retrofit 생성
retrofit.create(VideoService::class.java).also {
	it.getVideoList().enqueue(object : Callback<VideoDto> {
		override fun onResponse(call: Call<VideoDto>, response: Response<VideoDto>) {
			if (response.isSuccessful.not()) {
				// 실패처리
				return
			}
			response.body()?.let { videoDto ->
				// 성공 -> RecyclerView에 적용
				mainVideoAdapter.submitList(videoDto.videos)
				mainVideoAdapter.notifyDataSetChanged()
			}
		}
		override fun onFailure(call: Call<VideoDto>, t: Throwable) {
			// 실패처리
		}
	})
}
~~~

### Fragment
Activity에서 Fragment 호출
~~~kotlin
// activity_main.xml 에 fragmentContainer(FrameLayout) 생성
// PlayerFragment.kt 생성
supportFragmentManager.beginTransaction()
	.replace(R.id.fragmentContainer, PlayerFragment())
	.commit()
~~~
Activity에서 Fragment의 함수를 호출
~~~kotlin
supportFragmentManager.fragments.find {
	it is PlayerFragment
}?.let {
	(it as PlayerFragment).setPlayer(url, title)
}
~~~
Fragment 에서 Activity 호출
~~~kotlin

~~~

### ExoPlayer 라이브러리
~~~kotlin
// 전역으로 생성
private var player: SimpleExoPlayer? = null
// player 빌드
context?.let {
	player = SimpleExoPlayer.Builder(it).build()
}
// playerView에 player 세팅
binding.playerView.player = player
// Listener로 이벤트 수신
player?.addListener(object: Player.Listener {
	override fun onIsPlayingChanged(isPlaying: Boolean) {
		super.onIsPlayingChanged(isPlaying)
		if (isPlaying) {
			// playing
		} else {
			// not playing = pause
		}
	}
}
// 준비, 재생, 멈춤, 제거
player?.prepare()
player?.play()
player?.pause()
player?.release()
~~~

### MotionLayout
ConstraintLayout의 상위 라이브러리
동작(swipe, click 등)에 의한 뷰의 Motion을 정의할 수 있다.
~~~kotlin
// ConstraintLayout 우클릭 후 MotionLayout으로 확장
// xml 폴더가 생성되고 하위에 *_scene.xml이 생성된다.

// in *_scene.xml
// motion의 전체적인 구조 및 방식 설정(setting)
<Transition
	motion:constraintSetEnd="@+id/end"
	motion:constraintSetStart="@id/start"
	motion:duration="300">

	<KeyFrameSet>
		<KeyAttribute
			motion:motionTarget="@+id/bottomTitleTextView"
			motion:framePosition="10"
			android:alpha="0.0" />
		<KeyAttribute
			motion:motionTarget="@+id/bottomPlayerControlButton"
			motion:framePosition="10"
			android:alpha="0.0" />
		<KeyPosition
			motion:motionTarget="@+id/playerView"
			motion:framePosition="10"
			motion:keyPositionType="deltaRelative"
			motion:percentX="1"
			motion:percentWidth="1"/>
	</KeyFrameSet>

	<OnSwipe
		motion:touchAnchorId="@+id/mainContainerLayout"
		motion:touchAnchorSide="bottom" />

</Transition>

// 시작(start)과 끝(end) 지점 View의 속성 정의
    </Transition>

    <ConstraintSet android:id="@+id/start">

        <Constraint
            android:id="@+id/playerView"
            android:layout_width="0dp"
            android:layout_height="0dp"
            motion:layout_constraintDimensionRatio="4:2.5"
            motion:layout_constraintBottom_toBottomOf="@id/mainContainerLayout"
            motion:layout_constraintTop_toTopOf="@id/mainContainerLayout"
            motion:layout_constraintStart_toStartOf="@id/mainContainerLayout" />

    </ConstraintSet>

    <ConstraintSet android:id="@+id/end">

        <Constraint
            android:id="@+id/playerView"
            android:layout_width="0dp"
            android:layout_height="0dp"
            motion:layout_constraintBottom_toBottomOf="@id/mainContainerLayout"
            motion:layout_constraintTop_toTopOf="@id/mainContainerLayout"
            motion:layout_constraintStart_toStartOf="@id/mainContainerLayout"
            motion:layout_constraintEnd_toEndOf="@id/mainContainerLayout"/>

    </ConstraintSet>
~~~