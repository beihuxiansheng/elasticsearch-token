begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
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
name|index
operator|.
name|engine
operator|.
name|IgnoreOnRecoveryEngineException
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

begin_class
DECL|class|AlreadyExpiredException
specifier|public
class|class
name|AlreadyExpiredException
extends|extends
name|ElasticsearchException
implements|implements
name|IgnoreOnRecoveryEngineException
block|{
DECL|field|index
specifier|private
name|String
name|index
decl_stmt|;
DECL|field|type
specifier|private
name|String
name|type
decl_stmt|;
DECL|field|id
specifier|private
name|String
name|id
decl_stmt|;
DECL|field|timestamp
specifier|private
specifier|final
name|long
name|timestamp
decl_stmt|;
DECL|field|ttl
specifier|private
specifier|final
name|long
name|ttl
decl_stmt|;
DECL|field|now
specifier|private
specifier|final
name|long
name|now
decl_stmt|;
DECL|method|AlreadyExpiredException
specifier|public
name|AlreadyExpiredException
parameter_list|(
name|String
name|index
parameter_list|,
name|String
name|type
parameter_list|,
name|String
name|id
parameter_list|,
name|long
name|timestamp
parameter_list|,
name|long
name|ttl
parameter_list|,
name|long
name|now
parameter_list|)
block|{
name|super
argument_list|(
literal|"already expired ["
operator|+
name|index
operator|+
literal|"]/["
operator|+
name|type
operator|+
literal|"]/["
operator|+
name|id
operator|+
literal|"] due to expire at ["
operator|+
operator|(
name|timestamp
operator|+
name|ttl
operator|)
operator|+
literal|"] and was processed at ["
operator|+
name|now
operator|+
literal|"]"
argument_list|)
expr_stmt|;
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|timestamp
operator|=
name|timestamp
expr_stmt|;
name|this
operator|.
name|ttl
operator|=
name|ttl
expr_stmt|;
name|this
operator|.
name|now
operator|=
name|now
expr_stmt|;
block|}
DECL|method|index
specifier|public
name|String
name|index
parameter_list|()
block|{
return|return
name|index
return|;
block|}
DECL|method|type
specifier|public
name|String
name|type
parameter_list|()
block|{
return|return
name|type
return|;
block|}
DECL|method|id
specifier|public
name|String
name|id
parameter_list|()
block|{
return|return
name|id
return|;
block|}
DECL|method|timestamp
specifier|public
name|long
name|timestamp
parameter_list|()
block|{
return|return
name|timestamp
return|;
block|}
DECL|method|ttl
specifier|public
name|long
name|ttl
parameter_list|()
block|{
return|return
name|ttl
return|;
block|}
DECL|method|now
specifier|public
name|long
name|now
parameter_list|()
block|{
return|return
name|now
return|;
block|}
DECL|method|AlreadyExpiredException
specifier|public
name|AlreadyExpiredException
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
name|index
operator|=
name|in
operator|.
name|readOptionalString
argument_list|()
expr_stmt|;
name|type
operator|=
name|in
operator|.
name|readOptionalString
argument_list|()
expr_stmt|;
name|id
operator|=
name|in
operator|.
name|readOptionalString
argument_list|()
expr_stmt|;
name|timestamp
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|ttl
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|now
operator|=
name|in
operator|.
name|readLong
argument_list|()
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
name|writeOptionalString
argument_list|(
name|index
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalString
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalString
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|timestamp
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|ttl
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|now
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

