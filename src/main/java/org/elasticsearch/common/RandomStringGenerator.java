begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
package|;
end_package

begin_import
import|import
name|jsr166y
operator|.
name|ThreadLocalRandom
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_class
DECL|class|RandomStringGenerator
specifier|public
class|class
name|RandomStringGenerator
block|{
comment|/**      *<p><code>RandomStringUtils</code> instances should NOT be constructed in      * standard programming. Instead, the class should be used as      *<code>RandomStringUtils.random(5);</code>.</p>      *<p/>      *<p>This constructor is public to permit tools that require a JavaBean instance      * to operate.</p>      */
DECL|method|RandomStringGenerator
specifier|public
name|RandomStringGenerator
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|// Random
comment|//-----------------------------------------------------------------------
comment|/**      *<p>Creates a random string whose length is the number of characters      * specified.</p>      *<p/>      *<p>Characters will be chosen from the set of all characters.</p>      *      * @param count the length of random string to create      * @return the random string      */
DECL|method|random
specifier|public
specifier|static
name|String
name|random
parameter_list|(
name|int
name|count
parameter_list|)
block|{
return|return
name|random
argument_list|(
name|count
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**      *<p>Creates a random string whose length is the number of characters      * specified.</p>      *<p/>      *<p>Characters will be chosen from the set of characters whose      * ASCII value is between<code>32</code> and<code>126</code> (inclusive).</p>      *      * @param count the length of random string to create      * @return the random string      */
DECL|method|randomAscii
specifier|public
specifier|static
name|String
name|randomAscii
parameter_list|(
name|int
name|count
parameter_list|)
block|{
return|return
name|random
argument_list|(
name|count
argument_list|,
literal|32
argument_list|,
literal|127
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**      *<p>Creates a random string whose length is the number of characters      * specified.</p>      *<p/>      *<p>Characters will be chosen from the set of alphabetic      * characters.</p>      *      * @param count the length of random string to create      * @return the random string      */
DECL|method|randomAlphabetic
specifier|public
specifier|static
name|String
name|randomAlphabetic
parameter_list|(
name|int
name|count
parameter_list|)
block|{
return|return
name|random
argument_list|(
name|count
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**      *<p>Creates a random string whose length is the number of characters      * specified.</p>      *<p/>      *<p>Characters will be chosen from the set of alpha-numeric      * characters.</p>      *      * @param count the length of random string to create      * @return the random string      */
DECL|method|randomAlphanumeric
specifier|public
specifier|static
name|String
name|randomAlphanumeric
parameter_list|(
name|int
name|count
parameter_list|)
block|{
return|return
name|random
argument_list|(
name|count
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
return|;
block|}
comment|/**      *<p>Creates a random string whose length is the number of characters      * specified.</p>      *<p/>      *<p>Characters will be chosen from the set of numeric      * characters.</p>      *      * @param count the length of random string to create      * @return the random string      */
DECL|method|randomNumeric
specifier|public
specifier|static
name|String
name|randomNumeric
parameter_list|(
name|int
name|count
parameter_list|)
block|{
return|return
name|random
argument_list|(
name|count
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
return|;
block|}
comment|/**      *<p>Creates a random string whose length is the number of characters      * specified.</p>      *<p/>      *<p>Characters will be chosen from the set of alpha-numeric      * characters as indicated by the arguments.</p>      *      * @param count   the length of random string to create      * @param letters if<code>true</code>, generated string will include      *                alphabetic characters      * @param numbers if<code>true</code>, generated string will include      *                numeric characters      * @return the random string      */
DECL|method|random
specifier|public
specifier|static
name|String
name|random
parameter_list|(
name|int
name|count
parameter_list|,
name|boolean
name|letters
parameter_list|,
name|boolean
name|numbers
parameter_list|)
block|{
return|return
name|random
argument_list|(
name|count
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
name|letters
argument_list|,
name|numbers
argument_list|)
return|;
block|}
comment|/**      *<p>Creates a random string whose length is the number of characters      * specified.</p>      *<p/>      *<p>Characters will be chosen from the set of alpha-numeric      * characters as indicated by the arguments.</p>      *      * @param count   the length of random string to create      * @param start   the position in set of chars to start at      * @param end     the position in set of chars to end before      * @param letters if<code>true</code>, generated string will include      *                alphabetic characters      * @param numbers if<code>true</code>, generated string will include      *                numeric characters      * @return the random string      */
DECL|method|random
specifier|public
specifier|static
name|String
name|random
parameter_list|(
name|int
name|count
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|,
name|boolean
name|letters
parameter_list|,
name|boolean
name|numbers
parameter_list|)
block|{
return|return
name|random
argument_list|(
name|count
argument_list|,
name|start
argument_list|,
name|end
argument_list|,
name|letters
argument_list|,
name|numbers
argument_list|,
literal|null
argument_list|,
name|ThreadLocalRandom
operator|.
name|current
argument_list|()
argument_list|)
return|;
block|}
comment|/**      *<p>Creates a random string based on a variety of options, using      * default source of randomness.</p>      *<p/>      *<p>This method has exactly the same semantics as      * {@link #random(int, int, int, boolean, boolean, char[], Random)}, but      * instead of using an externally supplied source of randomness, it uses      * the internal static {@link Random} instance.</p>      *      * @param count   the length of random string to create      * @param start   the position in set of chars to start at      * @param end     the position in set of chars to end before      * @param letters only allow letters?      * @param numbers only allow numbers?      * @param chars   the set of chars to choose randoms from.      *                If<code>null</code>, then it will use the set of all chars.      * @return the random string      * @throws ArrayIndexOutOfBoundsException if there are not      *<code>(end - start) + 1</code> characters in the set array.      */
DECL|method|random
specifier|public
specifier|static
name|String
name|random
parameter_list|(
name|int
name|count
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|,
name|boolean
name|letters
parameter_list|,
name|boolean
name|numbers
parameter_list|,
name|char
index|[]
name|chars
parameter_list|)
block|{
return|return
name|random
argument_list|(
name|count
argument_list|,
name|start
argument_list|,
name|end
argument_list|,
name|letters
argument_list|,
name|numbers
argument_list|,
name|chars
argument_list|,
name|ThreadLocalRandom
operator|.
name|current
argument_list|()
argument_list|)
return|;
block|}
comment|/**      *<p>Creates a random string based on a variety of options, using      * supplied source of randomness.</p>      *<p/>      *<p>If start and end are both<code>0</code>, start and end are set      * to<code>' '</code> and<code>'z'</code>, the ASCII printable      * characters, will be used, unless letters and numbers are both      *<code>false</code>, in which case, start and end are set to      *<code>0</code> and<code>Integer.MAX_VALUE</code>.      *<p/>      *<p>If set is not<code>null</code>, characters between start and      * end are chosen.</p>      *<p/>      *<p>This method accepts a user-supplied {@link Random}      * instance to use as a source of randomness. By seeding a single      * {@link Random} instance with a fixed seed and using it for each call,      * the same random sequence of strings can be generated repeatedly      * and predictably.</p>      *      * @param count   the length of random string to create      * @param start   the position in set of chars to start at      * @param end     the position in set of chars to end before      * @param letters only allow letters?      * @param numbers only allow numbers?      * @param chars   the set of chars to choose randoms from.      *                If<code>null</code>, then it will use the set of all chars.      * @param random  a source of randomness.      * @return the random string      * @throws ArrayIndexOutOfBoundsException if there are not      *<code>(end - start) + 1</code> characters in the set array.      * @throws IllegalArgumentException       if<code>count</code>&lt; 0.      * @since 2.0      */
DECL|method|random
specifier|public
specifier|static
name|String
name|random
parameter_list|(
name|int
name|count
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|,
name|boolean
name|letters
parameter_list|,
name|boolean
name|numbers
parameter_list|,
name|char
index|[]
name|chars
parameter_list|,
name|Random
name|random
parameter_list|)
block|{
if|if
condition|(
name|count
operator|==
literal|0
condition|)
block|{
return|return
literal|""
return|;
block|}
elseif|else
if|if
condition|(
name|count
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Requested random string length "
operator|+
name|count
operator|+
literal|" is less than 0."
argument_list|)
throw|;
block|}
if|if
condition|(
operator|(
name|start
operator|==
literal|0
operator|)
operator|&&
operator|(
name|end
operator|==
literal|0
operator|)
condition|)
block|{
name|end
operator|=
literal|'z'
operator|+
literal|1
expr_stmt|;
name|start
operator|=
literal|' '
expr_stmt|;
if|if
condition|(
operator|!
name|letters
operator|&&
operator|!
name|numbers
condition|)
block|{
name|start
operator|=
literal|0
expr_stmt|;
name|end
operator|=
name|Integer
operator|.
name|MAX_VALUE
expr_stmt|;
block|}
block|}
name|char
index|[]
name|buffer
init|=
operator|new
name|char
index|[
name|count
index|]
decl_stmt|;
name|int
name|gap
init|=
name|end
operator|-
name|start
decl_stmt|;
while|while
condition|(
name|count
operator|--
operator|!=
literal|0
condition|)
block|{
name|char
name|ch
decl_stmt|;
if|if
condition|(
name|chars
operator|==
literal|null
condition|)
block|{
name|ch
operator|=
call|(
name|char
call|)
argument_list|(
name|random
operator|.
name|nextInt
argument_list|(
name|gap
argument_list|)
operator|+
name|start
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ch
operator|=
name|chars
index|[
name|random
operator|.
name|nextInt
argument_list|(
name|gap
argument_list|)
operator|+
name|start
index|]
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|letters
operator|&&
name|Character
operator|.
name|isLetter
argument_list|(
name|ch
argument_list|)
operator|)
operator|||
operator|(
name|numbers
operator|&&
name|Character
operator|.
name|isDigit
argument_list|(
name|ch
argument_list|)
operator|)
operator|||
operator|(
operator|!
name|letters
operator|&&
operator|!
name|numbers
operator|)
condition|)
block|{
if|if
condition|(
name|ch
operator|>=
literal|56320
operator|&&
name|ch
operator|<=
literal|57343
condition|)
block|{
if|if
condition|(
name|count
operator|==
literal|0
condition|)
block|{
name|count
operator|++
expr_stmt|;
block|}
else|else
block|{
comment|// low surrogate, insert high surrogate after putting it in
name|buffer
index|[
name|count
index|]
operator|=
name|ch
expr_stmt|;
name|count
operator|--
expr_stmt|;
name|buffer
index|[
name|count
index|]
operator|=
call|(
name|char
call|)
argument_list|(
literal|55296
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|128
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|ch
operator|>=
literal|55296
operator|&&
name|ch
operator|<=
literal|56191
condition|)
block|{
if|if
condition|(
name|count
operator|==
literal|0
condition|)
block|{
name|count
operator|++
expr_stmt|;
block|}
else|else
block|{
comment|// high surrogate, insert low surrogate before putting it in
name|buffer
index|[
name|count
index|]
operator|=
call|(
name|char
call|)
argument_list|(
literal|56320
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|128
argument_list|)
argument_list|)
expr_stmt|;
name|count
operator|--
expr_stmt|;
name|buffer
index|[
name|count
index|]
operator|=
name|ch
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|ch
operator|>=
literal|56192
operator|&&
name|ch
operator|<=
literal|56319
condition|)
block|{
comment|// private high surrogate, no effing clue, so skip it
name|count
operator|++
expr_stmt|;
block|}
else|else
block|{
name|buffer
index|[
name|count
index|]
operator|=
name|ch
expr_stmt|;
block|}
block|}
else|else
block|{
name|count
operator|++
expr_stmt|;
block|}
block|}
return|return
operator|new
name|String
argument_list|(
name|buffer
argument_list|)
return|;
block|}
comment|/**      *<p>Creates a random string whose length is the number of characters      * specified.</p>      *<p/>      *<p>Characters will be chosen from the set of characters      * specified.</p>      *      * @param count the length of random string to create      * @param chars the String containing the set of characters to use,      *              may be null      * @return the random string      * @throws IllegalArgumentException if<code>count</code>&lt; 0.      */
DECL|method|random
specifier|public
specifier|static
name|String
name|random
parameter_list|(
name|int
name|count
parameter_list|,
name|String
name|chars
parameter_list|)
block|{
if|if
condition|(
name|chars
operator|==
literal|null
condition|)
block|{
return|return
name|random
argument_list|(
name|count
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|,
name|ThreadLocalRandom
operator|.
name|current
argument_list|()
argument_list|)
return|;
block|}
return|return
name|random
argument_list|(
name|count
argument_list|,
name|chars
operator|.
name|toCharArray
argument_list|()
argument_list|)
return|;
block|}
comment|/**      *<p>Creates a random string whose length is the number of characters      * specified.</p>      *<p/>      *<p>Characters will be chosen from the set of characters specified.</p>      *      * @param count the length of random string to create      * @param chars the character array containing the set of characters to use,      *              may be null      * @return the random string      * @throws IllegalArgumentException if<code>count</code>&lt; 0.      */
DECL|method|random
specifier|public
specifier|static
name|String
name|random
parameter_list|(
name|int
name|count
parameter_list|,
name|char
index|[]
name|chars
parameter_list|)
block|{
if|if
condition|(
name|chars
operator|==
literal|null
condition|)
block|{
return|return
name|random
argument_list|(
name|count
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|,
name|ThreadLocalRandom
operator|.
name|current
argument_list|()
argument_list|)
return|;
block|}
return|return
name|random
argument_list|(
name|count
argument_list|,
literal|0
argument_list|,
name|chars
operator|.
name|length
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
name|chars
argument_list|,
name|ThreadLocalRandom
operator|.
name|current
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

