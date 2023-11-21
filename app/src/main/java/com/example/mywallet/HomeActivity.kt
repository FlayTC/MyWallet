package com.example.mywallet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.mywallet.adapter.ViewPagerAdapter
import com.example.mywallet.classes.Wallet
import com.example.mywallet.fragments.DashboardFragment
import com.example.mywallet.fragments.InfoFragment
import com.example.mywallet.fragments.WalletsFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class HomeActivity : AppCompatActivity(), WalletListener {
    private lateinit var walletsFragment: WalletsFragment
    private lateinit var adapter: ViewPagerAdapter

    companion object {
        var totalAmounts: MutableMap<String, Double> = mutableMapOf()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        val viewPager2 = findViewById<ViewPager2>(R.id.view_pager_2)
        adapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
        adapter.addFragment(DashboardFragment())
        adapter.addFragment(WalletsFragment())
        adapter.addFragment(InfoFragment())
        viewPager2.adapter = adapter
        walletsFragment = adapter.getFragment(1) as WalletsFragment
        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Dashboard"
                    tab.icon = ContextCompat.getDrawable(this@HomeActivity, R.drawable.ic_dashboard)
                }

                1 -> {
                    tab.text = "Wallets"
                    tab.icon = ContextCompat.getDrawable(this@HomeActivity, R.drawable.ic_wallet)
                }

                2 -> {
                    tab.text = "Info"
                    tab.icon = ContextCompat.getDrawable(this@HomeActivity, R.drawable.ic_info)
                }
            }
        }.attach()

        walletsFragment = adapter.getFragment(1) as WalletsFragment
    }

    override fun onWalletAdded(wallet: Wallet) {
        Log.d("HomeActivity", "onWalletAdded: Wallet added - $wallet")
        totalAmounts[wallet.currency] =
            totalAmounts.getOrDefault(wallet.currency, 0.0)
        updateDashboardFragment()
    }

    private fun updateDashboardFragment() {
        Log.d("HomeActivity", "updateDashboardFragment: Updating dashboard fragment")
        val dashboardFragment =
            supportFragmentManager.findFragmentByTag("dashboardFragment") as? DashboardFragment
        dashboardFragment?.updateTotalAmounts()
        Log.d("HomeActivity", "updateDashboardFragment: totalAmounts = $totalAmounts")
    }
}