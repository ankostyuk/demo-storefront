package ru.nullpointer.storefront.util;

import java.security.SecureRandom;
import java.util.Random;
import org.springframework.util.Assert;

/**
 *
 * @author Alexander Yastrebov
 */
public final class RandomUtils {

    public static final char[] DIGITS = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
    };
    //
    public static final char[] ASCII_LOWER = {
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
        'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
        'u', 'v', 'w', 'x', 'y', 'z'
    };
    //
    public static final char[] ASCII_UPPER = {
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
        'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
        'U', 'V', 'W', 'X', 'Y', 'Z'
    };

    private RandomUtils() {
    }

    public static String generateRandomString(int length, char[]... alphabets) {
        Assert.isTrue(length > 0);
        Assert.notNull(alphabets);
        Assert.isTrue(alphabets.length > 0);
        Assert.noNullElements(alphabets);

        int totalLength = 0;
        for (char[] a : alphabets) {
            totalLength += a.length;
        }

        Random rand = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = rand.nextInt(totalLength);
            for (char[] a : alphabets) {
                if (index < a.length) {
                    sb.append(a[index]);
                    break;
                } else {
                    index -= a.length;
                }
            }
        }
        return sb.toString();
    }
}
