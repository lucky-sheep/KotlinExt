package com.xueyu.kotlinextlibrary

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

/**
 * VpPagerStateAdapter
 *
 * @author wm
 * @date 20-2-12
 */
class VpPagerStateAdapter(
    fm: FragmentManager,
    fragments: List<Fragment>,
    private val tabStrings: List<String>? = null
) :
    FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private val mFragments: MutableList<Fragment>
    override fun getCount() = mFragments.size
    override fun getItem(position: Int): Fragment = mFragments[position]
    override fun getPageTitle(position: Int): CharSequence? = tabStrings?.get(position)

    init {
        mFragments = ArrayList()
        mFragments.clear()
        mFragments.addAll(fragments)
    }
}
