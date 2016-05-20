begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.painless
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|painless
package|;
end_package

begin_comment
comment|/**  * A set of methods for non-native boxing and non-native  * exact math operations used at both compile-time and runtime.  */
end_comment

begin_class
DECL|class|Utility
specifier|public
class|class
name|Utility
block|{
DECL|method|NumberToboolean
specifier|public
specifier|static
name|boolean
name|NumberToboolean
parameter_list|(
specifier|final
name|Number
name|value
parameter_list|)
block|{
return|return
name|value
operator|.
name|longValue
argument_list|()
operator|!=
literal|0
return|;
block|}
DECL|method|NumberTochar
specifier|public
specifier|static
name|char
name|NumberTochar
parameter_list|(
specifier|final
name|Number
name|value
parameter_list|)
block|{
return|return
operator|(
name|char
operator|)
name|value
operator|.
name|intValue
argument_list|()
return|;
block|}
DECL|method|NumberToBoolean
specifier|public
specifier|static
name|Boolean
name|NumberToBoolean
parameter_list|(
specifier|final
name|Number
name|value
parameter_list|)
block|{
return|return
name|value
operator|.
name|longValue
argument_list|()
operator|!=
literal|0
return|;
block|}
DECL|method|NumberToByte
specifier|public
specifier|static
name|Byte
name|NumberToByte
parameter_list|(
specifier|final
name|Number
name|value
parameter_list|)
block|{
return|return
name|value
operator|==
literal|null
condition|?
literal|null
else|:
name|value
operator|.
name|byteValue
argument_list|()
return|;
block|}
DECL|method|NumberToShort
specifier|public
specifier|static
name|Short
name|NumberToShort
parameter_list|(
specifier|final
name|Number
name|value
parameter_list|)
block|{
return|return
name|value
operator|==
literal|null
condition|?
literal|null
else|:
name|value
operator|.
name|shortValue
argument_list|()
return|;
block|}
DECL|method|NumberToCharacter
specifier|public
specifier|static
name|Character
name|NumberToCharacter
parameter_list|(
specifier|final
name|Number
name|value
parameter_list|)
block|{
return|return
name|value
operator|==
literal|null
condition|?
literal|null
else|:
operator|(
name|char
operator|)
name|value
operator|.
name|intValue
argument_list|()
return|;
block|}
DECL|method|NumberToInteger
specifier|public
specifier|static
name|Integer
name|NumberToInteger
parameter_list|(
specifier|final
name|Number
name|value
parameter_list|)
block|{
return|return
name|value
operator|==
literal|null
condition|?
literal|null
else|:
name|value
operator|.
name|intValue
argument_list|()
return|;
block|}
DECL|method|NumberToLong
specifier|public
specifier|static
name|Long
name|NumberToLong
parameter_list|(
specifier|final
name|Number
name|value
parameter_list|)
block|{
return|return
name|value
operator|==
literal|null
condition|?
literal|null
else|:
name|value
operator|.
name|longValue
argument_list|()
return|;
block|}
DECL|method|NumberToFloat
specifier|public
specifier|static
name|Float
name|NumberToFloat
parameter_list|(
specifier|final
name|Number
name|value
parameter_list|)
block|{
return|return
name|value
operator|==
literal|null
condition|?
literal|null
else|:
name|value
operator|.
name|floatValue
argument_list|()
return|;
block|}
DECL|method|NumberToDouble
specifier|public
specifier|static
name|Double
name|NumberToDouble
parameter_list|(
specifier|final
name|Number
name|value
parameter_list|)
block|{
return|return
name|value
operator|==
literal|null
condition|?
literal|null
else|:
name|value
operator|.
name|doubleValue
argument_list|()
return|;
block|}
DECL|method|booleanTobyte
specifier|public
specifier|static
name|byte
name|booleanTobyte
parameter_list|(
specifier|final
name|boolean
name|value
parameter_list|)
block|{
return|return
call|(
name|byte
call|)
argument_list|(
name|value
condition|?
literal|1
else|:
literal|0
argument_list|)
return|;
block|}
DECL|method|booleanToshort
specifier|public
specifier|static
name|short
name|booleanToshort
parameter_list|(
specifier|final
name|boolean
name|value
parameter_list|)
block|{
return|return
call|(
name|short
call|)
argument_list|(
name|value
condition|?
literal|1
else|:
literal|0
argument_list|)
return|;
block|}
DECL|method|booleanTochar
specifier|public
specifier|static
name|char
name|booleanTochar
parameter_list|(
specifier|final
name|boolean
name|value
parameter_list|)
block|{
return|return
call|(
name|char
call|)
argument_list|(
name|value
condition|?
literal|1
else|:
literal|0
argument_list|)
return|;
block|}
DECL|method|booleanToint
specifier|public
specifier|static
name|int
name|booleanToint
parameter_list|(
specifier|final
name|boolean
name|value
parameter_list|)
block|{
return|return
name|value
condition|?
literal|1
else|:
literal|0
return|;
block|}
DECL|method|booleanTolong
specifier|public
specifier|static
name|long
name|booleanTolong
parameter_list|(
specifier|final
name|boolean
name|value
parameter_list|)
block|{
return|return
name|value
condition|?
literal|1
else|:
literal|0
return|;
block|}
DECL|method|booleanTofloat
specifier|public
specifier|static
name|float
name|booleanTofloat
parameter_list|(
specifier|final
name|boolean
name|value
parameter_list|)
block|{
return|return
name|value
condition|?
literal|1
else|:
literal|0
return|;
block|}
DECL|method|booleanTodouble
specifier|public
specifier|static
name|double
name|booleanTodouble
parameter_list|(
specifier|final
name|boolean
name|value
parameter_list|)
block|{
return|return
name|value
condition|?
literal|1
else|:
literal|0
return|;
block|}
DECL|method|booleanToInteger
specifier|public
specifier|static
name|Integer
name|booleanToInteger
parameter_list|(
specifier|final
name|boolean
name|value
parameter_list|)
block|{
return|return
name|value
condition|?
literal|1
else|:
literal|0
return|;
block|}
DECL|method|BooleanTobyte
specifier|public
specifier|static
name|byte
name|BooleanTobyte
parameter_list|(
specifier|final
name|Boolean
name|value
parameter_list|)
block|{
return|return
call|(
name|byte
call|)
argument_list|(
name|value
condition|?
literal|1
else|:
literal|0
argument_list|)
return|;
block|}
DECL|method|BooleanToshort
specifier|public
specifier|static
name|short
name|BooleanToshort
parameter_list|(
specifier|final
name|Boolean
name|value
parameter_list|)
block|{
return|return
call|(
name|short
call|)
argument_list|(
name|value
condition|?
literal|1
else|:
literal|0
argument_list|)
return|;
block|}
DECL|method|BooleanTochar
specifier|public
specifier|static
name|char
name|BooleanTochar
parameter_list|(
specifier|final
name|Boolean
name|value
parameter_list|)
block|{
return|return
call|(
name|char
call|)
argument_list|(
name|value
condition|?
literal|1
else|:
literal|0
argument_list|)
return|;
block|}
DECL|method|BooleanToint
specifier|public
specifier|static
name|int
name|BooleanToint
parameter_list|(
specifier|final
name|Boolean
name|value
parameter_list|)
block|{
return|return
name|value
condition|?
literal|1
else|:
literal|0
return|;
block|}
DECL|method|BooleanTolong
specifier|public
specifier|static
name|long
name|BooleanTolong
parameter_list|(
specifier|final
name|Boolean
name|value
parameter_list|)
block|{
return|return
name|value
condition|?
literal|1
else|:
literal|0
return|;
block|}
DECL|method|BooleanTofloat
specifier|public
specifier|static
name|float
name|BooleanTofloat
parameter_list|(
specifier|final
name|Boolean
name|value
parameter_list|)
block|{
return|return
name|value
condition|?
literal|1
else|:
literal|0
return|;
block|}
DECL|method|BooleanTodouble
specifier|public
specifier|static
name|double
name|BooleanTodouble
parameter_list|(
specifier|final
name|Boolean
name|value
parameter_list|)
block|{
return|return
name|value
condition|?
literal|1
else|:
literal|0
return|;
block|}
DECL|method|BooleanToByte
specifier|public
specifier|static
name|Byte
name|BooleanToByte
parameter_list|(
specifier|final
name|Boolean
name|value
parameter_list|)
block|{
return|return
name|value
operator|==
literal|null
condition|?
literal|null
else|:
call|(
name|byte
call|)
argument_list|(
name|value
condition|?
literal|1
else|:
literal|0
argument_list|)
return|;
block|}
DECL|method|BooleanToShort
specifier|public
specifier|static
name|Short
name|BooleanToShort
parameter_list|(
specifier|final
name|Boolean
name|value
parameter_list|)
block|{
return|return
name|value
operator|==
literal|null
condition|?
literal|null
else|:
call|(
name|short
call|)
argument_list|(
name|value
condition|?
literal|1
else|:
literal|0
argument_list|)
return|;
block|}
DECL|method|BooleanToCharacter
specifier|public
specifier|static
name|Character
name|BooleanToCharacter
parameter_list|(
specifier|final
name|Boolean
name|value
parameter_list|)
block|{
return|return
name|value
operator|==
literal|null
condition|?
literal|null
else|:
call|(
name|char
call|)
argument_list|(
name|value
condition|?
literal|1
else|:
literal|0
argument_list|)
return|;
block|}
DECL|method|BooleanToInteger
specifier|public
specifier|static
name|Integer
name|BooleanToInteger
parameter_list|(
specifier|final
name|Boolean
name|value
parameter_list|)
block|{
return|return
name|value
operator|==
literal|null
condition|?
literal|null
else|:
name|value
condition|?
literal|1
else|:
literal|0
return|;
block|}
DECL|method|BooleanToLong
specifier|public
specifier|static
name|Long
name|BooleanToLong
parameter_list|(
specifier|final
name|Boolean
name|value
parameter_list|)
block|{
return|return
name|value
operator|==
literal|null
condition|?
literal|null
else|:
name|value
condition|?
literal|1L
else|:
literal|0L
return|;
block|}
DECL|method|BooleanToFloat
specifier|public
specifier|static
name|Float
name|BooleanToFloat
parameter_list|(
specifier|final
name|Boolean
name|value
parameter_list|)
block|{
return|return
name|value
operator|==
literal|null
condition|?
literal|null
else|:
name|value
condition|?
literal|1F
else|:
literal|0F
return|;
block|}
DECL|method|BooleanToDouble
specifier|public
specifier|static
name|Double
name|BooleanToDouble
parameter_list|(
specifier|final
name|Boolean
name|value
parameter_list|)
block|{
return|return
name|value
operator|==
literal|null
condition|?
literal|null
else|:
name|value
condition|?
literal|1D
else|:
literal|0D
return|;
block|}
DECL|method|byteToboolean
specifier|public
specifier|static
name|boolean
name|byteToboolean
parameter_list|(
specifier|final
name|byte
name|value
parameter_list|)
block|{
return|return
name|value
operator|!=
literal|0
return|;
block|}
DECL|method|byteToShort
specifier|public
specifier|static
name|Short
name|byteToShort
parameter_list|(
specifier|final
name|byte
name|value
parameter_list|)
block|{
return|return
operator|(
name|short
operator|)
name|value
return|;
block|}
DECL|method|byteToCharacter
specifier|public
specifier|static
name|Character
name|byteToCharacter
parameter_list|(
specifier|final
name|byte
name|value
parameter_list|)
block|{
return|return
operator|(
name|char
operator|)
name|value
return|;
block|}
DECL|method|byteToInteger
specifier|public
specifier|static
name|Integer
name|byteToInteger
parameter_list|(
specifier|final
name|byte
name|value
parameter_list|)
block|{
return|return
operator|(
name|int
operator|)
name|value
return|;
block|}
DECL|method|byteToLong
specifier|public
specifier|static
name|Long
name|byteToLong
parameter_list|(
specifier|final
name|byte
name|value
parameter_list|)
block|{
return|return
operator|(
name|long
operator|)
name|value
return|;
block|}
DECL|method|byteToFloat
specifier|public
specifier|static
name|Float
name|byteToFloat
parameter_list|(
specifier|final
name|byte
name|value
parameter_list|)
block|{
return|return
operator|(
name|float
operator|)
name|value
return|;
block|}
DECL|method|byteToDouble
specifier|public
specifier|static
name|Double
name|byteToDouble
parameter_list|(
specifier|final
name|byte
name|value
parameter_list|)
block|{
return|return
operator|(
name|double
operator|)
name|value
return|;
block|}
DECL|method|ByteToboolean
specifier|public
specifier|static
name|boolean
name|ByteToboolean
parameter_list|(
specifier|final
name|Byte
name|value
parameter_list|)
block|{
return|return
name|value
operator|!=
literal|0
return|;
block|}
DECL|method|ByteTochar
specifier|public
specifier|static
name|char
name|ByteTochar
parameter_list|(
specifier|final
name|Byte
name|value
parameter_list|)
block|{
return|return
operator|(
name|char
operator|)
name|value
operator|.
name|byteValue
argument_list|()
return|;
block|}
DECL|method|shortToboolean
specifier|public
specifier|static
name|boolean
name|shortToboolean
parameter_list|(
specifier|final
name|short
name|value
parameter_list|)
block|{
return|return
name|value
operator|!=
literal|0
return|;
block|}
DECL|method|shortToByte
specifier|public
specifier|static
name|Byte
name|shortToByte
parameter_list|(
specifier|final
name|short
name|value
parameter_list|)
block|{
return|return
operator|(
name|byte
operator|)
name|value
return|;
block|}
DECL|method|shortToCharacter
specifier|public
specifier|static
name|Character
name|shortToCharacter
parameter_list|(
specifier|final
name|short
name|value
parameter_list|)
block|{
return|return
operator|(
name|char
operator|)
name|value
return|;
block|}
DECL|method|shortToInteger
specifier|public
specifier|static
name|Integer
name|shortToInteger
parameter_list|(
specifier|final
name|short
name|value
parameter_list|)
block|{
return|return
operator|(
name|int
operator|)
name|value
return|;
block|}
DECL|method|shortToLong
specifier|public
specifier|static
name|Long
name|shortToLong
parameter_list|(
specifier|final
name|short
name|value
parameter_list|)
block|{
return|return
operator|(
name|long
operator|)
name|value
return|;
block|}
DECL|method|shortToFloat
specifier|public
specifier|static
name|Float
name|shortToFloat
parameter_list|(
specifier|final
name|short
name|value
parameter_list|)
block|{
return|return
operator|(
name|float
operator|)
name|value
return|;
block|}
DECL|method|shortToDouble
specifier|public
specifier|static
name|Double
name|shortToDouble
parameter_list|(
specifier|final
name|short
name|value
parameter_list|)
block|{
return|return
operator|(
name|double
operator|)
name|value
return|;
block|}
DECL|method|ShortToboolean
specifier|public
specifier|static
name|boolean
name|ShortToboolean
parameter_list|(
specifier|final
name|Short
name|value
parameter_list|)
block|{
return|return
name|value
operator|!=
literal|0
return|;
block|}
DECL|method|ShortTochar
specifier|public
specifier|static
name|char
name|ShortTochar
parameter_list|(
specifier|final
name|Short
name|value
parameter_list|)
block|{
return|return
operator|(
name|char
operator|)
name|value
operator|.
name|shortValue
argument_list|()
return|;
block|}
DECL|method|charToboolean
specifier|public
specifier|static
name|boolean
name|charToboolean
parameter_list|(
specifier|final
name|char
name|value
parameter_list|)
block|{
return|return
name|value
operator|!=
literal|0
return|;
block|}
DECL|method|charToByte
specifier|public
specifier|static
name|Byte
name|charToByte
parameter_list|(
specifier|final
name|char
name|value
parameter_list|)
block|{
return|return
operator|(
name|byte
operator|)
name|value
return|;
block|}
DECL|method|charToShort
specifier|public
specifier|static
name|Short
name|charToShort
parameter_list|(
specifier|final
name|char
name|value
parameter_list|)
block|{
return|return
operator|(
name|short
operator|)
name|value
return|;
block|}
DECL|method|charToInteger
specifier|public
specifier|static
name|Integer
name|charToInteger
parameter_list|(
specifier|final
name|char
name|value
parameter_list|)
block|{
return|return
operator|(
name|int
operator|)
name|value
return|;
block|}
DECL|method|charToLong
specifier|public
specifier|static
name|Long
name|charToLong
parameter_list|(
specifier|final
name|char
name|value
parameter_list|)
block|{
return|return
operator|(
name|long
operator|)
name|value
return|;
block|}
DECL|method|charToFloat
specifier|public
specifier|static
name|Float
name|charToFloat
parameter_list|(
specifier|final
name|char
name|value
parameter_list|)
block|{
return|return
operator|(
name|float
operator|)
name|value
return|;
block|}
DECL|method|charToDouble
specifier|public
specifier|static
name|Double
name|charToDouble
parameter_list|(
specifier|final
name|char
name|value
parameter_list|)
block|{
return|return
operator|(
name|double
operator|)
name|value
return|;
block|}
DECL|method|charToString
specifier|public
specifier|static
name|String
name|charToString
parameter_list|(
specifier|final
name|char
name|value
parameter_list|)
block|{
return|return
name|String
operator|.
name|valueOf
argument_list|(
name|value
argument_list|)
return|;
block|}
DECL|method|CharacterToboolean
specifier|public
specifier|static
name|boolean
name|CharacterToboolean
parameter_list|(
specifier|final
name|Character
name|value
parameter_list|)
block|{
return|return
name|value
operator|!=
literal|0
return|;
block|}
DECL|method|CharacterTobyte
specifier|public
specifier|static
name|byte
name|CharacterTobyte
parameter_list|(
specifier|final
name|Character
name|value
parameter_list|)
block|{
return|return
operator|(
name|byte
operator|)
name|value
operator|.
name|charValue
argument_list|()
return|;
block|}
DECL|method|CharacterToshort
specifier|public
specifier|static
name|short
name|CharacterToshort
parameter_list|(
specifier|final
name|Character
name|value
parameter_list|)
block|{
return|return
operator|(
name|short
operator|)
name|value
operator|.
name|charValue
argument_list|()
return|;
block|}
DECL|method|CharacterToint
specifier|public
specifier|static
name|int
name|CharacterToint
parameter_list|(
specifier|final
name|Character
name|value
parameter_list|)
block|{
return|return
name|value
return|;
block|}
DECL|method|CharacterTolong
specifier|public
specifier|static
name|long
name|CharacterTolong
parameter_list|(
specifier|final
name|Character
name|value
parameter_list|)
block|{
return|return
name|value
return|;
block|}
DECL|method|CharacterTofloat
specifier|public
specifier|static
name|float
name|CharacterTofloat
parameter_list|(
specifier|final
name|Character
name|value
parameter_list|)
block|{
return|return
name|value
return|;
block|}
DECL|method|CharacterTodouble
specifier|public
specifier|static
name|double
name|CharacterTodouble
parameter_list|(
specifier|final
name|Character
name|value
parameter_list|)
block|{
return|return
name|value
return|;
block|}
DECL|method|CharacterToBoolean
specifier|public
specifier|static
name|Boolean
name|CharacterToBoolean
parameter_list|(
specifier|final
name|Character
name|value
parameter_list|)
block|{
return|return
name|value
operator|==
literal|null
condition|?
literal|null
else|:
name|value
operator|!=
literal|0
return|;
block|}
DECL|method|CharacterToByte
specifier|public
specifier|static
name|Byte
name|CharacterToByte
parameter_list|(
specifier|final
name|Character
name|value
parameter_list|)
block|{
return|return
name|value
operator|==
literal|null
condition|?
literal|null
else|:
operator|(
name|byte
operator|)
name|value
operator|.
name|charValue
argument_list|()
return|;
block|}
DECL|method|CharacterToShort
specifier|public
specifier|static
name|Short
name|CharacterToShort
parameter_list|(
specifier|final
name|Character
name|value
parameter_list|)
block|{
return|return
name|value
operator|==
literal|null
condition|?
literal|null
else|:
operator|(
name|short
operator|)
name|value
operator|.
name|charValue
argument_list|()
return|;
block|}
DECL|method|CharacterToInteger
specifier|public
specifier|static
name|Integer
name|CharacterToInteger
parameter_list|(
specifier|final
name|Character
name|value
parameter_list|)
block|{
return|return
name|value
operator|==
literal|null
condition|?
literal|null
else|:
operator|(
name|int
operator|)
name|value
return|;
block|}
DECL|method|CharacterToLong
specifier|public
specifier|static
name|Long
name|CharacterToLong
parameter_list|(
specifier|final
name|Character
name|value
parameter_list|)
block|{
return|return
name|value
operator|==
literal|null
condition|?
literal|null
else|:
operator|(
name|long
operator|)
name|value
return|;
block|}
DECL|method|CharacterToFloat
specifier|public
specifier|static
name|Float
name|CharacterToFloat
parameter_list|(
specifier|final
name|Character
name|value
parameter_list|)
block|{
return|return
name|value
operator|==
literal|null
condition|?
literal|null
else|:
operator|(
name|float
operator|)
name|value
return|;
block|}
DECL|method|CharacterToDouble
specifier|public
specifier|static
name|Double
name|CharacterToDouble
parameter_list|(
specifier|final
name|Character
name|value
parameter_list|)
block|{
return|return
name|value
operator|==
literal|null
condition|?
literal|null
else|:
operator|(
name|double
operator|)
name|value
return|;
block|}
DECL|method|CharacterToString
specifier|public
specifier|static
name|String
name|CharacterToString
parameter_list|(
specifier|final
name|Character
name|value
parameter_list|)
block|{
return|return
name|value
operator|==
literal|null
condition|?
literal|null
else|:
name|value
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|intToboolean
specifier|public
specifier|static
name|boolean
name|intToboolean
parameter_list|(
specifier|final
name|int
name|value
parameter_list|)
block|{
return|return
name|value
operator|!=
literal|0
return|;
block|}
DECL|method|intToByte
specifier|public
specifier|static
name|Byte
name|intToByte
parameter_list|(
specifier|final
name|int
name|value
parameter_list|)
block|{
return|return
operator|(
name|byte
operator|)
name|value
return|;
block|}
DECL|method|intToShort
specifier|public
specifier|static
name|Short
name|intToShort
parameter_list|(
specifier|final
name|int
name|value
parameter_list|)
block|{
return|return
operator|(
name|short
operator|)
name|value
return|;
block|}
DECL|method|intToCharacter
specifier|public
specifier|static
name|Character
name|intToCharacter
parameter_list|(
specifier|final
name|int
name|value
parameter_list|)
block|{
return|return
operator|(
name|char
operator|)
name|value
return|;
block|}
DECL|method|intToLong
specifier|public
specifier|static
name|Long
name|intToLong
parameter_list|(
specifier|final
name|int
name|value
parameter_list|)
block|{
return|return
operator|(
name|long
operator|)
name|value
return|;
block|}
DECL|method|intToFloat
specifier|public
specifier|static
name|Float
name|intToFloat
parameter_list|(
specifier|final
name|int
name|value
parameter_list|)
block|{
return|return
operator|(
name|float
operator|)
name|value
return|;
block|}
DECL|method|intToDouble
specifier|public
specifier|static
name|Double
name|intToDouble
parameter_list|(
specifier|final
name|int
name|value
parameter_list|)
block|{
return|return
operator|(
name|double
operator|)
name|value
return|;
block|}
DECL|method|IntegerToboolean
specifier|public
specifier|static
name|boolean
name|IntegerToboolean
parameter_list|(
specifier|final
name|Integer
name|value
parameter_list|)
block|{
return|return
name|value
operator|!=
literal|0
return|;
block|}
DECL|method|IntegerTochar
specifier|public
specifier|static
name|char
name|IntegerTochar
parameter_list|(
specifier|final
name|Integer
name|value
parameter_list|)
block|{
return|return
operator|(
name|char
operator|)
name|value
operator|.
name|intValue
argument_list|()
return|;
block|}
DECL|method|longToboolean
specifier|public
specifier|static
name|boolean
name|longToboolean
parameter_list|(
specifier|final
name|long
name|value
parameter_list|)
block|{
return|return
name|value
operator|!=
literal|0
return|;
block|}
DECL|method|longToByte
specifier|public
specifier|static
name|Byte
name|longToByte
parameter_list|(
specifier|final
name|long
name|value
parameter_list|)
block|{
return|return
operator|(
name|byte
operator|)
name|value
return|;
block|}
DECL|method|longToShort
specifier|public
specifier|static
name|Short
name|longToShort
parameter_list|(
specifier|final
name|long
name|value
parameter_list|)
block|{
return|return
operator|(
name|short
operator|)
name|value
return|;
block|}
DECL|method|longToCharacter
specifier|public
specifier|static
name|Character
name|longToCharacter
parameter_list|(
specifier|final
name|long
name|value
parameter_list|)
block|{
return|return
operator|(
name|char
operator|)
name|value
return|;
block|}
DECL|method|longToInteger
specifier|public
specifier|static
name|Integer
name|longToInteger
parameter_list|(
specifier|final
name|long
name|value
parameter_list|)
block|{
return|return
operator|(
name|int
operator|)
name|value
return|;
block|}
DECL|method|longToFloat
specifier|public
specifier|static
name|Float
name|longToFloat
parameter_list|(
specifier|final
name|long
name|value
parameter_list|)
block|{
return|return
operator|(
name|float
operator|)
name|value
return|;
block|}
DECL|method|longToDouble
specifier|public
specifier|static
name|Double
name|longToDouble
parameter_list|(
specifier|final
name|long
name|value
parameter_list|)
block|{
return|return
operator|(
name|double
operator|)
name|value
return|;
block|}
DECL|method|LongToboolean
specifier|public
specifier|static
name|boolean
name|LongToboolean
parameter_list|(
specifier|final
name|Long
name|value
parameter_list|)
block|{
return|return
name|value
operator|!=
literal|0
return|;
block|}
DECL|method|LongTochar
specifier|public
specifier|static
name|char
name|LongTochar
parameter_list|(
specifier|final
name|Long
name|value
parameter_list|)
block|{
return|return
operator|(
name|char
operator|)
name|value
operator|.
name|longValue
argument_list|()
return|;
block|}
DECL|method|floatToboolean
specifier|public
specifier|static
name|boolean
name|floatToboolean
parameter_list|(
specifier|final
name|float
name|value
parameter_list|)
block|{
return|return
name|value
operator|!=
literal|0
return|;
block|}
DECL|method|floatToByte
specifier|public
specifier|static
name|Byte
name|floatToByte
parameter_list|(
specifier|final
name|float
name|value
parameter_list|)
block|{
return|return
operator|(
name|byte
operator|)
name|value
return|;
block|}
DECL|method|floatToShort
specifier|public
specifier|static
name|Short
name|floatToShort
parameter_list|(
specifier|final
name|float
name|value
parameter_list|)
block|{
return|return
operator|(
name|short
operator|)
name|value
return|;
block|}
DECL|method|floatToCharacter
specifier|public
specifier|static
name|Character
name|floatToCharacter
parameter_list|(
specifier|final
name|float
name|value
parameter_list|)
block|{
return|return
operator|(
name|char
operator|)
name|value
return|;
block|}
DECL|method|floatToInteger
specifier|public
specifier|static
name|Integer
name|floatToInteger
parameter_list|(
specifier|final
name|float
name|value
parameter_list|)
block|{
return|return
operator|(
name|int
operator|)
name|value
return|;
block|}
DECL|method|floatToLong
specifier|public
specifier|static
name|Long
name|floatToLong
parameter_list|(
specifier|final
name|float
name|value
parameter_list|)
block|{
return|return
operator|(
name|long
operator|)
name|value
return|;
block|}
DECL|method|floatToDouble
specifier|public
specifier|static
name|Double
name|floatToDouble
parameter_list|(
specifier|final
name|float
name|value
parameter_list|)
block|{
return|return
operator|(
name|double
operator|)
name|value
return|;
block|}
DECL|method|FloatToboolean
specifier|public
specifier|static
name|boolean
name|FloatToboolean
parameter_list|(
specifier|final
name|Float
name|value
parameter_list|)
block|{
return|return
name|value
operator|!=
literal|0
return|;
block|}
DECL|method|FloatTochar
specifier|public
specifier|static
name|char
name|FloatTochar
parameter_list|(
specifier|final
name|Float
name|value
parameter_list|)
block|{
return|return
operator|(
name|char
operator|)
name|value
operator|.
name|floatValue
argument_list|()
return|;
block|}
DECL|method|doubleToboolean
specifier|public
specifier|static
name|boolean
name|doubleToboolean
parameter_list|(
specifier|final
name|double
name|value
parameter_list|)
block|{
return|return
name|value
operator|!=
literal|0
return|;
block|}
DECL|method|doubleToByte
specifier|public
specifier|static
name|Byte
name|doubleToByte
parameter_list|(
specifier|final
name|double
name|value
parameter_list|)
block|{
return|return
operator|(
name|byte
operator|)
name|value
return|;
block|}
DECL|method|doubleToShort
specifier|public
specifier|static
name|Short
name|doubleToShort
parameter_list|(
specifier|final
name|double
name|value
parameter_list|)
block|{
return|return
operator|(
name|short
operator|)
name|value
return|;
block|}
DECL|method|doubleToCharacter
specifier|public
specifier|static
name|Character
name|doubleToCharacter
parameter_list|(
specifier|final
name|double
name|value
parameter_list|)
block|{
return|return
operator|(
name|char
operator|)
name|value
return|;
block|}
DECL|method|doubleToInteger
specifier|public
specifier|static
name|Integer
name|doubleToInteger
parameter_list|(
specifier|final
name|double
name|value
parameter_list|)
block|{
return|return
operator|(
name|int
operator|)
name|value
return|;
block|}
DECL|method|doubleToLong
specifier|public
specifier|static
name|Long
name|doubleToLong
parameter_list|(
specifier|final
name|double
name|value
parameter_list|)
block|{
return|return
operator|(
name|long
operator|)
name|value
return|;
block|}
DECL|method|doubleToFloat
specifier|public
specifier|static
name|Float
name|doubleToFloat
parameter_list|(
specifier|final
name|double
name|value
parameter_list|)
block|{
return|return
operator|(
name|float
operator|)
name|value
return|;
block|}
DECL|method|DoubleToboolean
specifier|public
specifier|static
name|boolean
name|DoubleToboolean
parameter_list|(
specifier|final
name|Double
name|value
parameter_list|)
block|{
return|return
name|value
operator|!=
literal|0
return|;
block|}
DECL|method|DoubleTochar
specifier|public
specifier|static
name|char
name|DoubleTochar
parameter_list|(
specifier|final
name|Double
name|value
parameter_list|)
block|{
return|return
operator|(
name|char
operator|)
name|value
operator|.
name|doubleValue
argument_list|()
return|;
block|}
DECL|method|StringTochar
specifier|public
specifier|static
name|char
name|StringTochar
parameter_list|(
specifier|final
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|.
name|length
argument_list|()
operator|!=
literal|1
condition|)
block|{
throw|throw
operator|new
name|ClassCastException
argument_list|(
literal|"Cannot cast [String] with length greater than one to [char]."
argument_list|)
throw|;
block|}
return|return
name|value
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
return|;
block|}
DECL|method|checkEquals
specifier|public
specifier|static
name|boolean
name|checkEquals
parameter_list|(
specifier|final
name|Object
name|left
parameter_list|,
specifier|final
name|Object
name|right
parameter_list|)
block|{
if|if
condition|(
name|left
operator|!=
literal|null
condition|)
block|{
return|return
name|left
operator|.
name|equals
argument_list|(
name|right
argument_list|)
return|;
block|}
return|return
name|right
operator|==
literal|null
operator|||
name|right
operator|.
name|equals
argument_list|(
literal|null
argument_list|)
return|;
block|}
DECL|method|Utility
specifier|private
name|Utility
parameter_list|()
block|{}
block|}
end_class

end_unit

