package com.xueyu.kotlinextlibrary

import android.os.Bundle
import androidx.fragment.app.Fragment

/**
 * fragment当中开启fragment扩展函数
 *
 * @author wm
 * @date 20-2-13
 */
/**
 * 单独创建fragment，相当于newInstance(),泛型则为显示fragment类型
 *
 * @param init fragment参数
 *
 * @return 创建fragment
 */
inline fun <reified F : Fragment> Fragment.newFragment(noinline init: Bundle.() -> Unit = {}): F {
    val bundle = Bundle()
    bundle.init()
    val f = childFragmentManager
        .fragmentFactory
        .instantiate(ClassLoader.getSystemClassLoader(), F::class.java.name) as F
    f.arguments = bundle
    return f
}

/**
 * 隐藏指定fragment，添加当前fragment(如果不存在则创在，存在则显示)，泛型则为显示fragment类型
 *
 * @param frameId containerId
 * @param tag 需要显示fragment的tag
 * @param tagsToHide 需要隐藏的fragment的tag，不定参数，可添加多个
 * @param animIn fragment显示时候的动画
 * @param addToBackPress 是否添加进回退栈管理
 * @param init fragment参数
 * @param onShow fragment显示
 *
 * @return 显示fragment
 */
inline fun <reified T : Fragment> Fragment.showFragment(
    frameId: Int,
    tag: String,
    vararg tagsToHide: String,
    animIn: Int = 0,
    addToBackPress: Boolean = false,
    noinline onShow: (() -> Unit)? = null,
    noinline init: Bundle.() -> Unit = {}
): T {
    val ft = childFragmentManager.beginTransaction()
    ft.setCustomAnimations(animIn, 0)
    var fragment: Fragment? = null
    childFragmentManager.inTransaction {
        if (!tagsToHide.isNullOrEmpty()) {
            tagsToHide.forEach {
                val f = childFragmentManager.findFragmentByTag(it)
                if (f != null && !f.isHidden) {
                    ft.hide(f)
                }
            }
        }
        fragment = childFragmentManager.findFragmentByTag(tag)
        if (fragment != null && !fragment!!.isHidden) {
            ft.hide(fragment!!)
        }
        if (fragment == null) {
            val t = newFragment<T>(init)
            fragment = t
            ft.add(frameId, t, tag)
        } else {
            ft.show(fragment!!)
        }
        if (addToBackPress) {
            ft.addToBackStack(tag)
        }
        ft
    }
    onShow?.invoke()
    return fragment as T
}

@Suppress("UNCHECKED_CAST")
fun <T : Fragment> Fragment.showFragment(
    frameId: Int,
    tag: String,
    selfFragment: Fragment,
    vararg tagsToHide: String,
    animIn: Int = 0,
    addToBackPress: Boolean = false,
    onShow: (() -> Unit)? = null
): T {
    val ft = childFragmentManager.beginTransaction()
    ft.setCustomAnimations(animIn, 0)
    var fragment: Fragment? = null
    childFragmentManager.inTransaction {
        if (!tagsToHide.isNullOrEmpty()) {
            tagsToHide.forEach {
                val f = childFragmentManager.findFragmentByTag(it)
                if (f != null && !f.isHidden) {
                    ft.hide(f)
                }
            }
        }
        fragment = childFragmentManager.findFragmentByTag(tag)
        if (fragment != null && !fragment!!.isHidden) {
            ft.hide(fragment!!)
        }
        if (fragment == null) {
            fragment = selfFragment
            ft.add(frameId, selfFragment, tag)
        } else {
            ft.show(fragment!!)
        }
        if (addToBackPress) {
            ft.addToBackStack(tag)
        }
        ft
    }
    onShow?.invoke()
    return fragment as T
}

/**
 * 隐藏指定tag的fragment
 *
 * @param tag tag
 * @param animOut 退出动画（需要注意的是：如果希望退出动画生效，则container不可以具有wrap_content的属性）
 * @param onHide 当隐藏时（如果为创建，也会回调此方法,true:隐藏 false:未创建）
 */
fun Fragment.hideFragment(
    tag: String,
    animOut: Int = 0,
    onHide: ((Boolean) -> Unit)? = null
) {
    val ft = childFragmentManager.beginTransaction()
    ft.setCustomAnimations(0, animOut)
    val fragment: Fragment? = childFragmentManager.findFragmentByTag(tag)
    if (fragment != null && !fragment.isHidden) {
        val fragmentManager = childFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(0, animOut)
        fragmentTransaction.hide(fragment)
        fragmentTransaction.commitAllowingStateLoss()
    }
    onHide?.invoke(fragment != null)
}

/**
 * 指定tag的fragment是否隐藏
 *
 * @return 如果这个fragment并没有创建或者隐藏 返回false 只有显示的时候返回true
 */
fun Fragment.isFragmentShow(tag: String): Boolean {
    val fragment: Fragment? = childFragmentManager.findFragmentByTag(tag)
    if (fragment != null && !fragment.isHidden) {
        return true
    }
    return false
}

/**
 * replaceFragment
 *
 * @param fragment 替换进入的fragment
 * @param frameId containerId
 */
fun Fragment.replaceFragment(frameId: Int, fragment: Fragment) {
    childFragmentManager.inTransaction {
        replace(frameId, fragment)
    }
}

/**
 * 创建viewPager pagerAdapter
 *
 * @return fragmentPagerAdapter
 */
fun Fragment.createVpPagerAdapter(
    fragments: List<Fragment>,
    tabStrings: List<String>? = null
): VpPagerAdapter {
    return VpPagerAdapter(childFragmentManager, fragments, tabStrings)
}

/**
 * 创建viewPager pagerStateAdapter
 *
 * @return fragmentPagerStateAdapter
 */
fun Fragment.createVpPagerStateAdapter(
    fragments: List<Fragment>,
    tabStrings: List<String>? = null
): VpPagerStateAdapter {
    return VpPagerStateAdapter(childFragmentManager, fragments, tabStrings)
}