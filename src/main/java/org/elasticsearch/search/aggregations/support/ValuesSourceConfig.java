begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.support
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|support
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|SearchScript
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|support
operator|.
name|format
operator|.
name|ValueFormat
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|support
operator|.
name|format
operator|.
name|ValueFormatter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|support
operator|.
name|format
operator|.
name|ValueParser
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|ValuesSourceConfig
specifier|public
class|class
name|ValuesSourceConfig
parameter_list|<
name|VS
extends|extends
name|ValuesSource
parameter_list|>
block|{
DECL|field|valueSourceType
specifier|final
name|Class
argument_list|<
name|VS
argument_list|>
name|valueSourceType
decl_stmt|;
DECL|field|fieldContext
name|FieldContext
name|fieldContext
decl_stmt|;
DECL|field|script
name|SearchScript
name|script
decl_stmt|;
DECL|field|scriptValueType
name|ValueType
name|scriptValueType
decl_stmt|;
DECL|field|unmapped
name|boolean
name|unmapped
init|=
literal|false
decl_stmt|;
DECL|field|formatPattern
name|String
name|formatPattern
decl_stmt|;
DECL|field|format
name|ValueFormat
name|format
decl_stmt|;
DECL|method|ValuesSourceConfig
specifier|public
name|ValuesSourceConfig
parameter_list|(
name|Class
argument_list|<
name|VS
argument_list|>
name|valueSourceType
parameter_list|)
block|{
name|this
operator|.
name|valueSourceType
operator|=
name|valueSourceType
expr_stmt|;
block|}
DECL|method|valueSourceType
specifier|public
name|Class
argument_list|<
name|VS
argument_list|>
name|valueSourceType
parameter_list|()
block|{
return|return
name|valueSourceType
return|;
block|}
DECL|method|fieldContext
specifier|public
name|FieldContext
name|fieldContext
parameter_list|()
block|{
return|return
name|fieldContext
return|;
block|}
DECL|method|script
specifier|public
name|SearchScript
name|script
parameter_list|()
block|{
return|return
name|script
return|;
block|}
DECL|method|unmapped
specifier|public
name|boolean
name|unmapped
parameter_list|()
block|{
return|return
name|unmapped
return|;
block|}
DECL|method|valid
specifier|public
name|boolean
name|valid
parameter_list|()
block|{
return|return
name|fieldContext
operator|!=
literal|null
operator|||
name|script
operator|!=
literal|null
operator|||
name|unmapped
return|;
block|}
DECL|method|fieldContext
specifier|public
name|ValuesSourceConfig
argument_list|<
name|VS
argument_list|>
name|fieldContext
parameter_list|(
name|FieldContext
name|fieldContext
parameter_list|)
block|{
name|this
operator|.
name|fieldContext
operator|=
name|fieldContext
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|script
specifier|public
name|ValuesSourceConfig
argument_list|<
name|VS
argument_list|>
name|script
parameter_list|(
name|SearchScript
name|script
parameter_list|)
block|{
name|this
operator|.
name|script
operator|=
name|script
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|unmapped
specifier|public
name|ValuesSourceConfig
argument_list|<
name|VS
argument_list|>
name|unmapped
parameter_list|(
name|boolean
name|unmapped
parameter_list|)
block|{
name|this
operator|.
name|unmapped
operator|=
name|unmapped
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|format
specifier|public
name|ValueFormat
name|format
parameter_list|()
block|{
return|return
name|format
return|;
block|}
DECL|method|formatter
specifier|public
name|ValueFormatter
name|formatter
parameter_list|()
block|{
return|return
name|format
operator|!=
literal|null
condition|?
name|format
operator|.
name|formatter
argument_list|()
else|:
literal|null
return|;
block|}
DECL|method|parser
specifier|public
name|ValueParser
name|parser
parameter_list|()
block|{
return|return
name|format
operator|!=
literal|null
condition|?
name|format
operator|.
name|parser
argument_list|()
else|:
literal|null
return|;
block|}
block|}
end_class

end_unit

