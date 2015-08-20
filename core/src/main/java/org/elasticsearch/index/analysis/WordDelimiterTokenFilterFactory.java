begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.analysis
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|analysis
package|;
end_package

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
name|miscellaneous
operator|.
name|Lucene47WordDelimiterFilter
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
name|miscellaneous
operator|.
name|WordDelimiterFilter
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
name|miscellaneous
operator|.
name|WordDelimiterIterator
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
name|util
operator|.
name|CharArraySet
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
name|inject
operator|.
name|Inject
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
name|inject
operator|.
name|assistedinject
operator|.
name|Assisted
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
name|settings
operator|.
name|Settings
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|env
operator|.
name|Environment
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|Index
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|settings
operator|.
name|IndexSettings
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
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
name|Matcher
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

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|miscellaneous
operator|.
name|WordDelimiterFilter
operator|.
name|*
import|;
end_import

begin_class
DECL|class|WordDelimiterTokenFilterFactory
specifier|public
class|class
name|WordDelimiterTokenFilterFactory
extends|extends
name|AbstractTokenFilterFactory
block|{
DECL|field|charTypeTable
specifier|private
specifier|final
name|byte
index|[]
name|charTypeTable
decl_stmt|;
DECL|field|flags
specifier|private
specifier|final
name|int
name|flags
decl_stmt|;
DECL|field|protoWords
specifier|private
specifier|final
name|CharArraySet
name|protoWords
decl_stmt|;
annotation|@
name|Inject
DECL|method|WordDelimiterTokenFilterFactory
specifier|public
name|WordDelimiterTokenFilterFactory
parameter_list|(
name|Index
name|index
parameter_list|,
annotation|@
name|IndexSettings
name|Settings
name|indexSettings
parameter_list|,
name|Environment
name|env
parameter_list|,
annotation|@
name|Assisted
name|String
name|name
parameter_list|,
annotation|@
name|Assisted
name|Settings
name|settings
parameter_list|)
block|{
name|super
argument_list|(
name|index
argument_list|,
name|indexSettings
argument_list|,
name|name
argument_list|,
name|settings
argument_list|)
expr_stmt|;
comment|// Sample Format for the type table:
comment|// $ => DIGIT
comment|// % => DIGIT
comment|// . => DIGIT
comment|// \u002C => DIGIT
comment|// \u200D => ALPHANUM
name|List
argument_list|<
name|String
argument_list|>
name|charTypeTableValues
init|=
name|Analysis
operator|.
name|getWordList
argument_list|(
name|env
argument_list|,
name|settings
argument_list|,
literal|"type_table"
argument_list|)
decl_stmt|;
if|if
condition|(
name|charTypeTableValues
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|charTypeTable
operator|=
name|WordDelimiterIterator
operator|.
name|DEFAULT_WORD_DELIM_TABLE
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|charTypeTable
operator|=
name|parseTypes
argument_list|(
name|charTypeTableValues
argument_list|)
expr_stmt|;
block|}
name|int
name|flags
init|=
literal|0
decl_stmt|;
comment|// If set, causes parts of words to be generated: "PowerShot" => "Power" "Shot"
name|flags
operator||=
name|getFlag
argument_list|(
name|GENERATE_WORD_PARTS
argument_list|,
name|settings
argument_list|,
literal|"generate_word_parts"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// If set, causes number subwords to be generated: "500-42" => "500" "42"
name|flags
operator||=
name|getFlag
argument_list|(
name|GENERATE_NUMBER_PARTS
argument_list|,
name|settings
argument_list|,
literal|"generate_number_parts"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// 1, causes maximum runs of word parts to be catenated: "wi-fi" => "wifi"
name|flags
operator||=
name|getFlag
argument_list|(
name|CATENATE_WORDS
argument_list|,
name|settings
argument_list|,
literal|"catenate_words"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// If set, causes maximum runs of number parts to be catenated: "500-42" => "50042"
name|flags
operator||=
name|getFlag
argument_list|(
name|CATENATE_NUMBERS
argument_list|,
name|settings
argument_list|,
literal|"catenate_numbers"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// If set, causes all subword parts to be catenated: "wi-fi-4000" => "wifi4000"
name|flags
operator||=
name|getFlag
argument_list|(
name|CATENATE_ALL
argument_list|,
name|settings
argument_list|,
literal|"catenate_all"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// 1, causes "PowerShot" to be two tokens; ("Power-Shot" remains two parts regards)
name|flags
operator||=
name|getFlag
argument_list|(
name|SPLIT_ON_CASE_CHANGE
argument_list|,
name|settings
argument_list|,
literal|"split_on_case_change"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// If set, includes original words in subwords: "500-42" => "500" "42" "500-42"
name|flags
operator||=
name|getFlag
argument_list|(
name|PRESERVE_ORIGINAL
argument_list|,
name|settings
argument_list|,
literal|"preserve_original"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// 1, causes "j2se" to be three tokens; "j" "2" "se"
name|flags
operator||=
name|getFlag
argument_list|(
name|SPLIT_ON_NUMERICS
argument_list|,
name|settings
argument_list|,
literal|"split_on_numerics"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// If set, causes trailing "'s" to be removed for each subword: "O'Neil's" => "O", "Neil"
name|flags
operator||=
name|getFlag
argument_list|(
name|STEM_ENGLISH_POSSESSIVE
argument_list|,
name|settings
argument_list|,
literal|"stem_english_possessive"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// If not null is the set of tokens to protect from being delimited
name|Set
argument_list|<
name|?
argument_list|>
name|protectedWords
init|=
name|Analysis
operator|.
name|getWordSet
argument_list|(
name|env
argument_list|,
name|settings
argument_list|,
literal|"protected_words"
argument_list|)
decl_stmt|;
name|this
operator|.
name|protoWords
operator|=
name|protectedWords
operator|==
literal|null
condition|?
literal|null
else|:
name|CharArraySet
operator|.
name|copy
argument_list|(
name|protectedWords
argument_list|)
expr_stmt|;
name|this
operator|.
name|flags
operator|=
name|flags
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|create
specifier|public
name|TokenStream
name|create
parameter_list|(
name|TokenStream
name|tokenStream
parameter_list|)
block|{
if|if
condition|(
name|version
operator|.
name|onOrAfter
argument_list|(
name|Version
operator|.
name|LUCENE_4_8
argument_list|)
condition|)
block|{
return|return
operator|new
name|WordDelimiterFilter
argument_list|(
name|tokenStream
argument_list|,
name|charTypeTable
argument_list|,
name|flags
argument_list|,
name|protoWords
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|Lucene47WordDelimiterFilter
argument_list|(
name|tokenStream
argument_list|,
name|charTypeTable
argument_list|,
name|flags
argument_list|,
name|protoWords
argument_list|)
return|;
block|}
block|}
DECL|method|getFlag
specifier|public
name|int
name|getFlag
parameter_list|(
name|int
name|flag
parameter_list|,
name|Settings
name|settings
parameter_list|,
name|String
name|key
parameter_list|,
name|boolean
name|defaultValue
parameter_list|)
block|{
if|if
condition|(
name|settings
operator|.
name|getAsBoolean
argument_list|(
name|key
argument_list|,
name|defaultValue
argument_list|)
condition|)
block|{
return|return
name|flag
return|;
block|}
return|return
literal|0
return|;
block|}
comment|// source => type
DECL|field|typePattern
specifier|private
specifier|static
name|Pattern
name|typePattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"(.*)\\s*=>\\s*(.*)\\s*$"
argument_list|)
decl_stmt|;
comment|/**      * parses a list of MappingCharFilter style rules into a custom byte[] type table      */
DECL|method|parseTypes
specifier|private
name|byte
index|[]
name|parseTypes
parameter_list|(
name|Collection
argument_list|<
name|String
argument_list|>
name|rules
parameter_list|)
block|{
name|SortedMap
argument_list|<
name|Character
argument_list|,
name|Byte
argument_list|>
name|typeMap
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|rule
range|:
name|rules
control|)
block|{
name|Matcher
name|m
init|=
name|typePattern
operator|.
name|matcher
argument_list|(
name|rule
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|m
operator|.
name|find
argument_list|()
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Invalid Mapping Rule : ["
operator|+
name|rule
operator|+
literal|"]"
argument_list|)
throw|;
name|String
name|lhs
init|=
name|parseString
argument_list|(
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
operator|.
name|trim
argument_list|()
argument_list|)
decl_stmt|;
name|Byte
name|rhs
init|=
name|parseType
argument_list|(
name|m
operator|.
name|group
argument_list|(
literal|2
argument_list|)
operator|.
name|trim
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|lhs
operator|.
name|length
argument_list|()
operator|!=
literal|1
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Invalid Mapping Rule : ["
operator|+
name|rule
operator|+
literal|"]. Only a single character is allowed."
argument_list|)
throw|;
if|if
condition|(
name|rhs
operator|==
literal|null
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Invalid Mapping Rule : ["
operator|+
name|rule
operator|+
literal|"]. Illegal type."
argument_list|)
throw|;
name|typeMap
operator|.
name|put
argument_list|(
name|lhs
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
argument_list|,
name|rhs
argument_list|)
expr_stmt|;
block|}
comment|// ensure the table is always at least as big as DEFAULT_WORD_DELIM_TABLE for performance
name|byte
name|types
index|[]
init|=
operator|new
name|byte
index|[
name|Math
operator|.
name|max
argument_list|(
name|typeMap
operator|.
name|lastKey
argument_list|()
operator|+
literal|1
argument_list|,
name|WordDelimiterIterator
operator|.
name|DEFAULT_WORD_DELIM_TABLE
operator|.
name|length
argument_list|)
index|]
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
name|types
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|types
index|[
name|i
index|]
operator|=
name|WordDelimiterIterator
operator|.
name|getType
argument_list|(
name|i
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Character
argument_list|,
name|Byte
argument_list|>
name|mapping
range|:
name|typeMap
operator|.
name|entrySet
argument_list|()
control|)
name|types
index|[
name|mapping
operator|.
name|getKey
argument_list|()
index|]
operator|=
name|mapping
operator|.
name|getValue
argument_list|()
expr_stmt|;
return|return
name|types
return|;
block|}
DECL|method|parseType
specifier|private
name|Byte
name|parseType
parameter_list|(
name|String
name|s
parameter_list|)
block|{
if|if
condition|(
name|s
operator|.
name|equals
argument_list|(
literal|"LOWER"
argument_list|)
condition|)
return|return
name|WordDelimiterFilter
operator|.
name|LOWER
return|;
elseif|else
if|if
condition|(
name|s
operator|.
name|equals
argument_list|(
literal|"UPPER"
argument_list|)
condition|)
return|return
name|WordDelimiterFilter
operator|.
name|UPPER
return|;
elseif|else
if|if
condition|(
name|s
operator|.
name|equals
argument_list|(
literal|"ALPHA"
argument_list|)
condition|)
return|return
name|WordDelimiterFilter
operator|.
name|ALPHA
return|;
elseif|else
if|if
condition|(
name|s
operator|.
name|equals
argument_list|(
literal|"DIGIT"
argument_list|)
condition|)
return|return
name|WordDelimiterFilter
operator|.
name|DIGIT
return|;
elseif|else
if|if
condition|(
name|s
operator|.
name|equals
argument_list|(
literal|"ALPHANUM"
argument_list|)
condition|)
return|return
name|WordDelimiterFilter
operator|.
name|ALPHANUM
return|;
elseif|else
if|if
condition|(
name|s
operator|.
name|equals
argument_list|(
literal|"SUBWORD_DELIM"
argument_list|)
condition|)
return|return
name|WordDelimiterFilter
operator|.
name|SUBWORD_DELIM
return|;
else|else
return|return
literal|null
return|;
block|}
DECL|field|out
name|char
index|[]
name|out
init|=
operator|new
name|char
index|[
literal|256
index|]
decl_stmt|;
DECL|method|parseString
specifier|private
name|String
name|parseString
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|int
name|readPos
init|=
literal|0
decl_stmt|;
name|int
name|len
init|=
name|s
operator|.
name|length
argument_list|()
decl_stmt|;
name|int
name|writePos
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|readPos
operator|<
name|len
condition|)
block|{
name|char
name|c
init|=
name|s
operator|.
name|charAt
argument_list|(
name|readPos
operator|++
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|'\\'
condition|)
block|{
if|if
condition|(
name|readPos
operator|>=
name|len
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Invalid escaped char in ["
operator|+
name|s
operator|+
literal|"]"
argument_list|)
throw|;
name|c
operator|=
name|s
operator|.
name|charAt
argument_list|(
name|readPos
operator|++
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|c
condition|)
block|{
case|case
literal|'\\'
case|:
name|c
operator|=
literal|'\\'
expr_stmt|;
break|break;
case|case
literal|'n'
case|:
name|c
operator|=
literal|'\n'
expr_stmt|;
break|break;
case|case
literal|'t'
case|:
name|c
operator|=
literal|'\t'
expr_stmt|;
break|break;
case|case
literal|'r'
case|:
name|c
operator|=
literal|'\r'
expr_stmt|;
break|break;
case|case
literal|'b'
case|:
name|c
operator|=
literal|'\b'
expr_stmt|;
break|break;
case|case
literal|'f'
case|:
name|c
operator|=
literal|'\f'
expr_stmt|;
break|break;
case|case
literal|'u'
case|:
if|if
condition|(
name|readPos
operator|+
literal|3
operator|>=
name|len
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Invalid escaped char in ["
operator|+
name|s
operator|+
literal|"]"
argument_list|)
throw|;
name|c
operator|=
operator|(
name|char
operator|)
name|Integer
operator|.
name|parseInt
argument_list|(
name|s
operator|.
name|substring
argument_list|(
name|readPos
argument_list|,
name|readPos
operator|+
literal|4
argument_list|)
argument_list|,
literal|16
argument_list|)
expr_stmt|;
name|readPos
operator|+=
literal|4
expr_stmt|;
break|break;
block|}
block|}
name|out
index|[
name|writePos
operator|++
index|]
operator|=
name|c
expr_stmt|;
block|}
return|return
operator|new
name|String
argument_list|(
name|out
argument_list|,
literal|0
argument_list|,
name|writePos
argument_list|)
return|;
block|}
block|}
end_class

end_unit
