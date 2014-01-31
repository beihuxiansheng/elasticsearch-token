begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.search.type
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|search
operator|.
name|type
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|Version
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
name|Tuple
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
DECL|class|ParsedScrollId
specifier|public
class|class
name|ParsedScrollId
block|{
DECL|field|QUERY_THEN_FETCH_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|QUERY_THEN_FETCH_TYPE
init|=
literal|"queryThenFetch"
decl_stmt|;
DECL|field|QUERY_AND_FETCH_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|QUERY_AND_FETCH_TYPE
init|=
literal|"queryAndFetch"
decl_stmt|;
DECL|field|SCAN
specifier|public
specifier|static
specifier|final
name|String
name|SCAN
init|=
literal|"scan"
decl_stmt|;
DECL|field|SCROLL_SEARCH_AFTER_MINIMUM_VERSION
specifier|public
specifier|static
specifier|final
name|Version
name|SCROLL_SEARCH_AFTER_MINIMUM_VERSION
init|=
name|Version
operator|.
name|V_1_2_0
decl_stmt|;
DECL|field|source
specifier|private
specifier|final
name|String
name|source
decl_stmt|;
DECL|field|type
specifier|private
specifier|final
name|String
name|type
decl_stmt|;
DECL|field|context
specifier|private
specifier|final
name|Tuple
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
index|[]
name|context
decl_stmt|;
DECL|field|attributes
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|attributes
decl_stmt|;
DECL|method|ParsedScrollId
specifier|public
name|ParsedScrollId
parameter_list|(
name|String
name|source
parameter_list|,
name|String
name|type
parameter_list|,
name|Tuple
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
index|[]
name|context
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|attributes
parameter_list|)
block|{
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|this
operator|.
name|attributes
operator|=
name|attributes
expr_stmt|;
block|}
DECL|method|getSource
specifier|public
name|String
name|getSource
parameter_list|()
block|{
return|return
name|source
return|;
block|}
DECL|method|getType
specifier|public
name|String
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
DECL|method|getContext
specifier|public
name|Tuple
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
index|[]
name|getContext
parameter_list|()
block|{
return|return
name|context
return|;
block|}
DECL|method|getAttributes
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getAttributes
parameter_list|()
block|{
return|return
name|this
operator|.
name|attributes
return|;
block|}
block|}
end_class

end_unit

