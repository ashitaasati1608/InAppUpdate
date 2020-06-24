package com.ashita.inappupdate.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.ashita.inappupdate.R
import com.ashita.inappupdate.viewmodel.ListViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.tasks.Task
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    companion object {
        private const val IN_APP_UPDATE_REQUEST_CODE = 1
        private const val APP_UPDATE_TYPE_SUPPORTED = AppUpdateType.IMMEDIATE
    }

    private val listViewModel by viewModel<ListViewModel>()
    private val newsListAdapter = NewsListAdapter()
    private lateinit var installStateUpdatedListener: InstallStateUpdatedListener

    private var appUpdateManager: AppUpdateManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Creates instance of the manager.
        appUpdateManager = AppUpdateManagerFactory.create(baseContext)

        newsList.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = newsListAdapter
        }

        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        checkAppUpdate()
    }

    private fun checkAppUpdate() {
        // Returns an intent object that you use to check for an update.
        val appUpdateInfo = appUpdateManager?.appUpdateInfo

        // Checks that the platform will allow the specified type of update.
        appUpdateInfo?.addOnSuccessListener {
            handleUpdate(appUpdateManager, appUpdateInfo)
        }
    }

    private fun handleUpdate(manager: AppUpdateManager?, info: Task<AppUpdateInfo>) {
        when (APP_UPDATE_TYPE_SUPPORTED) {
            AppUpdateType.IMMEDIATE -> handleImmediateUpdate(manager, info)
            AppUpdateType.FLEXIBLE -> handleFlexibleUpdate(manager, info)
            else -> throw Exception("Unexpected error")
        }
    }

    private fun handleFlexibleUpdate(
        appUpdateManager: AppUpdateManager?,
        info: Task<AppUpdateInfo>
    ) {
        if ((info.result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE ||
                    info.result.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) &&
            info.result.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
        ) {
            btn_update.visibility = View.VISIBLE
            setUpdateAction(appUpdateManager, info)
        }
    }

    private fun handleImmediateUpdate(
        appUpdateManager: AppUpdateManager?,
        info: Task<AppUpdateInfo>
    ) {
        if ((info.result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE ||
                    info.result.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) &&
            info.result.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
        ) {
            appUpdateManager?.startUpdateFlowForResult(
                info.result,
                AppUpdateType.IMMEDIATE, this, IN_APP_UPDATE_REQUEST_CODE
            )
        }
    }

    private fun setUpdateAction(manager: AppUpdateManager?, info: Task<AppUpdateInfo>) {
        // Before starting an update, register a listener for updates.

        btn_update.setOnClickListener {
            installStateUpdatedListener = InstallStateUpdatedListener {
                btn_update.visibility = View.GONE
                tv_status.visibility = View.VISIBLE
                when (it.installStatus()) {
                    InstallStatus.FAILED, InstallStatus.UNKNOWN -> {
                        tv_status.text = getString(R.string.info_failed)
                        btn_update.visibility = View.VISIBLE
                    }
                    InstallStatus.PENDING -> {
                        tv_status.text = getString(R.string.info_pending)
                    }
                    InstallStatus.CANCELED -> {
                        tv_status.text = getString(R.string.info_canceled)
                    }
                    InstallStatus.DOWNLOADING -> {
                        tv_status.text = getString(R.string.info_downloading)
                    }
                    InstallStatus.DOWNLOADED -> {
                        tv_status.text = getString(R.string.info_downloaded)
                        popupSnackbarForCompleteUpdate(manager)
                    }
                    InstallStatus.INSTALLING -> {
                        tv_status.text = getString(R.string.info_installing)
                    }
                    InstallStatus.INSTALLED -> {
                        tv_status.text = getString(R.string.info_installed)
                        manager?.unregisterListener(installStateUpdatedListener)
                    }
                    else -> {
                        tv_status.text = getString(R.string.info_restart)
                    }
                }
            }
            manager?.registerListener(installStateUpdatedListener)
            manager?.startUpdateFlowForResult(
                info.result, AppUpdateType.FLEXIBLE,
                this, IN_APP_UPDATE_REQUEST_CODE
            )
        }
    }

    /** This is needed to handle the result of the manager.startConfirmationDialogForResult request */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == IN_APP_UPDATE_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                toastAndLog("Update flow failed! Result code: $resultCode")
                // If the update is cancelled or fails,
                // you can request to start the update again.
                checkAppUpdate()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    /* Displays the snackbar notification and call to action. */
    private fun popupSnackbarForCompleteUpdate(appUpdateManager: AppUpdateManager?) {
        Snackbar.make(
            findViewById(R.id.mainLayoutParent),
            "Ready to INSTALL.", Snackbar.LENGTH_INDEFINITE
        ).apply {
            setAction("RESTART") { appUpdateManager?.completeUpdate() }
            setActionTextColor(ContextCompat.getColor(applicationContext, R.color.colorAccent))
            show()
        }
    }

    private fun toastAndLog(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
        Log.d("TAG", text)
    }

    private fun observeViewModel() {
        listViewModel.newsArticles.observe(this, Observer { article ->
            loading_view.visibility = View.GONE
            newsList.visibility = View.VISIBLE
            article.data?.let {
                newsListAdapter.onAddNewsItem(article.data)
            }
            newsList.smoothScrollToPosition(0)
        })
    }

}

