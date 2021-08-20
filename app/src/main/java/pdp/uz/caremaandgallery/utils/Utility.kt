package pdp.uz.caremaandgallery.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object Utility {

    fun makeCall(context: Context, phoneNumber:String){
        val callIntent = Intent(Intent.ACTION_CALL)
        callIntent.data = Uri.parse("tel:".plus(phoneNumber))
        if (ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        ContextCompat.startActivity(context, callIntent, null)
        Toast.makeText(context, "Calling...", Toast.LENGTH_SHORT).show()
    }
}