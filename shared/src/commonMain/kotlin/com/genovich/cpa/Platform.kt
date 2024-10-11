package com.genovich.cpa

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform