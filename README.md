
# 前言
前几天遇到一个动画需求，由于当时的时间比较急，因此当时这个效果就直接使用之前的原生动画工具类来实现了，现在有时间了，我们一起来使用Compose实现这个动画,试一下！

![ezgif-7-d238cf9cad.gif](https://p0-xtjj-private.juejin.cn/tos-cn-i-73owjymdk6/f4ab396eb405492a874a6d5a7e5e7f3c~tplv-73owjymdk6-jj-mark-v1:0:0:0:0:5o6Y6YeR5oqA5pyv56S-5Yy6IEAg6IuP54G_54Ok6bG8:q75.awebp?policy=eyJ2bSI6MywidWlkIjoiMzY2NzYyNjUxOTcwNDU1OCJ9&rk3s=f64ab15b&x-orig-authkey=f32326d3454f2ac7e96d3d06cdbb035152127018&x-orig-expires=1726450692&x-orig-sign=QEZrSv6XbYAobEKQmvy6D2Sc9Xc%3D)

观察这个动画，其实是由两个动画完成的，中间还有一个间隙，我们接下来就一步一步来实现它。

# 绘制第一个动画

第一个动画是金币由屏幕中心展开，并散成一个不规则的圆。

## 我们先画它一个金币试一下！



```kotlin
@Composable
fun CreateAnimation(){
    Image(painterResource(id = R.drawable.common_coin_normal), contentDescription = "coin", modifier = Modifier.size(32.dp))
}
```

当点击时，将金币展示出来
```kotlin
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    var show by remember { mutableStateOf(false) }
    Box(modifier.fillMaxSize()) {

        Button(modifier = Modifier.align(Alignment.Center),onClick = {
            show = !show
        }) {
            Text("点击测试动画")
        }

        if (show){
            Box(modifier = Modifier.align(Alignment.Center)) {
                CreateAnimation()
            }

        }
    }
}
```


![image.png](https://p0-xtjj-private.juejin.cn/tos-cn-i-73owjymdk6/ed31c00bb3534f568cc6358c61cea750~tplv-73owjymdk6-jj-mark-v1:0:0:0:0:5o6Y6YeR5oqA5pyv56S-5Yy6IEAg6IuP54G_54Ok6bG8:q75.awebp?policy=eyJ2bSI6MywidWlkIjoiMzY2NzYyNjUxOTcwNDU1OCJ9&rk3s=f64ab15b&x-orig-authkey=f32326d3454f2ac7e96d3d06cdbb035152127018&x-orig-expires=1726450692&x-orig-sign=7A6XFKSfxUPwv%2BTiF3h89CdCd%2FI%3D)

## 再画它20个金币
```kotlin
@Composable
fun CreateAnimation(){
    for (i in 0 until 20){
        Image(painterResource(id = R.drawable.common_coin_normal), contentDescription = "coin", modifier = Modifier.size(32.dp))
    }
    
}
```
这个时候`20`个金币摊叠在一起了，我们看不到位置，接下来尝试让它动起来！
动画的时候，我们设定动画的时长先设置`1000`毫秒,然后每个金币展开的距离为随机值，代码如下：
```kotlin
@Composable
fun CreateAnimation() {
    // 使用 Animatable 控制初始扩展动画
    val expansionProgress = remember { Animatable(0f) }

    // 定义 20 个金币，存储它们的随机半径和角度
    val circleCount = 20
    val randomDistances = remember { List(circleCount) { Random.nextFloat() * 100 + 200 } }
    val angles = remember { List(circleCount) { it * (360f / circleCount) * (Math.PI / 180f).toFloat() } }

    LaunchedEffect(Unit) {
        expansionProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 300, easing = LinearEasing)
        )
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

        for (i in 0 until circleCount) {
            // 动态计算每个金币的展开距离
            val distance = randomDistances[i] * expansionProgress.value
            val offsetX = cos(angles[i]) * distance
            val offsetY = sin(angles[i]) * distance


            Image(
                painter = painterResource(id = R.drawable.common_coin_normal),
                contentDescription = "coin",
                modifier = Modifier
                    .size(32.dp)
                    .offset(pxToDp(offsetX), pxToDp(offsetY))
            )
        }

    }

}


@Composable
fun pxToDp(px: Float): Dp {
    // 获取 LocalDensity 实例
    val density = LocalDensity.current
    // 使用 density 转换 px 为 dp
    return with(density) { px.toDp() }
}
```
由于圆的外面已经有一个`Box`组件了，因此外面可以直接使用`CreateAnimation`
```kotlin
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    var show by remember { mutableStateOf(false) }
    Box(modifier.fillMaxSize()) {

        Button(modifier = Modifier.align(Alignment.Center),onClick = {
            show = !show
        }) {
            Text("点击测试动画")
        }
        if (show){
            CreateAnimation()

        }
    }

}
```
接下来看看效果：

![Screen_Recording_20240907-182521_DialogApplication-ezgif.com-video-to-gif-converter.gif](https://p0-xtjj-private.juejin.cn/tos-cn-i-73owjymdk6/ebb40fb2defd4c019d63ac0a9d613726~tplv-73owjymdk6-jj-mark-v1:0:0:0:0:5o6Y6YeR5oqA5pyv56S-5Yy6IEAg6IuP54G_54Ok6bG8:q75.awebp?policy=eyJ2bSI6MywidWlkIjoiMzY2NzYyNjUxOTcwNDU1OCJ9&rk3s=f64ab15b&x-orig-authkey=f32326d3454f2ac7e96d3d06cdbb035152127018&x-orig-expires=1726450692&x-orig-sign=tnZ9PrFl31ZZd34Ehz0HNycoFL4%3D)

速度有点快，调试到`300`毫秒试一下


![Screen_Recording_20240907-183651_DialogApplication-ezgif.com-video-to-gif-converter.gif](https://p0-xtjj-private.juejin.cn/tos-cn-i-73owjymdk6/287072734fad4aa7ae6617aa99a64355~tplv-73owjymdk6-jj-mark-v1:0:0:0:0:5o6Y6YeR5oqA5pyv56S-5Yy6IEAg6IuP54G_54Ok6bG8:q75.awebp?policy=eyJ2bSI6MywidWlkIjoiMzY2NzYyNjUxOTcwNDU1OCJ9&rk3s=f64ab15b&x-orig-authkey=f32326d3454f2ac7e96d3d06cdbb035152127018&x-orig-expires=1726450692&x-orig-sign=1B95QnKjD3fW832O0P1ehYL8Zfk%3D)

## 小结
至此，第一个动画就绘制完成了。

- 我们使用了`Animatable`来声明了类似属性动画的对象，初始值为`0`
- 接下来计算了`20`个圆需要偏移的直线距离和角度
- 接下来开启动画，`Animatable`目标值设为`1`,整个动画时长为`300`毫秒，且插值器为`LinearEasing`
- 然后遍历了`20`个圆,根据动画的变化值，通过正弦三角函数和余弦三角函数得到移动过程中的坐标，并将坐标由`px`转为`dp`，交给`offset`使用.

接下来绘制第二个动画.

# 绘制第二个动画
仔细观察原动画可以发现，在第一个动画结束后，第二个动画的启动时间并不一致，然后每个动画偏移到指定位置的过程中还有一个缩小的动画。那我们就先偏移到指定位置，然后缩小，最后设置动画的延迟间隙。

## 偏移到指定位置
其实现思路同第一个动画类似，先计算目标位置的相对坐标，然后声明`20`个金币的偏移动画`Animatable`,在第一个动画`expansionProgress`完成后，遍历`20`个金币动画并依次启动，然后在绘制过程中计算偏移位置，代码如下：
```kotlin
@Composable
fun CreateAnimation() {
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
                animatable.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = durations[index],
                        easing = LinearEasing
                    )
                )
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

        for (i in 0 until circleCount) {
            // 动态计算每个金币的展开距离
            val distance = randomDistances[i] * expansionProgress.value
            var offsetX = cos(angles[i]) * distance
            var offsetY = sin(angles[i]) * distance

            // 计算每个金币向目标点偏移的插值位置
            val moveProgress = moveAnimations[i].value
             offsetX = lerp(offsetX, targetPosition.x, moveProgress)
             offsetY = lerp(offsetY, targetPosition.y, moveProgress)

            Image(
                painter = painterResource(id = R.drawable.common_coin_normal),
                contentDescription = "coin",
                modifier = Modifier
                    .size(32.dp)
                    .offset(pxToDp(offsetX), pxToDp(offsetY))
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
```
我们看看这个效果：

![Screen_Recording_20240910-173645_DialogApplication-ezgif.com-video-to-gif-converter.gif](https://p0-xtjj-private.juejin.cn/tos-cn-i-73owjymdk6/1d15c56272234171b68aafb93b970674~tplv-73owjymdk6-jj-mark-v1:0:0:0:0:5o6Y6YeR5oqA5pyv56S-5Yy6IEAg6IuP54G_54Ok6bG8:q75.awebp?policy=eyJ2bSI6MywidWlkIjoiMzY2NzYyNjUxOTcwNDU1OCJ9&rk3s=e9ecf3d6&x-orig-authkey=f32326d3454f2ac7e96d3d06cdbb035152127018&x-orig-expires=1726047479&x-orig-sign=EcbXamtE7i8gdFlD8%2B4JKDMwxuQ%3D)
## 缩小视图
现在偏移过程是没有问题了，但是还有一个缩小的动画。这个缩小动画和偏移动画使用同一个`Animatable`，代码如下：

```kotlin
@Composable
fun CreateAnimation() {
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
                animatable.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = durations[index],
                        easing = LinearEasing
                    )
                )
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

        for (i in 0 until circleCount) {
            // 动态计算每个金币的展开距离
            val distance = randomDistances[i] * expansionProgress.value
            var offsetX = cos(angles[i]) * distance
            var offsetY = sin(angles[i]) * distance

            // 计算每个金币向目标点偏移的插值位置
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
```
效果已经七七八八了，但是我们再增加一些随机，让动画更自然一些。
```
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
        }
    }
}
```
最后来看看完整的效果：

![Screen_Recording_20240910-173645_DialogApplication-ezgif.com-video-to-gif-converter.gif](https://p0-xtjj-private.juejin.cn/tos-cn-i-73owjymdk6/70521718f9884b38b844ce3b07980248~tplv-73owjymdk6-jj-mark-v1:0:0:0:0:5o6Y6YeR5oqA5pyv56S-5Yy6IEAg6IuP54G_54Ok6bG8:q75.awebp?policy=eyJ2bSI6MywidWlkIjoiMzY2NzYyNjUxOTcwNDU1OCJ9&rk3s=e9ecf3d6&x-orig-authkey=f32326d3454f2ac7e96d3d06cdbb035152127018&x-orig-expires=1726048529&x-orig-sign=1L3E%2FyY89F6l%2BuSvm6mBIcQLYhI%3D)

# 动画完成回调

这样整个动画就算是完成了。如果需要在动画执行结束以后增加回调，那么我们还可以添加一个接口回调：
```
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
            // 动态计算每个金币的展开距离
            val distance = randomDistances[i] * expansionProgress.value
            var offsetX = cos(angles[i]) * distance
            var offsetY = sin(angles[i]) * distance

            // 计算每个金币向目标点偏移的插值位置
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
```
如此就算完成了。

## 小结

第二个动画在第一个动画执行完毕后，遍历`20`个金币的动画，执行一个随机`50~100`毫秒的延迟后，开启金币偏移/缩小动画,然后绘制的时候，计算每个金币的展开距离后，再次计算偏移动画需要偏移的距离，最后通过`Modifier#scale`执行每个金币的缩放。

以上就是整个金币收集动画的全部实现了，看上去挺复杂，其实代码上手还是很容易的。就酱！





