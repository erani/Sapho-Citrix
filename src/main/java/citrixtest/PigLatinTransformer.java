package citrixtest;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.apache.commons.lang3.CharUtils.isAsciiAlphaUpper;

public class PigLatinTransformer {

    private final static char SPACE = ' ';
    private final static char HYPHEN = '-';
    private final static char[] VOWELS = new char[]{'a', 'e', 'i', 'o', 'u', 'A', 'E', 'I', 'O', 'U'};

    private static final String CONSONANT_SUFFIX = "ay";
    private static final String VOWEL_SUFFIX = "way";


    @Nonnull
    public String transform(@Nonnull final String phrase) {
        Preconditions.checkArgument(StringUtils.isNotBlank(phrase), "Provided phrase is blank!");
        return transform(phrase, SPACE);
    }

    private String transform(final String phrase, final char splitter) {
        final List<String> transformedTokens = new ArrayList<>();

        final Iterable<String> tokens = Splitter.on(splitter).split(phrase);
        for (String token : tokens) {
            if (StringUtils.isBlank(token)) {
                transformedTokens.add(token);
                continue;
            }

            if (StringUtils.contains(token, HYPHEN)) {
                transformedTokens.add(transform(token, HYPHEN));
                continue;
            }

            if (token.endsWith(VOWEL_SUFFIX)) { // Should it be case sensitive?
                transformedTokens.add(token);
                continue;
            }

            final char firstCh = token.charAt(0);

            if (!CharUtils.isAsciiAlpha(firstCh)) {
                transformedTokens.add(token);
                continue;
            }

            if (ArrayUtils.contains(VOWELS, firstCh)) {
                transformedTokens.add(transformVowel(token));
                continue;
            }

            transformedTokens.add(transformConsonant(token));
        }

        return Joiner.on(splitter).join(transformedTokens);
    }

    /**
     * It is not clear from requirements how char case should behave.
     * From requirement does "same place" mean position in String array?
     * Example: "d'Arc!" should it be "arC'day!"? The letter "A" in original string is on position 2, so in new string
     * the letter in position 2 is also capitalized. It also can be "aRc'day!" if punctuation is not taken in consideration.
     * In this implementation first approach was chosen.
     */
    String transformConsonant(String token) {
        int offset = CONSONANT_SUFFIX.length();
        char[] chars = new char[token.length() + offset];

        int j = -1;
        for (int i = 1; i < token.length(); i++) {
            final char ch = token.charAt(i);
            if (isPunctuation(ch)) {
                chars[i + offset] = ch;
                continue;
            }

            while (chars[++j] != 0);
            chars[j] = ch;
        }

        while (chars[++j] != 0);
        chars[j] = token.charAt(0);

        for (int i = 0; i < offset; i++) {
            while (chars[++j] != 0);
            chars[j] = CONSONANT_SUFFIX.charAt(i);
        }

        for (int i = 0; i < token.length(); i++) {
            char ch = token.charAt(i);
            if (!isPunctuation(ch)) {
                chars[i] = inProperCase(ch, chars[i]);
            }
        }

        return String.valueOf(chars);
    }

    /**
     * Same concerns regarding capitalization.
     * Example: "Aloh!A" is converted to "AlohAWa!y". The "A" in position 5 remain capitalized because in origin on
     * same position is "!" so the capitalization mechanism is skiped (should in be lower case? not specified in
     * requirement) as result the "W" in position 6 is capitalized because in origin on position 6 is capitalized "A",
     * the position is preserved, looks like it meet the requirement.
     * @param token
     * @return
     */
    String transformVowel(String token) {
        int offset = VOWEL_SUFFIX.length();
        char[] chars = new char[token.length() + offset];

        int j = -1;
        for (int i = 0; i < token.length(); i++) {
            final char ch = token.charAt(i);
            if (isPunctuation(ch)) {
                chars[i + offset] = ch;
                continue;
            }

            while (chars[++j] != 0);
            chars[j] = ch;
        }

        for (int i = 0; i < offset; i++) {
            while (chars[++j] != 0);
            chars[j] = VOWEL_SUFFIX.charAt(i);
        }

        for (int i = 0; i < token.length(); i++) {
            char ch = token.charAt(i);
            if (!isPunctuation(ch)) {
                chars[i] = inProperCase(ch, chars[i]);
            }
        }

        return String.valueOf(chars);
    }

    private boolean isPunctuation(char ch) {
        return CharUtils.isAscii(ch) && !CharUtils.isAsciiAlphanumeric(ch);
    }

    private char inProperCase(char origChar, char newChar) {
        return isAsciiAlphaUpper(origChar)
                ? Character.toUpperCase(newChar)
                : Character.toLowerCase(newChar);
    }
}
