begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.analysis.phonetic
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|analysis
operator|.
name|phonetic
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|codec
operator|.
name|EncoderException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|codec
operator|.
name|StringEncoder
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_comment
comment|/**  *  * Taken from commons-codec trunk (unreleased yet)  *  * Encodes a string into a NYSIIS value. NYSIIS is an encoding used to relate  * similar names, but can also be used as a general purpose scheme to find word  * with similar phonemes.  *  *<p> NYSIIS features an accuracy increase of 2.7% over the traditional Soundex  * algorithm.</p>  *  *<p>Algorithm description:  *<pre>  * 1. Transcode first characters of name  *   1a. MAC -&gt;   MCC  *   1b. KN  -&gt;   NN  *   1c. K   -&gt;   C  *   1d. PH  -&gt;   FF  *   1e. PF  -&gt;   FF  *   1f. SCH -&gt;   SSS  * 2. Transcode last characters of name  *   2a. EE, IE          -&gt;   Y  *   2b. DT,RT,RD,NT,ND  -&gt;   D  * 3. First character of key = first character of name  * 4. Transcode remaining characters by following these rules, incrementing by one character each time  *   4a. EV  -&gt;   AF  else A,E,I,O,U -&gt; A  *   4b. Q   -&gt;   G  *   4c. Z   -&gt;   S  *   4d. M   -&gt;   N  *   4e. KN  -&gt;   N   else K -&gt; C  *   4f. SCH -&gt;   SSS  *   4g. PH  -&gt;   FF  *   4h. H   -&gt;   If previous or next is nonvowel, previous  *   4i. W   -&gt;   If previous is vowel, previous  *   4j. Add current to key if current != last key character  * 5. If last character is S, remove it  * 6. If last characters are AY, replace with Y  * 7. If last character is A, remove it  * 8. Collapse all strings of repeated characters  * 9. Add original first character of name as first character of key  *</pre>  *  * @see<a href="http://en.wikipedia.org/wiki/NYSIIS">NYSIIS on Wikipedia</a>  * @see<a href="http://www.dropby.com/NYSIIS.html">NYSIIS on dropby.com</a>  *  */
end_comment

begin_class
DECL|class|Nysiis
specifier|public
class|class
name|Nysiis
implements|implements
name|StringEncoder
block|{
DECL|field|CHARS_A
specifier|private
specifier|static
specifier|final
name|char
index|[]
name|CHARS_A
init|=
operator|new
name|char
index|[]
block|{
literal|'A'
block|}
decl_stmt|;
DECL|field|CHARS_AF
specifier|private
specifier|static
specifier|final
name|char
index|[]
name|CHARS_AF
init|=
operator|new
name|char
index|[]
block|{
literal|'A'
block|,
literal|'F'
block|}
decl_stmt|;
DECL|field|CHARS_C
specifier|private
specifier|static
specifier|final
name|char
index|[]
name|CHARS_C
init|=
operator|new
name|char
index|[]
block|{
literal|'C'
block|}
decl_stmt|;
DECL|field|CHARS_FF
specifier|private
specifier|static
specifier|final
name|char
index|[]
name|CHARS_FF
init|=
operator|new
name|char
index|[]
block|{
literal|'F'
block|,
literal|'F'
block|}
decl_stmt|;
DECL|field|CHARS_G
specifier|private
specifier|static
specifier|final
name|char
index|[]
name|CHARS_G
init|=
operator|new
name|char
index|[]
block|{
literal|'G'
block|}
decl_stmt|;
DECL|field|CHARS_N
specifier|private
specifier|static
specifier|final
name|char
index|[]
name|CHARS_N
init|=
operator|new
name|char
index|[]
block|{
literal|'N'
block|}
decl_stmt|;
DECL|field|CHARS_NN
specifier|private
specifier|static
specifier|final
name|char
index|[]
name|CHARS_NN
init|=
operator|new
name|char
index|[]
block|{
literal|'N'
block|,
literal|'N'
block|}
decl_stmt|;
DECL|field|CHARS_S
specifier|private
specifier|static
specifier|final
name|char
index|[]
name|CHARS_S
init|=
operator|new
name|char
index|[]
block|{
literal|'S'
block|}
decl_stmt|;
DECL|field|CHARS_SSS
specifier|private
specifier|static
specifier|final
name|char
index|[]
name|CHARS_SSS
init|=
operator|new
name|char
index|[]
block|{
literal|'S'
block|,
literal|'S'
block|,
literal|'S'
block|}
decl_stmt|;
DECL|field|PAT_MAC
specifier|private
specifier|static
specifier|final
name|Pattern
name|PAT_MAC
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^MAC"
argument_list|)
decl_stmt|;
DECL|field|PAT_KN
specifier|private
specifier|static
specifier|final
name|Pattern
name|PAT_KN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^KN"
argument_list|)
decl_stmt|;
DECL|field|PAT_K
specifier|private
specifier|static
specifier|final
name|Pattern
name|PAT_K
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^K"
argument_list|)
decl_stmt|;
DECL|field|PAT_PH_PF
specifier|private
specifier|static
specifier|final
name|Pattern
name|PAT_PH_PF
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^(PH|PF)"
argument_list|)
decl_stmt|;
DECL|field|PAT_SCH
specifier|private
specifier|static
specifier|final
name|Pattern
name|PAT_SCH
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^SCH"
argument_list|)
decl_stmt|;
DECL|field|PAT_EE_IE
specifier|private
specifier|static
specifier|final
name|Pattern
name|PAT_EE_IE
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"(EE|IE)$"
argument_list|)
decl_stmt|;
DECL|field|PAT_DT_ETC
specifier|private
specifier|static
specifier|final
name|Pattern
name|PAT_DT_ETC
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"(DT|RT|RD|NT|ND)$"
argument_list|)
decl_stmt|;
DECL|field|SPACE
specifier|private
specifier|static
specifier|final
name|char
name|SPACE
init|=
literal|' '
decl_stmt|;
DECL|field|TRUE_LENGTH
specifier|private
specifier|static
specifier|final
name|int
name|TRUE_LENGTH
init|=
literal|6
decl_stmt|;
comment|/**      * Tests if the given character is a vowel.      *      * @param c the character to test      * @return {@code true} if the character is a vowel, {@code false} otherwise      */
DECL|method|isVowel
specifier|private
specifier|static
name|boolean
name|isVowel
parameter_list|(
specifier|final
name|char
name|c
parameter_list|)
block|{
return|return
name|c
operator|==
literal|'A'
operator|||
name|c
operator|==
literal|'E'
operator|||
name|c
operator|==
literal|'I'
operator|||
name|c
operator|==
literal|'O'
operator|||
name|c
operator|==
literal|'U'
return|;
block|}
comment|/**      * Transcodes the remaining parts of the String. The method operates on a      * sliding window, looking at 4 characters at a time: [i-1, i, i+1, i+2].      *      * @param prev the previous character      * @param curr the current character      * @param next the next character      * @param aNext the after next character      * @return a transcoded array of characters, starting from the current      * position      */
DECL|method|transcodeRemaining
specifier|private
specifier|static
name|char
index|[]
name|transcodeRemaining
parameter_list|(
specifier|final
name|char
name|prev
parameter_list|,
specifier|final
name|char
name|curr
parameter_list|,
specifier|final
name|char
name|next
parameter_list|,
specifier|final
name|char
name|aNext
parameter_list|)
block|{
comment|// 1. EV -> AF
if|if
condition|(
name|curr
operator|==
literal|'E'
operator|&&
name|next
operator|==
literal|'V'
condition|)
block|{
return|return
name|CHARS_AF
return|;
block|}
comment|// A, E, I, O, U -> A
if|if
condition|(
name|isVowel
argument_list|(
name|curr
argument_list|)
condition|)
block|{
return|return
name|CHARS_A
return|;
block|}
comment|// 2. Q -> G, Z -> S, M -> N
if|if
condition|(
name|curr
operator|==
literal|'Q'
condition|)
block|{
return|return
name|CHARS_G
return|;
block|}
elseif|else
if|if
condition|(
name|curr
operator|==
literal|'Z'
condition|)
block|{
return|return
name|CHARS_S
return|;
block|}
elseif|else
if|if
condition|(
name|curr
operator|==
literal|'M'
condition|)
block|{
return|return
name|CHARS_N
return|;
block|}
comment|// 3. KN -> NN else K -> C
if|if
condition|(
name|curr
operator|==
literal|'K'
condition|)
block|{
if|if
condition|(
name|next
operator|==
literal|'N'
condition|)
block|{
return|return
name|CHARS_NN
return|;
block|}
else|else
block|{
return|return
name|CHARS_C
return|;
block|}
block|}
comment|// 4. SCH -> SSS
if|if
condition|(
name|curr
operator|==
literal|'S'
operator|&&
name|next
operator|==
literal|'C'
operator|&&
name|aNext
operator|==
literal|'H'
condition|)
block|{
return|return
name|CHARS_SSS
return|;
block|}
comment|// PH -> FF
if|if
condition|(
name|curr
operator|==
literal|'P'
operator|&&
name|next
operator|==
literal|'H'
condition|)
block|{
return|return
name|CHARS_FF
return|;
block|}
comment|// 5. H -> If previous or next is a non vowel, previous.
if|if
condition|(
name|curr
operator|==
literal|'H'
operator|&&
operator|(
operator|!
name|isVowel
argument_list|(
name|prev
argument_list|)
operator|||
operator|!
name|isVowel
argument_list|(
name|next
argument_list|)
operator|)
condition|)
block|{
return|return
operator|new
name|char
index|[]
block|{
name|prev
block|}
return|;
block|}
comment|// 6. W -> If previous is vowel, previous.
if|if
condition|(
name|curr
operator|==
literal|'W'
operator|&&
name|isVowel
argument_list|(
name|prev
argument_list|)
condition|)
block|{
return|return
operator|new
name|char
index|[]
block|{
name|prev
block|}
return|;
block|}
return|return
operator|new
name|char
index|[]
block|{
name|curr
block|}
return|;
block|}
comment|/**      * Indicates the strict mode.      */
DECL|field|strict
specifier|private
specifier|final
name|boolean
name|strict
decl_stmt|;
comment|/**      * Creates an instance of the {@link Nysiis} encoder with strict mode      * (original form), i.e. encoded strings have a maximum length of 6.      */
DECL|method|Nysiis
specifier|public
name|Nysiis
parameter_list|()
block|{
name|this
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**      * Create an instance of the {@link Nysiis} encoder with the specified      * strict mode:      *      *<ul><li>{@code true}: encoded strings have a maximum length of 6</li><li>{@code false}:      * encoded strings may have arbitrary length</li></ul>      *      * @param strict the strict mode      */
DECL|method|Nysiis
specifier|public
name|Nysiis
parameter_list|(
specifier|final
name|boolean
name|strict
parameter_list|)
block|{
name|this
operator|.
name|strict
operator|=
name|strict
expr_stmt|;
block|}
comment|/**      * Encodes an Object using the NYSIIS algorithm. This method is provided in      * order to satisfy the requirements of the Encoder interface, and will      * throw an {@link EncoderException} if the supplied object is not of type      * {@link String}.      *      * @param obj Object to encode      * @return An object (or a {@link String}) containing the NYSIIS code which      * corresponds to the given String.      * @throws EncoderException if the parameter supplied is not of a {@link String}      * @throws IllegalArgumentException if a character is not mapped      */
annotation|@
name|Override
DECL|method|encode
specifier|public
name|Object
name|encode
parameter_list|(
name|Object
name|obj
parameter_list|)
throws|throws
name|EncoderException
block|{
if|if
condition|(
operator|!
operator|(
name|obj
operator|instanceof
name|String
operator|)
condition|)
block|{
throw|throw
operator|new
name|EncoderException
argument_list|(
literal|"Parameter supplied to Nysiis encode is not of type java.lang.String"
argument_list|)
throw|;
block|}
return|return
name|this
operator|.
name|nysiis
argument_list|(
operator|(
name|String
operator|)
name|obj
argument_list|)
return|;
block|}
comment|/**      * Encodes a String using the NYSIIS algorithm.      *      * @param str A String object to encode      * @return A Nysiis code corresponding to the String supplied      * @throws IllegalArgumentException if a character is not mapped      */
annotation|@
name|Override
DECL|method|encode
specifier|public
name|String
name|encode
parameter_list|(
name|String
name|str
parameter_list|)
block|{
return|return
name|this
operator|.
name|nysiis
argument_list|(
name|str
argument_list|)
return|;
block|}
comment|/**      * Indicates the strict mode for this {@link Nysiis} encoder.      *      * @return {@code true} if the encoder is configured for strict mode, {@code false}      * otherwise      */
DECL|method|isStrict
specifier|public
name|boolean
name|isStrict
parameter_list|()
block|{
return|return
name|this
operator|.
name|strict
return|;
block|}
comment|/**      * Retrieves the NYSIIS code for a given String object.      *      * @param str String to encode using the NYSIIS algorithm      * @return A NYSIIS code for the String supplied      */
DECL|method|nysiis
specifier|public
name|String
name|nysiis
parameter_list|(
name|String
name|str
parameter_list|)
block|{
if|if
condition|(
name|str
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// Use the same clean rules as Soundex
name|str
operator|=
name|clean
argument_list|(
name|str
argument_list|)
expr_stmt|;
if|if
condition|(
name|str
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
name|str
return|;
block|}
comment|// Translate first characters of name:
comment|// MAC -> MCC, KN -> NN, K -> C, PH | PF -> FF, SCH -> SSS
name|str
operator|=
name|PAT_MAC
operator|.
name|matcher
argument_list|(
name|str
argument_list|)
operator|.
name|replaceFirst
argument_list|(
literal|"MCC"
argument_list|)
expr_stmt|;
name|str
operator|=
name|PAT_KN
operator|.
name|matcher
argument_list|(
name|str
argument_list|)
operator|.
name|replaceFirst
argument_list|(
literal|"NN"
argument_list|)
expr_stmt|;
name|str
operator|=
name|PAT_K
operator|.
name|matcher
argument_list|(
name|str
argument_list|)
operator|.
name|replaceFirst
argument_list|(
literal|"C"
argument_list|)
expr_stmt|;
name|str
operator|=
name|PAT_PH_PF
operator|.
name|matcher
argument_list|(
name|str
argument_list|)
operator|.
name|replaceFirst
argument_list|(
literal|"FF"
argument_list|)
expr_stmt|;
name|str
operator|=
name|PAT_SCH
operator|.
name|matcher
argument_list|(
name|str
argument_list|)
operator|.
name|replaceFirst
argument_list|(
literal|"SSS"
argument_list|)
expr_stmt|;
comment|// Translate last characters of name:
comment|// EE -> Y, IE -> Y, DT | RT | RD | NT | ND -> D
name|str
operator|=
name|PAT_EE_IE
operator|.
name|matcher
argument_list|(
name|str
argument_list|)
operator|.
name|replaceFirst
argument_list|(
literal|"Y"
argument_list|)
expr_stmt|;
name|str
operator|=
name|PAT_DT_ETC
operator|.
name|matcher
argument_list|(
name|str
argument_list|)
operator|.
name|replaceFirst
argument_list|(
literal|"D"
argument_list|)
expr_stmt|;
comment|// First character of key = first character of name.
name|StringBuffer
name|key
init|=
operator|new
name|StringBuffer
argument_list|(
name|str
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|key
operator|.
name|append
argument_list|(
name|str
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// Transcode remaining characters, incrementing by one character each time
specifier|final
name|char
index|[]
name|chars
init|=
name|str
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
specifier|final
name|int
name|len
init|=
name|chars
operator|.
name|length
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|len
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|char
name|next
init|=
name|i
operator|<
name|len
operator|-
literal|1
condition|?
name|chars
index|[
name|i
operator|+
literal|1
index|]
else|:
name|SPACE
decl_stmt|;
specifier|final
name|char
name|aNext
init|=
name|i
operator|<
name|len
operator|-
literal|2
condition|?
name|chars
index|[
name|i
operator|+
literal|2
index|]
else|:
name|SPACE
decl_stmt|;
specifier|final
name|char
index|[]
name|transcoded
init|=
name|transcodeRemaining
argument_list|(
name|chars
index|[
name|i
operator|-
literal|1
index|]
argument_list|,
name|chars
index|[
name|i
index|]
argument_list|,
name|next
argument_list|,
name|aNext
argument_list|)
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|transcoded
argument_list|,
literal|0
argument_list|,
name|chars
argument_list|,
name|i
argument_list|,
name|transcoded
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// only append the current char to the key if it is different from the last one
if|if
condition|(
name|chars
index|[
name|i
index|]
operator|!=
name|chars
index|[
name|i
operator|-
literal|1
index|]
condition|)
block|{
name|key
operator|.
name|append
argument_list|(
name|chars
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|key
operator|.
name|length
argument_list|()
operator|>
literal|1
condition|)
block|{
name|char
name|lastChar
init|=
name|key
operator|.
name|charAt
argument_list|(
name|key
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
comment|// If last character is S, remove it.
if|if
condition|(
name|lastChar
operator|==
literal|'S'
condition|)
block|{
name|key
operator|.
name|deleteCharAt
argument_list|(
name|key
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
name|lastChar
operator|=
name|key
operator|.
name|charAt
argument_list|(
name|key
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|key
operator|.
name|length
argument_list|()
operator|>
literal|2
condition|)
block|{
specifier|final
name|char
name|last2Char
init|=
name|key
operator|.
name|charAt
argument_list|(
name|key
operator|.
name|length
argument_list|()
operator|-
literal|2
argument_list|)
decl_stmt|;
comment|// If last characters are AY, replace with Y.
if|if
condition|(
name|last2Char
operator|==
literal|'A'
operator|&&
name|lastChar
operator|==
literal|'Y'
condition|)
block|{
name|key
operator|.
name|deleteCharAt
argument_list|(
name|key
operator|.
name|length
argument_list|()
operator|-
literal|2
argument_list|)
expr_stmt|;
block|}
block|}
comment|// If last character is A, remove it.
if|if
condition|(
name|lastChar
operator|==
literal|'A'
condition|)
block|{
name|key
operator|.
name|deleteCharAt
argument_list|(
name|key
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|String
name|string
init|=
name|key
operator|.
name|toString
argument_list|()
decl_stmt|;
return|return
name|this
operator|.
name|isStrict
argument_list|()
condition|?
name|string
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|Math
operator|.
name|min
argument_list|(
name|TRUE_LENGTH
argument_list|,
name|string
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
else|:
name|string
return|;
block|}
DECL|method|clean
specifier|static
name|String
name|clean
parameter_list|(
name|String
name|str
parameter_list|)
block|{
if|if
condition|(
name|str
operator|==
literal|null
operator|||
name|str
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
name|str
return|;
block|}
name|int
name|len
init|=
name|str
operator|.
name|length
argument_list|()
decl_stmt|;
name|char
index|[]
name|chars
init|=
operator|new
name|char
index|[
name|len
index|]
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|len
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|Character
operator|.
name|isLetter
argument_list|(
name|str
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
argument_list|)
condition|)
block|{
name|chars
index|[
name|count
operator|++
index|]
operator|=
name|str
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|count
operator|==
name|len
condition|)
block|{
return|return
name|str
operator|.
name|toUpperCase
argument_list|(
name|java
operator|.
name|util
operator|.
name|Locale
operator|.
name|ENGLISH
argument_list|)
return|;
block|}
return|return
operator|new
name|String
argument_list|(
name|chars
argument_list|,
literal|0
argument_list|,
name|count
argument_list|)
operator|.
name|toUpperCase
argument_list|(
name|java
operator|.
name|util
operator|.
name|Locale
operator|.
name|ENGLISH
argument_list|)
return|;
block|}
block|}
end_class

end_unit

