package jp.kanoyastore.hiroto.ugajin.momotaro

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.ImageView
import jp.kanoyastore.hiroto.ugajin.momotaro.databinding.ActivityMainBinding

private lateinit var binding: ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val gridLayout: GridLayout = findViewById(R.id.gridLayout)

        val columnCount = 15
        val rowCount = 5

        // 正方形のサイズを計算
        val squareSize = (resources.displayMetrics.widthPixels) / columnCount

        val imageResources = arrayOf(
            arrayOf(R.drawable.a0, R.drawable.a3, R.drawable.a1, R.drawable.a7, R.drawable.a1, R.drawable.a1, R.drawable.a7, R.drawable.a1, R.drawable.a7, R.drawable.a1, R.drawable.a1, R.drawable.a7, R.drawable.a1, R.drawable.a4, R.drawable.a0,),
            arrayOf(R.drawable.a0, R.drawable.a10, R.drawable.a1, R.drawable.a9, R.drawable.a7, R.drawable.a1, R.drawable.a11, R.drawable.a1, R.drawable.a9, R.drawable.a7, R.drawable.a1, R.drawable.a11, R.drawable.a1, R.drawable.a5, R.drawable.a0,),
            arrayOf(R.drawable.a0, R.drawable.a10, R.drawable.a1, R.drawable.a7, R.drawable.a9, R.drawable.a1, R.drawable.a11, R.drawable.a1, R.drawable.a7, R.drawable.a9, R.drawable.a1, R.drawable.a5, R.drawable.a0, R.drawable.a0, R.drawable.a0,),
            arrayOf(R.drawable.a0, R.drawable.a6, R.drawable.a1, R.drawable.a9, R.drawable.a1, R.drawable.a1, R.drawable.a9, R.drawable.a1, R.drawable.a5, R.drawable.a0, R.drawable.a0, R.drawable.a20, R.drawable.a20, R.drawable.a20, R.drawable.a0,),
            arrayOf(R.drawable.a0, R.drawable.a0, R.drawable.a0, R.drawable.a0, R.drawable.a0, R.drawable.a0, R.drawable.a0, R.drawable.a0, R.drawable.a0, R.drawable.a0, R.drawable.a0, R.drawable.a20, R.drawable.a20, R.drawable.a20, R.drawable.a0,),
        )

        // ImageViewを追加
        for (i in 0 until rowCount) {
            for (j in 0 until columnCount) {
                val imageView = ImageView(this)
                imageView.setBackgroundColor(Color.TRANSPARENT)
                val imageResource = imageResources[i][j]
                imageView.setImageResource(imageResource)
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
    }
}






