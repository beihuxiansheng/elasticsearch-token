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
name|search
operator|.
name|aggregations
operator|.
name|support
operator|.
name|bytes
operator|.
name|BytesValuesSource
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
name|numeric
operator|.
name|NumericValuesSource
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_enum
DECL|enum|ScriptValueType
specifier|public
enum|enum
name|ScriptValueType
block|{
DECL|enum constant|STRING
name|STRING
parameter_list|(
name|BytesValuesSource
operator|.
name|class
parameter_list|)
operator|,
DECL|enum constant|LONG
constructor|LONG(NumericValuesSource.class
block|)
enum|,
DECL|enum constant|DOUBLE
name|DOUBLE
argument_list|(
name|NumericValuesSource
operator|.
name|class
argument_list|)
enum|;
end_enum

begin_decl_stmt
DECL|field|valuesSourceType
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|ValuesSource
argument_list|>
name|valuesSourceType
decl_stmt|;
end_decl_stmt

begin_constructor
DECL|method|ScriptValueType
specifier|private
name|ScriptValueType
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|ValuesSource
argument_list|>
name|valuesSourceType
parameter_list|)
block|{
name|this
operator|.
name|valuesSourceType
operator|=
name|valuesSourceType
expr_stmt|;
block|}
end_constructor

begin_function
DECL|method|getValuesSourceType
specifier|public
name|Class
argument_list|<
name|?
extends|extends
name|ValuesSource
argument_list|>
name|getValuesSourceType
parameter_list|()
block|{
return|return
name|valuesSourceType
return|;
block|}
end_function

begin_function
DECL|method|isNumeric
specifier|public
name|boolean
name|isNumeric
parameter_list|()
block|{
return|return
name|this
operator|!=
name|STRING
return|;
block|}
end_function

begin_function
DECL|method|isFloatingPoint
specifier|public
name|boolean
name|isFloatingPoint
parameter_list|()
block|{
return|return
name|this
operator|==
name|DOUBLE
return|;
block|}
end_function

unit|}
end_unit

