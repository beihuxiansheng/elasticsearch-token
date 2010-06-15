begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.util
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|util
package|;
end_package

begin_import
import|import
name|org
operator|.
name|testng
operator|.
name|annotations
operator|.
name|Test
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|Strings
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|MatcherAssert
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Tests for {@link org.elasticsearch.common.Strings}.  *  * @author kimchy (Shay Banon)  */
end_comment

begin_class
DECL|class|StringsTests
specifier|public
class|class
name|StringsTests
block|{
DECL|method|testCamelCase
annotation|@
name|Test
specifier|public
name|void
name|testCamelCase
parameter_list|()
block|{
name|assertThat
argument_list|(
name|toCamelCase
argument_list|(
literal|"test_value"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"testValue"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|testValue
init|=
literal|"testValue"
decl_stmt|;
name|assertThat
argument_list|(
name|toCamelCase
argument_list|(
name|testValue
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|testValue
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|toCamelCase
argument_list|(
name|testValue
argument_list|)
argument_list|,
name|sameInstance
argument_list|(
name|testValue
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testUnderscoreCase
annotation|@
name|Test
specifier|public
name|void
name|testUnderscoreCase
parameter_list|()
block|{
name|assertThat
argument_list|(
name|toUnderscoreCase
argument_list|(
literal|"testValue"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"test_value"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|testValue
init|=
literal|"test_value"
decl_stmt|;
name|assertThat
argument_list|(
name|toUnderscoreCase
argument_list|(
name|testValue
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|testValue
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|toUnderscoreCase
argument_list|(
name|testValue
argument_list|)
argument_list|,
name|sameInstance
argument_list|(
name|testValue
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//    @Test public void testHasTextBlank() throws Exception {
comment|//        String blank = "          ";
comment|//        assertEquals(false, Strings.hasText(blank));
comment|//    }
comment|//
comment|//    @Test public void testHasTextNullEmpty() throws Exception {
comment|//        assertEquals(false, Strings.hasText(null));
comment|//        assertEquals(false, Strings.hasText(""));
comment|//    }
comment|//
comment|//    @Test public void testHasTextValid() throws Exception {
comment|//        assertEquals(true, Strings.hasText("t"));
comment|//    }
comment|//
comment|//    @Test public void testContainsWhitespace() throws Exception {
comment|//        assertFalse(Strings.containsWhitespace(null));
comment|//        assertFalse(Strings.containsWhitespace(""));
comment|//        assertFalse(Strings.containsWhitespace("a"));
comment|//        assertFalse(Strings.containsWhitespace("abc"));
comment|//        assertTrue(Strings.containsWhitespace(" "));
comment|//        assertTrue(Strings.containsWhitespace(" a"));
comment|//        assertTrue(Strings.containsWhitespace("abc "));
comment|//        assertTrue(Strings.containsWhitespace("a b"));
comment|//        assertTrue(Strings.containsWhitespace("a  b"));
comment|//    }
comment|//
comment|//    @Test public void testTrimWhitespace() throws Exception {
comment|//        assertEquals(null, Strings.trimWhitespace(null));
comment|//        assertEquals("", Strings.trimWhitespace(""));
comment|//        assertEquals("", Strings.trimWhitespace(" "));
comment|//        assertEquals("", Strings.trimWhitespace("\t"));
comment|//        assertEquals("a", Strings.trimWhitespace(" a"));
comment|//        assertEquals("a", Strings.trimWhitespace("a "));
comment|//        assertEquals("a", Strings.trimWhitespace(" a "));
comment|//        assertEquals("a b", Strings.trimWhitespace(" a b "));
comment|//        assertEquals("a b  c", Strings.trimWhitespace(" a b  c "));
comment|//    }
comment|//
comment|//    @Test public void testTrimAllWhitespace() throws Exception {
comment|//        assertEquals("", Strings.trimAllWhitespace(""));
comment|//        assertEquals("", Strings.trimAllWhitespace(" "));
comment|//        assertEquals("", Strings.trimAllWhitespace("\t"));
comment|//        assertEquals("a", Strings.trimAllWhitespace(" a"));
comment|//        assertEquals("a", Strings.trimAllWhitespace("a "));
comment|//        assertEquals("a", Strings.trimAllWhitespace(" a "));
comment|//        assertEquals("ab", Strings.trimAllWhitespace(" a b "));
comment|//        assertEquals("abc", Strings.trimAllWhitespace(" a b  c "));
comment|//    }
comment|//
comment|//    @Test public void testTrimLeadingWhitespace() throws Exception {
comment|//        assertEquals(null, Strings.trimLeadingWhitespace(null));
comment|//        assertEquals("", Strings.trimLeadingWhitespace(""));
comment|//        assertEquals("", Strings.trimLeadingWhitespace(" "));
comment|//        assertEquals("", Strings.trimLeadingWhitespace("\t"));
comment|//        assertEquals("a", Strings.trimLeadingWhitespace(" a"));
comment|//        assertEquals("a ", Strings.trimLeadingWhitespace("a "));
comment|//        assertEquals("a ", Strings.trimLeadingWhitespace(" a "));
comment|//        assertEquals("a b ", Strings.trimLeadingWhitespace(" a b "));
comment|//        assertEquals("a b  c ", Strings.trimLeadingWhitespace(" a b  c "));
comment|//    }
comment|//
comment|//    @Test public void testTrimTrailingWhitespace() throws Exception {
comment|//        assertEquals(null, Strings.trimTrailingWhitespace(null));
comment|//        assertEquals("", Strings.trimTrailingWhitespace(""));
comment|//        assertEquals("", Strings.trimTrailingWhitespace(" "));
comment|//        assertEquals("", Strings.trimTrailingWhitespace("\t"));
comment|//        assertEquals("a", Strings.trimTrailingWhitespace("a "));
comment|//        assertEquals(" a", Strings.trimTrailingWhitespace(" a"));
comment|//        assertEquals(" a", Strings.trimTrailingWhitespace(" a "));
comment|//        assertEquals(" a b", Strings.trimTrailingWhitespace(" a b "));
comment|//        assertEquals(" a b  c", Strings.trimTrailingWhitespace(" a b  c "));
comment|//    }
comment|//
comment|//    @Test public void testTrimLeadingCharacter() throws Exception {
comment|//        assertEquals(null, Strings.trimLeadingCharacter(null, ' '));
comment|//        assertEquals("", Strings.trimLeadingCharacter("", ' '));
comment|//        assertEquals("", Strings.trimLeadingCharacter(" ", ' '));
comment|//        assertEquals("\t", Strings.trimLeadingCharacter("\t", ' '));
comment|//        assertEquals("a", Strings.trimLeadingCharacter(" a", ' '));
comment|//        assertEquals("a ", Strings.trimLeadingCharacter("a ", ' '));
comment|//        assertEquals("a ", Strings.trimLeadingCharacter(" a ", ' '));
comment|//        assertEquals("a b ", Strings.trimLeadingCharacter(" a b ", ' '));
comment|//        assertEquals("a b  c ", Strings.trimLeadingCharacter(" a b  c ", ' '));
comment|//    }
comment|//
comment|//    @Test public void testTrimTrailingCharacter() throws Exception {
comment|//        assertEquals(null, Strings.trimTrailingCharacter(null, ' '));
comment|//        assertEquals("", Strings.trimTrailingCharacter("", ' '));
comment|//        assertEquals("", Strings.trimTrailingCharacter(" ", ' '));
comment|//        assertEquals("\t", Strings.trimTrailingCharacter("\t", ' '));
comment|//        assertEquals("a", Strings.trimTrailingCharacter("a ", ' '));
comment|//        assertEquals(" a", Strings.trimTrailingCharacter(" a", ' '));
comment|//        assertEquals(" a", Strings.trimTrailingCharacter(" a ", ' '));
comment|//        assertEquals(" a b", Strings.trimTrailingCharacter(" a b ", ' '));
comment|//        assertEquals(" a b  c", Strings.trimTrailingCharacter(" a b  c ", ' '));
comment|//    }
comment|//
comment|//    @Test public void testCountOccurrencesOf() {
comment|//        assertTrue("nullx2 = 0",
comment|//                Strings.countOccurrencesOf(null, null) == 0);
comment|//        assertTrue("null string = 0",
comment|//                Strings.countOccurrencesOf("s", null) == 0);
comment|//        assertTrue("null substring = 0",
comment|//                Strings.countOccurrencesOf(null, "s") == 0);
comment|//        String s = "erowoiueoiur";
comment|//        assertTrue("not found = 0",
comment|//                Strings.countOccurrencesOf(s, "WERWER") == 0);
comment|//        assertTrue("not found char = 0",
comment|//                Strings.countOccurrencesOf(s, "x") == 0);
comment|//        assertTrue("not found ws = 0",
comment|//                Strings.countOccurrencesOf(s, " ") == 0);
comment|//        assertTrue("not found empty string = 0",
comment|//                Strings.countOccurrencesOf(s, "") == 0);
comment|//        assertTrue("found char=2", Strings.countOccurrencesOf(s, "e") == 2);
comment|//        assertTrue("found substring=2",
comment|//                Strings.countOccurrencesOf(s, "oi") == 2);
comment|//        assertTrue("found substring=2",
comment|//                Strings.countOccurrencesOf(s, "oiu") == 2);
comment|//        assertTrue("found substring=3",
comment|//                Strings.countOccurrencesOf(s, "oiur") == 1);
comment|//        assertTrue("test last", Strings.countOccurrencesOf(s, "r") == 2);
comment|//    }
comment|//
comment|//    @Test public void testReplace() throws Exception {
comment|//        String inString = "a6AazAaa77abaa";
comment|//        String oldPattern = "aa";
comment|//        String newPattern = "foo";
comment|//
comment|//        // Simple replace
comment|//        String s = Strings.replace(inString, oldPattern, newPattern);
comment|//        assertTrue("Replace 1 worked", s.equals("a6AazAfoo77abfoo"));
comment|//
comment|//        // Non match: no change
comment|//        s = Strings.replace(inString, "qwoeiruqopwieurpoqwieur", newPattern);
comment|//        assertTrue("Replace non matched is equal", s.equals(inString));
comment|//
comment|//        // Null new pattern: should ignore
comment|//        s = Strings.replace(inString, oldPattern, null);
comment|//        assertTrue("Replace non matched is equal", s.equals(inString));
comment|//
comment|//        // Null old pattern: should ignore
comment|//        s = Strings.replace(inString, null, newPattern);
comment|//        assertTrue("Replace non matched is equal", s.equals(inString));
comment|//    }
comment|//
comment|//    @Test public void testDelete() throws Exception {
comment|//        String inString = "The quick brown fox jumped over the lazy dog";
comment|//
comment|//        String noThe = Strings.delete(inString, "the");
comment|//        assertTrue("Result has no the [" + noThe + "]",
comment|//                noThe.equals("The quick brown fox jumped over  lazy dog"));
comment|//
comment|//        String nohe = Strings.delete(inString, "he");
comment|//        assertTrue("Result has no he [" + nohe + "]",
comment|//                nohe.equals("T quick brown fox jumped over t lazy dog"));
comment|//
comment|//        String nosp = Strings.delete(inString, " ");
comment|//        assertTrue("Result has no spaces",
comment|//                nosp.equals("Thequickbrownfoxjumpedoverthelazydog"));
comment|//
comment|//        String killEnd = Strings.delete(inString, "dog");
comment|//        assertTrue("Result has no dog",
comment|//                killEnd.equals("The quick brown fox jumped over the lazy "));
comment|//
comment|//        String mismatch = Strings.delete(inString, "dxxcxcxog");
comment|//        assertTrue("Result is unchanged", mismatch.equals(inString));
comment|//
comment|//        String nochange = Strings.delete(inString, "");
comment|//        assertTrue("Result is unchanged", nochange.equals(inString));
comment|//    }
comment|//
comment|//    @Test public void testDeleteAny() throws Exception {
comment|//        String inString = "Able was I ere I saw Elba";
comment|//
comment|//        String res = Strings.deleteAny(inString, "I");
comment|//        assertTrue("Result has no Is [" + res + "]", res.equals("Able was  ere  saw Elba"));
comment|//
comment|//        res = Strings.deleteAny(inString, "AeEba!");
comment|//        assertTrue("Result has no Is [" + res + "]", res.equals("l ws I r I sw l"));
comment|//
comment|//        String mismatch = Strings.deleteAny(inString, "#@$#$^");
comment|//        assertTrue("Result is unchanged", mismatch.equals(inString));
comment|//
comment|//        String whitespace = "This is\n\n\n    \t   a messagy string with whitespace\n";
comment|//        assertTrue("Has CR", whitespace.indexOf("\n") != -1);
comment|//        assertTrue("Has tab", whitespace.indexOf("\t") != -1);
comment|//        assertTrue("Has  sp", whitespace.indexOf(" ") != -1);
comment|//        String cleaned = Strings.deleteAny(whitespace, "\n\t ");
comment|//        assertTrue("Has no CR", cleaned.indexOf("\n") == -1);
comment|//        assertTrue("Has no tab", cleaned.indexOf("\t") == -1);
comment|//        assertTrue("Has no sp", cleaned.indexOf(" ") == -1);
comment|//        assertTrue("Still has chars", cleaned.length()> 10);
comment|//    }
comment|//
comment|//
comment|//    @Test public void testQuote() {
comment|//        assertEquals("'myString'", Strings.quote("myString"));
comment|//        assertEquals("''", Strings.quote(""));
comment|//        assertNull(Strings.quote(null));
comment|//    }
comment|//
comment|//    @Test public void testQuoteIfString() {
comment|//        assertEquals("'myString'", Strings.quoteIfString("myString"));
comment|//        assertEquals("''", Strings.quoteIfString(""));
comment|//        assertEquals(5, Strings.quoteIfString(5));
comment|//        assertNull(Strings.quoteIfString(null));
comment|//    }
comment|//
comment|//    @Test public void testUnqualify() {
comment|//        String qualified = "i.am.not.unqualified";
comment|//        assertEquals("unqualified", Strings.unqualify(qualified));
comment|//    }
comment|//
comment|//    @Test public void testCapitalize() {
comment|//        String capitalized = "i am not capitalized";
comment|//        assertEquals("I am not capitalized", Strings.capitalize(capitalized));
comment|//    }
comment|//
comment|//    @Test public void testUncapitalize() {
comment|//        String capitalized = "I am capitalized";
comment|//        assertEquals("i am capitalized", Strings.uncapitalize(capitalized));
comment|//    }
comment|//
comment|//    @Test public void testGetFilename() {
comment|//        assertEquals(null, Strings.getFilename(null));
comment|//        assertEquals("", Strings.getFilename(""));
comment|//        assertEquals("myfile", Strings.getFilename("myfile"));
comment|//        assertEquals("myfile", Strings.getFilename("mypath/myfile"));
comment|//        assertEquals("myfile.", Strings.getFilename("myfile."));
comment|//        assertEquals("myfile.", Strings.getFilename("mypath/myfile."));
comment|//        assertEquals("myfile.txt", Strings.getFilename("myfile.txt"));
comment|//        assertEquals("myfile.txt", Strings.getFilename("mypath/myfile.txt"));
comment|//    }
comment|//
comment|//    @Test public void testGetFilenameExtension() {
comment|//        assertEquals(null, Strings.getFilenameExtension(null));
comment|//        assertEquals(null, Strings.getFilenameExtension(""));
comment|//        assertEquals(null, Strings.getFilenameExtension("myfile"));
comment|//        assertEquals(null, Strings.getFilenameExtension("myPath/myfile"));
comment|//        assertEquals("", Strings.getFilenameExtension("myfile."));
comment|//        assertEquals("", Strings.getFilenameExtension("myPath/myfile."));
comment|//        assertEquals("txt", Strings.getFilenameExtension("myfile.txt"));
comment|//        assertEquals("txt", Strings.getFilenameExtension("mypath/myfile.txt"));
comment|//    }
comment|//
comment|//    @Test public void testStripFilenameExtension() {
comment|//        assertEquals(null, Strings.stripFilenameExtension(null));
comment|//        assertEquals("", Strings.stripFilenameExtension(""));
comment|//        assertEquals("myfile", Strings.stripFilenameExtension("myfile"));
comment|//        assertEquals("mypath/myfile", Strings.stripFilenameExtension("mypath/myfile"));
comment|//        assertEquals("myfile", Strings.stripFilenameExtension("myfile."));
comment|//        assertEquals("mypath/myfile", Strings.stripFilenameExtension("mypath/myfile."));
comment|//        assertEquals("myfile", Strings.stripFilenameExtension("myfile.txt"));
comment|//        assertEquals("mypath/myfile", Strings.stripFilenameExtension("mypath/myfile.txt"));
comment|//    }
comment|//
comment|//    @Test public void testCleanPath() {
comment|//        assertEquals("mypath/myfile", Strings.cleanPath("mypath/myfile"));
comment|//        assertEquals("mypath/myfile", Strings.cleanPath("mypath\\myfile"));
comment|//        assertEquals("mypath/myfile", Strings.cleanPath("mypath/../mypath/myfile"));
comment|//        assertEquals("mypath/myfile", Strings.cleanPath("mypath/myfile/../../mypath/myfile"));
comment|//        assertEquals("../mypath/myfile", Strings.cleanPath("../mypath/myfile"));
comment|//        assertEquals("../mypath/myfile", Strings.cleanPath("../mypath/../mypath/myfile"));
comment|//        assertEquals("../mypath/myfile", Strings.cleanPath("mypath/../../mypath/myfile"));
comment|//        assertEquals("/../mypath/myfile", Strings.cleanPath("/../mypath/myfile"));
comment|//    }
comment|//
comment|//    @Test public void testPathEquals() {
comment|//        assertTrue("Must be true for the same strings",
comment|//                Strings.pathEquals("/dummy1/dummy2/dummy3",
comment|//                        "/dummy1/dummy2/dummy3"));
comment|//        assertTrue("Must be true for the same win strings",
comment|//                Strings.pathEquals("C:\\dummy1\\dummy2\\dummy3",
comment|//                        "C:\\dummy1\\dummy2\\dummy3"));
comment|//        assertTrue("Must be true for one top path on 1",
comment|//                Strings.pathEquals("/dummy1/bin/../dummy2/dummy3",
comment|//                        "/dummy1/dummy2/dummy3"));
comment|//        assertTrue("Must be true for one win top path on 2",
comment|//                Strings.pathEquals("C:\\dummy1\\dummy2\\dummy3",
comment|//                        "C:\\dummy1\\bin\\..\\dummy2\\dummy3"));
comment|//        assertTrue("Must be true for two top paths on 1",
comment|//                Strings.pathEquals("/dummy1/bin/../dummy2/bin/../dummy3",
comment|//                        "/dummy1/dummy2/dummy3"));
comment|//        assertTrue("Must be true for two win top paths on 2",
comment|//                Strings.pathEquals("C:\\dummy1\\dummy2\\dummy3",
comment|//                        "C:\\dummy1\\bin\\..\\dummy2\\bin\\..\\dummy3"));
comment|//        assertTrue("Must be true for double top paths on 1",
comment|//                Strings.pathEquals("/dummy1/bin/tmp/../../dummy2/dummy3",
comment|//                        "/dummy1/dummy2/dummy3"));
comment|//        assertTrue("Must be true for double top paths on 2 with similarity",
comment|//                Strings.pathEquals("/dummy1/dummy2/dummy3",
comment|//                        "/dummy1/dum/dum/../../dummy2/dummy3"));
comment|//        assertTrue("Must be true for current paths",
comment|//                Strings.pathEquals("./dummy1/dummy2/dummy3",
comment|//                        "dummy1/dum/./dum/../../dummy2/dummy3"));
comment|//        assertFalse("Must be false for relative/absolute paths",
comment|//                Strings.pathEquals("./dummy1/dummy2/dummy3",
comment|//                        "/dummy1/dum/./dum/../../dummy2/dummy3"));
comment|//        assertFalse("Must be false for different strings",
comment|//                Strings.pathEquals("/dummy1/dummy2/dummy3",
comment|//                        "/dummy1/dummy4/dummy3"));
comment|//        assertFalse("Must be false for one false path on 1",
comment|//                Strings.pathEquals("/dummy1/bin/tmp/../dummy2/dummy3",
comment|//                        "/dummy1/dummy2/dummy3"));
comment|//        assertFalse("Must be false for one false win top path on 2",
comment|//                Strings.pathEquals("C:\\dummy1\\dummy2\\dummy3",
comment|//                        "C:\\dummy1\\bin\\tmp\\..\\dummy2\\dummy3"));
comment|//        assertFalse("Must be false for top path on 1 + difference",
comment|//                Strings.pathEquals("/dummy1/bin/../dummy2/dummy3",
comment|//                        "/dummy1/dummy2/dummy4"));
comment|//    }
comment|//
comment|//    @Test public void testConcatenateStringArrays() {
comment|//        String[] input1 = new String[]{"myString2"};
comment|//        String[] input2 = new String[]{"myString1", "myString2"};
comment|//        String[] result = Strings.concatenateStringArrays(input1, input2);
comment|//        assertEquals(3, result.length);
comment|//        assertEquals("myString2", result[0]);
comment|//        assertEquals("myString1", result[1]);
comment|//        assertEquals("myString2", result[2]);
comment|//
comment|//        assertArrayEquals(input1, Strings.concatenateStringArrays(input1, null));
comment|//        assertArrayEquals(input2, Strings.concatenateStringArrays(null, input2));
comment|//        assertNull(Strings.concatenateStringArrays(null, null));
comment|//    }
comment|//
comment|//    @Test public void testMergeStringArrays() {
comment|//        String[] input1 = new String[]{"myString2"};
comment|//        String[] input2 = new String[]{"myString1", "myString2"};
comment|//        String[] result = Strings.mergeStringArrays(input1, input2);
comment|//        assertEquals(2, result.length);
comment|//        assertEquals("myString2", result[0]);
comment|//        assertEquals("myString1", result[1]);
comment|//
comment|//        assertArrayEquals(input1, Strings.mergeStringArrays(input1, null));
comment|//        assertArrayEquals(input2, Strings.mergeStringArrays(null, input2));
comment|//        assertNull(Strings.mergeStringArrays(null, null));
comment|//    }
comment|//
comment|//    @Test public void testSortStringArray() {
comment|//        String[] input = new String[]{"myString2"};
comment|//        input = Strings.addStringToArray(input, "myString1");
comment|//        assertEquals("myString2", input[0]);
comment|//        assertEquals("myString1", input[1]);
comment|//
comment|//        Strings.sortStringArray(input);
comment|//        assertEquals("myString1", input[0]);
comment|//        assertEquals("myString2", input[1]);
comment|//    }
comment|//
comment|//    @Test public void testRemoveDuplicateStrings() {
comment|//        String[] input = new String[]{"myString2", "myString1", "myString2"};
comment|//        input = Strings.removeDuplicateStrings(input);
comment|//        assertEquals("myString1", input[0]);
comment|//        assertEquals("myString2", input[1]);
comment|//    }
comment|//
comment|//    @Test public void testSplitArrayElementsIntoProperties() {
comment|//        String[] input = new String[]{"key1=value1 ", "key2 =\"value2\""};
comment|//        Properties result = Strings.splitArrayElementsIntoProperties(input, "=");
comment|//        assertEquals("value1", result.getProperty("key1"));
comment|//        assertEquals("\"value2\"", result.getProperty("key2"));
comment|//    }
comment|//
comment|//    @Test public void testSplitArrayElementsIntoPropertiesAndDeletedChars() {
comment|//        String[] input = new String[]{"key1=value1 ", "key2 =\"value2\""};
comment|//        Properties result = Strings.splitArrayElementsIntoProperties(input, "=", "\"");
comment|//        assertEquals("value1", result.getProperty("key1"));
comment|//        assertEquals("value2", result.getProperty("key2"));
comment|//    }
comment|//
comment|//    @Test public void testTokenizeToStringArray() {
comment|//        String[] sa = Strings.tokenizeToStringArray("a,b , ,c", ",");
comment|//        assertEquals(3, sa.length);
comment|//        assertTrue("components are correct",
comment|//                sa[0].equals("a")&& sa[1].equals("b")&& sa[2].equals("c"));
comment|//    }
comment|//
comment|//    @Test public void testTokenizeToStringArrayWithNotIgnoreEmptyTokens() {
comment|//        String[] sa = Strings.tokenizeToStringArray("a,b , ,c", ",", true, false);
comment|//        assertEquals(4, sa.length);
comment|//        assertTrue("components are correct",
comment|//                sa[0].equals("a")&& sa[1].equals("b")&& sa[2].equals("")&& sa[3].equals("c"));
comment|//    }
comment|//
comment|//    @Test public void testTokenizeToStringArrayWithNotTrimTokens() {
comment|//        String[] sa = Strings.tokenizeToStringArray("a,b ,c", ",", false, true);
comment|//        assertEquals(3, sa.length);
comment|//        assertTrue("components are correct",
comment|//                sa[0].equals("a")&& sa[1].equals("b ")&& sa[2].equals("c"));
comment|//    }
comment|//
comment|//    @Test public void testCommaDelimitedListToStringArrayWithNullProducesEmptyArray() {
comment|//        String[] sa = Strings.commaDelimitedListToStringArray(null);
comment|//        assertTrue("String array isn't null with null input", sa != null);
comment|//        assertTrue("String array length == 0 with null input", sa.length == 0);
comment|//    }
comment|//
comment|//    @Test public void testCommaDelimitedListToStringArrayWithEmptyStringProducesEmptyArray() {
comment|//        String[] sa = Strings.commaDelimitedListToStringArray("");
comment|//        assertTrue("String array isn't null with null input", sa != null);
comment|//        assertTrue("String array length == 0 with null input", sa.length == 0);
comment|//    }
comment|//
comment|//    private void testStringArrayReverseTransformationMatches(String[] sa) {
comment|//        String[] reverse =
comment|//                Strings.commaDelimitedListToStringArray(Strings.arrayToCommaDelimitedString(sa));
comment|//        assertEquals("Reverse transformation is equal",
comment|//                Arrays.asList(sa),
comment|//                Arrays.asList(reverse));
comment|//    }
comment|//
comment|//    @Test public void testDelimitedListToStringArrayWithComma() {
comment|//        String[] sa = Strings.delimitedListToStringArray("a,b", ",");
comment|//        assertEquals(2, sa.length);
comment|//        assertEquals("a", sa[0]);
comment|//        assertEquals("b", sa[1]);
comment|//    }
comment|//
comment|//    @Test public void testDelimitedListToStringArrayWithSemicolon() {
comment|//        String[] sa = Strings.delimitedListToStringArray("a;b", ";");
comment|//        assertEquals(2, sa.length);
comment|//        assertEquals("a", sa[0]);
comment|//        assertEquals("b", sa[1]);
comment|//    }
comment|//
comment|//    @Test public void testDelimitedListToStringArrayWithEmptyString() {
comment|//        String[] sa = Strings.delimitedListToStringArray("a,b", "");
comment|//        assertEquals(3, sa.length);
comment|//        assertEquals("a", sa[0]);
comment|//        assertEquals(",", sa[1]);
comment|//        assertEquals("b", sa[2]);
comment|//    }
comment|//
comment|//    @Test public void testDelimitedListToStringArrayWithNullDelimiter() {
comment|//        String[] sa = Strings.delimitedListToStringArray("a,b", null);
comment|//        assertEquals(1, sa.length);
comment|//        assertEquals("a,b", sa[0]);
comment|//    }
comment|//
comment|//    @Test public void testCommaDelimitedListToStringArrayMatchWords() {
comment|//        // Could read these from files
comment|//        String[] sa = new String[]{"foo", "bar", "big"};
comment|//        doTestCommaDelimitedListToStringArrayLegalMatch(sa);
comment|//        testStringArrayReverseTransformationMatches(sa);
comment|//
comment|//        sa = new String[]{"a", "b", "c"};
comment|//        doTestCommaDelimitedListToStringArrayLegalMatch(sa);
comment|//        testStringArrayReverseTransformationMatches(sa);
comment|//
comment|//        // Test same words
comment|//        sa = new String[]{"AA", "AA", "AA", "AA", "AA"};
comment|//        doTestCommaDelimitedListToStringArrayLegalMatch(sa);
comment|//        testStringArrayReverseTransformationMatches(sa);
comment|//    }
comment|//
comment|//    @Test public void testCommaDelimitedListToStringArraySingleString() {
comment|//        // Could read these from files
comment|//        String s = "woeirqupoiewuropqiewuorpqiwueopriquwopeiurqopwieur";
comment|//        String[] sa = Strings.commaDelimitedListToStringArray(s);
comment|//        assertTrue("Found one String with no delimiters", sa.length == 1);
comment|//        assertTrue("Single array entry matches input String with no delimiters",
comment|//                sa[0].equals(s));
comment|//    }
comment|//
comment|//    @Test public void testCommaDelimitedListToStringArrayWithOtherPunctuation() {
comment|//        // Could read these from files
comment|//        String[] sa = new String[]{"xcvwert4456346&*.", "///", ".!", ".", ";"};
comment|//        doTestCommaDelimitedListToStringArrayLegalMatch(sa);
comment|//    }
comment|//
comment|//    /**
comment|//     * We expect to see the empty Strings in the output.
comment|//     */
comment|//    @Test public void testCommaDelimitedListToStringArrayEmptyStrings() {
comment|//        // Could read these from files
comment|//        String[] sa = Strings.commaDelimitedListToStringArray("a,,b");
comment|//        assertEquals("a,,b produces array length 3", 3, sa.length);
comment|//        assertTrue("components are correct",
comment|//                sa[0].equals("a")&& sa[1].equals("")&& sa[2].equals("b"));
comment|//
comment|//        sa = new String[]{"", "", "a", ""};
comment|//        doTestCommaDelimitedListToStringArrayLegalMatch(sa);
comment|//    }
comment|//
comment|//    private void doTestCommaDelimitedListToStringArrayLegalMatch(String[] components) {
comment|//        StringBuffer sbuf = new StringBuffer();
comment|//        for (int i = 0; i< components.length; i++) {
comment|//            if (i != 0) {
comment|//                sbuf.append(",");
comment|//            }
comment|//            sbuf.append(components[i]);
comment|//        }
comment|//        String[] sa = Strings.commaDelimitedListToStringArray(sbuf.toString());
comment|//        assertTrue("String array isn't null with legal match", sa != null);
comment|//        assertEquals("String array length is correct with legal match", components.length, sa.length);
comment|//        assertTrue("Output equals input", Arrays.equals(sa, components));
comment|//    }
comment|//
comment|//    @Test public void testEndsWithIgnoreCase() {
comment|//        String suffix = "fOo";
comment|//        assertTrue(Strings.endsWithIgnoreCase("foo", suffix));
comment|//        assertTrue(Strings.endsWithIgnoreCase("Foo", suffix));
comment|//        assertTrue(Strings.endsWithIgnoreCase("barfoo", suffix));
comment|//        assertTrue(Strings.endsWithIgnoreCase("barbarfoo", suffix));
comment|//        assertTrue(Strings.endsWithIgnoreCase("barFoo", suffix));
comment|//        assertTrue(Strings.endsWithIgnoreCase("barBarFoo", suffix));
comment|//        assertTrue(Strings.endsWithIgnoreCase("barfoO", suffix));
comment|//        assertTrue(Strings.endsWithIgnoreCase("barFOO", suffix));
comment|//        assertTrue(Strings.endsWithIgnoreCase("barfOo", suffix));
comment|//        assertFalse(Strings.endsWithIgnoreCase(null, suffix));
comment|//        assertFalse(Strings.endsWithIgnoreCase("barfOo", null));
comment|//        assertFalse(Strings.endsWithIgnoreCase("b", suffix));
comment|//    }
comment|//
comment|//    @Test public void testParseLocaleStringSunnyDay() throws Exception {
comment|//        Locale expectedLocale = Locale.UK;
comment|//        Locale locale = Strings.parseLocaleString(expectedLocale.toString());
comment|//        assertNotNull("When given a bona-fide Locale string, must not return null.", locale);
comment|//        assertEquals(expectedLocale, locale);
comment|//    }
comment|//
comment|//    @Test public void testParseLocaleStringWithMalformedLocaleString() throws Exception {
comment|//        Locale locale = Strings.parseLocaleString("_banjo_on_my_knee");
comment|//        assertNotNull("When given a malformed Locale string, must not return null.", locale);
comment|//    }
comment|//
comment|//    @Test public void testParseLocaleStringWithEmptyLocaleStringYieldsNullLocale() throws Exception {
comment|//        Locale locale = Strings.parseLocaleString("");
comment|//        assertNull("When given an empty Locale string, must return null.", locale);
comment|//    }
comment|//
comment|//    @Test public void testParseLocaleWithMultiValuedVariant() throws Exception {
comment|//        final String variant = "proper_northern";
comment|//        final String localeString = "en_GB_" + variant;
comment|//        Locale locale = Strings.parseLocaleString(localeString);
comment|//        assertEquals("Multi-valued variant portion of the Locale not extracted correctly.", variant, locale.getVariant());
comment|//    }
comment|//
comment|//    @Test public void testParseLocaleWithMultiValuedVariantUsingSpacesAsSeparators() throws Exception {
comment|//        final String variant = "proper northern";
comment|//        final String localeString = "en GB " + variant;
comment|//        Locale locale = Strings.parseLocaleString(localeString);
comment|//        assertEquals("Multi-valued variant portion of the Locale not extracted correctly.", variant, locale.getVariant());
comment|//    }
comment|//
comment|//    @Test public void testParseLocaleWithMultiValuedVariantUsingMixtureOfUnderscoresAndSpacesAsSeparators() throws Exception {
comment|//        final String variant = "proper northern";
comment|//        final String localeString = "en_GB_" + variant;
comment|//        Locale locale = Strings.parseLocaleString(localeString);
comment|//        assertEquals("Multi-valued variant portion of the Locale not extracted correctly.", variant, locale.getVariant());
comment|//    }
comment|//
comment|//    @Test public void testParseLocaleWithMultiValuedVariantUsingSpacesAsSeparatorsWithLotsOfLeadingWhitespace() throws Exception {
comment|//        final String variant = "proper northern";
comment|//        final String localeString = "en GB            " + variant; // lots of whitespace
comment|//        Locale locale = Strings.parseLocaleString(localeString);
comment|//        assertEquals("Multi-valued variant portion of the Locale not extracted correctly.", variant, locale.getVariant());
comment|//    }
comment|//
comment|//    @Test public void testParseLocaleWithMultiValuedVariantUsingUnderscoresAsSeparatorsWithLotsOfLeadingWhitespace() throws Exception {
comment|//        final String variant = "proper_northern";
comment|//        final String localeString = "en_GB_____" + variant; // lots of underscores
comment|//        Locale locale = Strings.parseLocaleString(localeString);
comment|//        assertEquals("Multi-valued variant portion of the Locale not extracted correctly.", variant, locale.getVariant());
comment|//    }
block|}
end_class

end_unit

