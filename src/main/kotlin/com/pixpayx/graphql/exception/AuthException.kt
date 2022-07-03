package com.pixpayx.graphql.exception

class AuthException : RuntimeException(
    "User not allowed to fetch this data",
    null, false, false
)
