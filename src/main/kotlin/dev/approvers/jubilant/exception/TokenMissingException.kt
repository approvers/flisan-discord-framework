package dev.approvers.jubilant.exception

class TokenMissingException : IllegalArgumentException(
    "The token is missing! \nProvide a token via ClientSettingInfo or Environment Variable \"DISCORD_TOKEN\"."
)
