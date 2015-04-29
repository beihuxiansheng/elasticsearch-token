begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.metrics
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|metrics
package|;
end_package

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
name|reducers
operator|.
name|Reducer
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
name|java
operator|.
name|util
operator|.
name|List
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
DECL|class|InternalNumericMetricsAggregation
specifier|public
specifier|abstract
class|class
name|InternalNumericMetricsAggregation
extends|extends
name|InternalMetricsAggregation
block|{
DECL|field|valueFormatter
specifier|protected
name|ValueFormatter
name|valueFormatter
decl_stmt|;
DECL|class|SingleValue
specifier|public
specifier|static
specifier|abstract
class|class
name|SingleValue
extends|extends
name|InternalNumericMetricsAggregation
implements|implements
name|NumericMetricsAggregation
operator|.
name|SingleValue
block|{
DECL|method|SingleValue
specifier|protected
name|SingleValue
parameter_list|()
block|{}
DECL|method|SingleValue
specifier|protected
name|SingleValue
parameter_list|(
name|String
name|name
parameter_list|,
name|List
argument_list|<
name|Reducer
argument_list|>
name|reducers
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|metaData
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|reducers
argument_list|,
name|metaData
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getValueAsString
specifier|public
name|String
name|getValueAsString
parameter_list|()
block|{
if|if
condition|(
name|valueFormatter
operator|==
literal|null
condition|)
block|{
return|return
name|ValueFormatter
operator|.
name|RAW
operator|.
name|format
argument_list|(
name|value
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|valueFormatter
operator|.
name|format
argument_list|(
name|value
argument_list|()
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getProperty
specifier|public
name|Object
name|getProperty
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|path
parameter_list|)
block|{
if|if
condition|(
name|path
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|this
return|;
block|}
elseif|else
if|if
condition|(
name|path
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|&&
literal|"value"
operator|.
name|equals
argument_list|(
name|path
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
condition|)
block|{
return|return
name|value
argument_list|()
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"path not supported for ["
operator|+
name|getName
argument_list|()
operator|+
literal|"]: "
operator|+
name|path
argument_list|)
throw|;
block|}
block|}
block|}
DECL|class|MultiValue
specifier|public
specifier|static
specifier|abstract
class|class
name|MultiValue
extends|extends
name|InternalNumericMetricsAggregation
implements|implements
name|NumericMetricsAggregation
operator|.
name|MultiValue
block|{
DECL|method|MultiValue
specifier|protected
name|MultiValue
parameter_list|()
block|{}
DECL|method|MultiValue
specifier|protected
name|MultiValue
parameter_list|(
name|String
name|name
parameter_list|,
name|List
argument_list|<
name|Reducer
argument_list|>
name|reducers
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|metaData
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|reducers
argument_list|,
name|metaData
argument_list|)
expr_stmt|;
block|}
DECL|method|value
specifier|public
specifier|abstract
name|double
name|value
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
DECL|method|valueAsString
specifier|public
name|String
name|valueAsString
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|valueFormatter
operator|==
literal|null
condition|)
block|{
return|return
name|ValueFormatter
operator|.
name|RAW
operator|.
name|format
argument_list|(
name|value
argument_list|(
name|name
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|valueFormatter
operator|.
name|format
argument_list|(
name|value
argument_list|(
name|name
argument_list|)
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getProperty
specifier|public
name|Object
name|getProperty
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|path
parameter_list|)
block|{
if|if
condition|(
name|path
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|this
return|;
block|}
elseif|else
if|if
condition|(
name|path
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
return|return
name|value
argument_list|(
name|path
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"path not supported for ["
operator|+
name|getName
argument_list|()
operator|+
literal|"]: "
operator|+
name|path
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|InternalNumericMetricsAggregation
specifier|private
name|InternalNumericMetricsAggregation
parameter_list|()
block|{}
comment|// for serialization
DECL|method|InternalNumericMetricsAggregation
specifier|private
name|InternalNumericMetricsAggregation
parameter_list|(
name|String
name|name
parameter_list|,
name|List
argument_list|<
name|Reducer
argument_list|>
name|reducers
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|metaData
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|reducers
argument_list|,
name|metaData
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

