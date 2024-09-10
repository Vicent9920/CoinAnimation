package com.example.dialogapplication.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.content.ContextCompat
import kotlin.random.Random

object AnimationUtils {
    private const val collectionCount = 20
    private const val angleIncrement = 360f / collectionCount
    private var centerX: Float = 0F
    private var centerY: Float = 0F

    fun startAnimation(
        contentView: View,
        destinationsX: MutableList<Float>,//target View 坐标
        destinationsY: MutableList<Float>,//target View 坐标
        res: Int,//金币资源
        animationSuccessCallback: ((Long) -> Unit)? = null
    ){
        if (contentView !is ViewGroup){
            return
        }
        val collectViewContainer = FrameLayout(contentView.context).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }
        contentView.addView(collectViewContainer)
        collectViewContainer.removeAllViews()

        centerX = contentView.width / 2f
        centerY = contentView.height / 2f

        val coins = mutableListOf<View>()
        for (i in 0 until collectionCount) {
            val collectView = createItemView(contentView.context)
            collectView.setImageDrawable(ContextCompat.getDrawable(contentView.context, res))
            val angle = i * angleIncrement
            val radius = Random.nextFloat() * 100 + 200
            val x = centerX + radius * Math.cos(Math.toRadians(angle.toDouble())).toFloat()
            val y = centerY + radius * Math.sin(Math.toRadians(angle.toDouble())).toFloat()
            val layoutParams = FrameLayout.LayoutParams(32F.dip2px(), 32F.dip2px())
            collectView.x = centerX
            collectView.y = centerY

            coins.add(collectView)
            collectViewContainer.addView(collectView, layoutParams)
            collectView.animate().x(x).y(y).setDuration(300L)
                .setListener(object : AnimatorListenerAdapter() {

                    override fun onAnimationEnd(animation: Animator) {

                        val durationRange = 1000L..1400L // 不同金币的收起时间范围

                        val animatorSet = AnimatorSet()
                        coins.forEachIndexed { index, coinView ->
                            val delay = 100L
                            val duration = Random.nextLong(
                                durationRange.first, durationRange.last
                            ) // 随机选择金币的收起时间

                            val scaleXAnimator =
                                ObjectAnimator.ofFloat(coinView, View.SCALE_X, 0f)
                            val scaleYAnimator =
                                ObjectAnimator.ofFloat(coinView, View.SCALE_Y, 0f)
                            //遍历destinationsX和destinationsY，分别设置金币的目标位置
                            val translationAnimator = mutableListOf<Animator>()
                            //随机0，1中的一个位置，设置动画
                            val pos = Random.nextInt(0, destinationsX.size)
                            val animx = ObjectAnimator.ofFloat(
                                coinView,
                                View.X,
                                destinationsX[pos]
                            )
                            animx.startDelay = delay
                            animx.duration = duration
                            translationAnimator.add(animx)
                            val animy = ObjectAnimator.ofFloat(
                                coinView,
                                View.Y,
                                destinationsY[pos]
                            )
                            animy.startDelay = delay
                            animy.duration = duration
                            translationAnimator.add(animy)

                            scaleXAnimator.duration = duration
                            scaleYAnimator.duration = duration

                            scaleXAnimator.startDelay = delay
                            scaleYAnimator.startDelay = delay


                            animatorSet.playTogether(
                                scaleXAnimator,
                                scaleYAnimator,
                            )
                            animatorSet.playTogether(translationAnimator)
                        }
                        animatorSet.interpolator = AccelerateDecelerateInterpolator()
                        animatorSet.start()
                        animationSuccessCallback?.invoke(durationRange.last)
                    }
                }).interpolator = AccelerateDecelerateInterpolator()
            contentView.postDelayed({
                contentView.removeView(collectViewContainer)
            }, 4000)
        }

    }

    fun startAnimation(
        activity: Activity,
        destinationsX: MutableList<Float>,//target View 坐标
        destinationsY: MutableList<Float>,//target View 坐标
        res: Int,//金币资源
        containerView: ViewGroup? = null,
        animationSuccessCallback: ((Long) -> Unit)? = null
    ) {
        val root = containerView?:activity.findViewById<FrameLayout>(android.R.id.content)
        startAnimation(root,destinationsX,destinationsY, res, animationSuccessCallback)
    }

    private fun createItemView(context: Context): ImageView {
        return ImageView(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                32F.dip2px(),
                32F.dip2px()
            )
            scaleType = ImageView.ScaleType.FIT_XY
            rotation = 0F
        }
    }

    private fun Float.dip2px(): Int {
        val density = AppManager.currentActivity().resources.displayMetrics.density
        return (this * density + 0.5f).toInt()
    }
}