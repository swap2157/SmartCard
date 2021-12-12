package com.Swapnil.smartcardreader

import android.graphics.drawable.Animatable2
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), NfcAdapter.ReaderCallback {

    private var tickToCross: AnimatedVectorDrawable?=null
    private var crossToTick:AnimatedVectorDrawable?=null
    private var nfcAdapter: NfcAdapter? = null
    private var isTick = false

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        tickToCross = getDrawable(R.drawable.avd_tick2cross) as AnimatedVectorDrawable?;
        crossToTick = getDrawable(R.drawable.avd_cross2tick) as AnimatedVectorDrawable?;


    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun animchange(){
        val drawable = if (isTick) tickToCross!! else crossToTick!!
        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.M) {
            drawable.registerAnimationCallback(@RequiresApi(Build.VERSION_CODES.M)
            object : Animatable2.AnimationCallback() {
                override fun onAnimationStart(drawable: Drawable) {
                    super.onAnimationStart(drawable)
                }

                override fun onAnimationEnd(drawable: Drawable) {
                    super.onAnimationEnd(drawable)
                }
            })
        }
        imageView.setImageDrawable(drawable);
        drawable.start();
        isTick=!isTick;
    }

    public override fun onResume() {
        super.onResume()
        nfcAdapter?.enableReaderMode(this, this,
                NfcAdapter.FLAG_READER_NFC_A or NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK,
                null)
    }

    public override fun onPause() {
        super.onPause()
        nfcAdapter?.disableReaderMode(this)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onTagDiscovered(tag: Tag?) {
        val isoDep = IsoDep.get(tag)
        isoDep.connect()
        val response = isoDep.transceive(Utils.hexStringToByteArray(
                "00A4040007A0000002471001"))
        textView.text=""
        runOnUiThread { textView.append("Card Response: " + Utils.toHex(response)) }
        animchange()
        Thread.sleep(2_000)
        animchange()
        textView.text="waiting"
        isoDep.close()
    }

}
