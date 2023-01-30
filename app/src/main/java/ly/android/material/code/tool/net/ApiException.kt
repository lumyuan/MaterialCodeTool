package ly.android.material.code.tool.net

import java.io.IOException

class ApiException(
    val timestamp: String? = null,
    val status: Int? = null,
    val error: String? = null,
    override val message: String? = null
): IOException(message)