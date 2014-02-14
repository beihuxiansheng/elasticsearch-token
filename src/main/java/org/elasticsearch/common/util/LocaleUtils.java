begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.util
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|util
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchIllegalArgumentException
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

begin_comment
comment|/**  * Utilities for for dealing with {@link Locale} objects  */
end_comment

begin_class
DECL|class|LocaleUtils
specifier|public
class|class
name|LocaleUtils
block|{
comment|/**      * Parse the string describing a locale into a {@link Locale} object      */
DECL|method|parse
specifier|public
specifier|static
name|Locale
name|parse
parameter_list|(
name|String
name|localeStr
parameter_list|)
block|{
specifier|final
name|String
index|[]
name|parts
init|=
name|localeStr
operator|.
name|split
argument_list|(
literal|"_"
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|parts
operator|.
name|length
condition|)
block|{
case|case
literal|3
case|:
comment|// lang_country_variant
return|return
operator|new
name|Locale
argument_list|(
name|parts
index|[
literal|0
index|]
argument_list|,
name|parts
index|[
literal|1
index|]
argument_list|,
name|parts
index|[
literal|2
index|]
argument_list|)
return|;
case|case
literal|2
case|:
comment|// lang_country
return|return
operator|new
name|Locale
argument_list|(
name|parts
index|[
literal|0
index|]
argument_list|,
name|parts
index|[
literal|1
index|]
argument_list|)
return|;
case|case
literal|1
case|:
if|if
condition|(
literal|"ROOT"
operator|.
name|equalsIgnoreCase
argument_list|(
name|parts
index|[
literal|0
index|]
argument_list|)
condition|)
block|{
return|return
name|Locale
operator|.
name|ROOT
return|;
block|}
comment|// lang
return|return
operator|new
name|Locale
argument_list|(
name|parts
index|[
literal|0
index|]
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|ElasticsearchIllegalArgumentException
argument_list|(
literal|"Can't parse locale: ["
operator|+
name|localeStr
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
comment|/**      * Return a string for a {@link Locale} object      */
DECL|method|toString
specifier|public
specifier|static
name|String
name|toString
parameter_list|(
name|Locale
name|locale
parameter_list|)
block|{
comment|// JAVA7 - use .toLanguageTag instead of .toString()
return|return
name|locale
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

