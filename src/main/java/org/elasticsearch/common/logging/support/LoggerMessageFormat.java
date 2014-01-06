begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.logging.support
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|logging
operator|.
name|support
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|LoggerMessageFormat
specifier|public
class|class
name|LoggerMessageFormat
block|{
DECL|field|DELIM_START
specifier|static
specifier|final
name|char
name|DELIM_START
init|=
literal|'{'
decl_stmt|;
DECL|field|DELIM_STOP
specifier|static
specifier|final
name|char
name|DELIM_STOP
init|=
literal|'}'
decl_stmt|;
DECL|field|DELIM_STR
specifier|static
specifier|final
name|String
name|DELIM_STR
init|=
literal|"{}"
decl_stmt|;
DECL|field|ESCAPE_CHAR
specifier|private
specifier|static
specifier|final
name|char
name|ESCAPE_CHAR
init|=
literal|'\\'
decl_stmt|;
DECL|method|format
specifier|public
specifier|static
name|String
name|format
parameter_list|(
specifier|final
name|String
name|messagePattern
parameter_list|,
specifier|final
name|Object
modifier|...
name|argArray
parameter_list|)
block|{
return|return
name|format
argument_list|(
literal|null
argument_list|,
name|messagePattern
argument_list|,
name|argArray
argument_list|)
return|;
block|}
DECL|method|format
specifier|public
specifier|static
name|String
name|format
parameter_list|(
specifier|final
name|String
name|prefix
parameter_list|,
specifier|final
name|String
name|messagePattern
parameter_list|,
specifier|final
name|Object
modifier|...
name|argArray
parameter_list|)
block|{
if|if
condition|(
name|messagePattern
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|argArray
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|prefix
operator|==
literal|null
condition|)
block|{
return|return
name|messagePattern
return|;
block|}
else|else
block|{
return|return
name|prefix
operator|+
name|messagePattern
return|;
block|}
block|}
name|int
name|i
init|=
literal|0
decl_stmt|;
name|int
name|j
decl_stmt|;
specifier|final
name|StringBuilder
name|sbuf
init|=
operator|new
name|StringBuilder
argument_list|(
name|messagePattern
operator|.
name|length
argument_list|()
operator|+
literal|50
argument_list|)
decl_stmt|;
if|if
condition|(
name|prefix
operator|!=
literal|null
condition|)
block|{
name|sbuf
operator|.
name|append
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|L
init|=
literal|0
init|;
name|L
operator|<
name|argArray
operator|.
name|length
condition|;
name|L
operator|++
control|)
block|{
name|j
operator|=
name|messagePattern
operator|.
name|indexOf
argument_list|(
name|DELIM_STR
argument_list|,
name|i
argument_list|)
expr_stmt|;
if|if
condition|(
name|j
operator|==
operator|-
literal|1
condition|)
block|{
comment|// no more variables
if|if
condition|(
name|i
operator|==
literal|0
condition|)
block|{
comment|// this is a simple string
return|return
name|messagePattern
return|;
block|}
else|else
block|{
comment|// add the tail string which contains no variables and return
comment|// the result.
name|sbuf
operator|.
name|append
argument_list|(
name|messagePattern
operator|.
name|substring
argument_list|(
name|i
argument_list|,
name|messagePattern
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|sbuf
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|isEscapedDelimeter
argument_list|(
name|messagePattern
argument_list|,
name|j
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|isDoubleEscaped
argument_list|(
name|messagePattern
argument_list|,
name|j
argument_list|)
condition|)
block|{
name|L
operator|--
expr_stmt|;
comment|// DELIM_START was escaped, thus should not be incremented
name|sbuf
operator|.
name|append
argument_list|(
name|messagePattern
operator|.
name|substring
argument_list|(
name|i
argument_list|,
name|j
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|sbuf
operator|.
name|append
argument_list|(
name|DELIM_START
argument_list|)
expr_stmt|;
name|i
operator|=
name|j
operator|+
literal|1
expr_stmt|;
block|}
else|else
block|{
comment|// The escape character preceding the delimiter start is
comment|// itself escaped: "abc x:\\{}"
comment|// we have to consume one backward slash
name|sbuf
operator|.
name|append
argument_list|(
name|messagePattern
operator|.
name|substring
argument_list|(
name|i
argument_list|,
name|j
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|deeplyAppendParameter
argument_list|(
name|sbuf
argument_list|,
name|argArray
index|[
name|L
index|]
argument_list|,
operator|new
name|HashMap
argument_list|()
argument_list|)
expr_stmt|;
name|i
operator|=
name|j
operator|+
literal|2
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// normal case
name|sbuf
operator|.
name|append
argument_list|(
name|messagePattern
operator|.
name|substring
argument_list|(
name|i
argument_list|,
name|j
argument_list|)
argument_list|)
expr_stmt|;
name|deeplyAppendParameter
argument_list|(
name|sbuf
argument_list|,
name|argArray
index|[
name|L
index|]
argument_list|,
operator|new
name|HashMap
argument_list|()
argument_list|)
expr_stmt|;
name|i
operator|=
name|j
operator|+
literal|2
expr_stmt|;
block|}
block|}
block|}
comment|// append the characters following the last {} pair.
name|sbuf
operator|.
name|append
argument_list|(
name|messagePattern
operator|.
name|substring
argument_list|(
name|i
argument_list|,
name|messagePattern
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|sbuf
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|isEscapedDelimeter
specifier|static
name|boolean
name|isEscapedDelimeter
parameter_list|(
name|String
name|messagePattern
parameter_list|,
name|int
name|delimeterStartIndex
parameter_list|)
block|{
if|if
condition|(
name|delimeterStartIndex
operator|==
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
name|char
name|potentialEscape
init|=
name|messagePattern
operator|.
name|charAt
argument_list|(
name|delimeterStartIndex
operator|-
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|potentialEscape
operator|==
name|ESCAPE_CHAR
condition|)
block|{
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
DECL|method|isDoubleEscaped
specifier|static
name|boolean
name|isDoubleEscaped
parameter_list|(
name|String
name|messagePattern
parameter_list|,
name|int
name|delimeterStartIndex
parameter_list|)
block|{
if|if
condition|(
name|delimeterStartIndex
operator|>=
literal|2
operator|&&
name|messagePattern
operator|.
name|charAt
argument_list|(
name|delimeterStartIndex
operator|-
literal|2
argument_list|)
operator|==
name|ESCAPE_CHAR
condition|)
block|{
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
DECL|method|deeplyAppendParameter
specifier|private
specifier|static
name|void
name|deeplyAppendParameter
parameter_list|(
name|StringBuilder
name|sbuf
parameter_list|,
name|Object
name|o
parameter_list|,
name|Map
name|seenMap
parameter_list|)
block|{
if|if
condition|(
name|o
operator|==
literal|null
condition|)
block|{
name|sbuf
operator|.
name|append
argument_list|(
literal|"null"
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
operator|!
name|o
operator|.
name|getClass
argument_list|()
operator|.
name|isArray
argument_list|()
condition|)
block|{
name|safeObjectAppend
argument_list|(
name|sbuf
argument_list|,
name|o
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// check for primitive array types because they
comment|// unfortunately cannot be cast to Object[]
if|if
condition|(
name|o
operator|instanceof
name|boolean
index|[]
condition|)
block|{
name|booleanArrayAppend
argument_list|(
name|sbuf
argument_list|,
operator|(
name|boolean
index|[]
operator|)
name|o
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|o
operator|instanceof
name|byte
index|[]
condition|)
block|{
name|byteArrayAppend
argument_list|(
name|sbuf
argument_list|,
operator|(
name|byte
index|[]
operator|)
name|o
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|o
operator|instanceof
name|char
index|[]
condition|)
block|{
name|charArrayAppend
argument_list|(
name|sbuf
argument_list|,
operator|(
name|char
index|[]
operator|)
name|o
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|o
operator|instanceof
name|short
index|[]
condition|)
block|{
name|shortArrayAppend
argument_list|(
name|sbuf
argument_list|,
operator|(
name|short
index|[]
operator|)
name|o
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|o
operator|instanceof
name|int
index|[]
condition|)
block|{
name|intArrayAppend
argument_list|(
name|sbuf
argument_list|,
operator|(
name|int
index|[]
operator|)
name|o
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|o
operator|instanceof
name|long
index|[]
condition|)
block|{
name|longArrayAppend
argument_list|(
name|sbuf
argument_list|,
operator|(
name|long
index|[]
operator|)
name|o
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|o
operator|instanceof
name|float
index|[]
condition|)
block|{
name|floatArrayAppend
argument_list|(
name|sbuf
argument_list|,
operator|(
name|float
index|[]
operator|)
name|o
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|o
operator|instanceof
name|double
index|[]
condition|)
block|{
name|doubleArrayAppend
argument_list|(
name|sbuf
argument_list|,
operator|(
name|double
index|[]
operator|)
name|o
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|objectArrayAppend
argument_list|(
name|sbuf
argument_list|,
operator|(
name|Object
index|[]
operator|)
name|o
argument_list|,
name|seenMap
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|safeObjectAppend
specifier|private
specifier|static
name|void
name|safeObjectAppend
parameter_list|(
name|StringBuilder
name|sbuf
parameter_list|,
name|Object
name|o
parameter_list|)
block|{
try|try
block|{
name|String
name|oAsString
init|=
name|o
operator|.
name|toString
argument_list|()
decl_stmt|;
name|sbuf
operator|.
name|append
argument_list|(
name|oAsString
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|sbuf
operator|.
name|append
argument_list|(
literal|"[FAILED toString()]"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|objectArrayAppend
specifier|private
specifier|static
name|void
name|objectArrayAppend
parameter_list|(
name|StringBuilder
name|sbuf
parameter_list|,
name|Object
index|[]
name|a
parameter_list|,
name|Map
name|seenMap
parameter_list|)
block|{
name|sbuf
operator|.
name|append
argument_list|(
literal|'['
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|seenMap
operator|.
name|containsKey
argument_list|(
name|a
argument_list|)
condition|)
block|{
name|seenMap
operator|.
name|put
argument_list|(
name|a
argument_list|,
literal|null
argument_list|)
expr_stmt|;
specifier|final
name|int
name|len
init|=
name|a
operator|.
name|length
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
name|deeplyAppendParameter
argument_list|(
name|sbuf
argument_list|,
name|a
index|[
name|i
index|]
argument_list|,
name|seenMap
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|!=
name|len
operator|-
literal|1
condition|)
name|sbuf
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
comment|// allow repeats in siblings
name|seenMap
operator|.
name|remove
argument_list|(
name|a
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sbuf
operator|.
name|append
argument_list|(
literal|"..."
argument_list|)
expr_stmt|;
block|}
name|sbuf
operator|.
name|append
argument_list|(
literal|']'
argument_list|)
expr_stmt|;
block|}
DECL|method|booleanArrayAppend
specifier|private
specifier|static
name|void
name|booleanArrayAppend
parameter_list|(
name|StringBuilder
name|sbuf
parameter_list|,
name|boolean
index|[]
name|a
parameter_list|)
block|{
name|sbuf
operator|.
name|append
argument_list|(
literal|'['
argument_list|)
expr_stmt|;
specifier|final
name|int
name|len
init|=
name|a
operator|.
name|length
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
name|sbuf
operator|.
name|append
argument_list|(
name|a
index|[
name|i
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|!=
name|len
operator|-
literal|1
condition|)
name|sbuf
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|sbuf
operator|.
name|append
argument_list|(
literal|']'
argument_list|)
expr_stmt|;
block|}
DECL|method|byteArrayAppend
specifier|private
specifier|static
name|void
name|byteArrayAppend
parameter_list|(
name|StringBuilder
name|sbuf
parameter_list|,
name|byte
index|[]
name|a
parameter_list|)
block|{
name|sbuf
operator|.
name|append
argument_list|(
literal|'['
argument_list|)
expr_stmt|;
specifier|final
name|int
name|len
init|=
name|a
operator|.
name|length
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
name|sbuf
operator|.
name|append
argument_list|(
name|a
index|[
name|i
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|!=
name|len
operator|-
literal|1
condition|)
name|sbuf
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|sbuf
operator|.
name|append
argument_list|(
literal|']'
argument_list|)
expr_stmt|;
block|}
DECL|method|charArrayAppend
specifier|private
specifier|static
name|void
name|charArrayAppend
parameter_list|(
name|StringBuilder
name|sbuf
parameter_list|,
name|char
index|[]
name|a
parameter_list|)
block|{
name|sbuf
operator|.
name|append
argument_list|(
literal|'['
argument_list|)
expr_stmt|;
specifier|final
name|int
name|len
init|=
name|a
operator|.
name|length
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
name|sbuf
operator|.
name|append
argument_list|(
name|a
index|[
name|i
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|!=
name|len
operator|-
literal|1
condition|)
name|sbuf
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|sbuf
operator|.
name|append
argument_list|(
literal|']'
argument_list|)
expr_stmt|;
block|}
DECL|method|shortArrayAppend
specifier|private
specifier|static
name|void
name|shortArrayAppend
parameter_list|(
name|StringBuilder
name|sbuf
parameter_list|,
name|short
index|[]
name|a
parameter_list|)
block|{
name|sbuf
operator|.
name|append
argument_list|(
literal|'['
argument_list|)
expr_stmt|;
specifier|final
name|int
name|len
init|=
name|a
operator|.
name|length
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
name|sbuf
operator|.
name|append
argument_list|(
name|a
index|[
name|i
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|!=
name|len
operator|-
literal|1
condition|)
name|sbuf
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|sbuf
operator|.
name|append
argument_list|(
literal|']'
argument_list|)
expr_stmt|;
block|}
DECL|method|intArrayAppend
specifier|private
specifier|static
name|void
name|intArrayAppend
parameter_list|(
name|StringBuilder
name|sbuf
parameter_list|,
name|int
index|[]
name|a
parameter_list|)
block|{
name|sbuf
operator|.
name|append
argument_list|(
literal|'['
argument_list|)
expr_stmt|;
specifier|final
name|int
name|len
init|=
name|a
operator|.
name|length
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
name|sbuf
operator|.
name|append
argument_list|(
name|a
index|[
name|i
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|!=
name|len
operator|-
literal|1
condition|)
name|sbuf
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|sbuf
operator|.
name|append
argument_list|(
literal|']'
argument_list|)
expr_stmt|;
block|}
DECL|method|longArrayAppend
specifier|private
specifier|static
name|void
name|longArrayAppend
parameter_list|(
name|StringBuilder
name|sbuf
parameter_list|,
name|long
index|[]
name|a
parameter_list|)
block|{
name|sbuf
operator|.
name|append
argument_list|(
literal|'['
argument_list|)
expr_stmt|;
specifier|final
name|int
name|len
init|=
name|a
operator|.
name|length
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
name|sbuf
operator|.
name|append
argument_list|(
name|a
index|[
name|i
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|!=
name|len
operator|-
literal|1
condition|)
name|sbuf
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|sbuf
operator|.
name|append
argument_list|(
literal|']'
argument_list|)
expr_stmt|;
block|}
DECL|method|floatArrayAppend
specifier|private
specifier|static
name|void
name|floatArrayAppend
parameter_list|(
name|StringBuilder
name|sbuf
parameter_list|,
name|float
index|[]
name|a
parameter_list|)
block|{
name|sbuf
operator|.
name|append
argument_list|(
literal|'['
argument_list|)
expr_stmt|;
specifier|final
name|int
name|len
init|=
name|a
operator|.
name|length
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
name|sbuf
operator|.
name|append
argument_list|(
name|a
index|[
name|i
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|!=
name|len
operator|-
literal|1
condition|)
name|sbuf
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|sbuf
operator|.
name|append
argument_list|(
literal|']'
argument_list|)
expr_stmt|;
block|}
DECL|method|doubleArrayAppend
specifier|private
specifier|static
name|void
name|doubleArrayAppend
parameter_list|(
name|StringBuilder
name|sbuf
parameter_list|,
name|double
index|[]
name|a
parameter_list|)
block|{
name|sbuf
operator|.
name|append
argument_list|(
literal|'['
argument_list|)
expr_stmt|;
specifier|final
name|int
name|len
init|=
name|a
operator|.
name|length
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
name|sbuf
operator|.
name|append
argument_list|(
name|a
index|[
name|i
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|!=
name|len
operator|-
literal|1
condition|)
name|sbuf
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|sbuf
operator|.
name|append
argument_list|(
literal|']'
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

