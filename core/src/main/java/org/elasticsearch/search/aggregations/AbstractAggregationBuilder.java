begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
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
name|xcontent
operator|.
name|ToXContent
import|;
end_import

begin_comment
comment|/**  * Base structure for aggregation builders.  */
end_comment

begin_class
DECL|class|AbstractAggregationBuilder
specifier|public
specifier|abstract
class|class
name|AbstractAggregationBuilder
implements|implements
name|ToXContent
block|{
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|type
specifier|protected
specifier|final
name|String
name|type
decl_stmt|;
comment|/**      * Sole constructor, typically used by sub-classes.      */
DECL|method|AbstractAggregationBuilder
specifier|protected
name|AbstractAggregationBuilder
parameter_list|(
name|String
name|name
parameter_list|,
name|String
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
name|type
operator|=
name|type
expr_stmt|;
block|}
comment|/**      * Return the name of the aggregation that is being built.      */
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
block|}
end_class

end_unit

