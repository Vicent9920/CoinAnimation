package com.example.dialogapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dialogapplication.animatior.CreateAnimation
import com.example.dialogapplication.ui.theme.DialogApplicationTheme
import com.example.dialogapplication.utils.AnimationUtils
import com.example.dialogapplication.utils.AppManager

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppManager.attach(this)
        enableEdgeToEdge()
        setContent {
            DialogApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    var show by remember { mutableStateOf(false) }
    Box(modifier.fillMaxSize()) {

        Button(modifier = Modifier.align(Alignment.Center),onClick = {
//            AnimationUtils.startAnimation(AppManager.currentActivity(), mutableListOf(60f),
//                mutableListOf(150f),R.drawable.common_coin_normal
//            )
            show = !show
        }) {
            Text("点击测试动画")
        }
        if (show){
            CreateAnimation()

        }
    }

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DialogApplicationTheme {
        Greeting("Android")
    }
}

