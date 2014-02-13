begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.miscellaneous
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|miscellaneous
package|;
end_package

begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|TokenFilter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|TokenStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|tokenattributes
operator|.
name|CharTermAttribute
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|tokenattributes
operator|.
name|PositionIncrementAttribute
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|ArrayUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|RamUsageEstimator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Version
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|lucene
operator|.
name|Lucene
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * This class converts alphabetic, numeric, and symbolic Unicode characters  * which are not in the first 127 ASCII characters (the "Basic Latin" Unicode  * block) into their ASCII equivalents, if one exists.  *  * Characters from the following Unicode blocks are converted; however, only  * those characters with reasonable ASCII alternatives are converted:  *  *<ul>  *<li>C1 Controls and Latin-1 Supplement:<a href="http://www.unicode.org/charts/PDF/U0080.pdf">http://www.unicode.org/charts/PDF/U0080.pdf</a>  *<li>Latin Extended-A:<a href="http://www.unicode.org/charts/PDF/U0100.pdf">http://www.unicode.org/charts/PDF/U0100.pdf</a>  *<li>Latin Extended-B:<a href="http://www.unicode.org/charts/PDF/U0180.pdf">http://www.unicode.org/charts/PDF/U0180.pdf</a>  *<li>Latin Extended Additional:<a href="http://www.unicode.org/charts/PDF/U1E00.pdf">http://www.unicode.org/charts/PDF/U1E00.pdf</a>  *<li>Latin Extended-C:<a href="http://www.unicode.org/charts/PDF/U2C60.pdf">http://www.unicode.org/charts/PDF/U2C60.pdf</a>  *<li>Latin Extended-D:<a href="http://www.unicode.org/charts/PDF/UA720.pdf">http://www.unicode.org/charts/PDF/UA720.pdf</a>  *<li>IPA Extensions:<a href="http://www.unicode.org/charts/PDF/U0250.pdf">http://www.unicode.org/charts/PDF/U0250.pdf</a>  *<li>Phonetic Extensions:<a href="http://www.unicode.org/charts/PDF/U1D00.pdf">http://www.unicode.org/charts/PDF/U1D00.pdf</a>  *<li>Phonetic Extensions Supplement:<a href="http://www.unicode.org/charts/PDF/U1D80.pdf">http://www.unicode.org/charts/PDF/U1D80.pdf</a>  *<li>General Punctuation:<a href="http://www.unicode.org/charts/PDF/U2000.pdf">http://www.unicode.org/charts/PDF/U2000.pdf</a>  *<li>Superscripts and Subscripts:<a href="http://www.unicode.org/charts/PDF/U2070.pdf">http://www.unicode.org/charts/PDF/U2070.pdf</a>  *<li>Enclosed Alphanumerics:<a href="http://www.unicode.org/charts/PDF/U2460.pdf">http://www.unicode.org/charts/PDF/U2460.pdf</a>  *<li>Dingbats:<a href="http://www.unicode.org/charts/PDF/U2700.pdf">http://www.unicode.org/charts/PDF/U2700.pdf</a>  *<li>Supplemental Punctuation:<a href="http://www.unicode.org/charts/PDF/U2E00.pdf">http://www.unicode.org/charts/PDF/U2E00.pdf</a>  *<li>Alphabetic Presentation Forms:<a href="http://www.unicode.org/charts/PDF/UFB00.pdf">http://www.unicode.org/charts/PDF/UFB00.pdf</a>  *<li>Halfwidth and Fullwidth Forms:<a href="http://www.unicode.org/charts/PDF/UFF00.pdf">http://www.unicode.org/charts/PDF/UFF00.pdf</a>  *</ul>  *    * See:<a href="http://en.wikipedia.org/wiki/Latin_characters_in_Unicode">http://en.wikipedia.org/wiki/Latin_characters_in_Unicode</a>  *  * For example, '&agrave;' will be replaced by 'a'.  */
end_comment

begin_class
DECL|class|XASCIIFoldingFilter
specifier|public
specifier|final
class|class
name|XASCIIFoldingFilter
extends|extends
name|TokenFilter
block|{
static|static
block|{
comment|//Copied from LUCENE-5437.  Remove when upgrading Lucene.
assert|assert
name|Lucene
operator|.
name|VERSION
operator|==
name|Version
operator|.
name|LUCENE_46
operator|:
literal|"LUCENE-5437"
assert|;
block|}
DECL|field|termAtt
specifier|private
specifier|final
name|CharTermAttribute
name|termAtt
init|=
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|posIncAttr
specifier|private
specifier|final
name|PositionIncrementAttribute
name|posIncAttr
init|=
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|preserveOriginal
specifier|private
specifier|final
name|boolean
name|preserveOriginal
decl_stmt|;
DECL|field|output
specifier|private
name|char
index|[]
name|output
init|=
operator|new
name|char
index|[
literal|512
index|]
decl_stmt|;
DECL|field|outputPos
specifier|private
name|int
name|outputPos
decl_stmt|;
DECL|field|state
specifier|private
name|State
name|state
decl_stmt|;
DECL|method|XASCIIFoldingFilter
specifier|public
name|XASCIIFoldingFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
name|this
argument_list|(
name|input
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a new {@link ASCIIFoldingFilter}.    *     * @param in    *          TokenStream to filter    * @param preserveOriginal    *          should the original tokens be kept on the input stream with a 0 position increment    *          from the folded tokens?    **/
DECL|method|XASCIIFoldingFilter
specifier|public
name|XASCIIFoldingFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|,
name|boolean
name|preserveOriginal
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|this
operator|.
name|preserveOriginal
operator|=
name|preserveOriginal
expr_stmt|;
block|}
comment|/**    * Does the filter preserve the original tokens?    */
DECL|method|isPreserveOriginal
specifier|public
name|boolean
name|isPreserveOriginal
parameter_list|()
block|{
return|return
name|preserveOriginal
return|;
block|}
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|state
operator|!=
literal|null
condition|)
block|{
assert|assert
name|preserveOriginal
operator|:
literal|"state should only be captured if preserveOriginal is true"
assert|;
name|restoreState
argument_list|(
name|state
argument_list|)
expr_stmt|;
name|posIncAttr
operator|.
name|setPositionIncrement
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|state
operator|=
literal|null
expr_stmt|;
return|return
literal|true
return|;
block|}
if|if
condition|(
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
specifier|final
name|char
index|[]
name|buffer
init|=
name|termAtt
operator|.
name|buffer
argument_list|()
decl_stmt|;
specifier|final
name|int
name|length
init|=
name|termAtt
operator|.
name|length
argument_list|()
decl_stmt|;
comment|// If no characters actually require rewriting then we
comment|// just return token as-is:
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|length
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|char
name|c
init|=
name|buffer
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|c
operator|>=
literal|'\u0080'
condition|)
block|{
name|foldToASCII
argument_list|(
name|buffer
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|termAtt
operator|.
name|copyBuffer
argument_list|(
name|output
argument_list|,
literal|0
argument_list|,
name|outputPos
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|reset
argument_list|()
expr_stmt|;
name|state
operator|=
literal|null
expr_stmt|;
block|}
comment|/**    * Converts characters above ASCII to their ASCII equivalents.  For example,    * accents are removed from accented characters.    * @param input The string to fold    * @param length The number of characters in the input string    */
DECL|method|foldToASCII
specifier|public
name|void
name|foldToASCII
parameter_list|(
name|char
index|[]
name|input
parameter_list|,
name|int
name|length
parameter_list|)
block|{
if|if
condition|(
name|preserveOriginal
condition|)
block|{
name|state
operator|=
name|captureState
argument_list|()
expr_stmt|;
block|}
comment|// Worst-case length required:
specifier|final
name|int
name|maxSizeNeeded
init|=
literal|4
operator|*
name|length
decl_stmt|;
if|if
condition|(
name|output
operator|.
name|length
operator|<
name|maxSizeNeeded
condition|)
block|{
name|output
operator|=
operator|new
name|char
index|[
name|ArrayUtil
operator|.
name|oversize
argument_list|(
name|maxSizeNeeded
argument_list|,
name|RamUsageEstimator
operator|.
name|NUM_BYTES_CHAR
argument_list|)
index|]
expr_stmt|;
block|}
name|outputPos
operator|=
name|foldToASCII
argument_list|(
name|input
argument_list|,
literal|0
argument_list|,
name|output
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
comment|/**    * Converts characters above ASCII to their ASCII equivalents.  For example,    * accents are removed from accented characters.    * @param input     The characters to fold    * @param inputPos  Index of the first character to fold    * @param output    The result of the folding. Should be of size>= {@code length * 4}.    * @param outputPos Index of output where to put the result of the folding    * @param length    The number of characters to fold    * @return length of output    * @lucene.internal    */
DECL|method|foldToASCII
specifier|public
specifier|static
specifier|final
name|int
name|foldToASCII
parameter_list|(
name|char
name|input
index|[]
parameter_list|,
name|int
name|inputPos
parameter_list|,
name|char
name|output
index|[]
parameter_list|,
name|int
name|outputPos
parameter_list|,
name|int
name|length
parameter_list|)
block|{
specifier|final
name|int
name|end
init|=
name|inputPos
operator|+
name|length
decl_stmt|;
for|for
control|(
name|int
name|pos
init|=
name|inputPos
init|;
name|pos
operator|<
name|end
condition|;
operator|++
name|pos
control|)
block|{
specifier|final
name|char
name|c
init|=
name|input
index|[
name|pos
index|]
decl_stmt|;
comment|// Quick test: if it's not in range then just keep current character
if|if
condition|(
name|c
operator|<
literal|'\u0080'
condition|)
block|{
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
name|c
expr_stmt|;
block|}
else|else
block|{
switch|switch
condition|(
name|c
condition|)
block|{
case|case
literal|'\u00C0'
case|:
comment|// Ã  [LATIN CAPITAL LETTER A WITH GRAVE]
case|case
literal|'\u00C1'
case|:
comment|// Ã  [LATIN CAPITAL LETTER A WITH ACUTE]
case|case
literal|'\u00C2'
case|:
comment|// Ã  [LATIN CAPITAL LETTER A WITH CIRCUMFLEX]
case|case
literal|'\u00C3'
case|:
comment|// Ã  [LATIN CAPITAL LETTER A WITH TILDE]
case|case
literal|'\u00C4'
case|:
comment|// Ã  [LATIN CAPITAL LETTER A WITH DIAERESIS]
case|case
literal|'\u00C5'
case|:
comment|// Ã  [LATIN CAPITAL LETTER A WITH RING ABOVE]
case|case
literal|'\u0100'
case|:
comment|// Ä  [LATIN CAPITAL LETTER A WITH MACRON]
case|case
literal|'\u0102'
case|:
comment|// Ä  [LATIN CAPITAL LETTER A WITH BREVE]
case|case
literal|'\u0104'
case|:
comment|// Ä  [LATIN CAPITAL LETTER A WITH OGONEK]
case|case
literal|'\u018F'
case|:
comment|// Æ  http://en.wikipedia.org/wiki/Schwa  [LATIN CAPITAL LETTER SCHWA]
case|case
literal|'\u01CD'
case|:
comment|// Ç  [LATIN CAPITAL LETTER A WITH CARON]
case|case
literal|'\u01DE'
case|:
comment|// Ç  [LATIN CAPITAL LETTER A WITH DIAERESIS AND MACRON]
case|case
literal|'\u01E0'
case|:
comment|// Ç   [LATIN CAPITAL LETTER A WITH DOT ABOVE AND MACRON]
case|case
literal|'\u01FA'
case|:
comment|// Çº  [LATIN CAPITAL LETTER A WITH RING ABOVE AND ACUTE]
case|case
literal|'\u0200'
case|:
comment|// È  [LATIN CAPITAL LETTER A WITH DOUBLE GRAVE]
case|case
literal|'\u0202'
case|:
comment|// È  [LATIN CAPITAL LETTER A WITH INVERTED BREVE]
case|case
literal|'\u0226'
case|:
comment|// È¦  [LATIN CAPITAL LETTER A WITH DOT ABOVE]
case|case
literal|'\u023A'
case|:
comment|// Èº  [LATIN CAPITAL LETTER A WITH STROKE]
case|case
literal|'\u1D00'
case|:
comment|// á´  [LATIN LETTER SMALL CAPITAL A]
case|case
literal|'\u1E00'
case|:
comment|// á¸  [LATIN CAPITAL LETTER A WITH RING BELOW]
case|case
literal|'\u1EA0'
case|:
comment|// áº   [LATIN CAPITAL LETTER A WITH DOT BELOW]
case|case
literal|'\u1EA2'
case|:
comment|// áº¢  [LATIN CAPITAL LETTER A WITH HOOK ABOVE]
case|case
literal|'\u1EA4'
case|:
comment|// áº¤  [LATIN CAPITAL LETTER A WITH CIRCUMFLEX AND ACUTE]
case|case
literal|'\u1EA6'
case|:
comment|// áº¦  [LATIN CAPITAL LETTER A WITH CIRCUMFLEX AND GRAVE]
case|case
literal|'\u1EA8'
case|:
comment|// áº¨  [LATIN CAPITAL LETTER A WITH CIRCUMFLEX AND HOOK ABOVE]
case|case
literal|'\u1EAA'
case|:
comment|// áºª  [LATIN CAPITAL LETTER A WITH CIRCUMFLEX AND TILDE]
case|case
literal|'\u1EAC'
case|:
comment|// áº¬  [LATIN CAPITAL LETTER A WITH CIRCUMFLEX AND DOT BELOW]
case|case
literal|'\u1EAE'
case|:
comment|// áº®  [LATIN CAPITAL LETTER A WITH BREVE AND ACUTE]
case|case
literal|'\u1EB0'
case|:
comment|// áº°  [LATIN CAPITAL LETTER A WITH BREVE AND GRAVE]
case|case
literal|'\u1EB2'
case|:
comment|// áº²  [LATIN CAPITAL LETTER A WITH BREVE AND HOOK ABOVE]
case|case
literal|'\u1EB4'
case|:
comment|// áº´  [LATIN CAPITAL LETTER A WITH BREVE AND TILDE]
case|case
literal|'\u1EB6'
case|:
comment|// áº¶  [LATIN CAPITAL LETTER A WITH BREVE AND DOT BELOW]
case|case
literal|'\u24B6'
case|:
comment|// â¶  [CIRCLED LATIN CAPITAL LETTER A]
case|case
literal|'\uFF21'
case|:
comment|// ï¼¡  [FULLWIDTH LATIN CAPITAL LETTER A]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'A'
expr_stmt|;
break|break;
case|case
literal|'\u00E0'
case|:
comment|// Ã   [LATIN SMALL LETTER A WITH GRAVE]
case|case
literal|'\u00E1'
case|:
comment|// Ã¡  [LATIN SMALL LETTER A WITH ACUTE]
case|case
literal|'\u00E2'
case|:
comment|// Ã¢  [LATIN SMALL LETTER A WITH CIRCUMFLEX]
case|case
literal|'\u00E3'
case|:
comment|// Ã£  [LATIN SMALL LETTER A WITH TILDE]
case|case
literal|'\u00E4'
case|:
comment|// Ã¤  [LATIN SMALL LETTER A WITH DIAERESIS]
case|case
literal|'\u00E5'
case|:
comment|// Ã¥  [LATIN SMALL LETTER A WITH RING ABOVE]
case|case
literal|'\u0101'
case|:
comment|// Ä  [LATIN SMALL LETTER A WITH MACRON]
case|case
literal|'\u0103'
case|:
comment|// Ä  [LATIN SMALL LETTER A WITH BREVE]
case|case
literal|'\u0105'
case|:
comment|// Ä  [LATIN SMALL LETTER A WITH OGONEK]
case|case
literal|'\u01CE'
case|:
comment|// Ç  [LATIN SMALL LETTER A WITH CARON]
case|case
literal|'\u01DF'
case|:
comment|// Ç  [LATIN SMALL LETTER A WITH DIAERESIS AND MACRON]
case|case
literal|'\u01E1'
case|:
comment|// Ç¡  [LATIN SMALL LETTER A WITH DOT ABOVE AND MACRON]
case|case
literal|'\u01FB'
case|:
comment|// Ç»  [LATIN SMALL LETTER A WITH RING ABOVE AND ACUTE]
case|case
literal|'\u0201'
case|:
comment|// È  [LATIN SMALL LETTER A WITH DOUBLE GRAVE]
case|case
literal|'\u0203'
case|:
comment|// È  [LATIN SMALL LETTER A WITH INVERTED BREVE]
case|case
literal|'\u0227'
case|:
comment|// È§  [LATIN SMALL LETTER A WITH DOT ABOVE]
case|case
literal|'\u0250'
case|:
comment|// É  [LATIN SMALL LETTER TURNED A]
case|case
literal|'\u0259'
case|:
comment|// É  [LATIN SMALL LETTER SCHWA]
case|case
literal|'\u025A'
case|:
comment|// É  [LATIN SMALL LETTER SCHWA WITH HOOK]
case|case
literal|'\u1D8F'
case|:
comment|// á¶  [LATIN SMALL LETTER A WITH RETROFLEX HOOK]
case|case
literal|'\u1D95'
case|:
comment|// á¶  [LATIN SMALL LETTER SCHWA WITH RETROFLEX HOOK]
case|case
literal|'\u1E01'
case|:
comment|// áº¡  [LATIN SMALL LETTER A WITH RING BELOW]
case|case
literal|'\u1E9A'
case|:
comment|// áº£  [LATIN SMALL LETTER A WITH RIGHT HALF RING]
case|case
literal|'\u1EA1'
case|:
comment|// áº¡  [LATIN SMALL LETTER A WITH DOT BELOW]
case|case
literal|'\u1EA3'
case|:
comment|// áº£  [LATIN SMALL LETTER A WITH HOOK ABOVE]
case|case
literal|'\u1EA5'
case|:
comment|// áº¥  [LATIN SMALL LETTER A WITH CIRCUMFLEX AND ACUTE]
case|case
literal|'\u1EA7'
case|:
comment|// áº§  [LATIN SMALL LETTER A WITH CIRCUMFLEX AND GRAVE]
case|case
literal|'\u1EA9'
case|:
comment|// áº©  [LATIN SMALL LETTER A WITH CIRCUMFLEX AND HOOK ABOVE]
case|case
literal|'\u1EAB'
case|:
comment|// áº«  [LATIN SMALL LETTER A WITH CIRCUMFLEX AND TILDE]
case|case
literal|'\u1EAD'
case|:
comment|// áº­  [LATIN SMALL LETTER A WITH CIRCUMFLEX AND DOT BELOW]
case|case
literal|'\u1EAF'
case|:
comment|// áº¯  [LATIN SMALL LETTER A WITH BREVE AND ACUTE]
case|case
literal|'\u1EB1'
case|:
comment|// áº±  [LATIN SMALL LETTER A WITH BREVE AND GRAVE]
case|case
literal|'\u1EB3'
case|:
comment|// áº³  [LATIN SMALL LETTER A WITH BREVE AND HOOK ABOVE]
case|case
literal|'\u1EB5'
case|:
comment|// áºµ  [LATIN SMALL LETTER A WITH BREVE AND TILDE]
case|case
literal|'\u1EB7'
case|:
comment|// áº·  [LATIN SMALL LETTER A WITH BREVE AND DOT BELOW]
case|case
literal|'\u2090'
case|:
comment|// â  [LATIN SUBSCRIPT SMALL LETTER A]
case|case
literal|'\u2094'
case|:
comment|// â  [LATIN SUBSCRIPT SMALL LETTER SCHWA]
case|case
literal|'\u24D0'
case|:
comment|// â  [CIRCLED LATIN SMALL LETTER A]
case|case
literal|'\u2C65'
case|:
comment|// â±¥  [LATIN SMALL LETTER A WITH STROKE]
case|case
literal|'\u2C6F'
case|:
comment|// â±¯  [LATIN CAPITAL LETTER TURNED A]
case|case
literal|'\uFF41'
case|:
comment|// ï½  [FULLWIDTH LATIN SMALL LETTER A]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'a'
expr_stmt|;
break|break;
case|case
literal|'\uA732'
case|:
comment|// ê²  [LATIN CAPITAL LETTER AA]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'A'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'A'
expr_stmt|;
break|break;
case|case
literal|'\u00C6'
case|:
comment|// Ã  [LATIN CAPITAL LETTER AE]
case|case
literal|'\u01E2'
case|:
comment|// Ç¢  [LATIN CAPITAL LETTER AE WITH MACRON]
case|case
literal|'\u01FC'
case|:
comment|// Ç¼  [LATIN CAPITAL LETTER AE WITH ACUTE]
case|case
literal|'\u1D01'
case|:
comment|// á´  [LATIN LETTER SMALL CAPITAL AE]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'A'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'E'
expr_stmt|;
break|break;
case|case
literal|'\uA734'
case|:
comment|// ê´  [LATIN CAPITAL LETTER AO]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'A'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'O'
expr_stmt|;
break|break;
case|case
literal|'\uA736'
case|:
comment|// ê¶  [LATIN CAPITAL LETTER AU]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'A'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'U'
expr_stmt|;
break|break;
case|case
literal|'\uA738'
case|:
comment|// ê¸  [LATIN CAPITAL LETTER AV]
case|case
literal|'\uA73A'
case|:
comment|// êº  [LATIN CAPITAL LETTER AV WITH HORIZONTAL BAR]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'A'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'V'
expr_stmt|;
break|break;
case|case
literal|'\uA73C'
case|:
comment|// ê¼  [LATIN CAPITAL LETTER AY]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'A'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'Y'
expr_stmt|;
break|break;
case|case
literal|'\u249C'
case|:
comment|// â  [PARENTHESIZED LATIN SMALL LETTER A]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'('
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'a'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|')'
expr_stmt|;
break|break;
case|case
literal|'\uA733'
case|:
comment|// ê³  [LATIN SMALL LETTER AA]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'a'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'a'
expr_stmt|;
break|break;
case|case
literal|'\u00E6'
case|:
comment|// Ã¦  [LATIN SMALL LETTER AE]
case|case
literal|'\u01E3'
case|:
comment|// Ç£  [LATIN SMALL LETTER AE WITH MACRON]
case|case
literal|'\u01FD'
case|:
comment|// Ç½  [LATIN SMALL LETTER AE WITH ACUTE]
case|case
literal|'\u1D02'
case|:
comment|// á´  [LATIN SMALL LETTER TURNED AE]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'a'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'e'
expr_stmt|;
break|break;
case|case
literal|'\uA735'
case|:
comment|// êµ  [LATIN SMALL LETTER AO]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'a'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'o'
expr_stmt|;
break|break;
case|case
literal|'\uA737'
case|:
comment|// ê·  [LATIN SMALL LETTER AU]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'a'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'u'
expr_stmt|;
break|break;
case|case
literal|'\uA739'
case|:
comment|// ê¹  [LATIN SMALL LETTER AV]
case|case
literal|'\uA73B'
case|:
comment|// ê»  [LATIN SMALL LETTER AV WITH HORIZONTAL BAR]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'a'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'v'
expr_stmt|;
break|break;
case|case
literal|'\uA73D'
case|:
comment|// ê½  [LATIN SMALL LETTER AY]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'a'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'y'
expr_stmt|;
break|break;
case|case
literal|'\u0181'
case|:
comment|// Æ  [LATIN CAPITAL LETTER B WITH HOOK]
case|case
literal|'\u0182'
case|:
comment|// Æ  [LATIN CAPITAL LETTER B WITH TOPBAR]
case|case
literal|'\u0243'
case|:
comment|// É  [LATIN CAPITAL LETTER B WITH STROKE]
case|case
literal|'\u0299'
case|:
comment|// Ê  [LATIN LETTER SMALL CAPITAL B]
case|case
literal|'\u1D03'
case|:
comment|// á´  [LATIN LETTER SMALL CAPITAL BARRED B]
case|case
literal|'\u1E02'
case|:
comment|// á¸  [LATIN CAPITAL LETTER B WITH DOT ABOVE]
case|case
literal|'\u1E04'
case|:
comment|// á¸  [LATIN CAPITAL LETTER B WITH DOT BELOW]
case|case
literal|'\u1E06'
case|:
comment|// á¸  [LATIN CAPITAL LETTER B WITH LINE BELOW]
case|case
literal|'\u24B7'
case|:
comment|// â·  [CIRCLED LATIN CAPITAL LETTER B]
case|case
literal|'\uFF22'
case|:
comment|// ï¼¢  [FULLWIDTH LATIN CAPITAL LETTER B]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'B'
expr_stmt|;
break|break;
case|case
literal|'\u0180'
case|:
comment|// Æ  [LATIN SMALL LETTER B WITH STROKE]
case|case
literal|'\u0183'
case|:
comment|// Æ  [LATIN SMALL LETTER B WITH TOPBAR]
case|case
literal|'\u0253'
case|:
comment|// É  [LATIN SMALL LETTER B WITH HOOK]
case|case
literal|'\u1D6C'
case|:
comment|// áµ¬  [LATIN SMALL LETTER B WITH MIDDLE TILDE]
case|case
literal|'\u1D80'
case|:
comment|// á¶  [LATIN SMALL LETTER B WITH PALATAL HOOK]
case|case
literal|'\u1E03'
case|:
comment|// á¸  [LATIN SMALL LETTER B WITH DOT ABOVE]
case|case
literal|'\u1E05'
case|:
comment|// á¸  [LATIN SMALL LETTER B WITH DOT BELOW]
case|case
literal|'\u1E07'
case|:
comment|// á¸  [LATIN SMALL LETTER B WITH LINE BELOW]
case|case
literal|'\u24D1'
case|:
comment|// â  [CIRCLED LATIN SMALL LETTER B]
case|case
literal|'\uFF42'
case|:
comment|// ï½  [FULLWIDTH LATIN SMALL LETTER B]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'b'
expr_stmt|;
break|break;
case|case
literal|'\u249D'
case|:
comment|// â  [PARENTHESIZED LATIN SMALL LETTER B]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'('
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'b'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|')'
expr_stmt|;
break|break;
case|case
literal|'\u00C7'
case|:
comment|// Ã  [LATIN CAPITAL LETTER C WITH CEDILLA]
case|case
literal|'\u0106'
case|:
comment|// Ä  [LATIN CAPITAL LETTER C WITH ACUTE]
case|case
literal|'\u0108'
case|:
comment|// Ä  [LATIN CAPITAL LETTER C WITH CIRCUMFLEX]
case|case
literal|'\u010A'
case|:
comment|// Ä  [LATIN CAPITAL LETTER C WITH DOT ABOVE]
case|case
literal|'\u010C'
case|:
comment|// Ä  [LATIN CAPITAL LETTER C WITH CARON]
case|case
literal|'\u0187'
case|:
comment|// Æ  [LATIN CAPITAL LETTER C WITH HOOK]
case|case
literal|'\u023B'
case|:
comment|// È»  [LATIN CAPITAL LETTER C WITH STROKE]
case|case
literal|'\u0297'
case|:
comment|// Ê  [LATIN LETTER STRETCHED C]
case|case
literal|'\u1D04'
case|:
comment|// á´  [LATIN LETTER SMALL CAPITAL C]
case|case
literal|'\u1E08'
case|:
comment|// á¸  [LATIN CAPITAL LETTER C WITH CEDILLA AND ACUTE]
case|case
literal|'\u24B8'
case|:
comment|// â¸  [CIRCLED LATIN CAPITAL LETTER C]
case|case
literal|'\uFF23'
case|:
comment|// ï¼£  [FULLWIDTH LATIN CAPITAL LETTER C]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'C'
expr_stmt|;
break|break;
case|case
literal|'\u00E7'
case|:
comment|// Ã§  [LATIN SMALL LETTER C WITH CEDILLA]
case|case
literal|'\u0107'
case|:
comment|// Ä  [LATIN SMALL LETTER C WITH ACUTE]
case|case
literal|'\u0109'
case|:
comment|// Ä  [LATIN SMALL LETTER C WITH CIRCUMFLEX]
case|case
literal|'\u010B'
case|:
comment|// Ä  [LATIN SMALL LETTER C WITH DOT ABOVE]
case|case
literal|'\u010D'
case|:
comment|// Ä  [LATIN SMALL LETTER C WITH CARON]
case|case
literal|'\u0188'
case|:
comment|// Æ  [LATIN SMALL LETTER C WITH HOOK]
case|case
literal|'\u023C'
case|:
comment|// È¼  [LATIN SMALL LETTER C WITH STROKE]
case|case
literal|'\u0255'
case|:
comment|// É  [LATIN SMALL LETTER C WITH CURL]
case|case
literal|'\u1E09'
case|:
comment|// á¸  [LATIN SMALL LETTER C WITH CEDILLA AND ACUTE]
case|case
literal|'\u2184'
case|:
comment|// â  [LATIN SMALL LETTER REVERSED C]
case|case
literal|'\u24D2'
case|:
comment|// â  [CIRCLED LATIN SMALL LETTER C]
case|case
literal|'\uA73E'
case|:
comment|// ê¾  [LATIN CAPITAL LETTER REVERSED C WITH DOT]
case|case
literal|'\uA73F'
case|:
comment|// ê¿  [LATIN SMALL LETTER REVERSED C WITH DOT]
case|case
literal|'\uFF43'
case|:
comment|// ï½  [FULLWIDTH LATIN SMALL LETTER C]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'c'
expr_stmt|;
break|break;
case|case
literal|'\u249E'
case|:
comment|// â  [PARENTHESIZED LATIN SMALL LETTER C]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'('
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'c'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|')'
expr_stmt|;
break|break;
case|case
literal|'\u00D0'
case|:
comment|// Ã  [LATIN CAPITAL LETTER ETH]
case|case
literal|'\u010E'
case|:
comment|// Ä  [LATIN CAPITAL LETTER D WITH CARON]
case|case
literal|'\u0110'
case|:
comment|// Ä  [LATIN CAPITAL LETTER D WITH STROKE]
case|case
literal|'\u0189'
case|:
comment|// Æ  [LATIN CAPITAL LETTER AFRICAN D]
case|case
literal|'\u018A'
case|:
comment|// Æ  [LATIN CAPITAL LETTER D WITH HOOK]
case|case
literal|'\u018B'
case|:
comment|// Æ  [LATIN CAPITAL LETTER D WITH TOPBAR]
case|case
literal|'\u1D05'
case|:
comment|// á´  [LATIN LETTER SMALL CAPITAL D]
case|case
literal|'\u1D06'
case|:
comment|// á´  [LATIN LETTER SMALL CAPITAL ETH]
case|case
literal|'\u1E0A'
case|:
comment|// á¸  [LATIN CAPITAL LETTER D WITH DOT ABOVE]
case|case
literal|'\u1E0C'
case|:
comment|// á¸  [LATIN CAPITAL LETTER D WITH DOT BELOW]
case|case
literal|'\u1E0E'
case|:
comment|// á¸  [LATIN CAPITAL LETTER D WITH LINE BELOW]
case|case
literal|'\u1E10'
case|:
comment|// á¸  [LATIN CAPITAL LETTER D WITH CEDILLA]
case|case
literal|'\u1E12'
case|:
comment|// á¸  [LATIN CAPITAL LETTER D WITH CIRCUMFLEX BELOW]
case|case
literal|'\u24B9'
case|:
comment|// â¹  [CIRCLED LATIN CAPITAL LETTER D]
case|case
literal|'\uA779'
case|:
comment|// ê¹  [LATIN CAPITAL LETTER INSULAR D]
case|case
literal|'\uFF24'
case|:
comment|// ï¼¤  [FULLWIDTH LATIN CAPITAL LETTER D]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'D'
expr_stmt|;
break|break;
case|case
literal|'\u00F0'
case|:
comment|// Ã°  [LATIN SMALL LETTER ETH]
case|case
literal|'\u010F'
case|:
comment|// Ä  [LATIN SMALL LETTER D WITH CARON]
case|case
literal|'\u0111'
case|:
comment|// Ä  [LATIN SMALL LETTER D WITH STROKE]
case|case
literal|'\u018C'
case|:
comment|// Æ  [LATIN SMALL LETTER D WITH TOPBAR]
case|case
literal|'\u0221'
case|:
comment|// È¡  [LATIN SMALL LETTER D WITH CURL]
case|case
literal|'\u0256'
case|:
comment|// É  [LATIN SMALL LETTER D WITH TAIL]
case|case
literal|'\u0257'
case|:
comment|// É  [LATIN SMALL LETTER D WITH HOOK]
case|case
literal|'\u1D6D'
case|:
comment|// áµ­  [LATIN SMALL LETTER D WITH MIDDLE TILDE]
case|case
literal|'\u1D81'
case|:
comment|// á¶  [LATIN SMALL LETTER D WITH PALATAL HOOK]
case|case
literal|'\u1D91'
case|:
comment|// á¶  [LATIN SMALL LETTER D WITH HOOK AND TAIL]
case|case
literal|'\u1E0B'
case|:
comment|// á¸  [LATIN SMALL LETTER D WITH DOT ABOVE]
case|case
literal|'\u1E0D'
case|:
comment|// á¸  [LATIN SMALL LETTER D WITH DOT BELOW]
case|case
literal|'\u1E0F'
case|:
comment|// á¸  [LATIN SMALL LETTER D WITH LINE BELOW]
case|case
literal|'\u1E11'
case|:
comment|// á¸  [LATIN SMALL LETTER D WITH CEDILLA]
case|case
literal|'\u1E13'
case|:
comment|// á¸  [LATIN SMALL LETTER D WITH CIRCUMFLEX BELOW]
case|case
literal|'\u24D3'
case|:
comment|// â  [CIRCLED LATIN SMALL LETTER D]
case|case
literal|'\uA77A'
case|:
comment|// êº  [LATIN SMALL LETTER INSULAR D]
case|case
literal|'\uFF44'
case|:
comment|// ï½  [FULLWIDTH LATIN SMALL LETTER D]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'d'
expr_stmt|;
break|break;
case|case
literal|'\u01C4'
case|:
comment|// Ç  [LATIN CAPITAL LETTER DZ WITH CARON]
case|case
literal|'\u01F1'
case|:
comment|// Ç±  [LATIN CAPITAL LETTER DZ]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'D'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'Z'
expr_stmt|;
break|break;
case|case
literal|'\u01C5'
case|:
comment|// Ç  [LATIN CAPITAL LETTER D WITH SMALL LETTER Z WITH CARON]
case|case
literal|'\u01F2'
case|:
comment|// Ç²  [LATIN CAPITAL LETTER D WITH SMALL LETTER Z]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'D'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'z'
expr_stmt|;
break|break;
case|case
literal|'\u249F'
case|:
comment|// â  [PARENTHESIZED LATIN SMALL LETTER D]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'('
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'d'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|')'
expr_stmt|;
break|break;
case|case
literal|'\u0238'
case|:
comment|// È¸  [LATIN SMALL LETTER DB DIGRAPH]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'d'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'b'
expr_stmt|;
break|break;
case|case
literal|'\u01C6'
case|:
comment|// Ç  [LATIN SMALL LETTER DZ WITH CARON]
case|case
literal|'\u01F3'
case|:
comment|// Ç³  [LATIN SMALL LETTER DZ]
case|case
literal|'\u02A3'
case|:
comment|// Ê£  [LATIN SMALL LETTER DZ DIGRAPH]
case|case
literal|'\u02A5'
case|:
comment|// Ê¥  [LATIN SMALL LETTER DZ DIGRAPH WITH CURL]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'d'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'z'
expr_stmt|;
break|break;
case|case
literal|'\u00C8'
case|:
comment|// Ã  [LATIN CAPITAL LETTER E WITH GRAVE]
case|case
literal|'\u00C9'
case|:
comment|// Ã  [LATIN CAPITAL LETTER E WITH ACUTE]
case|case
literal|'\u00CA'
case|:
comment|// Ã  [LATIN CAPITAL LETTER E WITH CIRCUMFLEX]
case|case
literal|'\u00CB'
case|:
comment|// Ã  [LATIN CAPITAL LETTER E WITH DIAERESIS]
case|case
literal|'\u0112'
case|:
comment|// Ä  [LATIN CAPITAL LETTER E WITH MACRON]
case|case
literal|'\u0114'
case|:
comment|// Ä  [LATIN CAPITAL LETTER E WITH BREVE]
case|case
literal|'\u0116'
case|:
comment|// Ä  [LATIN CAPITAL LETTER E WITH DOT ABOVE]
case|case
literal|'\u0118'
case|:
comment|// Ä  [LATIN CAPITAL LETTER E WITH OGONEK]
case|case
literal|'\u011A'
case|:
comment|// Ä  [LATIN CAPITAL LETTER E WITH CARON]
case|case
literal|'\u018E'
case|:
comment|// Æ  [LATIN CAPITAL LETTER REVERSED E]
case|case
literal|'\u0190'
case|:
comment|// Æ  [LATIN CAPITAL LETTER OPEN E]
case|case
literal|'\u0204'
case|:
comment|// È  [LATIN CAPITAL LETTER E WITH DOUBLE GRAVE]
case|case
literal|'\u0206'
case|:
comment|// È  [LATIN CAPITAL LETTER E WITH INVERTED BREVE]
case|case
literal|'\u0228'
case|:
comment|// È¨  [LATIN CAPITAL LETTER E WITH CEDILLA]
case|case
literal|'\u0246'
case|:
comment|// É  [LATIN CAPITAL LETTER E WITH STROKE]
case|case
literal|'\u1D07'
case|:
comment|// á´  [LATIN LETTER SMALL CAPITAL E]
case|case
literal|'\u1E14'
case|:
comment|// á¸  [LATIN CAPITAL LETTER E WITH MACRON AND GRAVE]
case|case
literal|'\u1E16'
case|:
comment|// á¸  [LATIN CAPITAL LETTER E WITH MACRON AND ACUTE]
case|case
literal|'\u1E18'
case|:
comment|// á¸  [LATIN CAPITAL LETTER E WITH CIRCUMFLEX BELOW]
case|case
literal|'\u1E1A'
case|:
comment|// á¸  [LATIN CAPITAL LETTER E WITH TILDE BELOW]
case|case
literal|'\u1E1C'
case|:
comment|// á¸  [LATIN CAPITAL LETTER E WITH CEDILLA AND BREVE]
case|case
literal|'\u1EB8'
case|:
comment|// áº¸  [LATIN CAPITAL LETTER E WITH DOT BELOW]
case|case
literal|'\u1EBA'
case|:
comment|// áºº  [LATIN CAPITAL LETTER E WITH HOOK ABOVE]
case|case
literal|'\u1EBC'
case|:
comment|// áº¼  [LATIN CAPITAL LETTER E WITH TILDE]
case|case
literal|'\u1EBE'
case|:
comment|// áº¾  [LATIN CAPITAL LETTER E WITH CIRCUMFLEX AND ACUTE]
case|case
literal|'\u1EC0'
case|:
comment|// á»  [LATIN CAPITAL LETTER E WITH CIRCUMFLEX AND GRAVE]
case|case
literal|'\u1EC2'
case|:
comment|// á»  [LATIN CAPITAL LETTER E WITH CIRCUMFLEX AND HOOK ABOVE]
case|case
literal|'\u1EC4'
case|:
comment|// á»  [LATIN CAPITAL LETTER E WITH CIRCUMFLEX AND TILDE]
case|case
literal|'\u1EC6'
case|:
comment|// á»  [LATIN CAPITAL LETTER E WITH CIRCUMFLEX AND DOT BELOW]
case|case
literal|'\u24BA'
case|:
comment|// âº  [CIRCLED LATIN CAPITAL LETTER E]
case|case
literal|'\u2C7B'
case|:
comment|// â±»  [LATIN LETTER SMALL CAPITAL TURNED E]
case|case
literal|'\uFF25'
case|:
comment|// ï¼¥  [FULLWIDTH LATIN CAPITAL LETTER E]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'E'
expr_stmt|;
break|break;
case|case
literal|'\u00E8'
case|:
comment|// Ã¨  [LATIN SMALL LETTER E WITH GRAVE]
case|case
literal|'\u00E9'
case|:
comment|// Ã©  [LATIN SMALL LETTER E WITH ACUTE]
case|case
literal|'\u00EA'
case|:
comment|// Ãª  [LATIN SMALL LETTER E WITH CIRCUMFLEX]
case|case
literal|'\u00EB'
case|:
comment|// Ã«  [LATIN SMALL LETTER E WITH DIAERESIS]
case|case
literal|'\u0113'
case|:
comment|// Ä  [LATIN SMALL LETTER E WITH MACRON]
case|case
literal|'\u0115'
case|:
comment|// Ä  [LATIN SMALL LETTER E WITH BREVE]
case|case
literal|'\u0117'
case|:
comment|// Ä  [LATIN SMALL LETTER E WITH DOT ABOVE]
case|case
literal|'\u0119'
case|:
comment|// Ä  [LATIN SMALL LETTER E WITH OGONEK]
case|case
literal|'\u011B'
case|:
comment|// Ä  [LATIN SMALL LETTER E WITH CARON]
case|case
literal|'\u01DD'
case|:
comment|// Ç  [LATIN SMALL LETTER TURNED E]
case|case
literal|'\u0205'
case|:
comment|// È  [LATIN SMALL LETTER E WITH DOUBLE GRAVE]
case|case
literal|'\u0207'
case|:
comment|// È  [LATIN SMALL LETTER E WITH INVERTED BREVE]
case|case
literal|'\u0229'
case|:
comment|// È©  [LATIN SMALL LETTER E WITH CEDILLA]
case|case
literal|'\u0247'
case|:
comment|// É  [LATIN SMALL LETTER E WITH STROKE]
case|case
literal|'\u0258'
case|:
comment|// É  [LATIN SMALL LETTER REVERSED E]
case|case
literal|'\u025B'
case|:
comment|// É  [LATIN SMALL LETTER OPEN E]
case|case
literal|'\u025C'
case|:
comment|// É  [LATIN SMALL LETTER REVERSED OPEN E]
case|case
literal|'\u025D'
case|:
comment|// É  [LATIN SMALL LETTER REVERSED OPEN E WITH HOOK]
case|case
literal|'\u025E'
case|:
comment|// É  [LATIN SMALL LETTER CLOSED REVERSED OPEN E]
case|case
literal|'\u029A'
case|:
comment|// Ê  [LATIN SMALL LETTER CLOSED OPEN E]
case|case
literal|'\u1D08'
case|:
comment|// á´  [LATIN SMALL LETTER TURNED OPEN E]
case|case
literal|'\u1D92'
case|:
comment|// á¶  [LATIN SMALL LETTER E WITH RETROFLEX HOOK]
case|case
literal|'\u1D93'
case|:
comment|// á¶  [LATIN SMALL LETTER OPEN E WITH RETROFLEX HOOK]
case|case
literal|'\u1D94'
case|:
comment|// á¶  [LATIN SMALL LETTER REVERSED OPEN E WITH RETROFLEX HOOK]
case|case
literal|'\u1E15'
case|:
comment|// á¸  [LATIN SMALL LETTER E WITH MACRON AND GRAVE]
case|case
literal|'\u1E17'
case|:
comment|// á¸  [LATIN SMALL LETTER E WITH MACRON AND ACUTE]
case|case
literal|'\u1E19'
case|:
comment|// á¸  [LATIN SMALL LETTER E WITH CIRCUMFLEX BELOW]
case|case
literal|'\u1E1B'
case|:
comment|// á¸  [LATIN SMALL LETTER E WITH TILDE BELOW]
case|case
literal|'\u1E1D'
case|:
comment|// á¸  [LATIN SMALL LETTER E WITH CEDILLA AND BREVE]
case|case
literal|'\u1EB9'
case|:
comment|// áº¹  [LATIN SMALL LETTER E WITH DOT BELOW]
case|case
literal|'\u1EBB'
case|:
comment|// áº»  [LATIN SMALL LETTER E WITH HOOK ABOVE]
case|case
literal|'\u1EBD'
case|:
comment|// áº½  [LATIN SMALL LETTER E WITH TILDE]
case|case
literal|'\u1EBF'
case|:
comment|// áº¿  [LATIN SMALL LETTER E WITH CIRCUMFLEX AND ACUTE]
case|case
literal|'\u1EC1'
case|:
comment|// á»  [LATIN SMALL LETTER E WITH CIRCUMFLEX AND GRAVE]
case|case
literal|'\u1EC3'
case|:
comment|// á»  [LATIN SMALL LETTER E WITH CIRCUMFLEX AND HOOK ABOVE]
case|case
literal|'\u1EC5'
case|:
comment|// á»  [LATIN SMALL LETTER E WITH CIRCUMFLEX AND TILDE]
case|case
literal|'\u1EC7'
case|:
comment|// á»  [LATIN SMALL LETTER E WITH CIRCUMFLEX AND DOT BELOW]
case|case
literal|'\u2091'
case|:
comment|// â  [LATIN SUBSCRIPT SMALL LETTER E]
case|case
literal|'\u24D4'
case|:
comment|// â  [CIRCLED LATIN SMALL LETTER E]
case|case
literal|'\u2C78'
case|:
comment|// â±¸  [LATIN SMALL LETTER E WITH NOTCH]
case|case
literal|'\uFF45'
case|:
comment|// ï½  [FULLWIDTH LATIN SMALL LETTER E]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'e'
expr_stmt|;
break|break;
case|case
literal|'\u24A0'
case|:
comment|// â   [PARENTHESIZED LATIN SMALL LETTER E]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'('
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'e'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|')'
expr_stmt|;
break|break;
case|case
literal|'\u0191'
case|:
comment|// Æ  [LATIN CAPITAL LETTER F WITH HOOK]
case|case
literal|'\u1E1E'
case|:
comment|// á¸  [LATIN CAPITAL LETTER F WITH DOT ABOVE]
case|case
literal|'\u24BB'
case|:
comment|// â»  [CIRCLED LATIN CAPITAL LETTER F]
case|case
literal|'\uA730'
case|:
comment|// ê°  [LATIN LETTER SMALL CAPITAL F]
case|case
literal|'\uA77B'
case|:
comment|// ê»  [LATIN CAPITAL LETTER INSULAR F]
case|case
literal|'\uA7FB'
case|:
comment|// ê»  [LATIN EPIGRAPHIC LETTER REVERSED F]
case|case
literal|'\uFF26'
case|:
comment|// ï¼¦  [FULLWIDTH LATIN CAPITAL LETTER F]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'F'
expr_stmt|;
break|break;
case|case
literal|'\u0192'
case|:
comment|// Æ  [LATIN SMALL LETTER F WITH HOOK]
case|case
literal|'\u1D6E'
case|:
comment|// áµ®  [LATIN SMALL LETTER F WITH MIDDLE TILDE]
case|case
literal|'\u1D82'
case|:
comment|// á¶  [LATIN SMALL LETTER F WITH PALATAL HOOK]
case|case
literal|'\u1E1F'
case|:
comment|// á¸  [LATIN SMALL LETTER F WITH DOT ABOVE]
case|case
literal|'\u1E9B'
case|:
comment|// áº  [LATIN SMALL LETTER LONG S WITH DOT ABOVE]
case|case
literal|'\u24D5'
case|:
comment|// â  [CIRCLED LATIN SMALL LETTER F]
case|case
literal|'\uA77C'
case|:
comment|// ê¼  [LATIN SMALL LETTER INSULAR F]
case|case
literal|'\uFF46'
case|:
comment|// ï½  [FULLWIDTH LATIN SMALL LETTER F]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'f'
expr_stmt|;
break|break;
case|case
literal|'\u24A1'
case|:
comment|// â¡  [PARENTHESIZED LATIN SMALL LETTER F]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'('
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'f'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|')'
expr_stmt|;
break|break;
case|case
literal|'\uFB00'
case|:
comment|// ï¬  [LATIN SMALL LIGATURE FF]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'f'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'f'
expr_stmt|;
break|break;
case|case
literal|'\uFB03'
case|:
comment|// ï¬  [LATIN SMALL LIGATURE FFI]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'f'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'f'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'i'
expr_stmt|;
break|break;
case|case
literal|'\uFB04'
case|:
comment|// ï¬  [LATIN SMALL LIGATURE FFL]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'f'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'f'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'l'
expr_stmt|;
break|break;
case|case
literal|'\uFB01'
case|:
comment|// ï¬  [LATIN SMALL LIGATURE FI]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'f'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'i'
expr_stmt|;
break|break;
case|case
literal|'\uFB02'
case|:
comment|// ï¬  [LATIN SMALL LIGATURE FL]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'f'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'l'
expr_stmt|;
break|break;
case|case
literal|'\u011C'
case|:
comment|// Ä  [LATIN CAPITAL LETTER G WITH CIRCUMFLEX]
case|case
literal|'\u011E'
case|:
comment|// Ä  [LATIN CAPITAL LETTER G WITH BREVE]
case|case
literal|'\u0120'
case|:
comment|// Ä   [LATIN CAPITAL LETTER G WITH DOT ABOVE]
case|case
literal|'\u0122'
case|:
comment|// Ä¢  [LATIN CAPITAL LETTER G WITH CEDILLA]
case|case
literal|'\u0193'
case|:
comment|// Æ  [LATIN CAPITAL LETTER G WITH HOOK]
case|case
literal|'\u01E4'
case|:
comment|// Ç¤  [LATIN CAPITAL LETTER G WITH STROKE]
case|case
literal|'\u01E5'
case|:
comment|// Ç¥  [LATIN SMALL LETTER G WITH STROKE]
case|case
literal|'\u01E6'
case|:
comment|// Ç¦  [LATIN CAPITAL LETTER G WITH CARON]
case|case
literal|'\u01E7'
case|:
comment|// Ç§  [LATIN SMALL LETTER G WITH CARON]
case|case
literal|'\u01F4'
case|:
comment|// Ç´  [LATIN CAPITAL LETTER G WITH ACUTE]
case|case
literal|'\u0262'
case|:
comment|// É¢  [LATIN LETTER SMALL CAPITAL G]
case|case
literal|'\u029B'
case|:
comment|// Ê  [LATIN LETTER SMALL CAPITAL G WITH HOOK]
case|case
literal|'\u1E20'
case|:
comment|// á¸   [LATIN CAPITAL LETTER G WITH MACRON]
case|case
literal|'\u24BC'
case|:
comment|// â¼  [CIRCLED LATIN CAPITAL LETTER G]
case|case
literal|'\uA77D'
case|:
comment|// ê½  [LATIN CAPITAL LETTER INSULAR G]
case|case
literal|'\uA77E'
case|:
comment|// ê¾  [LATIN CAPITAL LETTER TURNED INSULAR G]
case|case
literal|'\uFF27'
case|:
comment|// ï¼§  [FULLWIDTH LATIN CAPITAL LETTER G]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'G'
expr_stmt|;
break|break;
case|case
literal|'\u011D'
case|:
comment|// Ä  [LATIN SMALL LETTER G WITH CIRCUMFLEX]
case|case
literal|'\u011F'
case|:
comment|// Ä  [LATIN SMALL LETTER G WITH BREVE]
case|case
literal|'\u0121'
case|:
comment|// Ä¡  [LATIN SMALL LETTER G WITH DOT ABOVE]
case|case
literal|'\u0123'
case|:
comment|// Ä£  [LATIN SMALL LETTER G WITH CEDILLA]
case|case
literal|'\u01F5'
case|:
comment|// Çµ  [LATIN SMALL LETTER G WITH ACUTE]
case|case
literal|'\u0260'
case|:
comment|// É   [LATIN SMALL LETTER G WITH HOOK]
case|case
literal|'\u0261'
case|:
comment|// É¡  [LATIN SMALL LETTER SCRIPT G]
case|case
literal|'\u1D77'
case|:
comment|// áµ·  [LATIN SMALL LETTER TURNED G]
case|case
literal|'\u1D79'
case|:
comment|// áµ¹  [LATIN SMALL LETTER INSULAR G]
case|case
literal|'\u1D83'
case|:
comment|// á¶  [LATIN SMALL LETTER G WITH PALATAL HOOK]
case|case
literal|'\u1E21'
case|:
comment|// á¸¡  [LATIN SMALL LETTER G WITH MACRON]
case|case
literal|'\u24D6'
case|:
comment|// â  [CIRCLED LATIN SMALL LETTER G]
case|case
literal|'\uA77F'
case|:
comment|// ê¿  [LATIN SMALL LETTER TURNED INSULAR G]
case|case
literal|'\uFF47'
case|:
comment|// ï½  [FULLWIDTH LATIN SMALL LETTER G]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'g'
expr_stmt|;
break|break;
case|case
literal|'\u24A2'
case|:
comment|// â¢  [PARENTHESIZED LATIN SMALL LETTER G]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'('
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'g'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|')'
expr_stmt|;
break|break;
case|case
literal|'\u0124'
case|:
comment|// Ä¤  [LATIN CAPITAL LETTER H WITH CIRCUMFLEX]
case|case
literal|'\u0126'
case|:
comment|// Ä¦  [LATIN CAPITAL LETTER H WITH STROKE]
case|case
literal|'\u021E'
case|:
comment|// È  [LATIN CAPITAL LETTER H WITH CARON]
case|case
literal|'\u029C'
case|:
comment|// Ê  [LATIN LETTER SMALL CAPITAL H]
case|case
literal|'\u1E22'
case|:
comment|// á¸¢  [LATIN CAPITAL LETTER H WITH DOT ABOVE]
case|case
literal|'\u1E24'
case|:
comment|// á¸¤  [LATIN CAPITAL LETTER H WITH DOT BELOW]
case|case
literal|'\u1E26'
case|:
comment|// á¸¦  [LATIN CAPITAL LETTER H WITH DIAERESIS]
case|case
literal|'\u1E28'
case|:
comment|// á¸¨  [LATIN CAPITAL LETTER H WITH CEDILLA]
case|case
literal|'\u1E2A'
case|:
comment|// á¸ª  [LATIN CAPITAL LETTER H WITH BREVE BELOW]
case|case
literal|'\u24BD'
case|:
comment|// â½  [CIRCLED LATIN CAPITAL LETTER H]
case|case
literal|'\u2C67'
case|:
comment|// â±§  [LATIN CAPITAL LETTER H WITH DESCENDER]
case|case
literal|'\u2C75'
case|:
comment|// â±µ  [LATIN CAPITAL LETTER HALF H]
case|case
literal|'\uFF28'
case|:
comment|// ï¼¨  [FULLWIDTH LATIN CAPITAL LETTER H]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'H'
expr_stmt|;
break|break;
case|case
literal|'\u0125'
case|:
comment|// Ä¥  [LATIN SMALL LETTER H WITH CIRCUMFLEX]
case|case
literal|'\u0127'
case|:
comment|// Ä§  [LATIN SMALL LETTER H WITH STROKE]
case|case
literal|'\u021F'
case|:
comment|// È  [LATIN SMALL LETTER H WITH CARON]
case|case
literal|'\u0265'
case|:
comment|// É¥  [LATIN SMALL LETTER TURNED H]
case|case
literal|'\u0266'
case|:
comment|// É¦  [LATIN SMALL LETTER H WITH HOOK]
case|case
literal|'\u02AE'
case|:
comment|// Ê®  [LATIN SMALL LETTER TURNED H WITH FISHHOOK]
case|case
literal|'\u02AF'
case|:
comment|// Ê¯  [LATIN SMALL LETTER TURNED H WITH FISHHOOK AND TAIL]
case|case
literal|'\u1E23'
case|:
comment|// á¸£  [LATIN SMALL LETTER H WITH DOT ABOVE]
case|case
literal|'\u1E25'
case|:
comment|// á¸¥  [LATIN SMALL LETTER H WITH DOT BELOW]
case|case
literal|'\u1E27'
case|:
comment|// á¸§  [LATIN SMALL LETTER H WITH DIAERESIS]
case|case
literal|'\u1E29'
case|:
comment|// á¸©  [LATIN SMALL LETTER H WITH CEDILLA]
case|case
literal|'\u1E2B'
case|:
comment|// á¸«  [LATIN SMALL LETTER H WITH BREVE BELOW]
case|case
literal|'\u1E96'
case|:
comment|// áº  [LATIN SMALL LETTER H WITH LINE BELOW]
case|case
literal|'\u24D7'
case|:
comment|// â  [CIRCLED LATIN SMALL LETTER H]
case|case
literal|'\u2C68'
case|:
comment|// â±¨  [LATIN SMALL LETTER H WITH DESCENDER]
case|case
literal|'\u2C76'
case|:
comment|// â±¶  [LATIN SMALL LETTER HALF H]
case|case
literal|'\uFF48'
case|:
comment|// ï½  [FULLWIDTH LATIN SMALL LETTER H]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'h'
expr_stmt|;
break|break;
case|case
literal|'\u01F6'
case|:
comment|// Ç¶  http://en.wikipedia.org/wiki/Hwair  [LATIN CAPITAL LETTER HWAIR]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'H'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'V'
expr_stmt|;
break|break;
case|case
literal|'\u24A3'
case|:
comment|// â£  [PARENTHESIZED LATIN SMALL LETTER H]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'('
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'h'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|')'
expr_stmt|;
break|break;
case|case
literal|'\u0195'
case|:
comment|// Æ  [LATIN SMALL LETTER HV]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'h'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'v'
expr_stmt|;
break|break;
case|case
literal|'\u00CC'
case|:
comment|// Ã  [LATIN CAPITAL LETTER I WITH GRAVE]
case|case
literal|'\u00CD'
case|:
comment|// Ã  [LATIN CAPITAL LETTER I WITH ACUTE]
case|case
literal|'\u00CE'
case|:
comment|// Ã  [LATIN CAPITAL LETTER I WITH CIRCUMFLEX]
case|case
literal|'\u00CF'
case|:
comment|// Ã  [LATIN CAPITAL LETTER I WITH DIAERESIS]
case|case
literal|'\u0128'
case|:
comment|// Ä¨  [LATIN CAPITAL LETTER I WITH TILDE]
case|case
literal|'\u012A'
case|:
comment|// Äª  [LATIN CAPITAL LETTER I WITH MACRON]
case|case
literal|'\u012C'
case|:
comment|// Ä¬  [LATIN CAPITAL LETTER I WITH BREVE]
case|case
literal|'\u012E'
case|:
comment|// Ä®  [LATIN CAPITAL LETTER I WITH OGONEK]
case|case
literal|'\u0130'
case|:
comment|// Ä°  [LATIN CAPITAL LETTER I WITH DOT ABOVE]
case|case
literal|'\u0196'
case|:
comment|// Æ  [LATIN CAPITAL LETTER IOTA]
case|case
literal|'\u0197'
case|:
comment|// Æ  [LATIN CAPITAL LETTER I WITH STROKE]
case|case
literal|'\u01CF'
case|:
comment|// Ç  [LATIN CAPITAL LETTER I WITH CARON]
case|case
literal|'\u0208'
case|:
comment|// È  [LATIN CAPITAL LETTER I WITH DOUBLE GRAVE]
case|case
literal|'\u020A'
case|:
comment|// È  [LATIN CAPITAL LETTER I WITH INVERTED BREVE]
case|case
literal|'\u026A'
case|:
comment|// Éª  [LATIN LETTER SMALL CAPITAL I]
case|case
literal|'\u1D7B'
case|:
comment|// áµ»  [LATIN SMALL CAPITAL LETTER I WITH STROKE]
case|case
literal|'\u1E2C'
case|:
comment|// á¸¬  [LATIN CAPITAL LETTER I WITH TILDE BELOW]
case|case
literal|'\u1E2E'
case|:
comment|// á¸®  [LATIN CAPITAL LETTER I WITH DIAERESIS AND ACUTE]
case|case
literal|'\u1EC8'
case|:
comment|// á»  [LATIN CAPITAL LETTER I WITH HOOK ABOVE]
case|case
literal|'\u1ECA'
case|:
comment|// á»  [LATIN CAPITAL LETTER I WITH DOT BELOW]
case|case
literal|'\u24BE'
case|:
comment|// â¾  [CIRCLED LATIN CAPITAL LETTER I]
case|case
literal|'\uA7FE'
case|:
comment|// ê¾  [LATIN EPIGRAPHIC LETTER I LONGA]
case|case
literal|'\uFF29'
case|:
comment|// ï¼©  [FULLWIDTH LATIN CAPITAL LETTER I]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'I'
expr_stmt|;
break|break;
case|case
literal|'\u00EC'
case|:
comment|// Ã¬  [LATIN SMALL LETTER I WITH GRAVE]
case|case
literal|'\u00ED'
case|:
comment|// Ã­  [LATIN SMALL LETTER I WITH ACUTE]
case|case
literal|'\u00EE'
case|:
comment|// Ã®  [LATIN SMALL LETTER I WITH CIRCUMFLEX]
case|case
literal|'\u00EF'
case|:
comment|// Ã¯  [LATIN SMALL LETTER I WITH DIAERESIS]
case|case
literal|'\u0129'
case|:
comment|// Ä©  [LATIN SMALL LETTER I WITH TILDE]
case|case
literal|'\u012B'
case|:
comment|// Ä«  [LATIN SMALL LETTER I WITH MACRON]
case|case
literal|'\u012D'
case|:
comment|// Ä­  [LATIN SMALL LETTER I WITH BREVE]
case|case
literal|'\u012F'
case|:
comment|// Ä¯  [LATIN SMALL LETTER I WITH OGONEK]
case|case
literal|'\u0131'
case|:
comment|// Ä±  [LATIN SMALL LETTER DOTLESS I]
case|case
literal|'\u01D0'
case|:
comment|// Ç  [LATIN SMALL LETTER I WITH CARON]
case|case
literal|'\u0209'
case|:
comment|// È  [LATIN SMALL LETTER I WITH DOUBLE GRAVE]
case|case
literal|'\u020B'
case|:
comment|// È  [LATIN SMALL LETTER I WITH INVERTED BREVE]
case|case
literal|'\u0268'
case|:
comment|// É¨  [LATIN SMALL LETTER I WITH STROKE]
case|case
literal|'\u1D09'
case|:
comment|// á´  [LATIN SMALL LETTER TURNED I]
case|case
literal|'\u1D62'
case|:
comment|// áµ¢  [LATIN SUBSCRIPT SMALL LETTER I]
case|case
literal|'\u1D7C'
case|:
comment|// áµ¼  [LATIN SMALL LETTER IOTA WITH STROKE]
case|case
literal|'\u1D96'
case|:
comment|// á¶  [LATIN SMALL LETTER I WITH RETROFLEX HOOK]
case|case
literal|'\u1E2D'
case|:
comment|// á¸­  [LATIN SMALL LETTER I WITH TILDE BELOW]
case|case
literal|'\u1E2F'
case|:
comment|// á¸¯  [LATIN SMALL LETTER I WITH DIAERESIS AND ACUTE]
case|case
literal|'\u1EC9'
case|:
comment|// á»  [LATIN SMALL LETTER I WITH HOOK ABOVE]
case|case
literal|'\u1ECB'
case|:
comment|// á»  [LATIN SMALL LETTER I WITH DOT BELOW]
case|case
literal|'\u2071'
case|:
comment|// â±  [SUPERSCRIPT LATIN SMALL LETTER I]
case|case
literal|'\u24D8'
case|:
comment|// â  [CIRCLED LATIN SMALL LETTER I]
case|case
literal|'\uFF49'
case|:
comment|// ï½  [FULLWIDTH LATIN SMALL LETTER I]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'i'
expr_stmt|;
break|break;
case|case
literal|'\u0132'
case|:
comment|// Ä²  [LATIN CAPITAL LIGATURE IJ]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'I'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'J'
expr_stmt|;
break|break;
case|case
literal|'\u24A4'
case|:
comment|// â¤  [PARENTHESIZED LATIN SMALL LETTER I]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'('
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'i'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|')'
expr_stmt|;
break|break;
case|case
literal|'\u0133'
case|:
comment|// Ä³  [LATIN SMALL LIGATURE IJ]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'i'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'j'
expr_stmt|;
break|break;
case|case
literal|'\u0134'
case|:
comment|// Ä´  [LATIN CAPITAL LETTER J WITH CIRCUMFLEX]
case|case
literal|'\u0248'
case|:
comment|// É  [LATIN CAPITAL LETTER J WITH STROKE]
case|case
literal|'\u1D0A'
case|:
comment|// á´  [LATIN LETTER SMALL CAPITAL J]
case|case
literal|'\u24BF'
case|:
comment|// â¿  [CIRCLED LATIN CAPITAL LETTER J]
case|case
literal|'\uFF2A'
case|:
comment|// ï¼ª  [FULLWIDTH LATIN CAPITAL LETTER J]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'J'
expr_stmt|;
break|break;
case|case
literal|'\u0135'
case|:
comment|// Äµ  [LATIN SMALL LETTER J WITH CIRCUMFLEX]
case|case
literal|'\u01F0'
case|:
comment|// Ç°  [LATIN SMALL LETTER J WITH CARON]
case|case
literal|'\u0237'
case|:
comment|// È·  [LATIN SMALL LETTER DOTLESS J]
case|case
literal|'\u0249'
case|:
comment|// É  [LATIN SMALL LETTER J WITH STROKE]
case|case
literal|'\u025F'
case|:
comment|// É  [LATIN SMALL LETTER DOTLESS J WITH STROKE]
case|case
literal|'\u0284'
case|:
comment|// Ê  [LATIN SMALL LETTER DOTLESS J WITH STROKE AND HOOK]
case|case
literal|'\u029D'
case|:
comment|// Ê  [LATIN SMALL LETTER J WITH CROSSED-TAIL]
case|case
literal|'\u24D9'
case|:
comment|// â  [CIRCLED LATIN SMALL LETTER J]
case|case
literal|'\u2C7C'
case|:
comment|// â±¼  [LATIN SUBSCRIPT SMALL LETTER J]
case|case
literal|'\uFF4A'
case|:
comment|// ï½  [FULLWIDTH LATIN SMALL LETTER J]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'j'
expr_stmt|;
break|break;
case|case
literal|'\u24A5'
case|:
comment|// â¥  [PARENTHESIZED LATIN SMALL LETTER J]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'('
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'j'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|')'
expr_stmt|;
break|break;
case|case
literal|'\u0136'
case|:
comment|// Ä¶  [LATIN CAPITAL LETTER K WITH CEDILLA]
case|case
literal|'\u0198'
case|:
comment|// Æ  [LATIN CAPITAL LETTER K WITH HOOK]
case|case
literal|'\u01E8'
case|:
comment|// Ç¨  [LATIN CAPITAL LETTER K WITH CARON]
case|case
literal|'\u1D0B'
case|:
comment|// á´  [LATIN LETTER SMALL CAPITAL K]
case|case
literal|'\u1E30'
case|:
comment|// á¸°  [LATIN CAPITAL LETTER K WITH ACUTE]
case|case
literal|'\u1E32'
case|:
comment|// á¸²  [LATIN CAPITAL LETTER K WITH DOT BELOW]
case|case
literal|'\u1E34'
case|:
comment|// á¸´  [LATIN CAPITAL LETTER K WITH LINE BELOW]
case|case
literal|'\u24C0'
case|:
comment|// â  [CIRCLED LATIN CAPITAL LETTER K]
case|case
literal|'\u2C69'
case|:
comment|// â±©  [LATIN CAPITAL LETTER K WITH DESCENDER]
case|case
literal|'\uA740'
case|:
comment|// ê  [LATIN CAPITAL LETTER K WITH STROKE]
case|case
literal|'\uA742'
case|:
comment|// ê  [LATIN CAPITAL LETTER K WITH DIAGONAL STROKE]
case|case
literal|'\uA744'
case|:
comment|// ê  [LATIN CAPITAL LETTER K WITH STROKE AND DIAGONAL STROKE]
case|case
literal|'\uFF2B'
case|:
comment|// ï¼«  [FULLWIDTH LATIN CAPITAL LETTER K]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'K'
expr_stmt|;
break|break;
case|case
literal|'\u0137'
case|:
comment|// Ä·  [LATIN SMALL LETTER K WITH CEDILLA]
case|case
literal|'\u0199'
case|:
comment|// Æ  [LATIN SMALL LETTER K WITH HOOK]
case|case
literal|'\u01E9'
case|:
comment|// Ç©  [LATIN SMALL LETTER K WITH CARON]
case|case
literal|'\u029E'
case|:
comment|// Ê  [LATIN SMALL LETTER TURNED K]
case|case
literal|'\u1D84'
case|:
comment|// á¶  [LATIN SMALL LETTER K WITH PALATAL HOOK]
case|case
literal|'\u1E31'
case|:
comment|// á¸±  [LATIN SMALL LETTER K WITH ACUTE]
case|case
literal|'\u1E33'
case|:
comment|// á¸³  [LATIN SMALL LETTER K WITH DOT BELOW]
case|case
literal|'\u1E35'
case|:
comment|// á¸µ  [LATIN SMALL LETTER K WITH LINE BELOW]
case|case
literal|'\u24DA'
case|:
comment|// â  [CIRCLED LATIN SMALL LETTER K]
case|case
literal|'\u2C6A'
case|:
comment|// â±ª  [LATIN SMALL LETTER K WITH DESCENDER]
case|case
literal|'\uA741'
case|:
comment|// ê  [LATIN SMALL LETTER K WITH STROKE]
case|case
literal|'\uA743'
case|:
comment|// ê  [LATIN SMALL LETTER K WITH DIAGONAL STROKE]
case|case
literal|'\uA745'
case|:
comment|// ê  [LATIN SMALL LETTER K WITH STROKE AND DIAGONAL STROKE]
case|case
literal|'\uFF4B'
case|:
comment|// ï½  [FULLWIDTH LATIN SMALL LETTER K]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'k'
expr_stmt|;
break|break;
case|case
literal|'\u24A6'
case|:
comment|// â¦  [PARENTHESIZED LATIN SMALL LETTER K]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'('
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'k'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|')'
expr_stmt|;
break|break;
case|case
literal|'\u0139'
case|:
comment|// Ä¹  [LATIN CAPITAL LETTER L WITH ACUTE]
case|case
literal|'\u013B'
case|:
comment|// Ä»  [LATIN CAPITAL LETTER L WITH CEDILLA]
case|case
literal|'\u013D'
case|:
comment|// Ä½  [LATIN CAPITAL LETTER L WITH CARON]
case|case
literal|'\u013F'
case|:
comment|// Ä¿  [LATIN CAPITAL LETTER L WITH MIDDLE DOT]
case|case
literal|'\u0141'
case|:
comment|// Å  [LATIN CAPITAL LETTER L WITH STROKE]
case|case
literal|'\u023D'
case|:
comment|// È½  [LATIN CAPITAL LETTER L WITH BAR]
case|case
literal|'\u029F'
case|:
comment|// Ê  [LATIN LETTER SMALL CAPITAL L]
case|case
literal|'\u1D0C'
case|:
comment|// á´  [LATIN LETTER SMALL CAPITAL L WITH STROKE]
case|case
literal|'\u1E36'
case|:
comment|// á¸¶  [LATIN CAPITAL LETTER L WITH DOT BELOW]
case|case
literal|'\u1E38'
case|:
comment|// á¸¸  [LATIN CAPITAL LETTER L WITH DOT BELOW AND MACRON]
case|case
literal|'\u1E3A'
case|:
comment|// á¸º  [LATIN CAPITAL LETTER L WITH LINE BELOW]
case|case
literal|'\u1E3C'
case|:
comment|// á¸¼  [LATIN CAPITAL LETTER L WITH CIRCUMFLEX BELOW]
case|case
literal|'\u24C1'
case|:
comment|// â  [CIRCLED LATIN CAPITAL LETTER L]
case|case
literal|'\u2C60'
case|:
comment|// â±   [LATIN CAPITAL LETTER L WITH DOUBLE BAR]
case|case
literal|'\u2C62'
case|:
comment|// â±¢  [LATIN CAPITAL LETTER L WITH MIDDLE TILDE]
case|case
literal|'\uA746'
case|:
comment|// ê  [LATIN CAPITAL LETTER BROKEN L]
case|case
literal|'\uA748'
case|:
comment|// ê  [LATIN CAPITAL LETTER L WITH HIGH STROKE]
case|case
literal|'\uA780'
case|:
comment|// ê  [LATIN CAPITAL LETTER TURNED L]
case|case
literal|'\uFF2C'
case|:
comment|// ï¼¬  [FULLWIDTH LATIN CAPITAL LETTER L]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'L'
expr_stmt|;
break|break;
case|case
literal|'\u013A'
case|:
comment|// Äº  [LATIN SMALL LETTER L WITH ACUTE]
case|case
literal|'\u013C'
case|:
comment|// Ä¼  [LATIN SMALL LETTER L WITH CEDILLA]
case|case
literal|'\u013E'
case|:
comment|// Ä¾  [LATIN SMALL LETTER L WITH CARON]
case|case
literal|'\u0140'
case|:
comment|// Å  [LATIN SMALL LETTER L WITH MIDDLE DOT]
case|case
literal|'\u0142'
case|:
comment|// Å  [LATIN SMALL LETTER L WITH STROKE]
case|case
literal|'\u019A'
case|:
comment|// Æ  [LATIN SMALL LETTER L WITH BAR]
case|case
literal|'\u0234'
case|:
comment|// È´  [LATIN SMALL LETTER L WITH CURL]
case|case
literal|'\u026B'
case|:
comment|// É«  [LATIN SMALL LETTER L WITH MIDDLE TILDE]
case|case
literal|'\u026C'
case|:
comment|// É¬  [LATIN SMALL LETTER L WITH BELT]
case|case
literal|'\u026D'
case|:
comment|// É­  [LATIN SMALL LETTER L WITH RETROFLEX HOOK]
case|case
literal|'\u1D85'
case|:
comment|// á¶  [LATIN SMALL LETTER L WITH PALATAL HOOK]
case|case
literal|'\u1E37'
case|:
comment|// á¸·  [LATIN SMALL LETTER L WITH DOT BELOW]
case|case
literal|'\u1E39'
case|:
comment|// á¸¹  [LATIN SMALL LETTER L WITH DOT BELOW AND MACRON]
case|case
literal|'\u1E3B'
case|:
comment|// á¸»  [LATIN SMALL LETTER L WITH LINE BELOW]
case|case
literal|'\u1E3D'
case|:
comment|// á¸½  [LATIN SMALL LETTER L WITH CIRCUMFLEX BELOW]
case|case
literal|'\u24DB'
case|:
comment|// â  [CIRCLED LATIN SMALL LETTER L]
case|case
literal|'\u2C61'
case|:
comment|// â±¡  [LATIN SMALL LETTER L WITH DOUBLE BAR]
case|case
literal|'\uA747'
case|:
comment|// ê  [LATIN SMALL LETTER BROKEN L]
case|case
literal|'\uA749'
case|:
comment|// ê  [LATIN SMALL LETTER L WITH HIGH STROKE]
case|case
literal|'\uA781'
case|:
comment|// ê  [LATIN SMALL LETTER TURNED L]
case|case
literal|'\uFF4C'
case|:
comment|// ï½  [FULLWIDTH LATIN SMALL LETTER L]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'l'
expr_stmt|;
break|break;
case|case
literal|'\u01C7'
case|:
comment|// Ç  [LATIN CAPITAL LETTER LJ]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'L'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'J'
expr_stmt|;
break|break;
case|case
literal|'\u1EFA'
case|:
comment|// á»º  [LATIN CAPITAL LETTER MIDDLE-WELSH LL]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'L'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'L'
expr_stmt|;
break|break;
case|case
literal|'\u01C8'
case|:
comment|// Ç  [LATIN CAPITAL LETTER L WITH SMALL LETTER J]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'L'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'j'
expr_stmt|;
break|break;
case|case
literal|'\u24A7'
case|:
comment|// â§  [PARENTHESIZED LATIN SMALL LETTER L]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'('
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'l'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|')'
expr_stmt|;
break|break;
case|case
literal|'\u01C9'
case|:
comment|// Ç  [LATIN SMALL LETTER LJ]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'l'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'j'
expr_stmt|;
break|break;
case|case
literal|'\u1EFB'
case|:
comment|// á»»  [LATIN SMALL LETTER MIDDLE-WELSH LL]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'l'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'l'
expr_stmt|;
break|break;
case|case
literal|'\u02AA'
case|:
comment|// Êª  [LATIN SMALL LETTER LS DIGRAPH]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'l'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'s'
expr_stmt|;
break|break;
case|case
literal|'\u02AB'
case|:
comment|// Ê«  [LATIN SMALL LETTER LZ DIGRAPH]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'l'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'z'
expr_stmt|;
break|break;
case|case
literal|'\u019C'
case|:
comment|// Æ  [LATIN CAPITAL LETTER TURNED M]
case|case
literal|'\u1D0D'
case|:
comment|// á´  [LATIN LETTER SMALL CAPITAL M]
case|case
literal|'\u1E3E'
case|:
comment|// á¸¾  [LATIN CAPITAL LETTER M WITH ACUTE]
case|case
literal|'\u1E40'
case|:
comment|// á¹  [LATIN CAPITAL LETTER M WITH DOT ABOVE]
case|case
literal|'\u1E42'
case|:
comment|// á¹  [LATIN CAPITAL LETTER M WITH DOT BELOW]
case|case
literal|'\u24C2'
case|:
comment|// â  [CIRCLED LATIN CAPITAL LETTER M]
case|case
literal|'\u2C6E'
case|:
comment|// â±®  [LATIN CAPITAL LETTER M WITH HOOK]
case|case
literal|'\uA7FD'
case|:
comment|// ê½  [LATIN EPIGRAPHIC LETTER INVERTED M]
case|case
literal|'\uA7FF'
case|:
comment|// ê¿  [LATIN EPIGRAPHIC LETTER ARCHAIC M]
case|case
literal|'\uFF2D'
case|:
comment|// ï¼­  [FULLWIDTH LATIN CAPITAL LETTER M]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'M'
expr_stmt|;
break|break;
case|case
literal|'\u026F'
case|:
comment|// É¯  [LATIN SMALL LETTER TURNED M]
case|case
literal|'\u0270'
case|:
comment|// É°  [LATIN SMALL LETTER TURNED M WITH LONG LEG]
case|case
literal|'\u0271'
case|:
comment|// É±  [LATIN SMALL LETTER M WITH HOOK]
case|case
literal|'\u1D6F'
case|:
comment|// áµ¯  [LATIN SMALL LETTER M WITH MIDDLE TILDE]
case|case
literal|'\u1D86'
case|:
comment|// á¶  [LATIN SMALL LETTER M WITH PALATAL HOOK]
case|case
literal|'\u1E3F'
case|:
comment|// á¸¿  [LATIN SMALL LETTER M WITH ACUTE]
case|case
literal|'\u1E41'
case|:
comment|// á¹  [LATIN SMALL LETTER M WITH DOT ABOVE]
case|case
literal|'\u1E43'
case|:
comment|// á¹  [LATIN SMALL LETTER M WITH DOT BELOW]
case|case
literal|'\u24DC'
case|:
comment|// â  [CIRCLED LATIN SMALL LETTER M]
case|case
literal|'\uFF4D'
case|:
comment|// ï½  [FULLWIDTH LATIN SMALL LETTER M]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'m'
expr_stmt|;
break|break;
case|case
literal|'\u24A8'
case|:
comment|// â¨  [PARENTHESIZED LATIN SMALL LETTER M]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'('
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'m'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|')'
expr_stmt|;
break|break;
case|case
literal|'\u00D1'
case|:
comment|// Ã  [LATIN CAPITAL LETTER N WITH TILDE]
case|case
literal|'\u0143'
case|:
comment|// Å  [LATIN CAPITAL LETTER N WITH ACUTE]
case|case
literal|'\u0145'
case|:
comment|// Å  [LATIN CAPITAL LETTER N WITH CEDILLA]
case|case
literal|'\u0147'
case|:
comment|// Å  [LATIN CAPITAL LETTER N WITH CARON]
case|case
literal|'\u014A'
case|:
comment|// Å  http://en.wikipedia.org/wiki/Eng_(letter)  [LATIN CAPITAL LETTER ENG]
case|case
literal|'\u019D'
case|:
comment|// Æ  [LATIN CAPITAL LETTER N WITH LEFT HOOK]
case|case
literal|'\u01F8'
case|:
comment|// Ç¸  [LATIN CAPITAL LETTER N WITH GRAVE]
case|case
literal|'\u0220'
case|:
comment|// È   [LATIN CAPITAL LETTER N WITH LONG RIGHT LEG]
case|case
literal|'\u0274'
case|:
comment|// É´  [LATIN LETTER SMALL CAPITAL N]
case|case
literal|'\u1D0E'
case|:
comment|// á´  [LATIN LETTER SMALL CAPITAL REVERSED N]
case|case
literal|'\u1E44'
case|:
comment|// á¹  [LATIN CAPITAL LETTER N WITH DOT ABOVE]
case|case
literal|'\u1E46'
case|:
comment|// á¹  [LATIN CAPITAL LETTER N WITH DOT BELOW]
case|case
literal|'\u1E48'
case|:
comment|// á¹  [LATIN CAPITAL LETTER N WITH LINE BELOW]
case|case
literal|'\u1E4A'
case|:
comment|// á¹  [LATIN CAPITAL LETTER N WITH CIRCUMFLEX BELOW]
case|case
literal|'\u24C3'
case|:
comment|// â  [CIRCLED LATIN CAPITAL LETTER N]
case|case
literal|'\uFF2E'
case|:
comment|// ï¼®  [FULLWIDTH LATIN CAPITAL LETTER N]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'N'
expr_stmt|;
break|break;
case|case
literal|'\u00F1'
case|:
comment|// Ã±  [LATIN SMALL LETTER N WITH TILDE]
case|case
literal|'\u0144'
case|:
comment|// Å  [LATIN SMALL LETTER N WITH ACUTE]
case|case
literal|'\u0146'
case|:
comment|// Å  [LATIN SMALL LETTER N WITH CEDILLA]
case|case
literal|'\u0148'
case|:
comment|// Å  [LATIN SMALL LETTER N WITH CARON]
case|case
literal|'\u0149'
case|:
comment|// Å  [LATIN SMALL LETTER N PRECEDED BY APOSTROPHE]
case|case
literal|'\u014B'
case|:
comment|// Å  http://en.wikipedia.org/wiki/Eng_(letter)  [LATIN SMALL LETTER ENG]
case|case
literal|'\u019E'
case|:
comment|// Æ  [LATIN SMALL LETTER N WITH LONG RIGHT LEG]
case|case
literal|'\u01F9'
case|:
comment|// Ç¹  [LATIN SMALL LETTER N WITH GRAVE]
case|case
literal|'\u0235'
case|:
comment|// Èµ  [LATIN SMALL LETTER N WITH CURL]
case|case
literal|'\u0272'
case|:
comment|// É²  [LATIN SMALL LETTER N WITH LEFT HOOK]
case|case
literal|'\u0273'
case|:
comment|// É³  [LATIN SMALL LETTER N WITH RETROFLEX HOOK]
case|case
literal|'\u1D70'
case|:
comment|// áµ°  [LATIN SMALL LETTER N WITH MIDDLE TILDE]
case|case
literal|'\u1D87'
case|:
comment|// á¶  [LATIN SMALL LETTER N WITH PALATAL HOOK]
case|case
literal|'\u1E45'
case|:
comment|// á¹  [LATIN SMALL LETTER N WITH DOT ABOVE]
case|case
literal|'\u1E47'
case|:
comment|// á¹  [LATIN SMALL LETTER N WITH DOT BELOW]
case|case
literal|'\u1E49'
case|:
comment|// á¹  [LATIN SMALL LETTER N WITH LINE BELOW]
case|case
literal|'\u1E4B'
case|:
comment|// á¹  [LATIN SMALL LETTER N WITH CIRCUMFLEX BELOW]
case|case
literal|'\u207F'
case|:
comment|// â¿  [SUPERSCRIPT LATIN SMALL LETTER N]
case|case
literal|'\u24DD'
case|:
comment|// â  [CIRCLED LATIN SMALL LETTER N]
case|case
literal|'\uFF4E'
case|:
comment|// ï½  [FULLWIDTH LATIN SMALL LETTER N]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'n'
expr_stmt|;
break|break;
case|case
literal|'\u01CA'
case|:
comment|// Ç  [LATIN CAPITAL LETTER NJ]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'N'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'J'
expr_stmt|;
break|break;
case|case
literal|'\u01CB'
case|:
comment|// Ç  [LATIN CAPITAL LETTER N WITH SMALL LETTER J]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'N'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'j'
expr_stmt|;
break|break;
case|case
literal|'\u24A9'
case|:
comment|// â©  [PARENTHESIZED LATIN SMALL LETTER N]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'('
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'n'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|')'
expr_stmt|;
break|break;
case|case
literal|'\u01CC'
case|:
comment|// Ç  [LATIN SMALL LETTER NJ]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'n'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'j'
expr_stmt|;
break|break;
case|case
literal|'\u00D2'
case|:
comment|// Ã  [LATIN CAPITAL LETTER O WITH GRAVE]
case|case
literal|'\u00D3'
case|:
comment|// Ã  [LATIN CAPITAL LETTER O WITH ACUTE]
case|case
literal|'\u00D4'
case|:
comment|// Ã  [LATIN CAPITAL LETTER O WITH CIRCUMFLEX]
case|case
literal|'\u00D5'
case|:
comment|// Ã  [LATIN CAPITAL LETTER O WITH TILDE]
case|case
literal|'\u00D6'
case|:
comment|// Ã  [LATIN CAPITAL LETTER O WITH DIAERESIS]
case|case
literal|'\u00D8'
case|:
comment|// Ã  [LATIN CAPITAL LETTER O WITH STROKE]
case|case
literal|'\u014C'
case|:
comment|// Å  [LATIN CAPITAL LETTER O WITH MACRON]
case|case
literal|'\u014E'
case|:
comment|// Å  [LATIN CAPITAL LETTER O WITH BREVE]
case|case
literal|'\u0150'
case|:
comment|// Å  [LATIN CAPITAL LETTER O WITH DOUBLE ACUTE]
case|case
literal|'\u0186'
case|:
comment|// Æ  [LATIN CAPITAL LETTER OPEN O]
case|case
literal|'\u019F'
case|:
comment|// Æ  [LATIN CAPITAL LETTER O WITH MIDDLE TILDE]
case|case
literal|'\u01A0'
case|:
comment|// Æ   [LATIN CAPITAL LETTER O WITH HORN]
case|case
literal|'\u01D1'
case|:
comment|// Ç  [LATIN CAPITAL LETTER O WITH CARON]
case|case
literal|'\u01EA'
case|:
comment|// Çª  [LATIN CAPITAL LETTER O WITH OGONEK]
case|case
literal|'\u01EC'
case|:
comment|// Ç¬  [LATIN CAPITAL LETTER O WITH OGONEK AND MACRON]
case|case
literal|'\u01FE'
case|:
comment|// Ç¾  [LATIN CAPITAL LETTER O WITH STROKE AND ACUTE]
case|case
literal|'\u020C'
case|:
comment|// È  [LATIN CAPITAL LETTER O WITH DOUBLE GRAVE]
case|case
literal|'\u020E'
case|:
comment|// È  [LATIN CAPITAL LETTER O WITH INVERTED BREVE]
case|case
literal|'\u022A'
case|:
comment|// Èª  [LATIN CAPITAL LETTER O WITH DIAERESIS AND MACRON]
case|case
literal|'\u022C'
case|:
comment|// È¬  [LATIN CAPITAL LETTER O WITH TILDE AND MACRON]
case|case
literal|'\u022E'
case|:
comment|// È®  [LATIN CAPITAL LETTER O WITH DOT ABOVE]
case|case
literal|'\u0230'
case|:
comment|// È°  [LATIN CAPITAL LETTER O WITH DOT ABOVE AND MACRON]
case|case
literal|'\u1D0F'
case|:
comment|// á´  [LATIN LETTER SMALL CAPITAL O]
case|case
literal|'\u1D10'
case|:
comment|// á´  [LATIN LETTER SMALL CAPITAL OPEN O]
case|case
literal|'\u1E4C'
case|:
comment|// á¹  [LATIN CAPITAL LETTER O WITH TILDE AND ACUTE]
case|case
literal|'\u1E4E'
case|:
comment|// á¹  [LATIN CAPITAL LETTER O WITH TILDE AND DIAERESIS]
case|case
literal|'\u1E50'
case|:
comment|// á¹  [LATIN CAPITAL LETTER O WITH MACRON AND GRAVE]
case|case
literal|'\u1E52'
case|:
comment|// á¹  [LATIN CAPITAL LETTER O WITH MACRON AND ACUTE]
case|case
literal|'\u1ECC'
case|:
comment|// á»  [LATIN CAPITAL LETTER O WITH DOT BELOW]
case|case
literal|'\u1ECE'
case|:
comment|// á»  [LATIN CAPITAL LETTER O WITH HOOK ABOVE]
case|case
literal|'\u1ED0'
case|:
comment|// á»  [LATIN CAPITAL LETTER O WITH CIRCUMFLEX AND ACUTE]
case|case
literal|'\u1ED2'
case|:
comment|// á»  [LATIN CAPITAL LETTER O WITH CIRCUMFLEX AND GRAVE]
case|case
literal|'\u1ED4'
case|:
comment|// á»  [LATIN CAPITAL LETTER O WITH CIRCUMFLEX AND HOOK ABOVE]
case|case
literal|'\u1ED6'
case|:
comment|// á»  [LATIN CAPITAL LETTER O WITH CIRCUMFLEX AND TILDE]
case|case
literal|'\u1ED8'
case|:
comment|// á»  [LATIN CAPITAL LETTER O WITH CIRCUMFLEX AND DOT BELOW]
case|case
literal|'\u1EDA'
case|:
comment|// á»  [LATIN CAPITAL LETTER O WITH HORN AND ACUTE]
case|case
literal|'\u1EDC'
case|:
comment|// á»  [LATIN CAPITAL LETTER O WITH HORN AND GRAVE]
case|case
literal|'\u1EDE'
case|:
comment|// á»  [LATIN CAPITAL LETTER O WITH HORN AND HOOK ABOVE]
case|case
literal|'\u1EE0'
case|:
comment|// á»   [LATIN CAPITAL LETTER O WITH HORN AND TILDE]
case|case
literal|'\u1EE2'
case|:
comment|// á»¢  [LATIN CAPITAL LETTER O WITH HORN AND DOT BELOW]
case|case
literal|'\u24C4'
case|:
comment|// â  [CIRCLED LATIN CAPITAL LETTER O]
case|case
literal|'\uA74A'
case|:
comment|// ê  [LATIN CAPITAL LETTER O WITH LONG STROKE OVERLAY]
case|case
literal|'\uA74C'
case|:
comment|// ê  [LATIN CAPITAL LETTER O WITH LOOP]
case|case
literal|'\uFF2F'
case|:
comment|// ï¼¯  [FULLWIDTH LATIN CAPITAL LETTER O]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'O'
expr_stmt|;
break|break;
case|case
literal|'\u00F2'
case|:
comment|// Ã²  [LATIN SMALL LETTER O WITH GRAVE]
case|case
literal|'\u00F3'
case|:
comment|// Ã³  [LATIN SMALL LETTER O WITH ACUTE]
case|case
literal|'\u00F4'
case|:
comment|// Ã´  [LATIN SMALL LETTER O WITH CIRCUMFLEX]
case|case
literal|'\u00F5'
case|:
comment|// Ãµ  [LATIN SMALL LETTER O WITH TILDE]
case|case
literal|'\u00F6'
case|:
comment|// Ã¶  [LATIN SMALL LETTER O WITH DIAERESIS]
case|case
literal|'\u00F8'
case|:
comment|// Ã¸  [LATIN SMALL LETTER O WITH STROKE]
case|case
literal|'\u014D'
case|:
comment|// Å  [LATIN SMALL LETTER O WITH MACRON]
case|case
literal|'\u014F'
case|:
comment|// Å  [LATIN SMALL LETTER O WITH BREVE]
case|case
literal|'\u0151'
case|:
comment|// Å  [LATIN SMALL LETTER O WITH DOUBLE ACUTE]
case|case
literal|'\u01A1'
case|:
comment|// Æ¡  [LATIN SMALL LETTER O WITH HORN]
case|case
literal|'\u01D2'
case|:
comment|// Ç  [LATIN SMALL LETTER O WITH CARON]
case|case
literal|'\u01EB'
case|:
comment|// Ç«  [LATIN SMALL LETTER O WITH OGONEK]
case|case
literal|'\u01ED'
case|:
comment|// Ç­  [LATIN SMALL LETTER O WITH OGONEK AND MACRON]
case|case
literal|'\u01FF'
case|:
comment|// Ç¿  [LATIN SMALL LETTER O WITH STROKE AND ACUTE]
case|case
literal|'\u020D'
case|:
comment|// È  [LATIN SMALL LETTER O WITH DOUBLE GRAVE]
case|case
literal|'\u020F'
case|:
comment|// È  [LATIN SMALL LETTER O WITH INVERTED BREVE]
case|case
literal|'\u022B'
case|:
comment|// È«  [LATIN SMALL LETTER O WITH DIAERESIS AND MACRON]
case|case
literal|'\u022D'
case|:
comment|// È­  [LATIN SMALL LETTER O WITH TILDE AND MACRON]
case|case
literal|'\u022F'
case|:
comment|// È¯  [LATIN SMALL LETTER O WITH DOT ABOVE]
case|case
literal|'\u0231'
case|:
comment|// È±  [LATIN SMALL LETTER O WITH DOT ABOVE AND MACRON]
case|case
literal|'\u0254'
case|:
comment|// É  [LATIN SMALL LETTER OPEN O]
case|case
literal|'\u0275'
case|:
comment|// Éµ  [LATIN SMALL LETTER BARRED O]
case|case
literal|'\u1D16'
case|:
comment|// á´  [LATIN SMALL LETTER TOP HALF O]
case|case
literal|'\u1D17'
case|:
comment|// á´  [LATIN SMALL LETTER BOTTOM HALF O]
case|case
literal|'\u1D97'
case|:
comment|// á¶  [LATIN SMALL LETTER OPEN O WITH RETROFLEX HOOK]
case|case
literal|'\u1E4D'
case|:
comment|// á¹  [LATIN SMALL LETTER O WITH TILDE AND ACUTE]
case|case
literal|'\u1E4F'
case|:
comment|// á¹  [LATIN SMALL LETTER O WITH TILDE AND DIAERESIS]
case|case
literal|'\u1E51'
case|:
comment|// á¹  [LATIN SMALL LETTER O WITH MACRON AND GRAVE]
case|case
literal|'\u1E53'
case|:
comment|// á¹  [LATIN SMALL LETTER O WITH MACRON AND ACUTE]
case|case
literal|'\u1ECD'
case|:
comment|// á»  [LATIN SMALL LETTER O WITH DOT BELOW]
case|case
literal|'\u1ECF'
case|:
comment|// á»  [LATIN SMALL LETTER O WITH HOOK ABOVE]
case|case
literal|'\u1ED1'
case|:
comment|// á»  [LATIN SMALL LETTER O WITH CIRCUMFLEX AND ACUTE]
case|case
literal|'\u1ED3'
case|:
comment|// á»  [LATIN SMALL LETTER O WITH CIRCUMFLEX AND GRAVE]
case|case
literal|'\u1ED5'
case|:
comment|// á»  [LATIN SMALL LETTER O WITH CIRCUMFLEX AND HOOK ABOVE]
case|case
literal|'\u1ED7'
case|:
comment|// á»  [LATIN SMALL LETTER O WITH CIRCUMFLEX AND TILDE]
case|case
literal|'\u1ED9'
case|:
comment|// á»  [LATIN SMALL LETTER O WITH CIRCUMFLEX AND DOT BELOW]
case|case
literal|'\u1EDB'
case|:
comment|// á»  [LATIN SMALL LETTER O WITH HORN AND ACUTE]
case|case
literal|'\u1EDD'
case|:
comment|// á»  [LATIN SMALL LETTER O WITH HORN AND GRAVE]
case|case
literal|'\u1EDF'
case|:
comment|// á»  [LATIN SMALL LETTER O WITH HORN AND HOOK ABOVE]
case|case
literal|'\u1EE1'
case|:
comment|// á»¡  [LATIN SMALL LETTER O WITH HORN AND TILDE]
case|case
literal|'\u1EE3'
case|:
comment|// á»£  [LATIN SMALL LETTER O WITH HORN AND DOT BELOW]
case|case
literal|'\u2092'
case|:
comment|// â  [LATIN SUBSCRIPT SMALL LETTER O]
case|case
literal|'\u24DE'
case|:
comment|// â  [CIRCLED LATIN SMALL LETTER O]
case|case
literal|'\u2C7A'
case|:
comment|// â±º  [LATIN SMALL LETTER O WITH LOW RING INSIDE]
case|case
literal|'\uA74B'
case|:
comment|// ê  [LATIN SMALL LETTER O WITH LONG STROKE OVERLAY]
case|case
literal|'\uA74D'
case|:
comment|// ê  [LATIN SMALL LETTER O WITH LOOP]
case|case
literal|'\uFF4F'
case|:
comment|// ï½  [FULLWIDTH LATIN SMALL LETTER O]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'o'
expr_stmt|;
break|break;
case|case
literal|'\u0152'
case|:
comment|// Å  [LATIN CAPITAL LIGATURE OE]
case|case
literal|'\u0276'
case|:
comment|// É¶  [LATIN LETTER SMALL CAPITAL OE]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'O'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'E'
expr_stmt|;
break|break;
case|case
literal|'\uA74E'
case|:
comment|// ê  [LATIN CAPITAL LETTER OO]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'O'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'O'
expr_stmt|;
break|break;
case|case
literal|'\u0222'
case|:
comment|// È¢  http://en.wikipedia.org/wiki/OU  [LATIN CAPITAL LETTER OU]
case|case
literal|'\u1D15'
case|:
comment|// á´  [LATIN LETTER SMALL CAPITAL OU]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'O'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'U'
expr_stmt|;
break|break;
case|case
literal|'\u24AA'
case|:
comment|// âª  [PARENTHESIZED LATIN SMALL LETTER O]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'('
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'o'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|')'
expr_stmt|;
break|break;
case|case
literal|'\u0153'
case|:
comment|// Å  [LATIN SMALL LIGATURE OE]
case|case
literal|'\u1D14'
case|:
comment|// á´  [LATIN SMALL LETTER TURNED OE]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'o'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'e'
expr_stmt|;
break|break;
case|case
literal|'\uA74F'
case|:
comment|// ê  [LATIN SMALL LETTER OO]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'o'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'o'
expr_stmt|;
break|break;
case|case
literal|'\u0223'
case|:
comment|// È£  http://en.wikipedia.org/wiki/OU  [LATIN SMALL LETTER OU]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'o'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'u'
expr_stmt|;
break|break;
case|case
literal|'\u01A4'
case|:
comment|// Æ¤  [LATIN CAPITAL LETTER P WITH HOOK]
case|case
literal|'\u1D18'
case|:
comment|// á´  [LATIN LETTER SMALL CAPITAL P]
case|case
literal|'\u1E54'
case|:
comment|// á¹  [LATIN CAPITAL LETTER P WITH ACUTE]
case|case
literal|'\u1E56'
case|:
comment|// á¹  [LATIN CAPITAL LETTER P WITH DOT ABOVE]
case|case
literal|'\u24C5'
case|:
comment|// â  [CIRCLED LATIN CAPITAL LETTER P]
case|case
literal|'\u2C63'
case|:
comment|// â±£  [LATIN CAPITAL LETTER P WITH STROKE]
case|case
literal|'\uA750'
case|:
comment|// ê  [LATIN CAPITAL LETTER P WITH STROKE THROUGH DESCENDER]
case|case
literal|'\uA752'
case|:
comment|// ê  [LATIN CAPITAL LETTER P WITH FLOURISH]
case|case
literal|'\uA754'
case|:
comment|// ê  [LATIN CAPITAL LETTER P WITH SQUIRREL TAIL]
case|case
literal|'\uFF30'
case|:
comment|// ï¼°  [FULLWIDTH LATIN CAPITAL LETTER P]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'P'
expr_stmt|;
break|break;
case|case
literal|'\u01A5'
case|:
comment|// Æ¥  [LATIN SMALL LETTER P WITH HOOK]
case|case
literal|'\u1D71'
case|:
comment|// áµ±  [LATIN SMALL LETTER P WITH MIDDLE TILDE]
case|case
literal|'\u1D7D'
case|:
comment|// áµ½  [LATIN SMALL LETTER P WITH STROKE]
case|case
literal|'\u1D88'
case|:
comment|// á¶  [LATIN SMALL LETTER P WITH PALATAL HOOK]
case|case
literal|'\u1E55'
case|:
comment|// á¹  [LATIN SMALL LETTER P WITH ACUTE]
case|case
literal|'\u1E57'
case|:
comment|// á¹  [LATIN SMALL LETTER P WITH DOT ABOVE]
case|case
literal|'\u24DF'
case|:
comment|// â  [CIRCLED LATIN SMALL LETTER P]
case|case
literal|'\uA751'
case|:
comment|// ê  [LATIN SMALL LETTER P WITH STROKE THROUGH DESCENDER]
case|case
literal|'\uA753'
case|:
comment|// ê  [LATIN SMALL LETTER P WITH FLOURISH]
case|case
literal|'\uA755'
case|:
comment|// ê  [LATIN SMALL LETTER P WITH SQUIRREL TAIL]
case|case
literal|'\uA7FC'
case|:
comment|// ê¼  [LATIN EPIGRAPHIC LETTER REVERSED P]
case|case
literal|'\uFF50'
case|:
comment|// ï½  [FULLWIDTH LATIN SMALL LETTER P]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'p'
expr_stmt|;
break|break;
case|case
literal|'\u24AB'
case|:
comment|// â«  [PARENTHESIZED LATIN SMALL LETTER P]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'('
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'p'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|')'
expr_stmt|;
break|break;
case|case
literal|'\u024A'
case|:
comment|// É  [LATIN CAPITAL LETTER SMALL Q WITH HOOK TAIL]
case|case
literal|'\u24C6'
case|:
comment|// â  [CIRCLED LATIN CAPITAL LETTER Q]
case|case
literal|'\uA756'
case|:
comment|// ê  [LATIN CAPITAL LETTER Q WITH STROKE THROUGH DESCENDER]
case|case
literal|'\uA758'
case|:
comment|// ê  [LATIN CAPITAL LETTER Q WITH DIAGONAL STROKE]
case|case
literal|'\uFF31'
case|:
comment|// ï¼±  [FULLWIDTH LATIN CAPITAL LETTER Q]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'Q'
expr_stmt|;
break|break;
case|case
literal|'\u0138'
case|:
comment|// Ä¸  http://en.wikipedia.org/wiki/Kra_(letter)  [LATIN SMALL LETTER KRA]
case|case
literal|'\u024B'
case|:
comment|// É  [LATIN SMALL LETTER Q WITH HOOK TAIL]
case|case
literal|'\u02A0'
case|:
comment|// Ê   [LATIN SMALL LETTER Q WITH HOOK]
case|case
literal|'\u24E0'
case|:
comment|// â   [CIRCLED LATIN SMALL LETTER Q]
case|case
literal|'\uA757'
case|:
comment|// ê  [LATIN SMALL LETTER Q WITH STROKE THROUGH DESCENDER]
case|case
literal|'\uA759'
case|:
comment|// ê  [LATIN SMALL LETTER Q WITH DIAGONAL STROKE]
case|case
literal|'\uFF51'
case|:
comment|// ï½  [FULLWIDTH LATIN SMALL LETTER Q]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'q'
expr_stmt|;
break|break;
case|case
literal|'\u24AC'
case|:
comment|// â¬  [PARENTHESIZED LATIN SMALL LETTER Q]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'('
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'q'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|')'
expr_stmt|;
break|break;
case|case
literal|'\u0239'
case|:
comment|// È¹  [LATIN SMALL LETTER QP DIGRAPH]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'q'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'p'
expr_stmt|;
break|break;
case|case
literal|'\u0154'
case|:
comment|// Å  [LATIN CAPITAL LETTER R WITH ACUTE]
case|case
literal|'\u0156'
case|:
comment|// Å  [LATIN CAPITAL LETTER R WITH CEDILLA]
case|case
literal|'\u0158'
case|:
comment|// Å  [LATIN CAPITAL LETTER R WITH CARON]
case|case
literal|'\u0210'
case|:
comment|// È  [LATIN CAPITAL LETTER R WITH DOUBLE GRAVE]
case|case
literal|'\u0212'
case|:
comment|// È  [LATIN CAPITAL LETTER R WITH INVERTED BREVE]
case|case
literal|'\u024C'
case|:
comment|// É  [LATIN CAPITAL LETTER R WITH STROKE]
case|case
literal|'\u0280'
case|:
comment|// Ê  [LATIN LETTER SMALL CAPITAL R]
case|case
literal|'\u0281'
case|:
comment|// Ê  [LATIN LETTER SMALL CAPITAL INVERTED R]
case|case
literal|'\u1D19'
case|:
comment|// á´  [LATIN LETTER SMALL CAPITAL REVERSED R]
case|case
literal|'\u1D1A'
case|:
comment|// á´  [LATIN LETTER SMALL CAPITAL TURNED R]
case|case
literal|'\u1E58'
case|:
comment|// á¹  [LATIN CAPITAL LETTER R WITH DOT ABOVE]
case|case
literal|'\u1E5A'
case|:
comment|// á¹  [LATIN CAPITAL LETTER R WITH DOT BELOW]
case|case
literal|'\u1E5C'
case|:
comment|// á¹  [LATIN CAPITAL LETTER R WITH DOT BELOW AND MACRON]
case|case
literal|'\u1E5E'
case|:
comment|// á¹  [LATIN CAPITAL LETTER R WITH LINE BELOW]
case|case
literal|'\u24C7'
case|:
comment|// â  [CIRCLED LATIN CAPITAL LETTER R]
case|case
literal|'\u2C64'
case|:
comment|// â±¤  [LATIN CAPITAL LETTER R WITH TAIL]
case|case
literal|'\uA75A'
case|:
comment|// ê  [LATIN CAPITAL LETTER R ROTUNDA]
case|case
literal|'\uA782'
case|:
comment|// ê  [LATIN CAPITAL LETTER INSULAR R]
case|case
literal|'\uFF32'
case|:
comment|// ï¼²  [FULLWIDTH LATIN CAPITAL LETTER R]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'R'
expr_stmt|;
break|break;
case|case
literal|'\u0155'
case|:
comment|// Å  [LATIN SMALL LETTER R WITH ACUTE]
case|case
literal|'\u0157'
case|:
comment|// Å  [LATIN SMALL LETTER R WITH CEDILLA]
case|case
literal|'\u0159'
case|:
comment|// Å  [LATIN SMALL LETTER R WITH CARON]
case|case
literal|'\u0211'
case|:
comment|// È  [LATIN SMALL LETTER R WITH DOUBLE GRAVE]
case|case
literal|'\u0213'
case|:
comment|// È  [LATIN SMALL LETTER R WITH INVERTED BREVE]
case|case
literal|'\u024D'
case|:
comment|// É  [LATIN SMALL LETTER R WITH STROKE]
case|case
literal|'\u027C'
case|:
comment|// É¼  [LATIN SMALL LETTER R WITH LONG LEG]
case|case
literal|'\u027D'
case|:
comment|// É½  [LATIN SMALL LETTER R WITH TAIL]
case|case
literal|'\u027E'
case|:
comment|// É¾  [LATIN SMALL LETTER R WITH FISHHOOK]
case|case
literal|'\u027F'
case|:
comment|// É¿  [LATIN SMALL LETTER REVERSED R WITH FISHHOOK]
case|case
literal|'\u1D63'
case|:
comment|// áµ£  [LATIN SUBSCRIPT SMALL LETTER R]
case|case
literal|'\u1D72'
case|:
comment|// áµ²  [LATIN SMALL LETTER R WITH MIDDLE TILDE]
case|case
literal|'\u1D73'
case|:
comment|// áµ³  [LATIN SMALL LETTER R WITH FISHHOOK AND MIDDLE TILDE]
case|case
literal|'\u1D89'
case|:
comment|// á¶  [LATIN SMALL LETTER R WITH PALATAL HOOK]
case|case
literal|'\u1E59'
case|:
comment|// á¹  [LATIN SMALL LETTER R WITH DOT ABOVE]
case|case
literal|'\u1E5B'
case|:
comment|// á¹  [LATIN SMALL LETTER R WITH DOT BELOW]
case|case
literal|'\u1E5D'
case|:
comment|// á¹  [LATIN SMALL LETTER R WITH DOT BELOW AND MACRON]
case|case
literal|'\u1E5F'
case|:
comment|// á¹  [LATIN SMALL LETTER R WITH LINE BELOW]
case|case
literal|'\u24E1'
case|:
comment|// â¡  [CIRCLED LATIN SMALL LETTER R]
case|case
literal|'\uA75B'
case|:
comment|// ê  [LATIN SMALL LETTER R ROTUNDA]
case|case
literal|'\uA783'
case|:
comment|// ê  [LATIN SMALL LETTER INSULAR R]
case|case
literal|'\uFF52'
case|:
comment|// ï½  [FULLWIDTH LATIN SMALL LETTER R]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'r'
expr_stmt|;
break|break;
case|case
literal|'\u24AD'
case|:
comment|// â­  [PARENTHESIZED LATIN SMALL LETTER R]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'('
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'r'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|')'
expr_stmt|;
break|break;
case|case
literal|'\u015A'
case|:
comment|// Å  [LATIN CAPITAL LETTER S WITH ACUTE]
case|case
literal|'\u015C'
case|:
comment|// Å  [LATIN CAPITAL LETTER S WITH CIRCUMFLEX]
case|case
literal|'\u015E'
case|:
comment|// Å  [LATIN CAPITAL LETTER S WITH CEDILLA]
case|case
literal|'\u0160'
case|:
comment|// Å   [LATIN CAPITAL LETTER S WITH CARON]
case|case
literal|'\u0218'
case|:
comment|// È  [LATIN CAPITAL LETTER S WITH COMMA BELOW]
case|case
literal|'\u1E60'
case|:
comment|// á¹   [LATIN CAPITAL LETTER S WITH DOT ABOVE]
case|case
literal|'\u1E62'
case|:
comment|// á¹¢  [LATIN CAPITAL LETTER S WITH DOT BELOW]
case|case
literal|'\u1E64'
case|:
comment|// á¹¤  [LATIN CAPITAL LETTER S WITH ACUTE AND DOT ABOVE]
case|case
literal|'\u1E66'
case|:
comment|// á¹¦  [LATIN CAPITAL LETTER S WITH CARON AND DOT ABOVE]
case|case
literal|'\u1E68'
case|:
comment|// á¹¨  [LATIN CAPITAL LETTER S WITH DOT BELOW AND DOT ABOVE]
case|case
literal|'\u24C8'
case|:
comment|// â  [CIRCLED LATIN CAPITAL LETTER S]
case|case
literal|'\uA731'
case|:
comment|// ê±  [LATIN LETTER SMALL CAPITAL S]
case|case
literal|'\uA785'
case|:
comment|// ê  [LATIN SMALL LETTER INSULAR S]
case|case
literal|'\uFF33'
case|:
comment|// ï¼³  [FULLWIDTH LATIN CAPITAL LETTER S]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'S'
expr_stmt|;
break|break;
case|case
literal|'\u015B'
case|:
comment|// Å  [LATIN SMALL LETTER S WITH ACUTE]
case|case
literal|'\u015D'
case|:
comment|// Å  [LATIN SMALL LETTER S WITH CIRCUMFLEX]
case|case
literal|'\u015F'
case|:
comment|// Å  [LATIN SMALL LETTER S WITH CEDILLA]
case|case
literal|'\u0161'
case|:
comment|// Å¡  [LATIN SMALL LETTER S WITH CARON]
case|case
literal|'\u017F'
case|:
comment|// Å¿  http://en.wikipedia.org/wiki/Long_S  [LATIN SMALL LETTER LONG S]
case|case
literal|'\u0219'
case|:
comment|// È  [LATIN SMALL LETTER S WITH COMMA BELOW]
case|case
literal|'\u023F'
case|:
comment|// È¿  [LATIN SMALL LETTER S WITH SWASH TAIL]
case|case
literal|'\u0282'
case|:
comment|// Ê  [LATIN SMALL LETTER S WITH HOOK]
case|case
literal|'\u1D74'
case|:
comment|// áµ´  [LATIN SMALL LETTER S WITH MIDDLE TILDE]
case|case
literal|'\u1D8A'
case|:
comment|// á¶  [LATIN SMALL LETTER S WITH PALATAL HOOK]
case|case
literal|'\u1E61'
case|:
comment|// á¹¡  [LATIN SMALL LETTER S WITH DOT ABOVE]
case|case
literal|'\u1E63'
case|:
comment|// á¹£  [LATIN SMALL LETTER S WITH DOT BELOW]
case|case
literal|'\u1E65'
case|:
comment|// á¹¥  [LATIN SMALL LETTER S WITH ACUTE AND DOT ABOVE]
case|case
literal|'\u1E67'
case|:
comment|// á¹§  [LATIN SMALL LETTER S WITH CARON AND DOT ABOVE]
case|case
literal|'\u1E69'
case|:
comment|// á¹©  [LATIN SMALL LETTER S WITH DOT BELOW AND DOT ABOVE]
case|case
literal|'\u1E9C'
case|:
comment|// áº  [LATIN SMALL LETTER LONG S WITH DIAGONAL STROKE]
case|case
literal|'\u1E9D'
case|:
comment|// áº  [LATIN SMALL LETTER LONG S WITH HIGH STROKE]
case|case
literal|'\u24E2'
case|:
comment|// â¢  [CIRCLED LATIN SMALL LETTER S]
case|case
literal|'\uA784'
case|:
comment|// ê  [LATIN CAPITAL LETTER INSULAR S]
case|case
literal|'\uFF53'
case|:
comment|// ï½  [FULLWIDTH LATIN SMALL LETTER S]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'s'
expr_stmt|;
break|break;
case|case
literal|'\u1E9E'
case|:
comment|// áº  [LATIN CAPITAL LETTER SHARP S]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'S'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'S'
expr_stmt|;
break|break;
case|case
literal|'\u24AE'
case|:
comment|// â®  [PARENTHESIZED LATIN SMALL LETTER S]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'('
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'s'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|')'
expr_stmt|;
break|break;
case|case
literal|'\u00DF'
case|:
comment|// Ã  [LATIN SMALL LETTER SHARP S]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'s'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'s'
expr_stmt|;
break|break;
case|case
literal|'\uFB06'
case|:
comment|// ï¬  [LATIN SMALL LIGATURE ST]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'s'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'t'
expr_stmt|;
break|break;
case|case
literal|'\u0162'
case|:
comment|// Å¢  [LATIN CAPITAL LETTER T WITH CEDILLA]
case|case
literal|'\u0164'
case|:
comment|// Å¤  [LATIN CAPITAL LETTER T WITH CARON]
case|case
literal|'\u0166'
case|:
comment|// Å¦  [LATIN CAPITAL LETTER T WITH STROKE]
case|case
literal|'\u01AC'
case|:
comment|// Æ¬  [LATIN CAPITAL LETTER T WITH HOOK]
case|case
literal|'\u01AE'
case|:
comment|// Æ®  [LATIN CAPITAL LETTER T WITH RETROFLEX HOOK]
case|case
literal|'\u021A'
case|:
comment|// È  [LATIN CAPITAL LETTER T WITH COMMA BELOW]
case|case
literal|'\u023E'
case|:
comment|// È¾  [LATIN CAPITAL LETTER T WITH DIAGONAL STROKE]
case|case
literal|'\u1D1B'
case|:
comment|// á´  [LATIN LETTER SMALL CAPITAL T]
case|case
literal|'\u1E6A'
case|:
comment|// á¹ª  [LATIN CAPITAL LETTER T WITH DOT ABOVE]
case|case
literal|'\u1E6C'
case|:
comment|// á¹¬  [LATIN CAPITAL LETTER T WITH DOT BELOW]
case|case
literal|'\u1E6E'
case|:
comment|// á¹®  [LATIN CAPITAL LETTER T WITH LINE BELOW]
case|case
literal|'\u1E70'
case|:
comment|// á¹°  [LATIN CAPITAL LETTER T WITH CIRCUMFLEX BELOW]
case|case
literal|'\u24C9'
case|:
comment|// â  [CIRCLED LATIN CAPITAL LETTER T]
case|case
literal|'\uA786'
case|:
comment|// ê  [LATIN CAPITAL LETTER INSULAR T]
case|case
literal|'\uFF34'
case|:
comment|// ï¼´  [FULLWIDTH LATIN CAPITAL LETTER T]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'T'
expr_stmt|;
break|break;
case|case
literal|'\u0163'
case|:
comment|// Å£  [LATIN SMALL LETTER T WITH CEDILLA]
case|case
literal|'\u0165'
case|:
comment|// Å¥  [LATIN SMALL LETTER T WITH CARON]
case|case
literal|'\u0167'
case|:
comment|// Å§  [LATIN SMALL LETTER T WITH STROKE]
case|case
literal|'\u01AB'
case|:
comment|// Æ«  [LATIN SMALL LETTER T WITH PALATAL HOOK]
case|case
literal|'\u01AD'
case|:
comment|// Æ­  [LATIN SMALL LETTER T WITH HOOK]
case|case
literal|'\u021B'
case|:
comment|// È  [LATIN SMALL LETTER T WITH COMMA BELOW]
case|case
literal|'\u0236'
case|:
comment|// È¶  [LATIN SMALL LETTER T WITH CURL]
case|case
literal|'\u0287'
case|:
comment|// Ê  [LATIN SMALL LETTER TURNED T]
case|case
literal|'\u0288'
case|:
comment|// Ê  [LATIN SMALL LETTER T WITH RETROFLEX HOOK]
case|case
literal|'\u1D75'
case|:
comment|// áµµ  [LATIN SMALL LETTER T WITH MIDDLE TILDE]
case|case
literal|'\u1E6B'
case|:
comment|// á¹«  [LATIN SMALL LETTER T WITH DOT ABOVE]
case|case
literal|'\u1E6D'
case|:
comment|// á¹­  [LATIN SMALL LETTER T WITH DOT BELOW]
case|case
literal|'\u1E6F'
case|:
comment|// á¹¯  [LATIN SMALL LETTER T WITH LINE BELOW]
case|case
literal|'\u1E71'
case|:
comment|// á¹±  [LATIN SMALL LETTER T WITH CIRCUMFLEX BELOW]
case|case
literal|'\u1E97'
case|:
comment|// áº  [LATIN SMALL LETTER T WITH DIAERESIS]
case|case
literal|'\u24E3'
case|:
comment|// â£  [CIRCLED LATIN SMALL LETTER T]
case|case
literal|'\u2C66'
case|:
comment|// â±¦  [LATIN SMALL LETTER T WITH DIAGONAL STROKE]
case|case
literal|'\uFF54'
case|:
comment|// ï½  [FULLWIDTH LATIN SMALL LETTER T]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'t'
expr_stmt|;
break|break;
case|case
literal|'\u00DE'
case|:
comment|// Ã  [LATIN CAPITAL LETTER THORN]
case|case
literal|'\uA766'
case|:
comment|// ê¦  [LATIN CAPITAL LETTER THORN WITH STROKE THROUGH DESCENDER]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'T'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'H'
expr_stmt|;
break|break;
case|case
literal|'\uA728'
case|:
comment|// ê¨  [LATIN CAPITAL LETTER TZ]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'T'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'Z'
expr_stmt|;
break|break;
case|case
literal|'\u24AF'
case|:
comment|// â¯  [PARENTHESIZED LATIN SMALL LETTER T]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'('
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'t'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|')'
expr_stmt|;
break|break;
case|case
literal|'\u02A8'
case|:
comment|// Ê¨  [LATIN SMALL LETTER TC DIGRAPH WITH CURL]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'t'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'c'
expr_stmt|;
break|break;
case|case
literal|'\u00FE'
case|:
comment|// Ã¾  [LATIN SMALL LETTER THORN]
case|case
literal|'\u1D7A'
case|:
comment|// áµº  [LATIN SMALL LETTER TH WITH STRIKETHROUGH]
case|case
literal|'\uA767'
case|:
comment|// ê§  [LATIN SMALL LETTER THORN WITH STROKE THROUGH DESCENDER]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'t'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'h'
expr_stmt|;
break|break;
case|case
literal|'\u02A6'
case|:
comment|// Ê¦  [LATIN SMALL LETTER TS DIGRAPH]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'t'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'s'
expr_stmt|;
break|break;
case|case
literal|'\uA729'
case|:
comment|// ê©  [LATIN SMALL LETTER TZ]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'t'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'z'
expr_stmt|;
break|break;
case|case
literal|'\u00D9'
case|:
comment|// Ã  [LATIN CAPITAL LETTER U WITH GRAVE]
case|case
literal|'\u00DA'
case|:
comment|// Ã  [LATIN CAPITAL LETTER U WITH ACUTE]
case|case
literal|'\u00DB'
case|:
comment|// Ã  [LATIN CAPITAL LETTER U WITH CIRCUMFLEX]
case|case
literal|'\u00DC'
case|:
comment|// Ã  [LATIN CAPITAL LETTER U WITH DIAERESIS]
case|case
literal|'\u0168'
case|:
comment|// Å¨  [LATIN CAPITAL LETTER U WITH TILDE]
case|case
literal|'\u016A'
case|:
comment|// Åª  [LATIN CAPITAL LETTER U WITH MACRON]
case|case
literal|'\u016C'
case|:
comment|// Å¬  [LATIN CAPITAL LETTER U WITH BREVE]
case|case
literal|'\u016E'
case|:
comment|// Å®  [LATIN CAPITAL LETTER U WITH RING ABOVE]
case|case
literal|'\u0170'
case|:
comment|// Å°  [LATIN CAPITAL LETTER U WITH DOUBLE ACUTE]
case|case
literal|'\u0172'
case|:
comment|// Å²  [LATIN CAPITAL LETTER U WITH OGONEK]
case|case
literal|'\u01AF'
case|:
comment|// Æ¯  [LATIN CAPITAL LETTER U WITH HORN]
case|case
literal|'\u01D3'
case|:
comment|// Ç  [LATIN CAPITAL LETTER U WITH CARON]
case|case
literal|'\u01D5'
case|:
comment|// Ç  [LATIN CAPITAL LETTER U WITH DIAERESIS AND MACRON]
case|case
literal|'\u01D7'
case|:
comment|// Ç  [LATIN CAPITAL LETTER U WITH DIAERESIS AND ACUTE]
case|case
literal|'\u01D9'
case|:
comment|// Ç  [LATIN CAPITAL LETTER U WITH DIAERESIS AND CARON]
case|case
literal|'\u01DB'
case|:
comment|// Ç  [LATIN CAPITAL LETTER U WITH DIAERESIS AND GRAVE]
case|case
literal|'\u0214'
case|:
comment|// È  [LATIN CAPITAL LETTER U WITH DOUBLE GRAVE]
case|case
literal|'\u0216'
case|:
comment|// È  [LATIN CAPITAL LETTER U WITH INVERTED BREVE]
case|case
literal|'\u0244'
case|:
comment|// É  [LATIN CAPITAL LETTER U BAR]
case|case
literal|'\u1D1C'
case|:
comment|// á´  [LATIN LETTER SMALL CAPITAL U]
case|case
literal|'\u1D7E'
case|:
comment|// áµ¾  [LATIN SMALL CAPITAL LETTER U WITH STROKE]
case|case
literal|'\u1E72'
case|:
comment|// á¹²  [LATIN CAPITAL LETTER U WITH DIAERESIS BELOW]
case|case
literal|'\u1E74'
case|:
comment|// á¹´  [LATIN CAPITAL LETTER U WITH TILDE BELOW]
case|case
literal|'\u1E76'
case|:
comment|// á¹¶  [LATIN CAPITAL LETTER U WITH CIRCUMFLEX BELOW]
case|case
literal|'\u1E78'
case|:
comment|// á¹¸  [LATIN CAPITAL LETTER U WITH TILDE AND ACUTE]
case|case
literal|'\u1E7A'
case|:
comment|// á¹º  [LATIN CAPITAL LETTER U WITH MACRON AND DIAERESIS]
case|case
literal|'\u1EE4'
case|:
comment|// á»¤  [LATIN CAPITAL LETTER U WITH DOT BELOW]
case|case
literal|'\u1EE6'
case|:
comment|// á»¦  [LATIN CAPITAL LETTER U WITH HOOK ABOVE]
case|case
literal|'\u1EE8'
case|:
comment|// á»¨  [LATIN CAPITAL LETTER U WITH HORN AND ACUTE]
case|case
literal|'\u1EEA'
case|:
comment|// á»ª  [LATIN CAPITAL LETTER U WITH HORN AND GRAVE]
case|case
literal|'\u1EEC'
case|:
comment|// á»¬  [LATIN CAPITAL LETTER U WITH HORN AND HOOK ABOVE]
case|case
literal|'\u1EEE'
case|:
comment|// á»®  [LATIN CAPITAL LETTER U WITH HORN AND TILDE]
case|case
literal|'\u1EF0'
case|:
comment|// á»°  [LATIN CAPITAL LETTER U WITH HORN AND DOT BELOW]
case|case
literal|'\u24CA'
case|:
comment|// â  [CIRCLED LATIN CAPITAL LETTER U]
case|case
literal|'\uFF35'
case|:
comment|// ï¼µ  [FULLWIDTH LATIN CAPITAL LETTER U]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'U'
expr_stmt|;
break|break;
case|case
literal|'\u00F9'
case|:
comment|// Ã¹  [LATIN SMALL LETTER U WITH GRAVE]
case|case
literal|'\u00FA'
case|:
comment|// Ãº  [LATIN SMALL LETTER U WITH ACUTE]
case|case
literal|'\u00FB'
case|:
comment|// Ã»  [LATIN SMALL LETTER U WITH CIRCUMFLEX]
case|case
literal|'\u00FC'
case|:
comment|// Ã¼  [LATIN SMALL LETTER U WITH DIAERESIS]
case|case
literal|'\u0169'
case|:
comment|// Å©  [LATIN SMALL LETTER U WITH TILDE]
case|case
literal|'\u016B'
case|:
comment|// Å«  [LATIN SMALL LETTER U WITH MACRON]
case|case
literal|'\u016D'
case|:
comment|// Å­  [LATIN SMALL LETTER U WITH BREVE]
case|case
literal|'\u016F'
case|:
comment|// Å¯  [LATIN SMALL LETTER U WITH RING ABOVE]
case|case
literal|'\u0171'
case|:
comment|// Å±  [LATIN SMALL LETTER U WITH DOUBLE ACUTE]
case|case
literal|'\u0173'
case|:
comment|// Å³  [LATIN SMALL LETTER U WITH OGONEK]
case|case
literal|'\u01B0'
case|:
comment|// Æ°  [LATIN SMALL LETTER U WITH HORN]
case|case
literal|'\u01D4'
case|:
comment|// Ç  [LATIN SMALL LETTER U WITH CARON]
case|case
literal|'\u01D6'
case|:
comment|// Ç  [LATIN SMALL LETTER U WITH DIAERESIS AND MACRON]
case|case
literal|'\u01D8'
case|:
comment|// Ç  [LATIN SMALL LETTER U WITH DIAERESIS AND ACUTE]
case|case
literal|'\u01DA'
case|:
comment|// Ç  [LATIN SMALL LETTER U WITH DIAERESIS AND CARON]
case|case
literal|'\u01DC'
case|:
comment|// Ç  [LATIN SMALL LETTER U WITH DIAERESIS AND GRAVE]
case|case
literal|'\u0215'
case|:
comment|// È  [LATIN SMALL LETTER U WITH DOUBLE GRAVE]
case|case
literal|'\u0217'
case|:
comment|// È  [LATIN SMALL LETTER U WITH INVERTED BREVE]
case|case
literal|'\u0289'
case|:
comment|// Ê  [LATIN SMALL LETTER U BAR]
case|case
literal|'\u1D64'
case|:
comment|// áµ¤  [LATIN SUBSCRIPT SMALL LETTER U]
case|case
literal|'\u1D99'
case|:
comment|// á¶  [LATIN SMALL LETTER U WITH RETROFLEX HOOK]
case|case
literal|'\u1E73'
case|:
comment|// á¹³  [LATIN SMALL LETTER U WITH DIAERESIS BELOW]
case|case
literal|'\u1E75'
case|:
comment|// á¹µ  [LATIN SMALL LETTER U WITH TILDE BELOW]
case|case
literal|'\u1E77'
case|:
comment|// á¹·  [LATIN SMALL LETTER U WITH CIRCUMFLEX BELOW]
case|case
literal|'\u1E79'
case|:
comment|// á¹¹  [LATIN SMALL LETTER U WITH TILDE AND ACUTE]
case|case
literal|'\u1E7B'
case|:
comment|// á¹»  [LATIN SMALL LETTER U WITH MACRON AND DIAERESIS]
case|case
literal|'\u1EE5'
case|:
comment|// á»¥  [LATIN SMALL LETTER U WITH DOT BELOW]
case|case
literal|'\u1EE7'
case|:
comment|// á»§  [LATIN SMALL LETTER U WITH HOOK ABOVE]
case|case
literal|'\u1EE9'
case|:
comment|// á»©  [LATIN SMALL LETTER U WITH HORN AND ACUTE]
case|case
literal|'\u1EEB'
case|:
comment|// á»«  [LATIN SMALL LETTER U WITH HORN AND GRAVE]
case|case
literal|'\u1EED'
case|:
comment|// á»­  [LATIN SMALL LETTER U WITH HORN AND HOOK ABOVE]
case|case
literal|'\u1EEF'
case|:
comment|// á»¯  [LATIN SMALL LETTER U WITH HORN AND TILDE]
case|case
literal|'\u1EF1'
case|:
comment|// á»±  [LATIN SMALL LETTER U WITH HORN AND DOT BELOW]
case|case
literal|'\u24E4'
case|:
comment|// â¤  [CIRCLED LATIN SMALL LETTER U]
case|case
literal|'\uFF55'
case|:
comment|// ï½  [FULLWIDTH LATIN SMALL LETTER U]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'u'
expr_stmt|;
break|break;
case|case
literal|'\u24B0'
case|:
comment|// â°  [PARENTHESIZED LATIN SMALL LETTER U]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'('
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'u'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|')'
expr_stmt|;
break|break;
case|case
literal|'\u1D6B'
case|:
comment|// áµ«  [LATIN SMALL LETTER UE]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'u'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'e'
expr_stmt|;
break|break;
case|case
literal|'\u01B2'
case|:
comment|// Æ²  [LATIN CAPITAL LETTER V WITH HOOK]
case|case
literal|'\u0245'
case|:
comment|// É  [LATIN CAPITAL LETTER TURNED V]
case|case
literal|'\u1D20'
case|:
comment|// á´   [LATIN LETTER SMALL CAPITAL V]
case|case
literal|'\u1E7C'
case|:
comment|// á¹¼  [LATIN CAPITAL LETTER V WITH TILDE]
case|case
literal|'\u1E7E'
case|:
comment|// á¹¾  [LATIN CAPITAL LETTER V WITH DOT BELOW]
case|case
literal|'\u1EFC'
case|:
comment|// á»¼  [LATIN CAPITAL LETTER MIDDLE-WELSH V]
case|case
literal|'\u24CB'
case|:
comment|// â  [CIRCLED LATIN CAPITAL LETTER V]
case|case
literal|'\uA75E'
case|:
comment|// ê  [LATIN CAPITAL LETTER V WITH DIAGONAL STROKE]
case|case
literal|'\uA768'
case|:
comment|// ê¨  [LATIN CAPITAL LETTER VEND]
case|case
literal|'\uFF36'
case|:
comment|// ï¼¶  [FULLWIDTH LATIN CAPITAL LETTER V]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'V'
expr_stmt|;
break|break;
case|case
literal|'\u028B'
case|:
comment|// Ê  [LATIN SMALL LETTER V WITH HOOK]
case|case
literal|'\u028C'
case|:
comment|// Ê  [LATIN SMALL LETTER TURNED V]
case|case
literal|'\u1D65'
case|:
comment|// áµ¥  [LATIN SUBSCRIPT SMALL LETTER V]
case|case
literal|'\u1D8C'
case|:
comment|// á¶  [LATIN SMALL LETTER V WITH PALATAL HOOK]
case|case
literal|'\u1E7D'
case|:
comment|// á¹½  [LATIN SMALL LETTER V WITH TILDE]
case|case
literal|'\u1E7F'
case|:
comment|// á¹¿  [LATIN SMALL LETTER V WITH DOT BELOW]
case|case
literal|'\u24E5'
case|:
comment|// â¥  [CIRCLED LATIN SMALL LETTER V]
case|case
literal|'\u2C71'
case|:
comment|// â±±  [LATIN SMALL LETTER V WITH RIGHT HOOK]
case|case
literal|'\u2C74'
case|:
comment|// â±´  [LATIN SMALL LETTER V WITH CURL]
case|case
literal|'\uA75F'
case|:
comment|// ê  [LATIN SMALL LETTER V WITH DIAGONAL STROKE]
case|case
literal|'\uFF56'
case|:
comment|// ï½  [FULLWIDTH LATIN SMALL LETTER V]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'v'
expr_stmt|;
break|break;
case|case
literal|'\uA760'
case|:
comment|// ê   [LATIN CAPITAL LETTER VY]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'V'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'Y'
expr_stmt|;
break|break;
case|case
literal|'\u24B1'
case|:
comment|// â±  [PARENTHESIZED LATIN SMALL LETTER V]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'('
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'v'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|')'
expr_stmt|;
break|break;
case|case
literal|'\uA761'
case|:
comment|// ê¡  [LATIN SMALL LETTER VY]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'v'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'y'
expr_stmt|;
break|break;
case|case
literal|'\u0174'
case|:
comment|// Å´  [LATIN CAPITAL LETTER W WITH CIRCUMFLEX]
case|case
literal|'\u01F7'
case|:
comment|// Ç·  http://en.wikipedia.org/wiki/Wynn  [LATIN CAPITAL LETTER WYNN]
case|case
literal|'\u1D21'
case|:
comment|// á´¡  [LATIN LETTER SMALL CAPITAL W]
case|case
literal|'\u1E80'
case|:
comment|// áº  [LATIN CAPITAL LETTER W WITH GRAVE]
case|case
literal|'\u1E82'
case|:
comment|// áº  [LATIN CAPITAL LETTER W WITH ACUTE]
case|case
literal|'\u1E84'
case|:
comment|// áº  [LATIN CAPITAL LETTER W WITH DIAERESIS]
case|case
literal|'\u1E86'
case|:
comment|// áº  [LATIN CAPITAL LETTER W WITH DOT ABOVE]
case|case
literal|'\u1E88'
case|:
comment|// áº  [LATIN CAPITAL LETTER W WITH DOT BELOW]
case|case
literal|'\u24CC'
case|:
comment|// â  [CIRCLED LATIN CAPITAL LETTER W]
case|case
literal|'\u2C72'
case|:
comment|// â±²  [LATIN CAPITAL LETTER W WITH HOOK]
case|case
literal|'\uFF37'
case|:
comment|// ï¼·  [FULLWIDTH LATIN CAPITAL LETTER W]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'W'
expr_stmt|;
break|break;
case|case
literal|'\u0175'
case|:
comment|// Åµ  [LATIN SMALL LETTER W WITH CIRCUMFLEX]
case|case
literal|'\u01BF'
case|:
comment|// Æ¿  http://en.wikipedia.org/wiki/Wynn  [LATIN LETTER WYNN]
case|case
literal|'\u028D'
case|:
comment|// Ê  [LATIN SMALL LETTER TURNED W]
case|case
literal|'\u1E81'
case|:
comment|// áº  [LATIN SMALL LETTER W WITH GRAVE]
case|case
literal|'\u1E83'
case|:
comment|// áº  [LATIN SMALL LETTER W WITH ACUTE]
case|case
literal|'\u1E85'
case|:
comment|// áº  [LATIN SMALL LETTER W WITH DIAERESIS]
case|case
literal|'\u1E87'
case|:
comment|// áº  [LATIN SMALL LETTER W WITH DOT ABOVE]
case|case
literal|'\u1E89'
case|:
comment|// áº  [LATIN SMALL LETTER W WITH DOT BELOW]
case|case
literal|'\u1E98'
case|:
comment|// áº  [LATIN SMALL LETTER W WITH RING ABOVE]
case|case
literal|'\u24E6'
case|:
comment|// â¦  [CIRCLED LATIN SMALL LETTER W]
case|case
literal|'\u2C73'
case|:
comment|// â±³  [LATIN SMALL LETTER W WITH HOOK]
case|case
literal|'\uFF57'
case|:
comment|// ï½  [FULLWIDTH LATIN SMALL LETTER W]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'w'
expr_stmt|;
break|break;
case|case
literal|'\u24B2'
case|:
comment|// â²  [PARENTHESIZED LATIN SMALL LETTER W]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'('
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'w'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|')'
expr_stmt|;
break|break;
case|case
literal|'\u1E8A'
case|:
comment|// áº  [LATIN CAPITAL LETTER X WITH DOT ABOVE]
case|case
literal|'\u1E8C'
case|:
comment|// áº  [LATIN CAPITAL LETTER X WITH DIAERESIS]
case|case
literal|'\u24CD'
case|:
comment|// â  [CIRCLED LATIN CAPITAL LETTER X]
case|case
literal|'\uFF38'
case|:
comment|// ï¼¸  [FULLWIDTH LATIN CAPITAL LETTER X]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'X'
expr_stmt|;
break|break;
case|case
literal|'\u1D8D'
case|:
comment|// á¶  [LATIN SMALL LETTER X WITH PALATAL HOOK]
case|case
literal|'\u1E8B'
case|:
comment|// áº  [LATIN SMALL LETTER X WITH DOT ABOVE]
case|case
literal|'\u1E8D'
case|:
comment|// áº  [LATIN SMALL LETTER X WITH DIAERESIS]
case|case
literal|'\u2093'
case|:
comment|// â  [LATIN SUBSCRIPT SMALL LETTER X]
case|case
literal|'\u24E7'
case|:
comment|// â§  [CIRCLED LATIN SMALL LETTER X]
case|case
literal|'\uFF58'
case|:
comment|// ï½  [FULLWIDTH LATIN SMALL LETTER X]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'x'
expr_stmt|;
break|break;
case|case
literal|'\u24B3'
case|:
comment|// â³  [PARENTHESIZED LATIN SMALL LETTER X]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'('
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'x'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|')'
expr_stmt|;
break|break;
case|case
literal|'\u00DD'
case|:
comment|// Ã  [LATIN CAPITAL LETTER Y WITH ACUTE]
case|case
literal|'\u0176'
case|:
comment|// Å¶  [LATIN CAPITAL LETTER Y WITH CIRCUMFLEX]
case|case
literal|'\u0178'
case|:
comment|// Å¸  [LATIN CAPITAL LETTER Y WITH DIAERESIS]
case|case
literal|'\u01B3'
case|:
comment|// Æ³  [LATIN CAPITAL LETTER Y WITH HOOK]
case|case
literal|'\u0232'
case|:
comment|// È²  [LATIN CAPITAL LETTER Y WITH MACRON]
case|case
literal|'\u024E'
case|:
comment|// É  [LATIN CAPITAL LETTER Y WITH STROKE]
case|case
literal|'\u028F'
case|:
comment|// Ê  [LATIN LETTER SMALL CAPITAL Y]
case|case
literal|'\u1E8E'
case|:
comment|// áº  [LATIN CAPITAL LETTER Y WITH DOT ABOVE]
case|case
literal|'\u1EF2'
case|:
comment|// á»²  [LATIN CAPITAL LETTER Y WITH GRAVE]
case|case
literal|'\u1EF4'
case|:
comment|// á»´  [LATIN CAPITAL LETTER Y WITH DOT BELOW]
case|case
literal|'\u1EF6'
case|:
comment|// á»¶  [LATIN CAPITAL LETTER Y WITH HOOK ABOVE]
case|case
literal|'\u1EF8'
case|:
comment|// á»¸  [LATIN CAPITAL LETTER Y WITH TILDE]
case|case
literal|'\u1EFE'
case|:
comment|// á»¾  [LATIN CAPITAL LETTER Y WITH LOOP]
case|case
literal|'\u24CE'
case|:
comment|// â  [CIRCLED LATIN CAPITAL LETTER Y]
case|case
literal|'\uFF39'
case|:
comment|// ï¼¹  [FULLWIDTH LATIN CAPITAL LETTER Y]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'Y'
expr_stmt|;
break|break;
case|case
literal|'\u00FD'
case|:
comment|// Ã½  [LATIN SMALL LETTER Y WITH ACUTE]
case|case
literal|'\u00FF'
case|:
comment|// Ã¿  [LATIN SMALL LETTER Y WITH DIAERESIS]
case|case
literal|'\u0177'
case|:
comment|// Å·  [LATIN SMALL LETTER Y WITH CIRCUMFLEX]
case|case
literal|'\u01B4'
case|:
comment|// Æ´  [LATIN SMALL LETTER Y WITH HOOK]
case|case
literal|'\u0233'
case|:
comment|// È³  [LATIN SMALL LETTER Y WITH MACRON]
case|case
literal|'\u024F'
case|:
comment|// É  [LATIN SMALL LETTER Y WITH STROKE]
case|case
literal|'\u028E'
case|:
comment|// Ê  [LATIN SMALL LETTER TURNED Y]
case|case
literal|'\u1E8F'
case|:
comment|// áº  [LATIN SMALL LETTER Y WITH DOT ABOVE]
case|case
literal|'\u1E99'
case|:
comment|// áº  [LATIN SMALL LETTER Y WITH RING ABOVE]
case|case
literal|'\u1EF3'
case|:
comment|// á»³  [LATIN SMALL LETTER Y WITH GRAVE]
case|case
literal|'\u1EF5'
case|:
comment|// á»µ  [LATIN SMALL LETTER Y WITH DOT BELOW]
case|case
literal|'\u1EF7'
case|:
comment|// á»·  [LATIN SMALL LETTER Y WITH HOOK ABOVE]
case|case
literal|'\u1EF9'
case|:
comment|// á»¹  [LATIN SMALL LETTER Y WITH TILDE]
case|case
literal|'\u1EFF'
case|:
comment|// á»¿  [LATIN SMALL LETTER Y WITH LOOP]
case|case
literal|'\u24E8'
case|:
comment|// â¨  [CIRCLED LATIN SMALL LETTER Y]
case|case
literal|'\uFF59'
case|:
comment|// ï½  [FULLWIDTH LATIN SMALL LETTER Y]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'y'
expr_stmt|;
break|break;
case|case
literal|'\u24B4'
case|:
comment|// â´  [PARENTHESIZED LATIN SMALL LETTER Y]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'('
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'y'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|')'
expr_stmt|;
break|break;
case|case
literal|'\u0179'
case|:
comment|// Å¹  [LATIN CAPITAL LETTER Z WITH ACUTE]
case|case
literal|'\u017B'
case|:
comment|// Å»  [LATIN CAPITAL LETTER Z WITH DOT ABOVE]
case|case
literal|'\u017D'
case|:
comment|// Å½  [LATIN CAPITAL LETTER Z WITH CARON]
case|case
literal|'\u01B5'
case|:
comment|// Æµ  [LATIN CAPITAL LETTER Z WITH STROKE]
case|case
literal|'\u021C'
case|:
comment|// È  http://en.wikipedia.org/wiki/Yogh  [LATIN CAPITAL LETTER YOGH]
case|case
literal|'\u0224'
case|:
comment|// È¤  [LATIN CAPITAL LETTER Z WITH HOOK]
case|case
literal|'\u1D22'
case|:
comment|// á´¢  [LATIN LETTER SMALL CAPITAL Z]
case|case
literal|'\u1E90'
case|:
comment|// áº  [LATIN CAPITAL LETTER Z WITH CIRCUMFLEX]
case|case
literal|'\u1E92'
case|:
comment|// áº  [LATIN CAPITAL LETTER Z WITH DOT BELOW]
case|case
literal|'\u1E94'
case|:
comment|// áº  [LATIN CAPITAL LETTER Z WITH LINE BELOW]
case|case
literal|'\u24CF'
case|:
comment|// â  [CIRCLED LATIN CAPITAL LETTER Z]
case|case
literal|'\u2C6B'
case|:
comment|// â±«  [LATIN CAPITAL LETTER Z WITH DESCENDER]
case|case
literal|'\uA762'
case|:
comment|// ê¢  [LATIN CAPITAL LETTER VISIGOTHIC Z]
case|case
literal|'\uFF3A'
case|:
comment|// ï¼º  [FULLWIDTH LATIN CAPITAL LETTER Z]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'Z'
expr_stmt|;
break|break;
case|case
literal|'\u017A'
case|:
comment|// Åº  [LATIN SMALL LETTER Z WITH ACUTE]
case|case
literal|'\u017C'
case|:
comment|// Å¼  [LATIN SMALL LETTER Z WITH DOT ABOVE]
case|case
literal|'\u017E'
case|:
comment|// Å¾  [LATIN SMALL LETTER Z WITH CARON]
case|case
literal|'\u01B6'
case|:
comment|// Æ¶  [LATIN SMALL LETTER Z WITH STROKE]
case|case
literal|'\u021D'
case|:
comment|// È  http://en.wikipedia.org/wiki/Yogh  [LATIN SMALL LETTER YOGH]
case|case
literal|'\u0225'
case|:
comment|// È¥  [LATIN SMALL LETTER Z WITH HOOK]
case|case
literal|'\u0240'
case|:
comment|// É  [LATIN SMALL LETTER Z WITH SWASH TAIL]
case|case
literal|'\u0290'
case|:
comment|// Ê  [LATIN SMALL LETTER Z WITH RETROFLEX HOOK]
case|case
literal|'\u0291'
case|:
comment|// Ê  [LATIN SMALL LETTER Z WITH CURL]
case|case
literal|'\u1D76'
case|:
comment|// áµ¶  [LATIN SMALL LETTER Z WITH MIDDLE TILDE]
case|case
literal|'\u1D8E'
case|:
comment|// á¶  [LATIN SMALL LETTER Z WITH PALATAL HOOK]
case|case
literal|'\u1E91'
case|:
comment|// áº  [LATIN SMALL LETTER Z WITH CIRCUMFLEX]
case|case
literal|'\u1E93'
case|:
comment|// áº  [LATIN SMALL LETTER Z WITH DOT BELOW]
case|case
literal|'\u1E95'
case|:
comment|// áº  [LATIN SMALL LETTER Z WITH LINE BELOW]
case|case
literal|'\u24E9'
case|:
comment|// â©  [CIRCLED LATIN SMALL LETTER Z]
case|case
literal|'\u2C6C'
case|:
comment|// â±¬  [LATIN SMALL LETTER Z WITH DESCENDER]
case|case
literal|'\uA763'
case|:
comment|// ê£  [LATIN SMALL LETTER VISIGOTHIC Z]
case|case
literal|'\uFF5A'
case|:
comment|// ï½  [FULLWIDTH LATIN SMALL LETTER Z]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'z'
expr_stmt|;
break|break;
case|case
literal|'\u24B5'
case|:
comment|// âµ  [PARENTHESIZED LATIN SMALL LETTER Z]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'('
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'z'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|')'
expr_stmt|;
break|break;
case|case
literal|'\u2070'
case|:
comment|// â°  [SUPERSCRIPT ZERO]
case|case
literal|'\u2080'
case|:
comment|// â  [SUBSCRIPT ZERO]
case|case
literal|'\u24EA'
case|:
comment|// âª  [CIRCLED DIGIT ZERO]
case|case
literal|'\u24FF'
case|:
comment|// â¿  [NEGATIVE CIRCLED DIGIT ZERO]
case|case
literal|'\uFF10'
case|:
comment|// ï¼  [FULLWIDTH DIGIT ZERO]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'0'
expr_stmt|;
break|break;
case|case
literal|'\u00B9'
case|:
comment|// Â¹  [SUPERSCRIPT ONE]
case|case
literal|'\u2081'
case|:
comment|// â  [SUBSCRIPT ONE]
case|case
literal|'\u2460'
case|:
comment|// â   [CIRCLED DIGIT ONE]
case|case
literal|'\u24F5'
case|:
comment|// âµ  [DOUBLE CIRCLED DIGIT ONE]
case|case
literal|'\u2776'
case|:
comment|// â¶  [DINGBAT NEGATIVE CIRCLED DIGIT ONE]
case|case
literal|'\u2780'
case|:
comment|// â  [DINGBAT CIRCLED SANS-SERIF DIGIT ONE]
case|case
literal|'\u278A'
case|:
comment|// â  [DINGBAT NEGATIVE CIRCLED SANS-SERIF DIGIT ONE]
case|case
literal|'\uFF11'
case|:
comment|// ï¼  [FULLWIDTH DIGIT ONE]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'1'
expr_stmt|;
break|break;
case|case
literal|'\u2488'
case|:
comment|// â  [DIGIT ONE FULL STOP]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'1'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'.'
expr_stmt|;
break|break;
case|case
literal|'\u2474'
case|:
comment|// â´  [PARENTHESIZED DIGIT ONE]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'('
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'1'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|')'
expr_stmt|;
break|break;
case|case
literal|'\u00B2'
case|:
comment|// Â²  [SUPERSCRIPT TWO]
case|case
literal|'\u2082'
case|:
comment|// â  [SUBSCRIPT TWO]
case|case
literal|'\u2461'
case|:
comment|// â¡  [CIRCLED DIGIT TWO]
case|case
literal|'\u24F6'
case|:
comment|// â¶  [DOUBLE CIRCLED DIGIT TWO]
case|case
literal|'\u2777'
case|:
comment|// â·  [DINGBAT NEGATIVE CIRCLED DIGIT TWO]
case|case
literal|'\u2781'
case|:
comment|// â  [DINGBAT CIRCLED SANS-SERIF DIGIT TWO]
case|case
literal|'\u278B'
case|:
comment|// â  [DINGBAT NEGATIVE CIRCLED SANS-SERIF DIGIT TWO]
case|case
literal|'\uFF12'
case|:
comment|// ï¼  [FULLWIDTH DIGIT TWO]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'2'
expr_stmt|;
break|break;
case|case
literal|'\u2489'
case|:
comment|// â  [DIGIT TWO FULL STOP]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'2'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'.'
expr_stmt|;
break|break;
case|case
literal|'\u2475'
case|:
comment|// âµ  [PARENTHESIZED DIGIT TWO]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'('
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'2'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|')'
expr_stmt|;
break|break;
case|case
literal|'\u00B3'
case|:
comment|// Â³  [SUPERSCRIPT THREE]
case|case
literal|'\u2083'
case|:
comment|// â  [SUBSCRIPT THREE]
case|case
literal|'\u2462'
case|:
comment|// â¢  [CIRCLED DIGIT THREE]
case|case
literal|'\u24F7'
case|:
comment|// â·  [DOUBLE CIRCLED DIGIT THREE]
case|case
literal|'\u2778'
case|:
comment|// â¸  [DINGBAT NEGATIVE CIRCLED DIGIT THREE]
case|case
literal|'\u2782'
case|:
comment|// â  [DINGBAT CIRCLED SANS-SERIF DIGIT THREE]
case|case
literal|'\u278C'
case|:
comment|// â  [DINGBAT NEGATIVE CIRCLED SANS-SERIF DIGIT THREE]
case|case
literal|'\uFF13'
case|:
comment|// ï¼  [FULLWIDTH DIGIT THREE]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'3'
expr_stmt|;
break|break;
case|case
literal|'\u248A'
case|:
comment|// â  [DIGIT THREE FULL STOP]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'3'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'.'
expr_stmt|;
break|break;
case|case
literal|'\u2476'
case|:
comment|// â¶  [PARENTHESIZED DIGIT THREE]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'('
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'3'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|')'
expr_stmt|;
break|break;
case|case
literal|'\u2074'
case|:
comment|// â´  [SUPERSCRIPT FOUR]
case|case
literal|'\u2084'
case|:
comment|// â  [SUBSCRIPT FOUR]
case|case
literal|'\u2463'
case|:
comment|// â£  [CIRCLED DIGIT FOUR]
case|case
literal|'\u24F8'
case|:
comment|// â¸  [DOUBLE CIRCLED DIGIT FOUR]
case|case
literal|'\u2779'
case|:
comment|// â¹  [DINGBAT NEGATIVE CIRCLED DIGIT FOUR]
case|case
literal|'\u2783'
case|:
comment|// â  [DINGBAT CIRCLED SANS-SERIF DIGIT FOUR]
case|case
literal|'\u278D'
case|:
comment|// â  [DINGBAT NEGATIVE CIRCLED SANS-SERIF DIGIT FOUR]
case|case
literal|'\uFF14'
case|:
comment|// ï¼  [FULLWIDTH DIGIT FOUR]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'4'
expr_stmt|;
break|break;
case|case
literal|'\u248B'
case|:
comment|// â  [DIGIT FOUR FULL STOP]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'4'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'.'
expr_stmt|;
break|break;
case|case
literal|'\u2477'
case|:
comment|// â·  [PARENTHESIZED DIGIT FOUR]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'('
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'4'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|')'
expr_stmt|;
break|break;
case|case
literal|'\u2075'
case|:
comment|// âµ  [SUPERSCRIPT FIVE]
case|case
literal|'\u2085'
case|:
comment|// â  [SUBSCRIPT FIVE]
case|case
literal|'\u2464'
case|:
comment|// â¤  [CIRCLED DIGIT FIVE]
case|case
literal|'\u24F9'
case|:
comment|// â¹  [DOUBLE CIRCLED DIGIT FIVE]
case|case
literal|'\u277A'
case|:
comment|// âº  [DINGBAT NEGATIVE CIRCLED DIGIT FIVE]
case|case
literal|'\u2784'
case|:
comment|// â  [DINGBAT CIRCLED SANS-SERIF DIGIT FIVE]
case|case
literal|'\u278E'
case|:
comment|// â  [DINGBAT NEGATIVE CIRCLED SANS-SERIF DIGIT FIVE]
case|case
literal|'\uFF15'
case|:
comment|// ï¼  [FULLWIDTH DIGIT FIVE]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'5'
expr_stmt|;
break|break;
case|case
literal|'\u248C'
case|:
comment|// â  [DIGIT FIVE FULL STOP]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'5'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'.'
expr_stmt|;
break|break;
case|case
literal|'\u2478'
case|:
comment|// â¸  [PARENTHESIZED DIGIT FIVE]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'('
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'5'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|')'
expr_stmt|;
break|break;
case|case
literal|'\u2076'
case|:
comment|// â¶  [SUPERSCRIPT SIX]
case|case
literal|'\u2086'
case|:
comment|// â  [SUBSCRIPT SIX]
case|case
literal|'\u2465'
case|:
comment|// â¥  [CIRCLED DIGIT SIX]
case|case
literal|'\u24FA'
case|:
comment|// âº  [DOUBLE CIRCLED DIGIT SIX]
case|case
literal|'\u277B'
case|:
comment|// â»  [DINGBAT NEGATIVE CIRCLED DIGIT SIX]
case|case
literal|'\u2785'
case|:
comment|// â  [DINGBAT CIRCLED SANS-SERIF DIGIT SIX]
case|case
literal|'\u278F'
case|:
comment|// â  [DINGBAT NEGATIVE CIRCLED SANS-SERIF DIGIT SIX]
case|case
literal|'\uFF16'
case|:
comment|// ï¼  [FULLWIDTH DIGIT SIX]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'6'
expr_stmt|;
break|break;
case|case
literal|'\u248D'
case|:
comment|// â  [DIGIT SIX FULL STOP]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'6'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'.'
expr_stmt|;
break|break;
case|case
literal|'\u2479'
case|:
comment|// â¹  [PARENTHESIZED DIGIT SIX]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'('
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'6'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|')'
expr_stmt|;
break|break;
case|case
literal|'\u2077'
case|:
comment|// â·  [SUPERSCRIPT SEVEN]
case|case
literal|'\u2087'
case|:
comment|// â  [SUBSCRIPT SEVEN]
case|case
literal|'\u2466'
case|:
comment|// â¦  [CIRCLED DIGIT SEVEN]
case|case
literal|'\u24FB'
case|:
comment|// â»  [DOUBLE CIRCLED DIGIT SEVEN]
case|case
literal|'\u277C'
case|:
comment|// â¼  [DINGBAT NEGATIVE CIRCLED DIGIT SEVEN]
case|case
literal|'\u2786'
case|:
comment|// â  [DINGBAT CIRCLED SANS-SERIF DIGIT SEVEN]
case|case
literal|'\u2790'
case|:
comment|// â  [DINGBAT NEGATIVE CIRCLED SANS-SERIF DIGIT SEVEN]
case|case
literal|'\uFF17'
case|:
comment|// ï¼  [FULLWIDTH DIGIT SEVEN]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'7'
expr_stmt|;
break|break;
case|case
literal|'\u248E'
case|:
comment|// â  [DIGIT SEVEN FULL STOP]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'7'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'.'
expr_stmt|;
break|break;
case|case
literal|'\u247A'
case|:
comment|// âº  [PARENTHESIZED DIGIT SEVEN]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'('
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'7'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|')'
expr_stmt|;
break|break;
case|case
literal|'\u2078'
case|:
comment|// â¸  [SUPERSCRIPT EIGHT]
case|case
literal|'\u2088'
case|:
comment|// â  [SUBSCRIPT EIGHT]
case|case
literal|'\u2467'
case|:
comment|// â§  [CIRCLED DIGIT EIGHT]
case|case
literal|'\u24FC'
case|:
comment|// â¼  [DOUBLE CIRCLED DIGIT EIGHT]
case|case
literal|'\u277D'
case|:
comment|// â½  [DINGBAT NEGATIVE CIRCLED DIGIT EIGHT]
case|case
literal|'\u2787'
case|:
comment|// â  [DINGBAT CIRCLED SANS-SERIF DIGIT EIGHT]
case|case
literal|'\u2791'
case|:
comment|// â  [DINGBAT NEGATIVE CIRCLED SANS-SERIF DIGIT EIGHT]
case|case
literal|'\uFF18'
case|:
comment|// ï¼  [FULLWIDTH DIGIT EIGHT]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'8'
expr_stmt|;
break|break;
case|case
literal|'\u248F'
case|:
comment|// â  [DIGIT EIGHT FULL STOP]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'8'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'.'
expr_stmt|;
break|break;
case|case
literal|'\u247B'
case|:
comment|// â»  [PARENTHESIZED DIGIT EIGHT]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'('
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'8'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|')'
expr_stmt|;
break|break;
case|case
literal|'\u2079'
case|:
comment|// â¹  [SUPERSCRIPT NINE]
case|case
literal|'\u2089'
case|:
comment|// â  [SUBSCRIPT NINE]
case|case
literal|'\u2468'
case|:
comment|// â¨  [CIRCLED DIGIT NINE]
case|case
literal|'\u24FD'
case|:
comment|// â½  [DOUBLE CIRCLED DIGIT NINE]
case|case
literal|'\u277E'
case|:
comment|// â¾  [DINGBAT NEGATIVE CIRCLED DIGIT NINE]
case|case
literal|'\u2788'
case|:
comment|// â  [DINGBAT CIRCLED SANS-SERIF DIGIT NINE]
case|case
literal|'\u2792'
case|:
comment|// â  [DINGBAT NEGATIVE CIRCLED SANS-SERIF DIGIT NINE]
case|case
literal|'\uFF19'
case|:
comment|// ï¼  [FULLWIDTH DIGIT NINE]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'9'
expr_stmt|;
break|break;
case|case
literal|'\u2490'
case|:
comment|// â  [DIGIT NINE FULL STOP]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'9'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'.'
expr_stmt|;
break|break;
case|case
literal|'\u247C'
case|:
comment|// â¼  [PARENTHESIZED DIGIT NINE]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'('
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'9'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|')'
expr_stmt|;
break|break;
case|case
literal|'\u2469'
case|:
comment|// â©  [CIRCLED NUMBER TEN]
case|case
literal|'\u24FE'
case|:
comment|// â¾  [DOUBLE CIRCLED NUMBER TEN]
case|case
literal|'\u277F'
case|:
comment|// â¿  [DINGBAT NEGATIVE CIRCLED NUMBER TEN]
case|case
literal|'\u2789'
case|:
comment|// â  [DINGBAT CIRCLED SANS-SERIF NUMBER TEN]
case|case
literal|'\u2793'
case|:
comment|// â  [DINGBAT NEGATIVE CIRCLED SANS-SERIF NUMBER TEN]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'1'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'0'
expr_stmt|;
break|break;
case|case
literal|'\u2491'
case|:
comment|// â  [NUMBER TEN FULL STOP]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'1'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'0'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'.'
expr_stmt|;
break|break;
case|case
literal|'\u247D'
case|:
comment|// â½  [PARENTHESIZED NUMBER TEN]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'('
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'1'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'0'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|')'
expr_stmt|;
break|break;
case|case
literal|'\u246A'
case|:
comment|// âª  [CIRCLED NUMBER ELEVEN]
case|case
literal|'\u24EB'
case|:
comment|// â«  [NEGATIVE CIRCLED NUMBER ELEVEN]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'1'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'1'
expr_stmt|;
break|break;
case|case
literal|'\u2492'
case|:
comment|// â  [NUMBER ELEVEN FULL STOP]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'1'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'1'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'.'
expr_stmt|;
break|break;
case|case
literal|'\u247E'
case|:
comment|// â¾  [PARENTHESIZED NUMBER ELEVEN]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'('
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'1'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'1'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|')'
expr_stmt|;
break|break;
case|case
literal|'\u246B'
case|:
comment|// â«  [CIRCLED NUMBER TWELVE]
case|case
literal|'\u24EC'
case|:
comment|// â¬  [NEGATIVE CIRCLED NUMBER TWELVE]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'1'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'2'
expr_stmt|;
break|break;
case|case
literal|'\u2493'
case|:
comment|// â  [NUMBER TWELVE FULL STOP]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'1'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'2'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'.'
expr_stmt|;
break|break;
case|case
literal|'\u247F'
case|:
comment|// â¿  [PARENTHESIZED NUMBER TWELVE]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'('
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'1'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'2'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|')'
expr_stmt|;
break|break;
case|case
literal|'\u246C'
case|:
comment|// â¬  [CIRCLED NUMBER THIRTEEN]
case|case
literal|'\u24ED'
case|:
comment|// â­  [NEGATIVE CIRCLED NUMBER THIRTEEN]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'1'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'3'
expr_stmt|;
break|break;
case|case
literal|'\u2494'
case|:
comment|// â  [NUMBER THIRTEEN FULL STOP]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'1'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'3'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'.'
expr_stmt|;
break|break;
case|case
literal|'\u2480'
case|:
comment|// â  [PARENTHESIZED NUMBER THIRTEEN]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'('
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'1'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'3'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|')'
expr_stmt|;
break|break;
case|case
literal|'\u246D'
case|:
comment|// â­  [CIRCLED NUMBER FOURTEEN]
case|case
literal|'\u24EE'
case|:
comment|// â®  [NEGATIVE CIRCLED NUMBER FOURTEEN]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'1'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'4'
expr_stmt|;
break|break;
case|case
literal|'\u2495'
case|:
comment|// â  [NUMBER FOURTEEN FULL STOP]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'1'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'4'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'.'
expr_stmt|;
break|break;
case|case
literal|'\u2481'
case|:
comment|// â  [PARENTHESIZED NUMBER FOURTEEN]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'('
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'1'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'4'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|')'
expr_stmt|;
break|break;
case|case
literal|'\u246E'
case|:
comment|// â®  [CIRCLED NUMBER FIFTEEN]
case|case
literal|'\u24EF'
case|:
comment|// â¯  [NEGATIVE CIRCLED NUMBER FIFTEEN]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'1'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'5'
expr_stmt|;
break|break;
case|case
literal|'\u2496'
case|:
comment|// â  [NUMBER FIFTEEN FULL STOP]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'1'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'5'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'.'
expr_stmt|;
break|break;
case|case
literal|'\u2482'
case|:
comment|// â  [PARENTHESIZED NUMBER FIFTEEN]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'('
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'1'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'5'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|')'
expr_stmt|;
break|break;
case|case
literal|'\u246F'
case|:
comment|// â¯  [CIRCLED NUMBER SIXTEEN]
case|case
literal|'\u24F0'
case|:
comment|// â°  [NEGATIVE CIRCLED NUMBER SIXTEEN]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'1'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'6'
expr_stmt|;
break|break;
case|case
literal|'\u2497'
case|:
comment|// â  [NUMBER SIXTEEN FULL STOP]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'1'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'6'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'.'
expr_stmt|;
break|break;
case|case
literal|'\u2483'
case|:
comment|// â  [PARENTHESIZED NUMBER SIXTEEN]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'('
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'1'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'6'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|')'
expr_stmt|;
break|break;
case|case
literal|'\u2470'
case|:
comment|// â°  [CIRCLED NUMBER SEVENTEEN]
case|case
literal|'\u24F1'
case|:
comment|// â±  [NEGATIVE CIRCLED NUMBER SEVENTEEN]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'1'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'7'
expr_stmt|;
break|break;
case|case
literal|'\u2498'
case|:
comment|// â  [NUMBER SEVENTEEN FULL STOP]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'1'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'7'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'.'
expr_stmt|;
break|break;
case|case
literal|'\u2484'
case|:
comment|// â  [PARENTHESIZED NUMBER SEVENTEEN]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'('
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'1'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'7'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|')'
expr_stmt|;
break|break;
case|case
literal|'\u2471'
case|:
comment|// â±  [CIRCLED NUMBER EIGHTEEN]
case|case
literal|'\u24F2'
case|:
comment|// â²  [NEGATIVE CIRCLED NUMBER EIGHTEEN]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'1'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'8'
expr_stmt|;
break|break;
case|case
literal|'\u2499'
case|:
comment|// â  [NUMBER EIGHTEEN FULL STOP]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'1'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'8'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'.'
expr_stmt|;
break|break;
case|case
literal|'\u2485'
case|:
comment|// â  [PARENTHESIZED NUMBER EIGHTEEN]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'('
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'1'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'8'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|')'
expr_stmt|;
break|break;
case|case
literal|'\u2472'
case|:
comment|// â²  [CIRCLED NUMBER NINETEEN]
case|case
literal|'\u24F3'
case|:
comment|// â³  [NEGATIVE CIRCLED NUMBER NINETEEN]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'1'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'9'
expr_stmt|;
break|break;
case|case
literal|'\u249A'
case|:
comment|// â  [NUMBER NINETEEN FULL STOP]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'1'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'9'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'.'
expr_stmt|;
break|break;
case|case
literal|'\u2486'
case|:
comment|// â  [PARENTHESIZED NUMBER NINETEEN]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'('
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'1'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'9'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|')'
expr_stmt|;
break|break;
case|case
literal|'\u2473'
case|:
comment|// â³  [CIRCLED NUMBER TWENTY]
case|case
literal|'\u24F4'
case|:
comment|// â´  [NEGATIVE CIRCLED NUMBER TWENTY]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'2'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'0'
expr_stmt|;
break|break;
case|case
literal|'\u249B'
case|:
comment|// â  [NUMBER TWENTY FULL STOP]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'2'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'0'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'.'
expr_stmt|;
break|break;
case|case
literal|'\u2487'
case|:
comment|// â  [PARENTHESIZED NUMBER TWENTY]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'('
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'2'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'0'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|')'
expr_stmt|;
break|break;
case|case
literal|'\u00AB'
case|:
comment|// Â«  [LEFT-POINTING DOUBLE ANGLE QUOTATION MARK]
case|case
literal|'\u00BB'
case|:
comment|// Â»  [RIGHT-POINTING DOUBLE ANGLE QUOTATION MARK]
case|case
literal|'\u201C'
case|:
comment|// â  [LEFT DOUBLE QUOTATION MARK]
case|case
literal|'\u201D'
case|:
comment|// â  [RIGHT DOUBLE QUOTATION MARK]
case|case
literal|'\u201E'
case|:
comment|// â  [DOUBLE LOW-9 QUOTATION MARK]
case|case
literal|'\u2033'
case|:
comment|// â³  [DOUBLE PRIME]
case|case
literal|'\u2036'
case|:
comment|// â¶  [REVERSED DOUBLE PRIME]
case|case
literal|'\u275D'
case|:
comment|// â  [HEAVY DOUBLE TURNED COMMA QUOTATION MARK ORNAMENT]
case|case
literal|'\u275E'
case|:
comment|// â  [HEAVY DOUBLE COMMA QUOTATION MARK ORNAMENT]
case|case
literal|'\u276E'
case|:
comment|// â®  [HEAVY LEFT-POINTING ANGLE QUOTATION MARK ORNAMENT]
case|case
literal|'\u276F'
case|:
comment|// â¯  [HEAVY RIGHT-POINTING ANGLE QUOTATION MARK ORNAMENT]
case|case
literal|'\uFF02'
case|:
comment|// ï¼  [FULLWIDTH QUOTATION MARK]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'"'
expr_stmt|;
break|break;
case|case
literal|'\u2018'
case|:
comment|// â  [LEFT SINGLE QUOTATION MARK]
case|case
literal|'\u2019'
case|:
comment|// â  [RIGHT SINGLE QUOTATION MARK]
case|case
literal|'\u201A'
case|:
comment|// â  [SINGLE LOW-9 QUOTATION MARK]
case|case
literal|'\u201B'
case|:
comment|// â  [SINGLE HIGH-REVERSED-9 QUOTATION MARK]
case|case
literal|'\u2032'
case|:
comment|// â²  [PRIME]
case|case
literal|'\u2035'
case|:
comment|// âµ  [REVERSED PRIME]
case|case
literal|'\u2039'
case|:
comment|// â¹  [SINGLE LEFT-POINTING ANGLE QUOTATION MARK]
case|case
literal|'\u203A'
case|:
comment|// âº  [SINGLE RIGHT-POINTING ANGLE QUOTATION MARK]
case|case
literal|'\u275B'
case|:
comment|// â  [HEAVY SINGLE TURNED COMMA QUOTATION MARK ORNAMENT]
case|case
literal|'\u275C'
case|:
comment|// â  [HEAVY SINGLE COMMA QUOTATION MARK ORNAMENT]
case|case
literal|'\uFF07'
case|:
comment|// ï¼  [FULLWIDTH APOSTROPHE]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'\''
expr_stmt|;
break|break;
case|case
literal|'\u2010'
case|:
comment|// â  [HYPHEN]
case|case
literal|'\u2011'
case|:
comment|// â  [NON-BREAKING HYPHEN]
case|case
literal|'\u2012'
case|:
comment|// â  [FIGURE DASH]
case|case
literal|'\u2013'
case|:
comment|// â  [EN DASH]
case|case
literal|'\u2014'
case|:
comment|// â  [EM DASH]
case|case
literal|'\u207B'
case|:
comment|// â»  [SUPERSCRIPT MINUS]
case|case
literal|'\u208B'
case|:
comment|// â  [SUBSCRIPT MINUS]
case|case
literal|'\uFF0D'
case|:
comment|// ï¼  [FULLWIDTH HYPHEN-MINUS]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'-'
expr_stmt|;
break|break;
case|case
literal|'\u2045'
case|:
comment|// â  [LEFT SQUARE BRACKET WITH QUILL]
case|case
literal|'\u2772'
case|:
comment|// â²  [LIGHT LEFT TORTOISE SHELL BRACKET ORNAMENT]
case|case
literal|'\uFF3B'
case|:
comment|// ï¼»  [FULLWIDTH LEFT SQUARE BRACKET]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'['
expr_stmt|;
break|break;
case|case
literal|'\u2046'
case|:
comment|// â  [RIGHT SQUARE BRACKET WITH QUILL]
case|case
literal|'\u2773'
case|:
comment|// â³  [LIGHT RIGHT TORTOISE SHELL BRACKET ORNAMENT]
case|case
literal|'\uFF3D'
case|:
comment|// ï¼½  [FULLWIDTH RIGHT SQUARE BRACKET]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|']'
expr_stmt|;
break|break;
case|case
literal|'\u207D'
case|:
comment|// â½  [SUPERSCRIPT LEFT PARENTHESIS]
case|case
literal|'\u208D'
case|:
comment|// â  [SUBSCRIPT LEFT PARENTHESIS]
case|case
literal|'\u2768'
case|:
comment|// â¨  [MEDIUM LEFT PARENTHESIS ORNAMENT]
case|case
literal|'\u276A'
case|:
comment|// âª  [MEDIUM FLATTENED LEFT PARENTHESIS ORNAMENT]
case|case
literal|'\uFF08'
case|:
comment|// ï¼  [FULLWIDTH LEFT PARENTHESIS]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'('
expr_stmt|;
break|break;
case|case
literal|'\u2E28'
case|:
comment|// â¸¨  [LEFT DOUBLE PARENTHESIS]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'('
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'('
expr_stmt|;
break|break;
case|case
literal|'\u207E'
case|:
comment|// â¾  [SUPERSCRIPT RIGHT PARENTHESIS]
case|case
literal|'\u208E'
case|:
comment|// â  [SUBSCRIPT RIGHT PARENTHESIS]
case|case
literal|'\u2769'
case|:
comment|// â©  [MEDIUM RIGHT PARENTHESIS ORNAMENT]
case|case
literal|'\u276B'
case|:
comment|// â«  [MEDIUM FLATTENED RIGHT PARENTHESIS ORNAMENT]
case|case
literal|'\uFF09'
case|:
comment|// ï¼  [FULLWIDTH RIGHT PARENTHESIS]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|')'
expr_stmt|;
break|break;
case|case
literal|'\u2E29'
case|:
comment|// â¸©  [RIGHT DOUBLE PARENTHESIS]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|')'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|')'
expr_stmt|;
break|break;
case|case
literal|'\u276C'
case|:
comment|// â¬  [MEDIUM LEFT-POINTING ANGLE BRACKET ORNAMENT]
case|case
literal|'\u2770'
case|:
comment|// â°  [HEAVY LEFT-POINTING ANGLE BRACKET ORNAMENT]
case|case
literal|'\uFF1C'
case|:
comment|// ï¼  [FULLWIDTH LESS-THAN SIGN]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'<'
expr_stmt|;
break|break;
case|case
literal|'\u276D'
case|:
comment|// â­  [MEDIUM RIGHT-POINTING ANGLE BRACKET ORNAMENT]
case|case
literal|'\u2771'
case|:
comment|// â±  [HEAVY RIGHT-POINTING ANGLE BRACKET ORNAMENT]
case|case
literal|'\uFF1E'
case|:
comment|// ï¼  [FULLWIDTH GREATER-THAN SIGN]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'>'
expr_stmt|;
break|break;
case|case
literal|'\u2774'
case|:
comment|// â´  [MEDIUM LEFT CURLY BRACKET ORNAMENT]
case|case
literal|'\uFF5B'
case|:
comment|// ï½  [FULLWIDTH LEFT CURLY BRACKET]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'{'
expr_stmt|;
break|break;
case|case
literal|'\u2775'
case|:
comment|// âµ  [MEDIUM RIGHT CURLY BRACKET ORNAMENT]
case|case
literal|'\uFF5D'
case|:
comment|// ï½  [FULLWIDTH RIGHT CURLY BRACKET]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'}'
expr_stmt|;
break|break;
case|case
literal|'\u207A'
case|:
comment|// âº  [SUPERSCRIPT PLUS SIGN]
case|case
literal|'\u208A'
case|:
comment|// â  [SUBSCRIPT PLUS SIGN]
case|case
literal|'\uFF0B'
case|:
comment|// ï¼  [FULLWIDTH PLUS SIGN]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'+'
expr_stmt|;
break|break;
case|case
literal|'\u207C'
case|:
comment|// â¼  [SUPERSCRIPT EQUALS SIGN]
case|case
literal|'\u208C'
case|:
comment|// â  [SUBSCRIPT EQUALS SIGN]
case|case
literal|'\uFF1D'
case|:
comment|// ï¼  [FULLWIDTH EQUALS SIGN]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'='
expr_stmt|;
break|break;
case|case
literal|'\uFF01'
case|:
comment|// ï¼  [FULLWIDTH EXCLAMATION MARK]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'!'
expr_stmt|;
break|break;
case|case
literal|'\u203C'
case|:
comment|// â¼  [DOUBLE EXCLAMATION MARK]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'!'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'!'
expr_stmt|;
break|break;
case|case
literal|'\u2049'
case|:
comment|// â  [EXCLAMATION QUESTION MARK]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'!'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'?'
expr_stmt|;
break|break;
case|case
literal|'\uFF03'
case|:
comment|// ï¼  [FULLWIDTH NUMBER SIGN]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'#'
expr_stmt|;
break|break;
case|case
literal|'\uFF04'
case|:
comment|// ï¼  [FULLWIDTH DOLLAR SIGN]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'$'
expr_stmt|;
break|break;
case|case
literal|'\u2052'
case|:
comment|// â  [COMMERCIAL MINUS SIGN]
case|case
literal|'\uFF05'
case|:
comment|// ï¼  [FULLWIDTH PERCENT SIGN]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'%'
expr_stmt|;
break|break;
case|case
literal|'\uFF06'
case|:
comment|// ï¼  [FULLWIDTH AMPERSAND]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'&'
expr_stmt|;
break|break;
case|case
literal|'\u204E'
case|:
comment|// â  [LOW ASTERISK]
case|case
literal|'\uFF0A'
case|:
comment|// ï¼  [FULLWIDTH ASTERISK]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'*'
expr_stmt|;
break|break;
case|case
literal|'\uFF0C'
case|:
comment|// ï¼  [FULLWIDTH COMMA]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|','
expr_stmt|;
break|break;
case|case
literal|'\uFF0E'
case|:
comment|// ï¼  [FULLWIDTH FULL STOP]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'.'
expr_stmt|;
break|break;
case|case
literal|'\u2044'
case|:
comment|// â  [FRACTION SLASH]
case|case
literal|'\uFF0F'
case|:
comment|// ï¼  [FULLWIDTH SOLIDUS]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'/'
expr_stmt|;
break|break;
case|case
literal|'\uFF1A'
case|:
comment|// ï¼  [FULLWIDTH COLON]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|':'
expr_stmt|;
break|break;
case|case
literal|'\u204F'
case|:
comment|// â  [REVERSED SEMICOLON]
case|case
literal|'\uFF1B'
case|:
comment|// ï¼  [FULLWIDTH SEMICOLON]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|';'
expr_stmt|;
break|break;
case|case
literal|'\uFF1F'
case|:
comment|// ï¼  [FULLWIDTH QUESTION MARK]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'?'
expr_stmt|;
break|break;
case|case
literal|'\u2047'
case|:
comment|// â  [DOUBLE QUESTION MARK]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'?'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'?'
expr_stmt|;
break|break;
case|case
literal|'\u2048'
case|:
comment|// â  [QUESTION EXCLAMATION MARK]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'?'
expr_stmt|;
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'!'
expr_stmt|;
break|break;
case|case
literal|'\uFF20'
case|:
comment|// ï¼   [FULLWIDTH COMMERCIAL AT]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'@'
expr_stmt|;
break|break;
case|case
literal|'\uFF3C'
case|:
comment|// ï¼¼  [FULLWIDTH REVERSE SOLIDUS]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'\\'
expr_stmt|;
break|break;
case|case
literal|'\u2038'
case|:
comment|// â¸  [CARET]
case|case
literal|'\uFF3E'
case|:
comment|// ï¼¾  [FULLWIDTH CIRCUMFLEX ACCENT]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'^'
expr_stmt|;
break|break;
case|case
literal|'\uFF3F'
case|:
comment|// ï¼¿  [FULLWIDTH LOW LINE]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'_'
expr_stmt|;
break|break;
case|case
literal|'\u2053'
case|:
comment|// â  [SWUNG DASH]
case|case
literal|'\uFF5E'
case|:
comment|// ï½  [FULLWIDTH TILDE]
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
literal|'~'
expr_stmt|;
break|break;
default|default:
name|output
index|[
name|outputPos
operator|++
index|]
operator|=
name|c
expr_stmt|;
break|break;
block|}
block|}
block|}
return|return
name|outputPos
return|;
block|}
block|}
end_class

end_unit

