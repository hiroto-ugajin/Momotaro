package jp.kanoyastore.hiroto.ugajin.momotaro

import android.graphics.Color
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.content.ContextCompat
import jp.kanoyastore.hiroto.ugajin.momotaro.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

private lateinit var binding: ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer

    val columnCount = 13
    val rowCount = 5

    var heroIndex = 0

    var heroPower = 0
    var hasMeat = false
    var hasBanana = false
    var hasBeans = false

    var isFirstTouch = true
    var startTime = 0L // 開始時間を保持する変数


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // 正方形のサイズを計算
        val squareSize = (resources.displayMetrics.widthPixels) / columnCount

        val gridLayout: GridLayout = findViewById(R.id.gridLayout)

        val mediaPlayerNice = MediaPlayer.create(this, R.raw.nicesound)
        val mediaPlayerSmallNice = MediaPlayer.create(this, R.raw.smallnice)
        val mediaPlayerDog = MediaPlayer.create(this, R.raw.dog)
        val mediaPlayerVictory = MediaPlayer.create(this, R.raw.victory)
        val mediaPlayerThunder = MediaPlayer.create(this, R.raw.thunder)

        val characterArray = arrayOf( R.drawable.meat,
            R.drawable.banana,
            R.drawable.beans,
            R.drawable.peach,
            R.drawable.peach,
            R.drawable.peach,
            R.drawable.dog,
            R.drawable.monkey,
            R.drawable.bird,
            R.drawable.bluedemon,
            R.drawable.reddemon,
         )

        val imageResources = arrayOf(
            arrayOf(
                R.drawable.a3,
                R.drawable.a1,
                R.drawable.a7,
                R.drawable.a1,
                R.drawable.a1,
                R.drawable.a7,
                R.drawable.a1,
                R.drawable.a7,
                R.drawable.a1,
                R.drawable.a1,
                R.drawable.a7,
                R.drawable.a1,
                R.drawable.a4,
            ),
            arrayOf(
                R.drawable.a10,
                R.drawable.a1,
                R.drawable.a9,
                R.drawable.a7,
                R.drawable.a1,
                R.drawable.a11,
                R.drawable.a1,
                R.drawable.a9,
                R.drawable.a7,
                R.drawable.a1,
                R.drawable.a11,
                R.drawable.a1,
                R.drawable.a5,
            ),
            arrayOf(
                R.drawable.a10,
                R.drawable.a1,
                R.drawable.a7,
                R.drawable.a9,
                R.drawable.a1,
                R.drawable.a11,
                R.drawable.a1,
                R.drawable.a7,
                R.drawable.a9,
                R.drawable.a1,
                R.drawable.a5,
                R.drawable.a0,
                R.drawable.a0,
            ),
            arrayOf(
                R.drawable.a6,
                R.drawable.a1,
                R.drawable.a9,
                R.drawable.a1,
                R.drawable.a1,
                R.drawable.a9,
                R.drawable.a1,
                R.drawable.a5,
                R.drawable.a0,
                R.drawable.a0,
                R.drawable.a20,
                R.drawable.a20,
                R.drawable.a20,
            ),
            arrayOf(
                R.drawable.a0,
                R.drawable.a0,
                R.drawable.a0,
                R.drawable.a0,
                R.drawable.a0,
                R.drawable.a0,
                R.drawable.a0,
                R.drawable.a0,
                R.drawable.a0,
                R.drawable.a0,
                R.drawable.a20,
                R.drawable.a20,
                R.drawable.a20,
            ),
        )


        // ImageViewを追加
        for (i in 0 until rowCount) {
            for (j in 0 until columnCount) {
                val imageView = ImageView(this)
                imageView.setBackgroundColor(Color.TRANSPARENT)
                val imageResource = imageResources[i][j]
                val drawable = ContextCompat.getDrawable(this, imageResource)
                imageView.background = drawable
                imageView.scaleType = ImageView.ScaleType.CENTER_INSIDE

                val layoutParams = GridLayout.LayoutParams().apply {
                    width = squareSize
                    height = squareSize
                    setMargins(0, 0, 0, 0) // 枠の間隔を設定
                    columnSpec = GridLayout.spec(j)
                    rowSpec = GridLayout.spec(i)
                }
                gridLayout.addView(imageView, layoutParams)
            }
        }

        // キャラクターをランダムに配置する
        val random = Random()
        val excludedIndices = setOf(0, 1, 2, 13, 37, 38) // 除外するインデックスのセット
        val occupiedIndices = mutableSetOf<Int>() // すでにキャラクターが配置されているインデックスのセット
        var characterIndex = 0

        while (occupiedIndices.size < 11)  {
            val randomIndex = random.nextInt(47)
            if (randomIndex !in excludedIndices && randomIndex !in occupiedIndices) {
                val imageView = gridLayout.getChildAt(randomIndex) as? ImageView
                if (imageView != null) {
                    val characterResource = characterArray[characterIndex]
                    imageView.setImageResource(characterResource)
                    imageView.tag = characterIndex
                    occupiedIndices.add(randomIndex)
                    characterIndex += 1
                }
            }
        }

        setMomotarou()

//        val startTime = System.currentTimeMillis() // ゲーム開始時の時間

        val timer = Timer()
        val timerTask = object : TimerTask() {
            override fun run() {
                val currentTime = System.currentTimeMillis() // 現在の時間
                val elapsedTime = currentTime - startTime // 経過時間（ミリ秒）
                val secondsElapsed = elapsedTime / 1000 // 経過時間（秒）
                // 経過時間を表示するなどの処理を行う
                runOnUiThread {
                    // UIの更新をメインスレッドで行う
                    binding.timerTextView.text = "経過時間: ${secondsElapsed}秒"
                }
            }
        }

// スタートボタンのクリックリスナー
        fun startTimer() {
            timer.scheduleAtFixedRate(timerTask, 0, 1000) // 1秒ごとにタイマータスクを実行する
        }

        fun gameEnd() {
        timer.cancel()
        binding.centerTextView.text = "DEFEAT"
        binding.gridLayout.setOnTouchListener(null)
    }

        fun gameEnd2() {
            timer.cancel()
            binding.centerTextView.text = "VICTORY"
            binding.gridLayout.setOnTouchListener(null)
        }


        // タッチイベントリスナーの設定
            gridLayout.setOnTouchListener { view, event: MotionEvent ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {

                        if (isFirstTouch) {
                        startTime = System.currentTimeMillis() // 開始時間を設定
                        startTimer()
                            isFirstTouch = false }

                        val touchedImageView = getTouchedImageView(event)

                        val touchedPosition = if (touchedImageView != null) {
                            getPositionFromImageView(touchedImageView)

                        } else {
                            -1
                        }
//                        タグからimageViewを探し出す処理
                        val blueDemonImageView: ImageView? = gridLayout.findViewWithTag(9)
                        val redDemonImageView: ImageView? = gridLayout.findViewWithTag(10)

                        if ((touchedPosition/columnCount == heroIndex/columnCount) && (touchedPosition > heroIndex && heroIndex != 36) && (touchedPosition > heroIndex && heroIndex != 46)) {
                            moveHeroRight()
                            if (blueDemonImageView == gridLayout.getChildAt(heroIndex) as? ImageView  && 3 > heroPower) {
                                blueDemonImageView!!.setImageResource(R.drawable.batsu100)
                                mediaPlayerThunder.start()
                                gameEnd()
                            }
                            else if (blueDemonImageView == gridLayout.getChildAt(heroIndex) as? ImageView  && 6 == heroPower) {
                                blueDemonImageView!!.setImageResource(R.drawable.maru100)
                                heroPower += 1
                                blueDemonImageView!!.setTag(null)
                                mediaPlayerNice.start()
                            } else if (blueDemonImageView == gridLayout.getChildAt(heroIndex) as? ImageView  && 7 == heroPower) {
                                mediaPlayerVictory.start()
                                gameEnd2()
                            }

                            if (redDemonImageView == gridLayout.getChildAt(heroIndex) as? ImageView  && 3 > heroPower) {
                                redDemonImageView!!.setImageResource(R.drawable.batsu100)
                                mediaPlayerThunder.start()
                                gameEnd()
                            }
                            else if (redDemonImageView == gridLayout.getChildAt(heroIndex) as? ImageView  && 6 == heroPower) {
                                redDemonImageView!!.setImageResource(R.drawable.maru100)
                                heroPower += 1
                                mediaPlayerNice.start()
                                redDemonImageView!!.setTag(null)
                            } else if (redDemonImageView == gridLayout.getChildAt(heroIndex) as? ImageView  && 7 == heroPower) {
                                mediaPlayerVictory.start()
                                gameEnd2()
                            }


                        } else if (touchedPosition != -1 && (touchedPosition/columnCount == heroIndex/columnCount) && touchedPosition < heroIndex) {
                            moveHeroLeft()
                            if (blueDemonImageView == gridLayout.getChildAt(heroIndex) as? ImageView  && 3 > heroPower) {
                                blueDemonImageView!!.setImageResource(R.drawable.batsu100)
                                mediaPlayerThunder.start()
                                gameEnd()
                                }
                            else if (blueDemonImageView == gridLayout.getChildAt(heroIndex) as? ImageView  && 6 == heroPower) {
                                blueDemonImageView!!.setImageResource(R.drawable.maru100)
                                heroPower += 1
                                blueDemonImageView!!.setTag(null)
                                mediaPlayerNice.start()
                            } else if (blueDemonImageView == gridLayout.getChildAt(heroIndex) as? ImageView  && 7 == heroPower) {
                                mediaPlayerVictory.start()
                                gameEnd2()
                            }

                            if (redDemonImageView == gridLayout.getChildAt(heroIndex) as? ImageView  && 3 > heroPower) {
                                redDemonImageView!!.setImageResource(R.drawable.batsu100)
                                mediaPlayerThunder.start()
                                gameEnd()
                            }
                            else if (redDemonImageView == gridLayout.getChildAt(heroIndex) as? ImageView  && 6 == heroPower) {
                                redDemonImageView!!.setImageResource(R.drawable.maru100)
                                heroPower += 1
                                mediaPlayerNice.start()
                                redDemonImageView!!.setTag(null)
                            } else if (redDemonImageView == gridLayout.getChildAt(heroIndex) as? ImageView  && 7 == heroPower) {
                                mediaPlayerVictory.start()
                                gameEnd2()
                            }
                        }

                            val columnIndex = touchedPosition % columnCount
                            if (columnIndex == heroIndex % columnCount) {
                                if (touchedPosition < heroIndex && isUpSpecialHeroIndex(heroIndex)) {
                                    moveHeroUp()
                                    if (blueDemonImageView == gridLayout.getChildAt(heroIndex) as? ImageView  && 3 > heroPower) {
                                        blueDemonImageView!!.setImageResource(R.drawable.batsu100)
                                        mediaPlayerThunder.start()
                                        gameEnd()
                                    }
                                    else if (blueDemonImageView == gridLayout.getChildAt(heroIndex) as? ImageView  && 6 == heroPower) {
                                        blueDemonImageView!!.setImageResource(R.drawable.maru100)
                                        heroPower += 1
                                        blueDemonImageView!!.setTag(null)
                                        mediaPlayerNice.start()
                                    } else if (blueDemonImageView == gridLayout.getChildAt(heroIndex) as? ImageView  && 7 == heroPower) {
                                        mediaPlayerVictory.start()
                                        gameEnd2()
                                    }
                                    if (redDemonImageView == gridLayout.getChildAt(heroIndex) as? ImageView  && 3 > heroPower) {
                                        redDemonImageView!!.setImageResource(R.drawable.batsu100)
                                        mediaPlayerThunder.start()
                                        gameEnd()
                                    }
                                    else if (redDemonImageView == gridLayout.getChildAt(heroIndex) as? ImageView  && 6 == heroPower) {
                                        redDemonImageView!!.setImageResource(R.drawable.maru100)
                                        heroPower += 1
                                        mediaPlayerNice.start()
                                        redDemonImageView!!.setTag(null)
                                    } else if (redDemonImageView == gridLayout.getChildAt(heroIndex) as? ImageView  && 7 == heroPower) {
                                        mediaPlayerVictory.start()
                                        gameEnd2()
                                    }
                                }
                                if (touchedPosition > heroIndex && isDownSpecialHeroIndex(heroIndex)) {
                                    moveHeroDown()
                                    if (blueDemonImageView == gridLayout.getChildAt(heroIndex) as? ImageView  && 3 > heroPower) {
                                        blueDemonImageView!!.setImageResource(R.drawable.batsu100)
                                        mediaPlayerThunder.start()
                                        gameEnd()
                                    }
                                    else if (blueDemonImageView == gridLayout.getChildAt(heroIndex) as? ImageView  && 6 == heroPower) {
                                        blueDemonImageView!!.setImageResource(R.drawable.maru100)
                                        heroPower += 1
                                        blueDemonImageView!!.setTag(null)
                                        mediaPlayerNice.start()
                                    } else if (blueDemonImageView == gridLayout.getChildAt(heroIndex) as? ImageView  && 7 == heroPower) {
                                        mediaPlayerVictory.start()
                                        gameEnd2()
                                    }
                                    if (redDemonImageView == gridLayout.getChildAt(heroIndex) as? ImageView  && 3 > heroPower) {
                                        redDemonImageView!!.setImageResource(R.drawable.batsu100)
                                        mediaPlayerThunder.start()
                                        gameEnd()
                                    }
                                    else if (redDemonImageView == gridLayout.getChildAt(heroIndex) as? ImageView  && 6 == heroPower) {
                                        redDemonImageView!!.setImageResource(R.drawable.maru100)
                                        heroPower += 1
                                        mediaPlayerNice.start()
                                        redDemonImageView!!.setTag(null)
                                    } else if (redDemonImageView == gridLayout.getChildAt(heroIndex) as? ImageView  && 7 == heroPower) {
                                        mediaPlayerVictory.start()
                                        gameEnd2()
                                    }
                                }
                            }
                    }
                }
            true
        }
    }

    private fun isDownSpecialHeroIndex(heroIndex: Int): Boolean {
        val specialIndices = setOf(0, 2, 5, 7, 10, 12, 13, 16, 18, 21, 23, 26, 28, 31, 33)
        return heroIndex in specialIndices
    }

    private fun isUpSpecialHeroIndex(heroIndex: Int): Boolean {
        val specialIndices = setOf(13, 15, 18, 20, 23, 25, 26, 29, 31, 34, 36, 39, 41, 44, 46)
        return heroIndex in specialIndices
    }

    private fun getTouchedImageView(event: MotionEvent): ImageView? {
        val gridLayout: GridLayout = findViewById(R.id.gridLayout)
            val x = event.x.toInt()
            val y = event.y.toInt()
            for (i in 0 until rowCount) {
                for (j in 0 until columnCount) {
                    val imageView = gridLayout.getChildAt(i * columnCount + j) as? ImageView
                    if (imageView != null && imageView.left <= x && x <= imageView.right &&
                        imageView.top <= y && y <= imageView.bottom
                    ) {
                        return imageView
                    }
                }
            }
           return null
        }

    private fun getPositionFromImageView(imageView: ImageView): Int {
        val gridLayout: GridLayout = findViewById(R.id.gridLayout)
        // ImageView の位置情報を取得する
        val index = gridLayout.indexOfChild(imageView)
            val row = index / columnCount
            val column = index % columnCount
            return row * columnCount + column
    }

    private fun moveHeroLeft() {
        val gridLayout: GridLayout = findViewById(R.id.gridLayout)
        if (heroIndex % columnCount != 0) {
            val targetImageView = gridLayout.getChildAt(heroIndex - 1) as? ImageView
            val heroImageView = gridLayout.getChildAt(heroIndex) as? ImageView
            val heroTag = heroImageView?.tag
            if (targetImageView != null) {
                if (heroTag == 9 && 3 <= heroPower && heroPower < 6) {
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(200)
                        heroImageView.setImageResource(R.drawable.bluedemon)
                        targetImageView.setImageResource(R.drawable.momotarou)
                    }
                }
                if (heroTag == 10 && 3 <= heroPower && heroPower < 6) {
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(200)
                        heroImageView.setImageResource(R.drawable.reddemon)
                        targetImageView.setImageResource(R.drawable.momotarou)
                    }
                }
                else {
                    targetImageView.setImageResource(R.drawable.momotarou)
                    handleHeroMove()
                }
            }
            if (heroImageView != null) {
                heroImageView.setImageDrawable(null)
                heroIndex -= 1 // hero の位置を更新する
            }
        }
    }

    private fun moveHeroRight() {
        val gridLayout: GridLayout = findViewById(R.id.gridLayout)
        if (heroIndex % columnCount != columnCount - 1) {
            val targetImageView = gridLayout.getChildAt(heroIndex + 1) as? ImageView
            val heroImageView = gridLayout.getChildAt(heroIndex) as? ImageView
            val heroTag = heroImageView?.tag
            if (targetImageView != null) {
                if (heroTag == 9 && 3 <= heroPower && heroPower < 6) {
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(200)
                        heroImageView.setImageResource(R.drawable.bluedemon)
                        targetImageView.setImageResource(R.drawable.momotarou)
                    }
                }
                if (heroTag == 10 && 3 <= heroPower && heroPower < 6) {
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(200)
                        heroImageView.setImageResource(R.drawable.reddemon)
                        targetImageView.setImageResource(R.drawable.momotarou)
                    }
                }
                else {
                    targetImageView.setImageResource(R.drawable.momotarou)
                    handleHeroMove()
                }
            }
            if (heroImageView != null) {
                heroImageView.setImageDrawable(null)
                heroIndex += 1 // hero の位置を更新する
            }
        }
    }

    private fun moveHeroUp() {
        val gridLayout: GridLayout = findViewById(R.id.gridLayout)
            val targetImageView = gridLayout.getChildAt(heroIndex - columnCount) as? ImageView
            val heroImageView = gridLayout.getChildAt(heroIndex) as? ImageView
            val heroTag = heroImageView?.tag
            if (targetImageView != null) {
                if (heroTag == 9 && 3 <= heroPower && heroPower < 6) {
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(200)
                        heroImageView.setImageResource(R.drawable.bluedemon)
                        targetImageView.setImageResource(R.drawable.momotarou)
                    }
                }
                if (heroTag == 10 && 3 <= heroPower && heroPower < 6) {
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(200)
                        heroImageView.setImageResource(R.drawable.reddemon)
                        targetImageView.setImageResource(R.drawable.momotarou)
                    }
                }
                else {
                    targetImageView.setImageResource(R.drawable.momotarou)
                    handleHeroMove()
                }
            }
            if (heroImageView != null)
                heroImageView.setImageDrawable(null)
            heroIndex = heroIndex - columnCount // hero の位置を更新する
    }

    private fun moveHeroDown() {
        val gridLayout: GridLayout = findViewById(R.id.gridLayout)
        val targetImageView = gridLayout.getChildAt(heroIndex + columnCount) as? ImageView
        val heroImageView = gridLayout.getChildAt(heroIndex) as? ImageView
        val heroTag = heroImageView?.tag
        if (targetImageView != null) {
            if (heroTag == 9 && 3 <= heroPower && heroPower < 6) {
                CoroutineScope(Dispatchers.Main).launch {
                    delay(200)
                    heroImageView.setImageResource(R.drawable.bluedemon)
                    targetImageView.setImageResource(R.drawable.momotarou)
                }
            }
            if (heroTag == 10 && 3 <= heroPower && heroPower < 6) {
                CoroutineScope(Dispatchers.Main).launch {
                    delay(200)
                    heroImageView.setImageResource(R.drawable.reddemon)
                    targetImageView.setImageResource(R.drawable.momotarou)
                }
            }
            else {
                targetImageView.setImageResource(R.drawable.momotarou)
                handleHeroMove()
            }
        }

        if (heroImageView != null)
            heroImageView.setImageDrawable(null)
        heroIndex = heroIndex + columnCount // hero の位置を更新する
    }

    private fun handleHeroMove() {
        val gridLayout: GridLayout = findViewById(R.id.gridLayout)
        val heroImageView = gridLayout.getChildAt(heroIndex) as? ImageView
        val heroTag = heroImageView?.tag

        // 特定のタグのimageViewに来た場合の処理
        if (heroTag == 0) {
            hasMeat = true
            val jumpToImageView = gridLayout.getChildAt(49) as? ImageView
            jumpToImageView?.setImageResource(R.drawable.meat)
            heroImageView.setTag(null)
        }
        if (heroTag == 1) {
            hasBanana = true
            val jumpToImageView = gridLayout.getChildAt(50) as? ImageView
            jumpToImageView?.setImageResource(R.drawable.banana)
            heroImageView.setTag(null)
        }
        if (heroTag == 2) {
            hasBeans = true
            val jumpToImageView = gridLayout.getChildAt(51) as? ImageView
            jumpToImageView?.setImageResource(R.drawable.beans)
            heroImageView.setTag(null)
        }
        if (heroTag == 3) {
            heroPower += 1
            val jumpToImageView = gridLayout.getChildAt(62) as? ImageView
            jumpToImageView?.setImageResource(R.drawable.peach)
            heroImageView.setTag(null)
        }
        if (heroTag == 4) {
            heroPower += 1
            val jumpToImageView = gridLayout.getChildAt(63) as? ImageView
            jumpToImageView?.setImageResource(R.drawable.peach)
            heroImageView.setTag(null)
        }
        if (heroTag == 5) {
            heroPower += 1
            val jumpToImageView = gridLayout.getChildAt(64) as? ImageView
            jumpToImageView?.setImageResource(R.drawable.peach)
            heroImageView.setTag(null)
        }
        if (heroTag == 6) {
            if (hasMeat == true) {
                heroPower += 1
                val jumpToImageView = gridLayout.getChildAt(49) as? ImageView
                jumpToImageView?.setImageResource(R.drawable.dog)
                hasMeat = false
                heroImageView.setTag(null)
            } else {
                                CoroutineScope(Dispatchers.Main).launch {
                    delay(200)
                heroImageView.setImageResource(R.drawable.dog)
            }
            }
        }
        if (heroTag == 7) {
            if (hasBanana == true) {
                heroPower += 1
                val jumpToImageView = gridLayout.getChildAt(50) as? ImageView
                jumpToImageView?.setImageResource(R.drawable.monkey)
                hasBanana = false
                heroImageView.setTag(null)
            } else {
                                CoroutineScope(Dispatchers.Main).launch {
                    delay(200)
                    heroImageView.setImageResource(R.drawable.monkey)
            }
            }
        }
        if (heroTag == 8) {
            if (hasBeans == true) {
                heroPower += 1
                val jumpToImageView = gridLayout.getChildAt(51) as? ImageView
                jumpToImageView?.setImageResource(R.drawable.bird)
                hasBeans = false
                heroImageView.setTag(null)
            } else {
                                CoroutineScope(Dispatchers.Main).launch {
                    delay(200)
                heroImageView.setImageResource(R.drawable.bird)
            }
            }
        }
    }

    private fun setMomotarou() {
            val gridLayout: GridLayout = findViewById(R.id.gridLayout)
            val imageView: ImageView = gridLayout.getChildAt(0) as ImageView
            imageView.setImageResource(R.drawable.momotarou)
    }
}












