begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.unit
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|unit
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchParseException
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
name|Strings
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
name|io
operator|.
name|stream
operator|.
name|StreamInput
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
name|io
operator|.
name|stream
operator|.
name|StreamOutput
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
name|io
operator|.
name|stream
operator|.
name|Writeable
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_class
DECL|class|ByteSizeValue
specifier|public
class|class
name|ByteSizeValue
implements|implements
name|Writeable
implements|,
name|Comparable
argument_list|<
name|ByteSizeValue
argument_list|>
block|{
DECL|field|size
specifier|private
specifier|final
name|long
name|size
decl_stmt|;
DECL|field|unit
specifier|private
specifier|final
name|ByteSizeUnit
name|unit
decl_stmt|;
DECL|method|ByteSizeValue
specifier|public
name|ByteSizeValue
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|size
operator|=
name|in
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|unit
operator|=
name|ByteSizeUnit
operator|.
name|BYTES
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeTo
specifier|public
name|void
name|writeTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeVLong
argument_list|(
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|ByteSizeValue
specifier|public
name|ByteSizeValue
parameter_list|(
name|long
name|bytes
parameter_list|)
block|{
name|this
argument_list|(
name|bytes
argument_list|,
name|ByteSizeUnit
operator|.
name|BYTES
argument_list|)
expr_stmt|;
block|}
DECL|method|ByteSizeValue
specifier|public
name|ByteSizeValue
parameter_list|(
name|long
name|size
parameter_list|,
name|ByteSizeUnit
name|unit
parameter_list|)
block|{
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
name|this
operator|.
name|unit
operator|=
name|unit
expr_stmt|;
block|}
DECL|method|bytesAsInt
specifier|public
name|int
name|bytesAsInt
parameter_list|()
block|{
name|long
name|bytes
init|=
name|getBytes
argument_list|()
decl_stmt|;
if|if
condition|(
name|bytes
operator|>
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"size ["
operator|+
name|toString
argument_list|()
operator|+
literal|"] is bigger than max int"
argument_list|)
throw|;
block|}
return|return
operator|(
name|int
operator|)
name|bytes
return|;
block|}
DECL|method|getBytes
specifier|public
name|long
name|getBytes
parameter_list|()
block|{
return|return
name|unit
operator|.
name|toBytes
argument_list|(
name|size
argument_list|)
return|;
block|}
DECL|method|getKb
specifier|public
name|long
name|getKb
parameter_list|()
block|{
return|return
name|unit
operator|.
name|toKB
argument_list|(
name|size
argument_list|)
return|;
block|}
DECL|method|getMb
specifier|public
name|long
name|getMb
parameter_list|()
block|{
return|return
name|unit
operator|.
name|toMB
argument_list|(
name|size
argument_list|)
return|;
block|}
DECL|method|getGb
specifier|public
name|long
name|getGb
parameter_list|()
block|{
return|return
name|unit
operator|.
name|toGB
argument_list|(
name|size
argument_list|)
return|;
block|}
DECL|method|getTb
specifier|public
name|long
name|getTb
parameter_list|()
block|{
return|return
name|unit
operator|.
name|toTB
argument_list|(
name|size
argument_list|)
return|;
block|}
DECL|method|getPb
specifier|public
name|long
name|getPb
parameter_list|()
block|{
return|return
name|unit
operator|.
name|toPB
argument_list|(
name|size
argument_list|)
return|;
block|}
DECL|method|getKbFrac
specifier|public
name|double
name|getKbFrac
parameter_list|()
block|{
return|return
operator|(
operator|(
name|double
operator|)
name|getBytes
argument_list|()
operator|)
operator|/
name|ByteSizeUnit
operator|.
name|C1
return|;
block|}
DECL|method|getMbFrac
specifier|public
name|double
name|getMbFrac
parameter_list|()
block|{
return|return
operator|(
operator|(
name|double
operator|)
name|getBytes
argument_list|()
operator|)
operator|/
name|ByteSizeUnit
operator|.
name|C2
return|;
block|}
DECL|method|getGbFrac
specifier|public
name|double
name|getGbFrac
parameter_list|()
block|{
return|return
operator|(
operator|(
name|double
operator|)
name|getBytes
argument_list|()
operator|)
operator|/
name|ByteSizeUnit
operator|.
name|C3
return|;
block|}
DECL|method|getTbFrac
specifier|public
name|double
name|getTbFrac
parameter_list|()
block|{
return|return
operator|(
operator|(
name|double
operator|)
name|getBytes
argument_list|()
operator|)
operator|/
name|ByteSizeUnit
operator|.
name|C4
return|;
block|}
DECL|method|getPbFrac
specifier|public
name|double
name|getPbFrac
parameter_list|()
block|{
return|return
operator|(
operator|(
name|double
operator|)
name|getBytes
argument_list|()
operator|)
operator|/
name|ByteSizeUnit
operator|.
name|C5
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|long
name|bytes
init|=
name|getBytes
argument_list|()
decl_stmt|;
name|double
name|value
init|=
name|bytes
decl_stmt|;
name|String
name|suffix
init|=
literal|"b"
decl_stmt|;
if|if
condition|(
name|bytes
operator|>=
name|ByteSizeUnit
operator|.
name|C5
condition|)
block|{
name|value
operator|=
name|getPbFrac
argument_list|()
expr_stmt|;
name|suffix
operator|=
literal|"pb"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|bytes
operator|>=
name|ByteSizeUnit
operator|.
name|C4
condition|)
block|{
name|value
operator|=
name|getTbFrac
argument_list|()
expr_stmt|;
name|suffix
operator|=
literal|"tb"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|bytes
operator|>=
name|ByteSizeUnit
operator|.
name|C3
condition|)
block|{
name|value
operator|=
name|getGbFrac
argument_list|()
expr_stmt|;
name|suffix
operator|=
literal|"gb"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|bytes
operator|>=
name|ByteSizeUnit
operator|.
name|C2
condition|)
block|{
name|value
operator|=
name|getMbFrac
argument_list|()
expr_stmt|;
name|suffix
operator|=
literal|"mb"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|bytes
operator|>=
name|ByteSizeUnit
operator|.
name|C1
condition|)
block|{
name|value
operator|=
name|getKbFrac
argument_list|()
expr_stmt|;
name|suffix
operator|=
literal|"kb"
expr_stmt|;
block|}
return|return
name|Strings
operator|.
name|format1Decimals
argument_list|(
name|value
argument_list|,
name|suffix
argument_list|)
return|;
block|}
DECL|method|parseBytesSizeValue
specifier|public
specifier|static
name|ByteSizeValue
name|parseBytesSizeValue
parameter_list|(
name|String
name|sValue
parameter_list|,
name|String
name|settingName
parameter_list|)
throws|throws
name|ElasticsearchParseException
block|{
return|return
name|parseBytesSizeValue
argument_list|(
name|sValue
argument_list|,
literal|null
argument_list|,
name|settingName
argument_list|)
return|;
block|}
DECL|method|parseBytesSizeValue
specifier|public
specifier|static
name|ByteSizeValue
name|parseBytesSizeValue
parameter_list|(
name|String
name|sValue
parameter_list|,
name|ByteSizeValue
name|defaultValue
parameter_list|,
name|String
name|settingName
parameter_list|)
throws|throws
name|ElasticsearchParseException
block|{
name|settingName
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|settingName
argument_list|)
expr_stmt|;
if|if
condition|(
name|sValue
operator|==
literal|null
condition|)
block|{
return|return
name|defaultValue
return|;
block|}
name|long
name|bytes
decl_stmt|;
try|try
block|{
name|String
name|lowerSValue
init|=
name|sValue
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
if|if
condition|(
name|lowerSValue
operator|.
name|endsWith
argument_list|(
literal|"k"
argument_list|)
condition|)
block|{
name|bytes
operator|=
call|(
name|long
call|)
argument_list|(
name|Double
operator|.
name|parseDouble
argument_list|(
name|lowerSValue
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|lowerSValue
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
operator|*
name|ByteSizeUnit
operator|.
name|C1
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|lowerSValue
operator|.
name|endsWith
argument_list|(
literal|"kb"
argument_list|)
condition|)
block|{
name|bytes
operator|=
call|(
name|long
call|)
argument_list|(
name|Double
operator|.
name|parseDouble
argument_list|(
name|lowerSValue
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|lowerSValue
operator|.
name|length
argument_list|()
operator|-
literal|2
argument_list|)
argument_list|)
operator|*
name|ByteSizeUnit
operator|.
name|C1
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|lowerSValue
operator|.
name|endsWith
argument_list|(
literal|"m"
argument_list|)
condition|)
block|{
name|bytes
operator|=
call|(
name|long
call|)
argument_list|(
name|Double
operator|.
name|parseDouble
argument_list|(
name|lowerSValue
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|lowerSValue
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
operator|*
name|ByteSizeUnit
operator|.
name|C2
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|lowerSValue
operator|.
name|endsWith
argument_list|(
literal|"mb"
argument_list|)
condition|)
block|{
name|bytes
operator|=
call|(
name|long
call|)
argument_list|(
name|Double
operator|.
name|parseDouble
argument_list|(
name|lowerSValue
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|lowerSValue
operator|.
name|length
argument_list|()
operator|-
literal|2
argument_list|)
argument_list|)
operator|*
name|ByteSizeUnit
operator|.
name|C2
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|lowerSValue
operator|.
name|endsWith
argument_list|(
literal|"g"
argument_list|)
condition|)
block|{
name|bytes
operator|=
call|(
name|long
call|)
argument_list|(
name|Double
operator|.
name|parseDouble
argument_list|(
name|lowerSValue
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|lowerSValue
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
operator|*
name|ByteSizeUnit
operator|.
name|C3
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|lowerSValue
operator|.
name|endsWith
argument_list|(
literal|"gb"
argument_list|)
condition|)
block|{
name|bytes
operator|=
call|(
name|long
call|)
argument_list|(
name|Double
operator|.
name|parseDouble
argument_list|(
name|lowerSValue
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|lowerSValue
operator|.
name|length
argument_list|()
operator|-
literal|2
argument_list|)
argument_list|)
operator|*
name|ByteSizeUnit
operator|.
name|C3
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|lowerSValue
operator|.
name|endsWith
argument_list|(
literal|"t"
argument_list|)
condition|)
block|{
name|bytes
operator|=
call|(
name|long
call|)
argument_list|(
name|Double
operator|.
name|parseDouble
argument_list|(
name|lowerSValue
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|lowerSValue
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
operator|*
name|ByteSizeUnit
operator|.
name|C4
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|lowerSValue
operator|.
name|endsWith
argument_list|(
literal|"tb"
argument_list|)
condition|)
block|{
name|bytes
operator|=
call|(
name|long
call|)
argument_list|(
name|Double
operator|.
name|parseDouble
argument_list|(
name|lowerSValue
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|lowerSValue
operator|.
name|length
argument_list|()
operator|-
literal|2
argument_list|)
argument_list|)
operator|*
name|ByteSizeUnit
operator|.
name|C4
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|lowerSValue
operator|.
name|endsWith
argument_list|(
literal|"p"
argument_list|)
condition|)
block|{
name|bytes
operator|=
call|(
name|long
call|)
argument_list|(
name|Double
operator|.
name|parseDouble
argument_list|(
name|lowerSValue
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|lowerSValue
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
operator|*
name|ByteSizeUnit
operator|.
name|C5
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|lowerSValue
operator|.
name|endsWith
argument_list|(
literal|"pb"
argument_list|)
condition|)
block|{
name|bytes
operator|=
call|(
name|long
call|)
argument_list|(
name|Double
operator|.
name|parseDouble
argument_list|(
name|lowerSValue
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|lowerSValue
operator|.
name|length
argument_list|()
operator|-
literal|2
argument_list|)
argument_list|)
operator|*
name|ByteSizeUnit
operator|.
name|C5
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|lowerSValue
operator|.
name|endsWith
argument_list|(
literal|"b"
argument_list|)
condition|)
block|{
name|bytes
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|lowerSValue
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|lowerSValue
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|lowerSValue
operator|.
name|equals
argument_list|(
literal|"-1"
argument_list|)
condition|)
block|{
comment|// Allow this special value to be unit-less:
name|bytes
operator|=
operator|-
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|lowerSValue
operator|.
name|equals
argument_list|(
literal|"0"
argument_list|)
condition|)
block|{
comment|// Allow this special value to be unit-less:
name|bytes
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
comment|// Missing units:
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"failed to parse setting [{}] with value [{}] as a size in bytes: unit is missing or unrecognized"
argument_list|,
name|settingName
argument_list|,
name|sValue
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"failed to parse [{}]"
argument_list|,
name|e
argument_list|,
name|sValue
argument_list|)
throw|;
block|}
return|return
operator|new
name|ByteSizeValue
argument_list|(
name|bytes
argument_list|,
name|ByteSizeUnit
operator|.
name|BYTES
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|compareTo
argument_list|(
operator|(
name|ByteSizeValue
operator|)
name|o
argument_list|)
operator|==
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Double
operator|.
name|hashCode
argument_list|(
operator|(
operator|(
name|double
operator|)
name|size
operator|)
operator|*
name|unit
operator|.
name|toBytes
argument_list|(
literal|1
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|compareTo
specifier|public
name|int
name|compareTo
parameter_list|(
name|ByteSizeValue
name|other
parameter_list|)
block|{
name|double
name|thisValue
init|=
operator|(
operator|(
name|double
operator|)
name|size
operator|)
operator|*
name|unit
operator|.
name|toBytes
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|double
name|otherValue
init|=
operator|(
operator|(
name|double
operator|)
name|other
operator|.
name|size
operator|)
operator|*
name|other
operator|.
name|unit
operator|.
name|toBytes
argument_list|(
literal|1
argument_list|)
decl_stmt|;
return|return
name|Double
operator|.
name|compare
argument_list|(
name|thisValue
argument_list|,
name|otherValue
argument_list|)
return|;
block|}
block|}
end_class

end_unit

