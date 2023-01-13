package code.sn.monkey.appthemehelper.common

import androidx.appcompat.widget.Toolbar

import code.sn.monkey.appthemehelper.util.ToolbarContentTintHelper

class ATHActionBarActivity : ATHToolbarActivity() {

    override fun getATHToolbar(): Toolbar? {
        return ToolbarContentTintHelper.getSupportActionBarView(supportActionBar)
    }
}
