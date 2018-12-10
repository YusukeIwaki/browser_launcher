package io.github.yusukeiwaki.browser_launcher

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle

class LauncherActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = buildBrowserIntent("https://crowdworks.jp/")
        startActivity(intent)
        finish()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
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
