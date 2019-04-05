package citrixtest;


import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

@Test
public class PigLatinTransformerTest {

    private PigLatinTransformer transformer;

    @BeforeTest
    public void setUp() {
        transformer = new PigLatinTransformer();
    }

    @DataProvider(name = "testConsonantTransformationData")
    public Object[][] testConsonantTransformationData() {
        return new Object[][] {
                new Object[] { "BelGiuM", "ElgIumBay" },
                new Object[] { "b_e+l'g^i;u:m.", "elg_i+u'm^b;a:y." },
                new Object[] { "don't.", "ontda'y." },
                new Object[] { "d'Arc!", "arC'day!" },
                new Object[] { "frida;Y'", "ridayfA;y'" },
                new Object[] { "can'T", "antcA'y" }
        };
    }

    @Test(dataProvider = "testConsonantTransformationData")
    public void testConsonantTransformation(final String token, final String expected) {
        assertEquals(transformer.transformConsonant(token), expected);
    }

    @DataProvider(name = "testVowelTransformationData")
    public Object[][] testVowelTransformationData() {
        return new Object[][] {
                new Object[] { "Argentina!", "Argentinaway!" },
                new Object[] { "A_r+g*e/n;t:i,n?a!", "Arge_n+t*i/n;a:w,a?y!" },
                new Object[] { "IsN't", "IsNtwa'y" },
                new Object[] { "Aloh!A", "AlohAWa!y" },
                new Object[] { "isn'T", "isntWa'y" }
        };
    }

    @Test(dataProvider = "testVowelTransformationData")
    public void testVowelTransformation(final String token, final String expected) {
        assertEquals(transformer.transformVowel(token), expected);
    }

    @DataProvider(name = "testServiceData")
    public Object[][] testServiceData() {
        return new Object[][] {
                new Object[] { "Argentina Belgium", "Argentinaway Elgiumbay" },
                new Object[] { "!DoNotStartWithLetter", "!DoNotStartWithLetter" },
                new Object[] { "EndsWith!way ZEndsWith!way", "EndsWith!way ZEndsWith!way" },
                new Object[] { "Has-Hyphen", "Ashay-Yphenhay" },
                new Object[] { " Not  Trimmed ", " Otnay  Rimmedtay " },
                new Object[] { " --Wired---Hyphens-- ", " --Iredway---Yphenshay-- " },
        };
    }

    @Test(dataProvider = "testServiceData")
    public void testService(final String phrase, String expected) {
        assertEquals(transformer.transform(phrase), expected);
    }

}