package jp.kanoyastore.hiroto.ugajin.momotaro

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.content.ContextCompat
import jp.kanoyastore.hiroto.ugajin.momotaro.databinding.ActivityMainBinding
import java.util.*

private lateinit var binding: ActivityMainBinding

class MainActivity : AppCompatActivity() {


    val columnCount = 13
    val rowCount = 5
    var heroIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // 正方形のサイズを計算
        val squareSize = (resources.displayMetrics.widthPixels) / columnCount

        val gridLayout: GridLayout = findViewById(R.id.gridLayout)

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

        val random = Random()
        val excludedIndices = setOf(0, 1, 2, 13, 37, 38) // 除外するインデックスのセット
        val occupiedIndices = mutableSetOf<Int>() // すでにキャラクターが配置されているインデックスのセット
// キャラクターをランダムに配置する
        while (occupiedIndices.size < 11) {
            val randomIndex = random.nextInt(47)
            if (randomIndex !in excludedIndices && randomIndex !in occupiedIndices) {
                val imageView = gridLayout.getChildAt(randomIndex) as? ImageView
                if (imageView != null) {
                    val characterResource = characterArray[random.nextInt(characterArray.size)]
                    imageView.setImageResource(characterResource)
                    occupiedIndices.add(randomIndex)
                }
            }
        }

        setMomotarou()

        // タッチイベントリスナーの設定
            gridLayout.setOnTouchListener { view, event: MotionEvent ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        val touchedImageView = getTouchedImageView(event)

                        val touchedPosition = if (touchedImageView != null) {
                            getPositionFromImageView(touchedImageView)
                        } else {
                            -1
                        }

                        if ((touchedPosition/columnCount == heroIndex/columnCount) && (touchedPosition > heroIndex && heroIndex != 36) && (touchedPosition > heroIndex && heroIndex != 46)) {
                            moveHeroRight()
                        } else if (touchedPosition != -1 && (touchedPosition/columnCount == heroIndex/columnCount) && touchedPosition < heroIndex) {
                            moveHeroLeft()
                        }


                            val columnIndex = touchedPosition % columnCount
                            if (columnIndex == heroIndex % columnCount) {
                                if (touchedPosition < heroIndex && isUpSpecialHeroIndex(heroIndex)) {
                                    moveHeroUp()
                                }
                                if (touchedPosition > heroIndex && isDownSpecialHeroIndex(heroIndex)) {
                                    moveHeroDown()
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

    // hero を左に移動する関数
    private fun moveHeroLeft() {
        val gridLayout: GridLayout = findViewById(R.id.gridLayout)
        if (heroIndex % columnCount != 0) {
            val targetImageView = gridLayout.getChildAt(heroIndex - 1) as? ImageView
            if (targetImageView != null) {
                    // 移動先の ImageView に hero の画像を設定するなどの処理を行う
                targetImageView.setImageResource(R.drawable.momotarou)}
            val heroImageView = gridLayout.getChildAt(heroIndex) as? ImageView
            if (heroImageView != null) {
                heroImageView.setImageDrawable(null)
                heroIndex -= 1 // hero の位置を更新する
            }
        }
    }

    private fun moveHeroRight() {
        val gridLayout: GridLayout = findViewById(R.id.gridLayout)
        // ヒーローを右に移動する処理
        if (heroIndex % columnCount != columnCount - 1) {
            val targetImageView = gridLayout.getChildAt(heroIndex + 1) as? ImageView
            if (targetImageView != null) {
                    // 移動先の ImageView に hero の画像を設定するなどの処理を行う
                    targetImageView.setImageResource(R.drawable.momotarou)
            }
            val heroImageView = gridLayout.getChildAt(heroIndex) as? ImageView
            if (heroImageView != null)
                heroImageView.setImageDrawable(null)
            heroIndex += 1 // hero の位置を更新する
        }
    }

    private fun moveHeroUp() {
        val gridLayout: GridLayout = findViewById(R.id.gridLayout)
        val targetImageView = gridLayout.getChildAt(heroIndex - columnCount) as? ImageView
        if (targetImageView != null) {
            // 移動先の ImageView に hero の画像を設定するなどの処理を行う
            targetImageView.setImageResource(R.drawable.momotarou)
        }
        val heroImageView = gridLayout.getChildAt(heroIndex) as? ImageView
        if (heroImageView != null)
            heroImageView.setImageDrawable(null)
        heroIndex = heroIndex - columnCount // hero の位置を更新する
    }

    private fun moveHeroDown() {
        val gridLayout: GridLayout = findViewById(R.id.gridLayout)
        val targetImageView = gridLayout.getChildAt(heroIndex + columnCount) as? ImageView
        if (targetImageView != null) {
            // 移動先の ImageView に hero の画像を設定するなどの処理を行う
            targetImageView.setImageResource(R.drawable.momotarou)
        }
        val heroImageView = gridLayout.getChildAt(heroIndex) as? ImageView
        if (heroImageView != null)
            heroImageView.setImageDrawable(null)
        heroIndex = heroIndex + columnCount // hero の位置を更新する
    }

    private fun setMomotarou() {
            val gridLayout: GridLayout = findViewById(R.id.gridLayout)
            val imageView: ImageView = gridLayout.getChildAt(0) as ImageView
            imageView.setImageResource(R.drawable.momotarou)
    }
}












