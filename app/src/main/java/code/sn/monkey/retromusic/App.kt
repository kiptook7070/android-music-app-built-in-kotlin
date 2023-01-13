
package code.sn.monkey.retromusic

import android.widget.Toast
import androidx.multidex.MultiDexApplication
import code.sn.monkey.appthemehelper.ThemeStore
import code.sn.monkey.appthemehelper.util.VersionUtils
import code.sn.monkey.retromusic.Constants.PRO_VERSION_PRODUCT_ID
import code.sn.monkey.retromusic.appshortcuts.DynamicShortcutManager
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.TransactionDetails
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : MultiDexApplication() {

    lateinit var billingProcessor: BillingProcessor

    override fun onCreate() {
        super.onCreate()
        instance = this

        startKoin {
            androidContext(this@App)
            modules(appModules)
        }
        // default theme
        if (!ThemeStore.isConfigured(this, 3)) {
            ThemeStore.editTheme(this)
                .accentColorRes(R.color.md_deep_purple_A200)
                .coloredNavigationBar(true)
                .commit()
        }

        if (VersionUtils.hasNougatMR())
            DynamicShortcutManager(this).initDynamicShortcuts()

        // automatically restores purchases
        billingProcessor = BillingProcessor(this, BuildConfig.GOOGLE_PLAY_LICENSING_KEY,
            object : BillingProcessor.IBillingHandler {
                override fun onProductPurchased(productId: String, details: TransactionDetails?) {}

                override fun onPurchaseHistoryRestored() {
                    Toast.makeText(
                        this@App,
                        R.string.restored_previous_purchase_please_restart,
                        Toast.LENGTH_LONG
                    ).show()
                }

                override fun onBillingError(errorCode: Int, error: Throwable?) {}

                override fun onBillingInitialized() {}
            })
    }

    override fun onTerminate() {
        super.onTerminate()
        billingProcessor.release()
    }

    companion object {
        private var instance: App? = null

        fun getContext(): App {
            return instance!!
        }

        fun isProVersion(): Boolean {
            return BuildConfig.DEBUG || instance?.billingProcessor!!.isPurchased(
                PRO_VERSION_PRODUCT_ID
            )
        }
    }
}
