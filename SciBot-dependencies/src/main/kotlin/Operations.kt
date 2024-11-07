package org.scibot

interface HostOperations {
	fun getCurrentLogin(): LoginInfo
	fun getStrangerInfo(userId: Long, cache: Boolean): StrangerInfo
}

data class StrangerInfo(val userid: Long, val nickname: String, val sex: Gender, val age: Int)
data class LoginInfo(val userId: Long, val nickname: String)
enum class Gender {
	MALE, FEMALE, UNKNOWN
}