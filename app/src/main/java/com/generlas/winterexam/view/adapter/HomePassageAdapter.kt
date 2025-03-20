package com.generlas.winterexam.view.adapter

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.generlas.winterexam.R
import com.generlas.winterexam.model.CarouselInfo
import com.generlas.winterexam.model.PassageInfo
import com.generlas.winterexam.view.CarouselDot
import com.generlas.winterexam.view.activity.LoginActivity
import com.generlas.winterexam.view.activity.MainActivity
import com.generlas.winterexam.view.activity.WebViewActivity

/**
 * description ： TODO:home页的recyclerview
 * date : 2025/3/2 13:20
 */
class HomePassageAdapter(private val context: Context, private val carouselInfo: List<CarouselInfo>, private val itemClickListener: OnItemClickListener) :
    ListAdapter<PassageInfo, RecyclerView.ViewHolder>(ItemDiffCallback()) {

    private val TYPE_BANNER = 0
    private val TYPE_PASSAGE = 1

    lateinit var handler: Handler
    lateinit var runnable: Runnable

    interface OnItemClickListener {
        fun onItemClick(id: Int, collect: Boolean)
    }

    inner class PassageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val passageAuthor: TextView = view.findViewById(R.id.tv_card_author)
        val passageDate: TextView = view.findViewById(R.id.tv_card_date)
        val passageTitle: TextView = view.findViewById(R.id.tv_card_title)
        val passageChapterName: TextView = view.findViewById(R.id.tv_card_chapterName)
        val passageCollect : ImageView = view.findViewById(R.id.iv_card_collect)

    }

    inner class BannerViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var dotView : CarouselDot = view.findViewById(R.id.home_carouselDot)
        val carouselViewPager2: ViewPager2 = view.findViewById(R.id.home_viewpager2)
        val mTvCarouselTitle: TextView = view.findViewById(R.id.tv_carousel_text)

        init {
            autoCarousel()
            handCarousel()
        }

        //手动滑动时暂停自动滑动
        private fun handCarousel() {
            /*
            这里轮播图无限轮播的实现逻辑应该写在onPageScrollStateChanged里面因为我们要的是在滑动到那张后且不动了才会执行切换逻辑
            否则切换动画会很怪
             */
            carouselViewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageScrollStateChanged(state: Int) {
                    super.onPageScrollStateChanged(state)
                    if (state == ViewPager2.SCROLL_STATE_DRAGGING) {
                        handler.removeCallbacks(runnable)
                    } else {
                        if (state == ViewPager2.SCROLL_STATE_IDLE) {
                            if(carouselViewPager2.currentItem == carouselInfo.size - 1) {
                                carouselViewPager2.setCurrentItem(1, false)
                            }
                            if(carouselViewPager2.currentItem == 0) {
                                carouselViewPager2.setCurrentItem(carouselInfo.size - 2, false)
                            }
                            handler.removeCallbacks(runnable) //需把之前的线程关闭掉，否则线程越来越多，自动切换速度越快
                            handler.postDelayed(runnable, 3000)
                        }
                    }
                }

                //当滑动到最后一张(实际上展示为第一张的内容)时，立刻跳到第一张，滑动到第0张(展示为最后一张)时,同理
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    mTvCarouselTitle.text = carouselInfo[position].title
                    if (position == carouselInfo.size - 1) {
                        dotView.changeDots(0)
                        //carouselViewPager2.setCurrentItem(1, false)

                    } else if (position == 0) {
                        dotView.changeDots(carouselInfo.size - 1)
                        //carouselViewPager2.setCurrentItem(carouselInfo.size - 2, false)
                    } else {
                        dotView.changeDots(position - 1)
                    }
                }
            })
        }

        //自动轮播
        private fun autoCarousel() {
            handler = Handler(Looper.getMainLooper())
            runnable = object : Runnable {
                override fun run() {
                    val nextPassage = carouselViewPager2.currentItem + 1
                    if(nextPassage == carouselInfo.size) {
                        carouselViewPager2.setCurrentItem(1,false)
                    }
                    else {
                        carouselViewPager2.currentItem = nextPassage
                    }
                    handler.postDelayed(this, 3000)
                }
            }
            handler.postDelayed(runnable, 3000)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType == TYPE_PASSAGE) {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.home_card_item, parent, false)
            return PassageViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.banner, parent, false)
            return BannerViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val passage = getItem(position)
        when(holder) {
            is PassageViewHolder -> {
                if(passage.author != "") {
                    holder.passageAuthor.text = passage.author
                    holder.passageDate.text = passage.niceDate
                } else {
                    holder.passageAuthor.text = passage.shareUser
                    holder.passageDate.text = passage.niceShareDate
                }
                val trueText = Html.fromHtml(passage.title, Html.FROM_HTML_MODE_COMPACT)

                if(passage.collect == true) {
                    holder.passageCollect.setImageResource(R.drawable.ic_collect_selected)
                } else {
                    holder.passageCollect.setImageResource(R.drawable.ic_collect)
                }

                holder.passageTitle.text = trueText
                holder.passageChapterName.text = passage.chapterName

                //整个View的点击事件
                holder.itemView.setOnClickListener {
                    val intent = Intent(context, WebViewActivity::class.java)
                    intent.putExtra("url", passage.link)
                    context.startActivity(intent)
                }

                holder.passageCollect.setOnClickListener {
                    if((context as MainActivity).checkLogin() == true) {
                        itemClickListener.onItemClick(passage.id, passage.collect)
                        if(passage.collect == false) {
                            currentList[position].collect = true
                            holder.passageCollect.setImageResource(R.drawable.ic_collect_selected)
                            submitList(currentList)
                        } else {
                            currentList[position].collect = false
                            holder.passageCollect.setImageResource(R.drawable.ic_collect)
                            submitList(currentList)
                        }
                    } else {
                        Toast.makeText(context, "请先登录", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            is BannerViewHolder -> {
                val carouselAdapter = CarouselViewPager2Adapter(context, carouselInfo)
                holder.carouselViewPager2.adapter = carouselAdapter
                holder.carouselViewPager2.currentItem = 1
                holder.dotView.initDots(carouselInfo.size - 2, 0)
                holder.mTvCarouselTitle.text = carouselInfo[1].title
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if(position == 0) {
            return TYPE_BANNER
        } else {
            return TYPE_PASSAGE
        }
    }

}