package com.github.rodionk77.common.Utils

import ktsproject.composeapp.generated.resources.Res
import ktsproject.composeapp.generated.resources.error_400
import ktsproject.composeapp.generated.resources.error_403
import ktsproject.composeapp.generated.resources.error_404
import ktsproject.composeapp.generated.resources.error_409
import ktsproject.composeapp.generated.resources.error_410
import ktsproject.composeapp.generated.resources.error_422
import ktsproject.composeapp.generated.resources.error_503
import ktsproject.composeapp.generated.resources.no_internet
import ktsproject.composeapp.generated.resources.token_not_detected
import ktsproject.composeapp.generated.resources.unknown_error
import ktsproject.composeapp.generated.resources.unknown_server_answer

private val networkErrorKeywords = listOf(
    "UnknownHostException",
    "Unable to resolve host",
    "The Internet connection appears to be offline",
    "Network is unreachable"
)

fun Throwable?.toUiText(): UiText = when {
    this == null -> UiText.StringRes(Res.string.unknown_error)
    this is TokenNotFoundException -> UiText.StringRes(Res.string.token_not_detected)
    this is UnknownServerException -> UiText.StringRes(Res.string.unknown_server_answer)
    networkErrorKeywords.any { message?.contains(it) == true } ->
        UiText.StringRes(Res.string.no_internet)
    this is HttpException -> when (code) {
        400 -> UiText.StringRes(Res.string.error_400)
        403 -> UiText.StringRes(Res.string.error_403)
        404 -> UiText.StringRes(Res.string.error_404)
        409 -> UiText.StringRes(Res.string.error_409)
        410 -> UiText.StringRes(Res.string.error_410)
        422 -> UiText.StringRes(Res.string.error_422)
        503 -> UiText.StringRes(Res.string.error_503)
        else -> UiText.StringRes(Res.string.unknown_error)
    }
    else -> message?.let { UiText.DynamicString(it) } ?: UiText.StringRes(Res.string.unknown_error)
}
