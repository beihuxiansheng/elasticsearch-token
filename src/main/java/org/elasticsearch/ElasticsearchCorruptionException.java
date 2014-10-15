begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch
package|package
name|org
operator|.
name|elasticsearch
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|CorruptIndexException
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

begin_comment
comment|/**  * This exception is thrown when Elasticsearch detects  * an inconsistency in one of it's persistent files.  */
end_comment

begin_class
DECL|class|ElasticsearchCorruptionException
specifier|public
class|class
name|ElasticsearchCorruptionException
extends|extends
name|IOException
block|{
comment|/**      * Creates a new {@link ElasticsearchCorruptionException}      * @param message the exception message.      */
DECL|method|ElasticsearchCorruptionException
specifier|public
name|ElasticsearchCorruptionException
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a new {@link ElasticsearchCorruptionException} with the given exceptions stacktrace.      * This constructor copies the stacktrace as well as the message from the given      * {@link org.apache.lucene.index.CorruptIndexException} into this exception.      *      * @param ex the exception cause      */
DECL|method|ElasticsearchCorruptionException
specifier|public
name|ElasticsearchCorruptionException
parameter_list|(
name|CorruptIndexException
name|ex
parameter_list|)
block|{
name|this
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|ex
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|setStackTrace
argument_list|(
name|ex
operator|.
name|getStackTrace
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

