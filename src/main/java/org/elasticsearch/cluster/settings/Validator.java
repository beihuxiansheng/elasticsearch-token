begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster.settings
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|settings
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
name|Booleans
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
name|unit
operator|.
name|TimeValue
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
name|unit
operator|.
name|ByteSizeValue
operator|.
name|parseBytesSizeValue
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
name|unit
operator|.
name|MemorySizeValue
operator|.
name|parseBytesSizeValueOrHeapRatio
import|;
end_import

begin_comment
comment|/**  * Validates a setting, returning a failure message if applicable.  */
end_comment

begin_interface
DECL|interface|Validator
specifier|public
interface|interface
name|Validator
block|{
DECL|method|validate
name|String
name|validate
parameter_list|(
name|String
name|setting
parameter_list|,
name|String
name|value
parameter_list|)
function_decl|;
DECL|field|EMPTY
specifier|public
specifier|static
specifier|final
name|Validator
name|EMPTY
init|=
operator|new
name|Validator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|validate
parameter_list|(
name|String
name|setting
parameter_list|,
name|String
name|value
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
decl_stmt|;
DECL|field|TIME
specifier|public
specifier|static
specifier|final
name|Validator
name|TIME
init|=
operator|new
name|Validator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|validate
parameter_list|(
name|String
name|setting
parameter_list|,
name|String
name|value
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|TimeValue
operator|.
name|parseTimeValue
argument_list|(
name|value
argument_list|,
literal|null
argument_list|,
name|setting
argument_list|)
operator|==
literal|null
condition|)
block|{
return|return
literal|"cannot parse value ["
operator|+
name|value
operator|+
literal|"] as time"
return|;
block|}
block|}
catch|catch
parameter_list|(
name|ElasticsearchParseException
name|ex
parameter_list|)
block|{
return|return
literal|"cannot parse value ["
operator|+
name|value
operator|+
literal|"] as time"
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
decl_stmt|;
DECL|field|TIME_NON_NEGATIVE
specifier|public
specifier|static
specifier|final
name|Validator
name|TIME_NON_NEGATIVE
init|=
operator|new
name|Validator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|validate
parameter_list|(
name|String
name|setting
parameter_list|,
name|String
name|value
parameter_list|)
block|{
try|try
block|{
name|TimeValue
name|timeValue
init|=
name|TimeValue
operator|.
name|parseTimeValue
argument_list|(
name|value
argument_list|,
literal|null
argument_list|,
name|setting
argument_list|)
decl_stmt|;
if|if
condition|(
name|timeValue
operator|==
literal|null
condition|)
block|{
return|return
literal|"cannot parse value ["
operator|+
name|value
operator|+
literal|"] as time"
return|;
block|}
if|if
condition|(
name|timeValue
operator|.
name|millis
argument_list|()
operator|<
literal|0
condition|)
block|{
return|return
literal|"cannot parse value ["
operator|+
name|value
operator|+
literal|"] as non negative time"
return|;
block|}
block|}
catch|catch
parameter_list|(
name|ElasticsearchParseException
name|ex
parameter_list|)
block|{
return|return
literal|"cannot parse value ["
operator|+
name|value
operator|+
literal|"] as time"
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
decl_stmt|;
DECL|field|FLOAT
specifier|public
specifier|static
specifier|final
name|Validator
name|FLOAT
init|=
operator|new
name|Validator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|validate
parameter_list|(
name|String
name|setting
parameter_list|,
name|String
name|value
parameter_list|)
block|{
try|try
block|{
name|Float
operator|.
name|parseFloat
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|ex
parameter_list|)
block|{
return|return
literal|"cannot parse value ["
operator|+
name|value
operator|+
literal|"] as a float"
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
decl_stmt|;
DECL|field|NON_NEGATIVE_FLOAT
specifier|public
specifier|static
specifier|final
name|Validator
name|NON_NEGATIVE_FLOAT
init|=
operator|new
name|Validator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|validate
parameter_list|(
name|String
name|setting
parameter_list|,
name|String
name|value
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|Float
operator|.
name|parseFloat
argument_list|(
name|value
argument_list|)
operator|<
literal|0.0
condition|)
block|{
return|return
literal|"the value of the setting "
operator|+
name|setting
operator|+
literal|" must be a non negative float"
return|;
block|}
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|ex
parameter_list|)
block|{
return|return
literal|"cannot parse value ["
operator|+
name|value
operator|+
literal|"] as a double"
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
decl_stmt|;
DECL|field|DOUBLE
specifier|public
specifier|static
specifier|final
name|Validator
name|DOUBLE
init|=
operator|new
name|Validator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|validate
parameter_list|(
name|String
name|setting
parameter_list|,
name|String
name|value
parameter_list|)
block|{
try|try
block|{
name|Double
operator|.
name|parseDouble
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|ex
parameter_list|)
block|{
return|return
literal|"cannot parse value ["
operator|+
name|value
operator|+
literal|"] as a double"
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
decl_stmt|;
DECL|field|NON_NEGATIVE_DOUBLE
specifier|public
specifier|static
specifier|final
name|Validator
name|NON_NEGATIVE_DOUBLE
init|=
operator|new
name|Validator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|validate
parameter_list|(
name|String
name|setting
parameter_list|,
name|String
name|value
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|Double
operator|.
name|parseDouble
argument_list|(
name|value
argument_list|)
operator|<
literal|0.0
condition|)
block|{
return|return
literal|"the value of the setting "
operator|+
name|setting
operator|+
literal|" must be a non negative double"
return|;
block|}
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|ex
parameter_list|)
block|{
return|return
literal|"cannot parse value ["
operator|+
name|value
operator|+
literal|"] as a double"
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
decl_stmt|;
DECL|field|DOUBLE_GTE_2
specifier|public
specifier|static
specifier|final
name|Validator
name|DOUBLE_GTE_2
init|=
operator|new
name|Validator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|validate
parameter_list|(
name|String
name|setting
parameter_list|,
name|String
name|value
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|Double
operator|.
name|parseDouble
argument_list|(
name|value
argument_list|)
operator|<
literal|2.0
condition|)
block|{
return|return
literal|"the value of the setting "
operator|+
name|setting
operator|+
literal|" must be>= 2.0"
return|;
block|}
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|ex
parameter_list|)
block|{
return|return
literal|"cannot parse value ["
operator|+
name|value
operator|+
literal|"] as a double"
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
decl_stmt|;
DECL|field|INTEGER
specifier|public
specifier|static
specifier|final
name|Validator
name|INTEGER
init|=
operator|new
name|Validator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|validate
parameter_list|(
name|String
name|setting
parameter_list|,
name|String
name|value
parameter_list|)
block|{
try|try
block|{
name|Integer
operator|.
name|parseInt
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|ex
parameter_list|)
block|{
return|return
literal|"cannot parse value ["
operator|+
name|value
operator|+
literal|"] as an integer"
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
decl_stmt|;
DECL|field|POSITIVE_INTEGER
specifier|public
specifier|static
specifier|final
name|Validator
name|POSITIVE_INTEGER
init|=
operator|new
name|Validator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|validate
parameter_list|(
name|String
name|setting
parameter_list|,
name|String
name|value
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|value
argument_list|)
operator|<=
literal|0
condition|)
block|{
return|return
literal|"the value of the setting "
operator|+
name|setting
operator|+
literal|" must be a positive integer"
return|;
block|}
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|ex
parameter_list|)
block|{
return|return
literal|"cannot parse value ["
operator|+
name|value
operator|+
literal|"] as an integer"
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
decl_stmt|;
DECL|field|NON_NEGATIVE_INTEGER
specifier|public
specifier|static
specifier|final
name|Validator
name|NON_NEGATIVE_INTEGER
init|=
operator|new
name|Validator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|validate
parameter_list|(
name|String
name|setting
parameter_list|,
name|String
name|value
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|value
argument_list|)
operator|<
literal|0
condition|)
block|{
return|return
literal|"the value of the setting "
operator|+
name|setting
operator|+
literal|" must be a non negative integer"
return|;
block|}
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|ex
parameter_list|)
block|{
return|return
literal|"cannot parse value ["
operator|+
name|value
operator|+
literal|"] as an integer"
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
decl_stmt|;
DECL|field|INTEGER_GTE_2
specifier|public
specifier|static
specifier|final
name|Validator
name|INTEGER_GTE_2
init|=
operator|new
name|Validator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|validate
parameter_list|(
name|String
name|setting
parameter_list|,
name|String
name|value
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|value
argument_list|)
operator|<
literal|2
condition|)
block|{
return|return
literal|"the value of the setting "
operator|+
name|setting
operator|+
literal|" must be>= 2"
return|;
block|}
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|ex
parameter_list|)
block|{
return|return
literal|"cannot parse value ["
operator|+
name|value
operator|+
literal|"] as an integer"
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
decl_stmt|;
DECL|field|BYTES_SIZE
specifier|public
specifier|static
specifier|final
name|Validator
name|BYTES_SIZE
init|=
operator|new
name|Validator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|validate
parameter_list|(
name|String
name|setting
parameter_list|,
name|String
name|value
parameter_list|)
block|{
try|try
block|{
name|parseBytesSizeValue
argument_list|(
name|value
argument_list|,
name|setting
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ElasticsearchParseException
name|ex
parameter_list|)
block|{
return|return
name|ex
operator|.
name|getMessage
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
decl_stmt|;
DECL|field|PERCENTAGE
specifier|public
specifier|static
specifier|final
name|Validator
name|PERCENTAGE
init|=
operator|new
name|Validator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|validate
parameter_list|(
name|String
name|setting
parameter_list|,
name|String
name|value
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
return|return
literal|"the value of "
operator|+
name|setting
operator|+
literal|" can not be null"
return|;
block|}
if|if
condition|(
operator|!
name|value
operator|.
name|endsWith
argument_list|(
literal|"%"
argument_list|)
condition|)
block|{
return|return
literal|"the value ["
operator|+
name|value
operator|+
literal|"] for "
operator|+
name|setting
operator|+
literal|" must end with %"
return|;
block|}
specifier|final
name|double
name|asDouble
init|=
name|Double
operator|.
name|parseDouble
argument_list|(
name|value
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|value
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|asDouble
argument_list|<
literal|0.0
operator|||
name|asDouble
argument_list|>
literal|100.0
condition|)
block|{
return|return
literal|"the value ["
operator|+
name|value
operator|+
literal|"] for "
operator|+
name|setting
operator|+
literal|" must be a percentage between 0% and 100%"
return|;
block|}
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|ex
parameter_list|)
block|{
return|return
name|ex
operator|.
name|getMessage
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
decl_stmt|;
DECL|field|BYTES_SIZE_OR_PERCENTAGE
specifier|public
specifier|static
specifier|final
name|Validator
name|BYTES_SIZE_OR_PERCENTAGE
init|=
operator|new
name|Validator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|validate
parameter_list|(
name|String
name|setting
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|String
name|byteSize
init|=
name|BYTES_SIZE
operator|.
name|validate
argument_list|(
name|setting
argument_list|,
name|value
argument_list|)
decl_stmt|;
if|if
condition|(
name|byteSize
operator|!=
literal|null
condition|)
block|{
name|String
name|percentage
init|=
name|PERCENTAGE
operator|.
name|validate
argument_list|(
name|setting
argument_list|,
name|value
argument_list|)
decl_stmt|;
if|if
condition|(
name|percentage
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|percentage
operator|+
literal|" or be a valid bytes size value, like [16mb]"
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
decl_stmt|;
DECL|field|MEMORY_SIZE
specifier|public
specifier|static
specifier|final
name|Validator
name|MEMORY_SIZE
init|=
operator|new
name|Validator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|validate
parameter_list|(
name|String
name|setting
parameter_list|,
name|String
name|value
parameter_list|)
block|{
try|try
block|{
name|parseBytesSizeValueOrHeapRatio
argument_list|(
name|value
argument_list|,
name|setting
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ElasticsearchParseException
name|ex
parameter_list|)
block|{
return|return
name|ex
operator|.
name|getMessage
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
decl_stmt|;
DECL|field|BOOLEAN
specifier|public
specifier|static
specifier|final
name|Validator
name|BOOLEAN
init|=
operator|new
name|Validator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|validate
parameter_list|(
name|String
name|setting
parameter_list|,
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|!=
literal|null
operator|&&
operator|(
name|Booleans
operator|.
name|isExplicitFalse
argument_list|(
name|value
argument_list|)
operator|||
name|Booleans
operator|.
name|isExplicitTrue
argument_list|(
name|value
argument_list|)
operator|)
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
literal|"cannot parse value ["
operator|+
name|value
operator|+
literal|"] as a boolean"
return|;
block|}
block|}
decl_stmt|;
block|}
end_interface

end_unit

