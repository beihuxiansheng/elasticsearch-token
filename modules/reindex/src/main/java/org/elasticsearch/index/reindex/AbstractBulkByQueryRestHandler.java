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
name|GenericAction
import|;
end_import

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
name|settings
operator|.
name|Settings
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
name|xcontent
operator|.
name|XContentBuilder
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
name|xcontent
operator|.
name|XContentFactory
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
name|xcontent
operator|.
name|XContentParser
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
name|RestRequest
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
name|action
operator|.
name|search
operator|.
name|RestSearchAction
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Consumer
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|reindex
operator|.
name|AbstractBulkByScrollRequest
operator|.
name|SIZE_ALL_MATCHES
import|;
end_import

begin_comment
comment|/**  * Rest handler for reindex actions that accepts a search request like Update-By-Query or Delete-By-Query  */
end_comment

begin_class
DECL|class|AbstractBulkByQueryRestHandler
specifier|public
specifier|abstract
class|class
name|AbstractBulkByQueryRestHandler
parameter_list|<
name|Request
extends|extends
name|AbstractBulkByScrollRequest
parameter_list|<
name|Request
parameter_list|>
parameter_list|,
name|A
extends|extends
name|GenericAction
parameter_list|<
name|Request
parameter_list|,
name|BulkByScrollResponse
parameter_list|>
parameter_list|>
extends|extends
name|AbstractBaseReindexRestHandler
argument_list|<
name|Request
argument_list|,
name|A
argument_list|>
block|{
DECL|method|AbstractBulkByQueryRestHandler
specifier|protected
name|AbstractBulkByQueryRestHandler
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|A
name|action
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|,
name|action
argument_list|)
expr_stmt|;
block|}
DECL|method|parseInternalRequest
specifier|protected
name|void
name|parseInternalRequest
parameter_list|(
name|Request
name|internal
parameter_list|,
name|RestRequest
name|restRequest
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Consumer
argument_list|<
name|Object
argument_list|>
argument_list|>
name|bodyConsumers
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|internal
operator|!=
literal|null
operator|:
literal|"Request should not be null"
assert|;
assert|assert
name|restRequest
operator|!=
literal|null
operator|:
literal|"RestRequest should not be null"
assert|;
name|SearchRequest
name|searchRequest
init|=
name|internal
operator|.
name|getSearchRequest
argument_list|()
decl_stmt|;
name|int
name|scrollSize
init|=
name|searchRequest
operator|.
name|source
argument_list|()
operator|.
name|size
argument_list|()
decl_stmt|;
name|searchRequest
operator|.
name|source
argument_list|()
operator|.
name|size
argument_list|(
name|SIZE_ALL_MATCHES
argument_list|)
expr_stmt|;
try|try
init|(
name|XContentParser
name|parser
init|=
name|extractRequestSpecificFields
argument_list|(
name|restRequest
argument_list|,
name|bodyConsumers
argument_list|)
init|)
block|{
name|RestSearchAction
operator|.
name|parseSearchRequest
argument_list|(
name|searchRequest
argument_list|,
name|restRequest
argument_list|,
name|parser
argument_list|)
expr_stmt|;
block|}
name|internal
operator|.
name|setSize
argument_list|(
name|searchRequest
operator|.
name|source
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|searchRequest
operator|.
name|source
argument_list|()
operator|.
name|size
argument_list|(
name|restRequest
operator|.
name|paramAsInt
argument_list|(
literal|"scroll_size"
argument_list|,
name|scrollSize
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|conflicts
init|=
name|restRequest
operator|.
name|param
argument_list|(
literal|"conflicts"
argument_list|)
decl_stmt|;
if|if
condition|(
name|conflicts
operator|!=
literal|null
condition|)
block|{
name|internal
operator|.
name|setConflicts
argument_list|(
name|conflicts
argument_list|)
expr_stmt|;
block|}
comment|// Let the requester set search timeout. It is probably only going to be useful for testing but who knows.
if|if
condition|(
name|restRequest
operator|.
name|hasParam
argument_list|(
literal|"search_timeout"
argument_list|)
condition|)
block|{
name|searchRequest
operator|.
name|source
argument_list|()
operator|.
name|timeout
argument_list|(
name|restRequest
operator|.
name|paramAsTime
argument_list|(
literal|"search_timeout"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * We can't send parseSearchRequest REST content that it doesn't support      * so we will have to remove the content that is valid in addition to      * what it supports from the content first. This is a temporary hack and      * should get better when SearchRequest has full ObjectParser support      * then we can delegate and stuff.      */
DECL|method|extractRequestSpecificFields
specifier|private
name|XContentParser
name|extractRequestSpecificFields
parameter_list|(
name|RestRequest
name|restRequest
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Consumer
argument_list|<
name|Object
argument_list|>
argument_list|>
name|bodyConsumers
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|restRequest
operator|.
name|hasContentOrSourceParam
argument_list|()
operator|==
literal|false
condition|)
block|{
return|return
literal|null
return|;
comment|// body is optional
block|}
try|try
init|(
name|XContentParser
name|parser
init|=
name|restRequest
operator|.
name|contentOrSourceParamParser
argument_list|()
init|;
name|XContentBuilder
name|builder
operator|=
name|XContentFactory
operator|.
name|contentBuilder
argument_list|(
name|parser
operator|.
name|contentType
argument_list|()
argument_list|)
init|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|body
init|=
name|parser
operator|.
name|map
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Consumer
argument_list|<
name|Object
argument_list|>
argument_list|>
name|consumer
range|:
name|bodyConsumers
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Object
name|value
init|=
name|body
operator|.
name|remove
argument_list|(
name|consumer
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
name|consumer
operator|.
name|getValue
argument_list|()
operator|.
name|accept
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|parser
operator|.
name|contentType
argument_list|()
operator|.
name|xContent
argument_list|()
operator|.
name|createParser
argument_list|(
name|parser
operator|.
name|getXContentRegistry
argument_list|()
argument_list|,
name|builder
operator|.
name|map
argument_list|(
name|body
argument_list|)
operator|.
name|bytes
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

