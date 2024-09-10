package com.example.dialogapplication.animatior

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.dialogapplication.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random


@Composable
fun CreateAnimation(withEndAction:(() -> Unit)? = null) {
    // 使用 Animatable 控制初始扩展动画
    val expansionProgress = remember { Animatable(0f) }

    // 定义 20 个金币，存储它们的随机半径和角度
    val circleCount = 20
    val randomDistances = remember { List(circleCount) { Random.nextFloat() * 100 + 200 } }
    val angles = remember { List(circleCount) { it * (360f / circleCount) * (Math.PI / 180f).toFloat() } }
    // 获取屏幕宽高
    val screenWidthPx = LocalContext.current.resources.displayMetrics.widthPixels
    val screenHeightPx = LocalContext.current.resources.displayMetrics.heightPixels

    // 每个金币偏移的最终位置，以屏幕中心为圆点，计算左上角(20dp,50dp)的相对坐标
    val targetPosition = Offset(DpToPx(20f)-screenWidthPx/2, DpToPx(50f)-screenHeightPx/2)

    // 每个金币的移动动画的时长
    val durations = remember { List(circleCount) { Random.nextInt(1000, 1400) } }
    // 每个金币的动画
    val moveAnimations = remember { List(circleCount) { Animatable(0f) } }

    LaunchedEffect(Unit) {
        expansionProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 300, easing = LinearEasing)
        )

        // 开始每个金币的偏移
        moveAnimations.forEachIndexed { index, animatable ->
            launch {
                // 增加延迟的随机
                delay(Random.nextLong(50,100))
                animatable.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = durations[index],
                        easing = LinearEasing
                    )
                )
                if (index == moveAnimations.size-1){
                    withEndAction?.invoke()
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

        for (i in 0 until circleCount) {
            // 动态计算每个圆的展开距离
            val distance = randomDistances[i] * expansionProgress.value
            var offsetX = cos(angles[i]) * distance
            var offsetY = sin(angles[i]) * distance

            // 计算每个圆向目标点偏移的插值位置
            val moveProgress = moveAnimations[i].value
             offsetX = lerp(offsetX, targetPosition.x, moveProgress)
             offsetY = lerp(offsetY, targetPosition.y, moveProgress)

            Image(
                painter = painterResource(id = R.drawable.common_coin_normal),
                contentDescription = "coin",
                modifier = Modifier
                    .size(32.dp)
                    .offset(pxToDp(offsetX), pxToDp(offsetY))
                    .scale(1-moveProgress)
            )
        }

    }

}

// 线性插值函数
private fun lerp(start: Float, end: Float, progress: Float): Float {
    return start + (end - start) * progress
}


@Composable
fun pxToDp(px: Float): Dp {
    // 获取 LocalDensity 实例
    val density = LocalDensity.current
    // 使用 density 转换 px 为 dp
    return with(density) { px.toDp() }
}

@Composable
fun DpToPx(dpValue:Float):Float {
    val density = LocalDensity.current

    // 将 dp 转换为像素
    val pixelValue = with(density) { dpValue.dp.toPx() }

    return pixelValue
}

@Preview
@Composable
fun AnimationPreView(){
    CreateAnimation()
}