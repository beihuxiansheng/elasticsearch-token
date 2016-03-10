begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.reindex
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|reindex
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|search
operator|.
name|SearchRequest
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
name|Nullable
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
name|script
operator|.
name|Script
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
DECL|class|AbstractBulkIndexByScrollRequest
specifier|public
specifier|abstract
class|class
name|AbstractBulkIndexByScrollRequest
parameter_list|<
name|Self
extends|extends
name|AbstractBulkIndexByScrollRequest
parameter_list|<
name|Self
parameter_list|>
parameter_list|>
extends|extends
name|AbstractBulkByScrollRequest
argument_list|<
name|Self
argument_list|>
block|{
comment|/**      * Script to modify the documents before they are processed.      */
DECL|field|script
specifier|private
name|Script
name|script
decl_stmt|;
DECL|method|AbstractBulkIndexByScrollRequest
specifier|public
name|AbstractBulkIndexByScrollRequest
parameter_list|()
block|{     }
DECL|method|AbstractBulkIndexByScrollRequest
specifier|public
name|AbstractBulkIndexByScrollRequest
parameter_list|(
name|SearchRequest
name|source
parameter_list|)
block|{
name|super
argument_list|(
name|source
argument_list|)
expr_stmt|;
block|}
comment|/**      * Script to modify the documents before they are processed.      */
DECL|method|getScript
specifier|public
name|Script
name|getScript
parameter_list|()
block|{
return|return
name|script
return|;
block|}
comment|/**      * Script to modify the documents before they are processed.      */
DECL|method|setScript
specifier|public
name|Self
name|setScript
parameter_list|(
annotation|@
name|Nullable
name|Script
name|script
parameter_list|)
block|{
name|this
operator|.
name|script
operator|=
name|script
expr_stmt|;
return|return
name|self
argument_list|()
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
name|super
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|script
operator|=
name|Script
operator|.
name|readScript
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
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
name|writeOptionalStreamable
argument_list|(
name|script
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|searchToString
specifier|protected
name|void
name|searchToString
parameter_list|(
name|StringBuilder
name|b
parameter_list|)
block|{
name|super
operator|.
name|searchToString
argument_list|(
name|b
argument_list|)
expr_stmt|;
if|if
condition|(
name|script
operator|!=
literal|null
condition|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|" updated with ["
argument_list|)
operator|.
name|append
argument_list|(
name|script
argument_list|)
operator|.
name|append
argument_list|(
literal|']'
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit
