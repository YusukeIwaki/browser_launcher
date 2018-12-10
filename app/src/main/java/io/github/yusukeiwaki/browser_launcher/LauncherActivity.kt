package io.github.yusukeiwaki.browser_launcher

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.os.Bundle

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
        val appIntent = Intent(this, this::class.java).apply {
            action = Intent.ACTION_MAIN
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(ShortcutManager::class.java)
            val info = ShortcutInfo.Builder(this, "browser_launcher")
                .setShortLabel(getString(R.string.app_name))
                .setIcon(Icon.createWithResource(this, R.mipmap.ic_launcher))
                .setIntent(appIntent)
                .build()
            manager.requestPinShortcut(info, null)
        } else {
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
