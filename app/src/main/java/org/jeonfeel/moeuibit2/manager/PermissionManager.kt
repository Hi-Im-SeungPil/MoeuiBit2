package org.jeonfeel.moeuibit2.manager

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.karumi.dexter.Dexter
import com.karumi.dexter.DexterBuilder
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener


class PermissionManager(private val activity: Activity) {

    private val permissionArray = mutableListOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.INTERNET,
        Manifest.permission.ACCESS_NETWORK_STATE
    )

    fun requestPermission(): DexterBuilder? {
        return Dexter.withContext(activity).withPermissions(
            permissionArray
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                p0.let {
                    if (it!!.isAnyPermissionPermanentlyDenied) {
                        showSettingDialog()
                    }
                }
            }

            override fun onPermissionRationaleShouldBeShown(
                p0: MutableList<PermissionRequest>?,
                p1: PermissionToken?
            ) {
                p1.let { it!!.continuePermissionRequest() }
            }
        })
    }

    fun showSettingDialog() {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("권한 요청")
        builder.setMessage("앱 사용을 위해 권한이 꼭 필요합니다..!")
        builder.setPositiveButton("설정으로 이동") { dialog, _ ->
            dialog.cancel()
            openSettings()
        }
        builder.setNegativeButton("취소") { dialog, _ ->
            dialog.cancel()
        }
        builder.show()
    }

    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", activity.packageName, null)
        intent.data = uri
        activity.startActivity(intent)
    }
}