package com.time.yourguideapp.helper

expect object UserProfileStorage {
    fun initialize(context: Any? = null)
    fun loadUserProfile(): UserProfile?
    fun saveUserProfile(profile: UserProfile)
    fun clearUserProfile()
}
