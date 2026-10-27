package site.n4deem.dreamlock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import site.n4deem.dreamlock.ui.DreamlockApp
import site.n4deem.dreamlock.ui.theme.DreamlockTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DreamlockTheme {
                DreamlockApp()
            }
        }
    }
}
