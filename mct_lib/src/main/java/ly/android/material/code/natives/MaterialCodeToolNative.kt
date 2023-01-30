package ly.android.material.code.natives

object MaterialCodeToolNative {

    init {
        System.loadLibrary("natives")
    }

    external fun getKey(): String

    external fun getIvKey(): String

    external fun getIp(): String

}