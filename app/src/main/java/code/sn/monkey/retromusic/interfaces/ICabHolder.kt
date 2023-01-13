
package code.sn.monkey.retromusic.interfaces

import com.afollestad.materialcab.MaterialCab

interface ICabHolder {

    fun openCab(menuRes: Int, callback: MaterialCab.Callback): MaterialCab
}
