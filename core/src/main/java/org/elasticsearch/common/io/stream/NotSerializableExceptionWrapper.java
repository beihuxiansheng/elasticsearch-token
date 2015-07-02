begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.io.stream
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|io
operator|.
name|stream
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ExceptionsHelper
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
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|RestStatus
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
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
comment|/**  * This exception can be used to wrap a given, not serializable exception  * to serialize via {@link StreamOutput#writeThrowable(Throwable)}.  * This class will perserve the stacktrace as well as the suppressed exceptions of  * the throwable it was created with instead of it's own. The stacktrace has no indication  * of where this exception was created.  */
end_comment

begin_class
DECL|class|NotSerializableExceptionWrapper
specifier|public
specifier|final
class|class
name|NotSerializableExceptionWrapper
extends|extends
name|ElasticsearchException
operator|.
name|WithRestHeadersException
block|{
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|status
specifier|private
specifier|final
name|RestStatus
name|status
decl_stmt|;
DECL|method|NotSerializableExceptionWrapper
specifier|public
name|NotSerializableExceptionWrapper
parameter_list|(
name|Throwable
name|other
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|headers
parameter_list|)
block|{
name|super
argument_list|(
name|other
operator|.
name|getMessage
argument_list|()
argument_list|,
name|other
operator|.
name|getCause
argument_list|()
argument_list|,
name|headers
argument_list|)
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|ElasticsearchException
operator|.
name|getExceptionName
argument_list|(
name|other
argument_list|)
expr_stmt|;
name|this
operator|.
name|status
operator|=
name|ExceptionsHelper
operator|.
name|status
argument_list|(
name|other
argument_list|)
expr_stmt|;
name|setStackTrace
argument_list|(
name|other
operator|.
name|getStackTrace
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Throwable
name|otherSuppressed
range|:
name|other
operator|.
name|getSuppressed
argument_list|()
control|)
block|{
name|addSuppressed
argument_list|(
name|otherSuppressed
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|NotSerializableExceptionWrapper
specifier|public
name|NotSerializableExceptionWrapper
parameter_list|(
name|WithRestHeadersException
name|other
parameter_list|)
block|{
name|this
argument_list|(
name|other
argument_list|,
name|other
operator|.
name|getHeaders
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|NotSerializableExceptionWrapper
specifier|public
name|NotSerializableExceptionWrapper
parameter_list|(
name|Throwable
name|other
parameter_list|)
block|{
name|this
argument_list|(
name|other
argument_list|,
name|Collections
operator|.
name|EMPTY_MAP
argument_list|)
expr_stmt|;
block|}
DECL|method|NotSerializableExceptionWrapper
specifier|public
name|NotSerializableExceptionWrapper
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|name
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
name|status
operator|=
name|RestStatus
operator|.
name|readFrom
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
name|super
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeString
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|RestStatus
operator|.
name|writeTo
argument_list|(
name|out
argument_list|,
name|status
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getExceptionName
specifier|protected
name|String
name|getExceptionName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
annotation|@
name|Override
DECL|method|status
specifier|public
name|RestStatus
name|status
parameter_list|()
block|{
return|return
name|status
return|;
block|}
block|}
end_class

end_unit

