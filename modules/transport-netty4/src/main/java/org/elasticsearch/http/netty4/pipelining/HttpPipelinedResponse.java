begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.elasticsearch.http.netty4.pipelining
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|http
operator|.
name|netty4
operator|.
name|pipelining
package|;
end_package

begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|channel
operator|.
name|ChannelPromise
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|handler
operator|.
name|codec
operator|.
name|http
operator|.
name|FullHttpResponse
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|handler
operator|.
name|codec
operator|.
name|http
operator|.
name|HttpResponse
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|util
operator|.
name|ReferenceCounted
import|;
end_import

begin_class
DECL|class|HttpPipelinedResponse
class|class
name|HttpPipelinedResponse
implements|implements
name|Comparable
argument_list|<
name|HttpPipelinedResponse
argument_list|>
implements|,
name|ReferenceCounted
block|{
DECL|field|response
specifier|private
specifier|final
name|FullHttpResponse
name|response
decl_stmt|;
DECL|field|promise
specifier|private
specifier|final
name|ChannelPromise
name|promise
decl_stmt|;
DECL|field|sequence
specifier|private
specifier|final
name|int
name|sequence
decl_stmt|;
DECL|method|HttpPipelinedResponse
name|HttpPipelinedResponse
parameter_list|(
name|FullHttpResponse
name|response
parameter_list|,
name|ChannelPromise
name|promise
parameter_list|,
name|int
name|sequence
parameter_list|)
block|{
name|this
operator|.
name|response
operator|=
name|response
expr_stmt|;
name|this
operator|.
name|promise
operator|=
name|promise
expr_stmt|;
name|this
operator|.
name|sequence
operator|=
name|sequence
expr_stmt|;
block|}
DECL|method|response
specifier|public
name|FullHttpResponse
name|response
parameter_list|()
block|{
return|return
name|response
return|;
block|}
DECL|method|promise
specifier|public
name|ChannelPromise
name|promise
parameter_list|()
block|{
return|return
name|promise
return|;
block|}
DECL|method|sequence
specifier|public
name|int
name|sequence
parameter_list|()
block|{
return|return
name|sequence
return|;
block|}
annotation|@
name|Override
DECL|method|compareTo
specifier|public
name|int
name|compareTo
parameter_list|(
name|HttpPipelinedResponse
name|o
parameter_list|)
block|{
return|return
name|Integer
operator|.
name|compare
argument_list|(
name|sequence
argument_list|,
name|o
operator|.
name|sequence
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|refCnt
specifier|public
name|int
name|refCnt
parameter_list|()
block|{
return|return
name|response
operator|.
name|refCnt
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|retain
specifier|public
name|ReferenceCounted
name|retain
parameter_list|()
block|{
name|response
operator|.
name|retain
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|retain
specifier|public
name|ReferenceCounted
name|retain
parameter_list|(
name|int
name|increment
parameter_list|)
block|{
name|response
operator|.
name|retain
argument_list|(
name|increment
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|touch
specifier|public
name|ReferenceCounted
name|touch
parameter_list|()
block|{
name|response
operator|.
name|touch
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|touch
specifier|public
name|ReferenceCounted
name|touch
parameter_list|(
name|Object
name|hint
parameter_list|)
block|{
name|response
operator|.
name|touch
argument_list|(
name|hint
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|release
specifier|public
name|boolean
name|release
parameter_list|()
block|{
return|return
name|response
operator|.
name|release
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|release
specifier|public
name|boolean
name|release
parameter_list|(
name|int
name|decrement
parameter_list|)
block|{
return|return
name|response
operator|.
name|release
argument_list|(
name|decrement
argument_list|)
return|;
block|}
block|}
end_class

end_unit
