begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.query
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|query
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
name|io
operator|.
name|stream
operator|.
name|StreamInput
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
name|io
operator|.
name|stream
operator|.
name|StreamOutput
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
name|io
operator|.
name|stream
operator|.
name|Streamable
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
name|dfs
operator|.
name|AggregatedDfs
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
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|dfs
operator|.
name|AggregatedDfs
operator|.
name|readAggregatedDfs
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|QuerySearchRequest
specifier|public
class|class
name|QuerySearchRequest
implements|implements
name|Streamable
block|{
DECL|field|id
specifier|private
name|long
name|id
decl_stmt|;
DECL|field|dfs
specifier|private
name|AggregatedDfs
name|dfs
decl_stmt|;
DECL|method|QuerySearchRequest
specifier|public
name|QuerySearchRequest
parameter_list|()
block|{     }
DECL|method|QuerySearchRequest
specifier|public
name|QuerySearchRequest
parameter_list|(
name|long
name|id
parameter_list|,
name|AggregatedDfs
name|dfs
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|dfs
operator|=
name|dfs
expr_stmt|;
block|}
DECL|method|id
specifier|public
name|long
name|id
parameter_list|()
block|{
return|return
name|id
return|;
block|}
DECL|method|dfs
specifier|public
name|AggregatedDfs
name|dfs
parameter_list|()
block|{
return|return
name|dfs
return|;
block|}
annotation|@
name|Override
DECL|method|readFrom
specifier|public
name|void
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|id
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|dfs
operator|=
name|readAggregatedDfs
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeTo
specifier|public
name|void
name|writeTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeLong
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

