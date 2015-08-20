begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.indices.breaker
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|breaker
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
name|breaker
operator|.
name|CircuitBreaker
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
name|ByteSizeValue
import|;
end_import

begin_comment
comment|/**  * Settings for a {@link CircuitBreaker}  */
end_comment

begin_class
DECL|class|BreakerSettings
specifier|public
class|class
name|BreakerSettings
block|{
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|limitBytes
specifier|private
specifier|final
name|long
name|limitBytes
decl_stmt|;
DECL|field|overhead
specifier|private
specifier|final
name|double
name|overhead
decl_stmt|;
DECL|field|type
specifier|private
specifier|final
name|CircuitBreaker
operator|.
name|Type
name|type
decl_stmt|;
DECL|method|BreakerSettings
specifier|public
name|BreakerSettings
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|limitBytes
parameter_list|,
name|double
name|overhead
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|limitBytes
argument_list|,
name|overhead
argument_list|,
name|CircuitBreaker
operator|.
name|Type
operator|.
name|MEMORY
argument_list|)
expr_stmt|;
block|}
DECL|method|BreakerSettings
specifier|public
name|BreakerSettings
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|limitBytes
parameter_list|,
name|double
name|overhead
parameter_list|,
name|CircuitBreaker
operator|.
name|Type
name|type
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|limitBytes
operator|=
name|limitBytes
expr_stmt|;
name|this
operator|.
name|overhead
operator|=
name|overhead
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|this
operator|.
name|name
return|;
block|}
DECL|method|getLimit
specifier|public
name|long
name|getLimit
parameter_list|()
block|{
return|return
name|this
operator|.
name|limitBytes
return|;
block|}
DECL|method|getOverhead
specifier|public
name|double
name|getOverhead
parameter_list|()
block|{
return|return
name|this
operator|.
name|overhead
return|;
block|}
DECL|method|getType
specifier|public
name|CircuitBreaker
operator|.
name|Type
name|getType
parameter_list|()
block|{
return|return
name|this
operator|.
name|type
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
return|return
literal|"["
operator|+
name|this
operator|.
name|name
operator|+
literal|",type="
operator|+
name|this
operator|.
name|type
operator|.
name|toString
argument_list|()
operator|+
literal|",limit="
operator|+
name|this
operator|.
name|limitBytes
operator|+
literal|"/"
operator|+
operator|new
name|ByteSizeValue
argument_list|(
name|this
operator|.
name|limitBytes
argument_list|)
operator|+
literal|",overhead="
operator|+
name|this
operator|.
name|overhead
operator|+
literal|"]"
return|;
block|}
block|}
end_class

end_unit
