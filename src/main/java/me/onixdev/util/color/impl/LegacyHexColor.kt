package me.onixdev.util.color.impl

import me.onixdev.util.color.Colorizer

class LegacyHexColor : Colorizer {
    override fun colorize(message: String?): String? {
        if (message.isNullOrEmpty()) {
            return message
        }
        val chars = message.toCharArray()
        val length = chars.size
        val builder = StringBuilder(length + 32)
        var hex: CharArray? = null
        var start = 0
        var end: Int
        var i = 0
        loop@ while (i < length - 1) {
            val ch = chars[i]
            val altChar = '&'
            if (ch == altChar) {
                val nextChar = chars[++i]
                val colorChar = '§'
                if (nextChar == '#') {
                    if (i + 6 >= length) {
                        break
                    }
                    if (hex == null) {
                        hex = CharArray(14)
                        hex[0] = colorChar
                        hex[1] = 'x'
                    }
                    end = i - 1
                    var j = 0
                    var hexI = 1
                    while (j < 6) {
                        val hexChar = chars[++i]
                        if (!isHexCharacter(hexChar)) {
                            continue@loop
                        }
                        hex[++hexI] = colorChar
                        hex[++hexI] = hexChar
                        j++
                    }
                    builder.appendRange(chars, start, start + (end - start)).append(hex)
                    start = i + 1
                } else {
                    if (isColorCharacter(nextChar)) {
                        chars[i - 1] = colorChar
                        chars[i] = (chars[i].code or 0x20).toChar()
                    }
                }
            }
            ++i
        }
        builder.appendRange(chars, start, start + (length - start))
        return builder.toString()
    }

    private fun isHexCharacter(ch: Char): Boolean {
        when (ch) {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'A', 'b', 'B', 'c', 'C', 'd', 'D', 'e', 'E', 'f', 'F' -> {
                return true
            }

            else -> {
                return false
            }
        }
    }
    private fun isColorCharacter(ch: Char): Boolean {
        when (ch) {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'A', 'b', 'B', 'c', 'C', 'd', 'D', 'e', 'E', 'f', 'F', 'r', 'R', 'k', 'K', 'l', 'L', 'm', 'M', 'n', 'N', 'o', 'O', 'x', 'X' -> {
                return true
            }

            else -> {
                return false
            }
        }
    }
}