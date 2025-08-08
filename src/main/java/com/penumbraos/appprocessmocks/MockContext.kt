package com.penumbraos.appprocessmocks

import android.annotation.SuppressLint
import android.app.ActivityThread
import android.app.LoadedApk
import android.content.AttributionSource
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.ContentResolver
import android.content.Context
import android.content.ContextWrapper
import android.content.IIntentReceiver
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.content.res.Resources
import android.os.Handler
import android.os.Looper
import android.util.Log
import java.io.File

private const val TAG = "MockContext"

@SuppressLint("DiscouragedPrivateApi", "PrivateApi", "UnspecifiedRegisterReceiverFlag")
class MockContext(base: Context, basePackageName: String? = null) : ContextWrapper(base) {

    var mockAttributionTag: String? = null

    /**
     * Defaults to com.android.settings and uid 1000
     */
    var mockAttributionSource: AttributionSource? = AttributionSource.Builder(1000).setPackageName(basePackageName ?: "com.android.settings").setAttributionTag("*tag*").build()
    private val services = mutableMapOf<String, Any>()
    var mockApplicationContext: Context? = null
    var mockClassLoader: ClassLoader? = null
    var mockResources: Resources? = null
    var mockPackageName: String? = basePackageName
    var mockPackageResourcePath: String? = null
    var mockPackageCodePath: String? = null
    var mockAssets: AssetManager? = null
    var mockContentResolver: ContentResolver? = null
    var mockMainLooper: Looper? = null
    var mockCacheDir: File? = null
    var mockFilesDir: File? = null
    var mockNoBackupFilesDir: File? = null
    var mockCodeCacheDir: File? = null
    var mockExternalCacheDirs: Array<File>? = null
    var mockExternalFilesDirs: Array<File>? = null
    var mockObbDirs: Array<File>? = null
    var mockExternalMediaDirs: Array<File>? = null
    var mockPackageManager: PackageManager? = null

    var mockStartActivity: ((Intent) -> Unit)? = null
    var mockStartService: ((Intent) -> ComponentName?)? = null
    var mockSendBroadcast: ((Intent) -> Unit)? = null
    var mockReceivers = mutableMapOf<BroadcastReceiver, Any>()
    var mockSharedPreferences = mutableMapOf<String, SharedPreferences>()

    companion object {
        fun createWithAppContext(classLoader: ClassLoader, mainThread: ActivityThread, packageName: String): MockContext {
            // The android.jar plugin currently isn't letting us use these classes directly
            val loadedApkClass = classLoader.loadClass("android.app.LoadedApk")
            val loadedApkConstructor = loadedApkClass.getDeclaredConstructor(ActivityThread::class.java)
            loadedApkConstructor.isAccessible = true
            val loadedApk = loadedApkConstructor.newInstance(mainThread) as LoadedApk

            val loadedApkPackageNameField = loadedApkClass.getDeclaredField("mPackageName")
            loadedApkPackageNameField.isAccessible = true
            loadedApkPackageNameField.set(loadedApk, packageName)

            val loadedApkApplicationInfoField = loadedApkClass.getDeclaredField("mApplicationInfo")
            loadedApkApplicationInfoField.isAccessible = true
            val applicationInfo = loadedApkApplicationInfoField.get(loadedApk) as ApplicationInfo
            applicationInfo.packageName = packageName

            return createWithAppContext(classLoader, mainThread, loadedApk, packageName)
        }

        fun createWithAppContext(classLoader: ClassLoader, mainThread: ActivityThread, loadedApk: LoadedApk, packageName: String? = null): MockContext {
            // The android.jar plugin currently isn't letting us use these classes directly
            val contextImplClass = classLoader.loadClass("android.app.ContextImpl")
            val contextImplConstructor = contextImplClass.getDeclaredMethod("createAppContext",
                ActivityThread::class.java, LoadedApk::class.java)
            contextImplConstructor.isAccessible = true

            val context = contextImplConstructor.invoke(null, mainThread, loadedApk) as Context
            val mockContext = MockContext(context, packageName)
            mockContext.mockClassLoader = classLoader
            return mockContext
        }
    }

    override fun getAttributionTag(): String? {
        return mockAttributionTag ?: super.getAttributionTag()
    }

    override fun getAttributionSource(): AttributionSource {
        return mockAttributionSource ?: super.getAttributionSource()
    }

    override fun getSystemService(name: String): Any? {
        return services[name] ?: super.getSystemService(name)
    }

    override fun getApplicationContext(): Context {
        return mockApplicationContext ?: super.getApplicationContext()
    }

    override fun getClassLoader(): ClassLoader? {
        return mockClassLoader ?: super.getClassLoader()
    }

    override fun getResources(): Resources {
        return mockResources ?: super.getResources()
    }

    override fun getPackageName(): String {
        return mockPackageName ?: super.getPackageName()
    }

    override fun getPackageResourcePath(): String {
        return mockPackageResourcePath ?: super.getPackageResourcePath()
    }

    override fun getPackageCodePath(): String {
        return mockPackageCodePath ?: super.getPackageCodePath()
    }

    override fun getAssets(): AssetManager {
        return mockAssets ?: super.getAssets()
    }

    override fun getSharedPreferences(name: String?, mode: Int): SharedPreferences? {
        return mockSharedPreferences[name] ?: super.getSharedPreferences(name, mode)
    }

    override fun getContentResolver(): ContentResolver {
        return mockContentResolver ?: super.getContentResolver()
    }

    override fun getMainLooper(): Looper {
        return mockMainLooper ?: super.getMainLooper()
    }

    override fun getCacheDir(): File {
        return mockCacheDir ?: super.getCacheDir()
    }

    override fun getFilesDir(): File {
        return mockFilesDir ?: super.getFilesDir()
    }

    override fun getNoBackupFilesDir(): File {
        return mockNoBackupFilesDir ?: super.getNoBackupFilesDir()
    }

    override fun getCodeCacheDir(): File {
        return mockCodeCacheDir ?: super.getCodeCacheDir()
    }

    override fun getExternalCacheDirs(): Array<File> {
        return mockExternalCacheDirs ?: super.getExternalCacheDirs()
    }

    override fun getExternalFilesDirs(type: String?): Array<File> {
        return mockExternalFilesDirs ?: super.getExternalFilesDirs(type)
    }

    override fun getObbDirs(): Array<File> {
        return mockObbDirs ?: super.getObbDirs()
    }

    override fun getExternalMediaDirs(): Array<File> {
        return mockExternalMediaDirs ?: super.getExternalMediaDirs()
    }

    override fun getPackageManager(): PackageManager {
        return mockPackageManager ?: createPackageManager()
    }

    private fun createPackageManager(): PackageManager {
        return try {
            super.getPackageManager()
        } catch (e: Exception) {
            val systemPackageManager = try {
                val contextClass = Class.forName("android.app.ContextImpl")
                val getSystemContextMethod = contextClass.getDeclaredMethod("getSystemContext")
                getSystemContextMethod.isAccessible = true
                val systemContext = getSystemContextMethod.invoke(null) as Context
                systemContext.packageManager
            } catch (e: Exception) {
                null
            }

            if (systemPackageManager != null && mockPackageManager == null) {
                mockPackageManager = systemPackageManager
            }
            
            systemPackageManager ?: super.getPackageManager()
        }
    }

    override fun startActivity(intent: Intent) {
        mockStartActivity?.invoke(intent) ?: super.startActivity(intent)
    }

    override fun startService(service: Intent): ComponentName? {
        return mockStartService?.invoke(service) ?: super.startService(service)
    }

    override fun sendBroadcast(intent: Intent) {
        mockSendBroadcast?.invoke(intent) ?: super.sendBroadcast(intent)
    }

    @SuppressLint("PrivateApi")
    override fun registerReceiver(receiver: BroadcastReceiver?, filter: IntentFilter): Intent? {
        if (receiver == null) {
            return null
        }

        val activityManager = MockActivityManager.getOriginalIActivityManagerProxy()

        if (activityManager == null) {
            Log.e(TAG, "Activity manager is null. Cannot register receiver")
            return null
        }

        // Create IIntentReceiver wrapper for our BroadcastReceiver
        val intentReceiver = object : IIntentReceiver.Stub() {
            override fun performReceive(
                intent: Intent,
                resultCode: Int,
                data: String?,
                extras: android.os.Bundle?,
                ordered: Boolean,
                sticky: Boolean,
                sendingUser: Int
            ) {
                val handler = Handler(Looper.getMainLooper())
                handler.post {
                    receiver.onReceive(this@MockContext, intent)
                }
            }
        }

        val registerReceiverMethod = activityManager::class.java.getDeclaredMethod(
            "registerReceiver",
            Class.forName("android.app.IApplicationThread"),  // caller
            String::class.java,                               // callerPackage
            Class.forName("android.content.IIntentReceiver"), // receiver
            IntentFilter::class.java,                         // filter
            String::class.java,                               // requiredPermission
            Int::class.java,                                  // userId
            Int::class.java                                   // flags
        )
        registerReceiverMethod.isAccessible = true

        val intent = registerReceiverMethod.invoke(
            activityManager,
            null,             // caller
            null,             // callerPackage
            intentReceiver,   // receiver
            filter,           // filter
            null,             // requiredPermission
            0,                // userId
            0                 // flags
        ) as? Intent

        mockReceivers[receiver] = intentReceiver

        return intent
    }

    override fun unregisterReceiver(receiver: BroadcastReceiver?) {
        if (receiver == null) {
            return
        }

        val intentReceiver = mockReceivers.remove(receiver) ?: return

        val activityManager = MockActivityManager.getOriginalIActivityManagerProxy()

        if (activityManager == null) {
            Log.e(TAG, "Activity manager is null. Cannot unregister receiver")
            return
        }

        val unregisterReceiverMethod = activityManager.javaClass.getDeclaredMethod(
            "unregisterReceiver",
            Class.forName("android.content.IIntentReceiver")
        )
        unregisterReceiverMethod.isAccessible = true

        try {
            unregisterReceiverMethod.invoke(activityManager, intentReceiver)
        } catch (_: Exception) {
            // Ignore
        }
    }

    fun setService(name: String, service: Any) {
        services[name] = service
    }

    fun setSharedPreferences(name: String, sharedPreferences: SharedPreferences) {
        mockSharedPreferences[name] = sharedPreferences
    }

    override fun checkPermission(permission: String, pid: Int, uid: Int): Int {
        return PackageManager.PERMISSION_GRANTED
    }

    override fun checkCallingPermission(permission: String): Int {
        return PackageManager.PERMISSION_GRANTED
    }

    override fun checkCallingOrSelfPermission(permission: String): Int {
        return PackageManager.PERMISSION_GRANTED
    }

    override fun checkSelfPermission(permission: String): Int {
        return PackageManager.PERMISSION_GRANTED
    }
}