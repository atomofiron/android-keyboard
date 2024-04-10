package demo.atomofiron.keyboard

import android.annotation.SuppressLint
import android.graphics.Outline
import android.os.Bundle
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.view.ViewOutlineProvider
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat.Type
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.GridLayoutManager
import demo.atomofiron.keyboard.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val controller by lazy { WindowCompat.getInsetsController(window, window.decorView) }
    private val insetsCallback by lazy { InsetsCallback(binding) }
    private val animController by lazy { InsetsController(controller, insetsCallback) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.navigationBarColor = 0x01000000
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.config()
        binding.addMockItems()
        binding.insets()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun ActivityMainBinding.config() {
        val cornerRadius = resources.getDimension(R.dimen.corner_radius)
        searchBtn.setOnClickListener { insetsCallback.show() }
        root.controller = animController
        navigation.elevation = resources.getDimension(R.dimen.elevation)
        val currentNight = resources.getBoolean(R.bool.night)
        theme.setImageResource(if (currentNight) R.drawable.ic_mode_day else R.drawable.ic_mode_night)
        theme.setOnClickListener {
            AppCompatDelegate.setDefaultNightMode(if (currentNight) MODE_NIGHT_NO else MODE_NIGHT_YES)
            recreate()
        }
        categoriesBtn.isChecked = true
        buttons.clipToOutline = true
        buttons.outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) = outline.setRoundRect(0, 0, view.width, view.height, cornerRadius)
        }
        headerContent.addOnLayoutChangeListener { _, _, top, _, bottom, _, _, _, _ ->
            headerContent.translationY = (top - bottom).toFloat()
        }
    }

    private fun ActivityMainBinding.insets() {
        val half = resources.getDimensionPixelSize(R.dimen.half)
        ViewCompat.setOnApplyWindowInsetsListener(root) { _, insets ->
            poop("insets ime ${insets.getInsets(Type.ime())}")
            insetsCallback.setTarget(insets.getInsets(Type.ime()).bottom)
            val bars = insets.getInsets(Type.systemBars() or Type.displayCutout())
            navigation.updatePadding(left = bars.left, right = bars.right, bottom = bars.bottom)
            theme.updateLayoutParams<MarginLayoutParams> {
                topMargin = bars.top
                leftMargin = bars.left + half
                rightMargin = bars.right + half
            }
            insets
        }
        ViewCompat.setWindowInsetsAnimationCallback(root, insetsCallback)
    }

    private fun ActivityMainBinding.addMockItems() {
        val adapter = CategoriesAdapter()
        adapter.submitList(listOf("Где поесть\n", "Продукты\n", "Аптеки\n", "Салоны\nКрасоты", "Банкоматы\n", "АЗС\n", "Больницы\n", "Гостиницы\n", "Бары и пабы", "Торговые\nцентры", "Кинотеатры\n", "Доставка\nеды", "Автомойки\n", "Автосервисы\n", "Фитнес\n", "Что посетить\n"))
        categories.layoutManager = GridLayoutManager(this@MainActivity, 4)
        categories.adapter = adapter
    }
}