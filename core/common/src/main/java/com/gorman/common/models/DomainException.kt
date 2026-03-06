package com.gorman.common.models

class UserNotFoundError(override val message: String) : Exception(message)
class LocationNotDefined(override val message: String) : Exception(message)
class UnexpectedCredentialError(override val message: String) : Exception(message)
