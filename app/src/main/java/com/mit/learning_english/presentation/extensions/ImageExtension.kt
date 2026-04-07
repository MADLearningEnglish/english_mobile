package com.mit.learning_english.presentation.extensions

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.facebook.shimmer.ShimmerFrameLayout
import com.mit.learning_english.R

fun ImageView.loadAvatar(url: String?, shimmer: ShimmerFrameLayout) {
    if (url.isNullOrEmpty()) {
        shimmer.visibility = View.GONE
        setImageResource(R.drawable.ic_avatar_default)
        return
    }
    shimmer.visibility = View.VISIBLE
    shimmer.startShimmer()
    Glide.with(this).load(url).placeholder(R.drawable.ic_avatar_default)
        .error(R.drawable.ic_avatar_default).diskCacheStrategy(DiskCacheStrategy.ALL).circleCrop()
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?, model: Any?, target: Target<Drawable?>, isFirstResource: Boolean
            ): Boolean {
                shimmer.stopShimmer()
                shimmer.visibility = View.GONE
                return false
            }

            override fun onResourceReady(
                resource: Drawable,
                model: Any,
                target: Target<Drawable?>?,
                dataSource: DataSource,
                isFirstResource: Boolean
            ): Boolean {
                shimmer.stopShimmer()
                shimmer.visibility = View.GONE
                return false
            }
        }).into(this)
}

fun ImageView.loadImage(url: String?) {
    if (url.isNullOrEmpty()) {
        setImageResource(R.drawable.ic_image_place_holder)
        return
    }
    val progressDrawable = CircularProgressDrawable(context).apply {
        strokeWidth = 4f
        centerRadius = 24f
        setColorSchemeColors(ContextCompat.getColor(context, R.color.primary))
        start()
    }
//    val lottieDrawable = LottieDrawable

    Glide.with(this).load(url).placeholder(progressDrawable).error(R.drawable.ic_image_place_holder)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .transition(DrawableTransitionOptions.withCrossFade()).into(this)
}
