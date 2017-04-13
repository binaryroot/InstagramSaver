package com.example.instagramsaver

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.widget.Toast
import android.support.v4.content.ContextCompat.startActivity



/**
 * Created by binary on 3/5/17.
 */
class Utility {

    companion object{
        @JvmStatic
        fun openInstagram(context: Context) {
            val i = context.getPackageManager().getLaunchIntentForPackage("com.instagram.android");
            if(i !=null && isIntentAvailable(context, i)) {
                context.startActivity(i)
            } else {
                Toast.makeText(context, R.string.no_instagram, Toast.LENGTH_SHORT).show()
            }
        }

        @JvmStatic
        fun checkWriteExternalPermission(context: Context, per: String): Boolean {
            val res = context.checkCallingOrSelfPermission(per)
            return res == PackageManager.PERMISSION_GRANTED
        }

        @JvmStatic
        fun shareIntent(context: Context){
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TEXT, context.resources.getText(R.string.send_message))
            sendIntent.type = "text/plain"
            context.startActivity(Intent.createChooser(sendIntent, context.resources.getText(R.string.send_to)))
        }

        fun rateApp(context: Context){
            val uri = Uri.parse("market://details?id=" + context.packageManager)
            val goToMarket = Intent(Intent.ACTION_VIEW, uri)
            // To count with Play market backstack, After pressing back button,
            // to taken back to our application, we need to add following flags to intent.
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
            try {
                context.startActivity(goToMarket)
            } catch (e: ActivityNotFoundException) {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + context.packageManager)))
            }

        }

        private fun isIntentAvailable(context: Context, intent: Intent): Boolean {
            val pk = context.packageManager;
            val list = pk.queryIntentActivities(intent,PackageManager.MATCH_DEFAULT_ONLY)
            return !list.isEmpty()
        }
    }

    public fun <T : RecyclerView.ViewHolder> T.listen(event: (position: Int, type: Int) -> Unit): T {
        itemView.setOnClickListener {
            event.invoke(getAdapterPosition(), getItemViewType())
        }
        return this
    }
}