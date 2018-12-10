package io.github.yusukeiwaki.browser_launcher

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast

class LauncherActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestCreatingShortcut()
        launchBrowser("https://crowdworks.jp/")
        finish()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }

    private fun requestCreatingShortcut() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Toast.makeText(this, "Android 8.0以降はまだ対応してない", Toast.LENGTH_SHORT).show()
        } else {
            val appIntent = Intent(this, this::class.java).apply {
                action = Intent.ACTION_MAIN
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            val icon = Intent.ShortcutIconResource.fromContext(this, R.mipmap.ic_launcher);
            val shortcutIntent = Intent("com.android.launcher.action.INSTALL_SHORTCUT").apply {
                putExtra(Intent.EXTRA_SHORTCUT_INTENT, appIntent)
                putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.app_name))
                putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon)
                putExtra("duplicate", false)
            }
            sendBroadcast(shortcutIntent)
        }
    }

    private fun launchBrowser(url: String) {
        val intent = buildBrowserIntent(url)
        startActivity(intent)
    }

    private fun buildBrowserIntent(url: String): Intent {
        val targetIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        val resolveInfo: ResolveInfo? = packageManager.resolveActivity(targetIntent, PackageManager.MATCH_DEFAULT_ONLY)

        // デフォルト設定されていない、またはそもそも対応アプリがない（ほぼありえない）ときはとりあえず暗黙的インテントで起動
        if (resolveInfo == null || resolveInfo.activityInfo.applicationInfo.packageName == "android") {
            return targetIntent
        }

        // デフォルト設定されているブラウザアプリのパッケージを指定して起動
        return targetIntent.apply {
            setPackage(resolveInfo.activityInfo.packageName)
        }
    }
}
