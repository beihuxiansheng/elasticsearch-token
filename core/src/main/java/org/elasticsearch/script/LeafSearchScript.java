begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.script
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|script
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
name|lucene
operator|.
name|ScorerAware
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
comment|/**  * A per-segment {@link SearchScript}.  */
end_comment

begin_interface
DECL|interface|LeafSearchScript
specifier|public
interface|interface
name|LeafSearchScript
extends|extends
name|ScorerAware
extends|,
name|ExecutableScript
block|{
DECL|method|setDocument
name|void
name|setDocument
parameter_list|(
name|int
name|doc
parameter_list|)
function_decl|;
DECL|method|setSource
name|void
name|setSource
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|source
parameter_list|)
function_decl|;
comment|/**      * Sets per-document aggregation {@code _value}.      *<p>      * The default implementation just calls {@code setNextVar("_value", value)} but      * some engines might want to handle this differently for better performance.      *<p>      * @param value per-document value, typically a String, Long, or Double      */
DECL|method|setNextAggregationValue
specifier|default
name|void
name|setNextAggregationValue
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
name|setNextVar
argument_list|(
literal|"_value"
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
DECL|method|runAsLong
name|long
name|runAsLong
parameter_list|()
function_decl|;
DECL|method|runAsDouble
name|double
name|runAsDouble
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

