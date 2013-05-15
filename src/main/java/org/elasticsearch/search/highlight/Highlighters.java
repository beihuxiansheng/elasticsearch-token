begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.highlight
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|highlight
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableMap
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
name|collect
operator|.
name|MapBuilder
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
name|inject
operator|.
name|Inject
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|Highlighters
specifier|public
class|class
name|Highlighters
block|{
DECL|field|parsers
specifier|private
specifier|final
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|Highlighter
argument_list|>
name|parsers
decl_stmt|;
annotation|@
name|Inject
DECL|method|Highlighters
specifier|public
name|Highlighters
parameter_list|(
name|Set
argument_list|<
name|Highlighter
argument_list|>
name|parsers
parameter_list|)
block|{
name|MapBuilder
argument_list|<
name|String
argument_list|,
name|Highlighter
argument_list|>
name|builder
init|=
name|MapBuilder
operator|.
name|newMapBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|Highlighter
name|parser
range|:
name|parsers
control|)
block|{
for|for
control|(
name|String
name|type
range|:
name|parser
operator|.
name|names
argument_list|()
control|)
block|{
name|builder
operator|.
name|put
argument_list|(
name|type
argument_list|,
name|parser
argument_list|)
expr_stmt|;
block|}
block|}
name|this
operator|.
name|parsers
operator|=
name|builder
operator|.
name|immutableMap
argument_list|()
expr_stmt|;
block|}
DECL|method|get
specifier|public
name|Highlighter
name|get
parameter_list|(
name|String
name|type
parameter_list|)
block|{
return|return
name|parsers
operator|.
name|get
argument_list|(
name|type
argument_list|)
return|;
block|}
block|}
end_class

end_unit

