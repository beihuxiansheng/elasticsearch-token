begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.xcontent
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
package|;
end_package

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
name|Map
import|;
end_import

begin_comment
comment|/**  * An interface allowing to transfer an object to "XContent" using an {@link XContentBuilder}.  */
end_comment

begin_interface
DECL|interface|ToXContent
specifier|public
interface|interface
name|ToXContent
block|{
DECL|interface|Params
specifier|public
specifier|static
interface|interface
name|Params
block|{
DECL|method|param
name|String
name|param
parameter_list|(
name|String
name|key
parameter_list|)
function_decl|;
DECL|method|param
name|String
name|param
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|defaultValue
parameter_list|)
function_decl|;
DECL|method|paramAsBoolean
name|boolean
name|paramAsBoolean
parameter_list|(
name|String
name|key
parameter_list|,
name|boolean
name|defaultValue
parameter_list|)
function_decl|;
DECL|method|paramAsBooleanOptional
name|Boolean
name|paramAsBooleanOptional
parameter_list|(
name|String
name|key
parameter_list|,
name|Boolean
name|defaultValue
parameter_list|)
function_decl|;
block|}
DECL|field|EMPTY_PARAMS
specifier|public
specifier|static
specifier|final
name|Params
name|EMPTY_PARAMS
init|=
operator|new
name|Params
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|param
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|param
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|defaultValue
parameter_list|)
block|{
return|return
name|defaultValue
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|paramAsBoolean
parameter_list|(
name|String
name|key
parameter_list|,
name|boolean
name|defaultValue
parameter_list|)
block|{
return|return
name|defaultValue
return|;
block|}
annotation|@
name|Override
specifier|public
name|Boolean
name|paramAsBooleanOptional
parameter_list|(
name|String
name|key
parameter_list|,
name|Boolean
name|defaultValue
parameter_list|)
block|{
return|return
name|defaultValue
return|;
block|}
block|}
decl_stmt|;
DECL|class|MapParams
specifier|public
specifier|static
class|class
name|MapParams
implements|implements
name|Params
block|{
DECL|field|params
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
decl_stmt|;
DECL|method|MapParams
specifier|public
name|MapParams
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
parameter_list|)
block|{
name|this
operator|.
name|params
operator|=
name|params
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|param
specifier|public
name|String
name|param
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
name|params
operator|.
name|get
argument_list|(
name|key
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|param
specifier|public
name|String
name|param
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|defaultValue
parameter_list|)
block|{
name|String
name|value
init|=
name|params
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
return|return
name|defaultValue
return|;
block|}
return|return
name|value
return|;
block|}
annotation|@
name|Override
DECL|method|paramAsBoolean
specifier|public
name|boolean
name|paramAsBoolean
parameter_list|(
name|String
name|key
parameter_list|,
name|boolean
name|defaultValue
parameter_list|)
block|{
return|return
name|Booleans
operator|.
name|parseBoolean
argument_list|(
name|param
argument_list|(
name|key
argument_list|)
argument_list|,
name|defaultValue
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|paramAsBooleanOptional
specifier|public
name|Boolean
name|paramAsBooleanOptional
parameter_list|(
name|String
name|key
parameter_list|,
name|Boolean
name|defaultValue
parameter_list|)
block|{
name|String
name|sValue
init|=
name|param
argument_list|(
name|key
argument_list|)
decl_stmt|;
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
return|return
operator|!
operator|(
name|sValue
operator|.
name|equals
argument_list|(
literal|"false"
argument_list|)
operator|||
name|sValue
operator|.
name|equals
argument_list|(
literal|"0"
argument_list|)
operator|||
name|sValue
operator|.
name|equals
argument_list|(
literal|"off"
argument_list|)
operator|)
return|;
block|}
block|}
DECL|class|DelegatingMapParams
specifier|public
specifier|static
class|class
name|DelegatingMapParams
extends|extends
name|MapParams
block|{
DECL|field|delegate
specifier|private
specifier|final
name|Params
name|delegate
decl_stmt|;
DECL|method|DelegatingMapParams
specifier|public
name|DelegatingMapParams
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
parameter_list|,
name|Params
name|delegate
parameter_list|)
block|{
name|super
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|param
specifier|public
name|String
name|param
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
name|super
operator|.
name|param
argument_list|(
name|key
argument_list|,
name|delegate
operator|.
name|param
argument_list|(
name|key
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|param
specifier|public
name|String
name|param
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|defaultValue
parameter_list|)
block|{
return|return
name|super
operator|.
name|param
argument_list|(
name|key
argument_list|,
name|delegate
operator|.
name|param
argument_list|(
name|key
argument_list|,
name|defaultValue
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|paramAsBoolean
specifier|public
name|boolean
name|paramAsBoolean
parameter_list|(
name|String
name|key
parameter_list|,
name|boolean
name|defaultValue
parameter_list|)
block|{
return|return
name|super
operator|.
name|paramAsBoolean
argument_list|(
name|key
argument_list|,
name|delegate
operator|.
name|paramAsBoolean
argument_list|(
name|key
argument_list|,
name|defaultValue
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|paramAsBooleanOptional
specifier|public
name|Boolean
name|paramAsBooleanOptional
parameter_list|(
name|String
name|key
parameter_list|,
name|Boolean
name|defaultValue
parameter_list|)
block|{
return|return
name|super
operator|.
name|paramAsBooleanOptional
argument_list|(
name|key
argument_list|,
name|delegate
operator|.
name|paramAsBooleanOptional
argument_list|(
name|key
argument_list|,
name|defaultValue
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|method|toXContent
name|XContentBuilder
name|toXContent
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|,
name|Params
name|params
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

