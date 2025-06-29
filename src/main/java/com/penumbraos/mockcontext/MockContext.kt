import android.annotation.SuppressLint
import android.app.ActivityThread
import android.app.IApplicationThread
import android.app.LoadedApk
import android.content.AttributionSource
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.ContentResolver
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentFilter
import android.content.res.AssetManager
import android.content.res.Resources
import android.os.Looper
import java.io.File

@SuppressLint("DiscouragedPrivateApi", "UnspecifiedRegisterReceiverFlag")
class MockContext(base: Context) : ContextWrapper(base) {

    var mockAttributionTag: String? = null
    var mockAttributionSource: AttributionSource? = null
    private val services = mutableMapOf<String, Any>()
    var mockApplicationContext: Context? = null
    var mockResources: Resources? = null
    var mockPackageName: String? = null
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

    var mockStartActivity: ((Intent) -> Unit)? = null
    var mockStartService: ((Intent) -> ComponentName?)? = null
    var mockSendBroadcast: ((Intent) -> Unit)? = null
    var mockRegisterReceiver: ((BroadcastReceiver?, IntentFilter) -> Intent?)? = null
    var mockUnregisterReceiver: ((BroadcastReceiver?) -> Unit)? = null

    companion object {
        fun createWithAppContext(classLoader: ClassLoader, mainThread: IApplicationThread, loadedApk: LoadedApk): Context {
            // The android.jar plugin currently isn't letting us use these classes directly
            val contextImplClass = classLoader.loadClass("android.app.ContextImpl")
            val contextImplConstructor = contextImplClass.getDeclaredMethod("createAppContext",
                ActivityThread::class.java, LoadedApk::class.java)
            contextImplConstructor.isAccessible = true

            val context = contextImplConstructor.invoke(null, mainThread, loadedApk) as Context
            return MockContext(context)
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

    override fun startActivity(intent: Intent) {
        mockStartActivity?.invoke(intent) ?: super.startActivity(intent)
    }

    override fun startService(service: Intent): ComponentName? {
        return mockStartService?.invoke(service) ?: super.startService(service)
    }

    override fun sendBroadcast(intent: Intent) {
        mockSendBroadcast?.invoke(intent) ?: super.sendBroadcast(intent)
    }

    override fun registerReceiver(receiver: BroadcastReceiver?, filter: IntentFilter): Intent? {
        return mockRegisterReceiver?.invoke(receiver, filter) ?: super.registerReceiver(receiver, filter)
    }

    override fun unregisterReceiver(receiver: BroadcastReceiver?) {
        mockUnregisterReceiver?.invoke(receiver) ?: super.unregisterReceiver(receiver)
    }

    fun setService(name: String, service: Any) {
        services[name] = service
    }
}