package com.example.instagramsaver

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.view.View
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.Switch
import com.example.instagramsaver.fragment.PhotosFragment
import com.example.instagramsaver.service.ClipboardMonitorService
import com.example.instagramsaver.utility.L
import com.greysonparrelli.permiso.Permiso
import com.greysonparrelli.permiso.Permiso.IOnPermissionResult
import hotchemi.android.rate.AppRate
import hotchemi.android.rate.StoreType
import hotchemi.android.rate.OnClickButtonListener





class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var mApp: App? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Permiso.getInstance().setActivity(this)
        setContentView(R.layout.activity_main)
        mApp =  application as App
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        toolbar.title = ""
        setSupportActionBar(toolbar)

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.setDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)

        addFragment(R.id.content_main, PhotosFragment.newInstance(), false)
        configRate()
    }

    override fun onBackPressed() {
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onStart() {
        super.onStart()
        Permiso.getInstance().setActivity(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        val switchMenu = menu.findItem(R.id.myswitch)
        val switch = switchMenu.actionView.findViewById(R.id.switchForActionBar) as Switch
        val per = Utility.checkWriteExternalPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) && Utility.checkWriteExternalPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        val isRunning = mApp?.getSettings()?.isClipboardMonitorServiceRunning()!! && per
        switch.isChecked = isRunning
        startServiceIfNeeded(isRunning)
        switch.setOnCheckedChangeListener { compoundButton, b ->
            Permiso.getInstance().requestPermissions(object : IOnPermissionResult {
                override fun onRationaleRequested(callback: Permiso.IOnRationaleProvided?, vararg permissions: String?) {
                    Permiso.getInstance().showRationaleInDialog(getString(R.string.permiso_title), getString(R.string.permiso_message), null, callback!!)
                }

                override fun onPermissionResult(resultSet: Permiso.ResultSet?) {
                    resultSet?.let {
                        if(it.areAllPermissionsGranted()) {
                            startServiceIfNeeded(b)
                            mApp?.getSettings()?.setClipboardMonitorServiceRunning(b)
                        }
                    }
                }
            }, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE);

        }

        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Permiso.getInstance().onRequestPermissionResult(requestCode, permissions, grantResults)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_sdcard) {
            FileUtils.openSavedFolderViaExplorer(this)
        } else if (id == R.id.nav_share) {
            Utility.shareIntent(this)
        } else if (id == R.id.nav_rate) {
            Utility.rateApp(this)
        } else if (id == R.id.nav_instagram) {
            Utility.openInstagram(this)
        }

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    //region Utility API
    private fun startServiceIfNeeded(isNeedRun : Boolean) {
        if (isNeedRun) {startCMS()} else {stopCMS()}
    }
    private fun startCMS() = startService(Intent(this, ClipboardMonitorService::class.java))

    private fun stopCMS () = stopService(Intent(this, ClipboardMonitorService::class.java))

    fun addFragment(containerViewId: Int, fragment: Fragment, addToBackStack: Boolean) {
        val fragmentTransaction = this.supportFragmentManager.beginTransaction()
        fragmentTransaction.add(containerViewId, fragment)
        if (addToBackStack) {
            fragmentTransaction.addToBackStack(fragment.javaClass.name)
        }
        fragmentTransaction.commit()
    }

    private fun configRate() {
        AppRate.with(this)
                .setStoreType(StoreType.GOOGLEPLAY) //default is Google, other option is Amazon
                .setInstallDays(3) // default 10, 0 means install day.
                .setLaunchTimes(10) // default 10 times.
                .setRemindInterval(2) // default 1 day.
                .setShowLaterButton(true) // default true.
                .setDebug(false) // default false.
                .setCancelable(false) // default false.
                .setOnClickButtonListener { which -> // callback listener.
                    L.d("ii  "+Integer.toString(which))
                }
                .setTitle(R.string.new_rate_dialog_title)
                .setTextLater(R.string.new_rate_dialog_later)
                .setTextNever(R.string.new_rate_dialog_never)
                .setTextRateNow(R.string.new_rate_dialog_ok)
                .monitor()

        AppRate.showRateDialogIfMeetsConditions(this)
    }

    //endregion
}
