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
name|ElasticsearchParseException
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
name|client
operator|.
name|node
operator|.
name|NodeClient
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|service
operator|.
name|ClusterService
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
name|ParseFieldMatcher
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
name|rest
operator|.
name|RestController
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
name|script
operator|.
name|Script
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
name|ScriptType
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
name|SearchRequestParsers
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|rest
operator|.
name|RestRequest
operator|.
name|Method
operator|.
name|POST
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|Script
operator|.
name|ScriptField
import|;
end_import

begin_class
DECL|class|RestUpdateByQueryAction
specifier|public
class|class
name|RestUpdateByQueryAction
extends|extends
name|AbstractBulkByQueryRestHandler
argument_list|<
name|UpdateByQueryRequest
argument_list|,
name|UpdateByQueryAction
argument_list|>
block|{
annotation|@
name|Inject
DECL|method|RestUpdateByQueryAction
specifier|public
name|RestUpdateByQueryAction
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|RestController
name|controller
parameter_list|,
name|SearchRequestParsers
name|searchRequestParsers
parameter_list|,
name|ClusterService
name|clusterService
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|,
name|searchRequestParsers
argument_list|,
name|clusterService
argument_list|,
name|UpdateByQueryAction
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
name|controller
operator|.
name|registerHandler
argument_list|(
name|POST
argument_list|,
literal|"/{index}/_update_by_query"
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|controller
operator|.
name|registerHandler
argument_list|(
name|POST
argument_list|,
literal|"/{index}/{type}/_update_by_query"
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|prepareRequest
specifier|public
name|RestChannelConsumer
name|prepareRequest
parameter_list|(
name|RestRequest
name|request
parameter_list|,
name|NodeClient
name|client
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|doPrepareRequest
argument_list|(
name|request
argument_list|,
name|client
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|buildRequest
specifier|protected
name|UpdateByQueryRequest
name|buildRequest
parameter_list|(
name|RestRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
comment|/*          * Passing the search request through UpdateByQueryRequest first allows          * it to set its own defaults which differ from SearchRequest's          * defaults. Then the parse can override them.          */
name|UpdateByQueryRequest
name|internal
init|=
operator|new
name|UpdateByQueryRequest
argument_list|(
operator|new
name|SearchRequest
argument_list|()
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Consumer
argument_list|<
name|Object
argument_list|>
argument_list|>
name|consumers
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|consumers
operator|.
name|put
argument_list|(
literal|"conflicts"
argument_list|,
name|o
lambda|->
name|internal
operator|.
name|setConflicts
argument_list|(
operator|(
name|String
operator|)
name|o
argument_list|)
argument_list|)
expr_stmt|;
name|consumers
operator|.
name|put
argument_list|(
literal|"script"
argument_list|,
name|o
lambda|->
name|internal
operator|.
name|setScript
argument_list|(
name|parseScript
argument_list|(
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|o
argument_list|,
name|parseFieldMatcher
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|parseInternalRequest
argument_list|(
name|internal
argument_list|,
name|request
argument_list|,
name|consumers
argument_list|)
expr_stmt|;
name|internal
operator|.
name|setPipeline
argument_list|(
name|request
operator|.
name|param
argument_list|(
literal|"pipeline"
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|internal
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|parseScript
specifier|static
name|Script
name|parseScript
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|config
parameter_list|,
name|ParseFieldMatcher
name|parseFieldMatcher
parameter_list|)
block|{
name|String
name|script
init|=
literal|null
decl_stmt|;
name|ScriptType
name|type
init|=
literal|null
decl_stmt|;
name|String
name|lang
init|=
literal|null
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|params
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|itr
init|=
name|config
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|itr
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|entry
init|=
name|itr
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|parameterName
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Object
name|parameterValue
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|parseFieldMatcher
operator|.
name|match
argument_list|(
name|parameterName
argument_list|,
name|ScriptField
operator|.
name|LANG
argument_list|)
condition|)
block|{
if|if
condition|(
name|parameterValue
operator|instanceof
name|String
operator|||
name|parameterValue
operator|==
literal|null
condition|)
block|{
name|lang
operator|=
operator|(
name|String
operator|)
name|parameterValue
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"Value must be of type String: ["
operator|+
name|parameterName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
name|parseFieldMatcher
operator|.
name|match
argument_list|(
name|parameterName
argument_list|,
name|ScriptField
operator|.
name|PARAMS
argument_list|)
condition|)
block|{
if|if
condition|(
name|parameterValue
operator|instanceof
name|Map
operator|||
name|parameterValue
operator|==
literal|null
condition|)
block|{
name|params
operator|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|parameterValue
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"Value must be of type String: ["
operator|+
name|parameterName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
name|parseFieldMatcher
operator|.
name|match
argument_list|(
name|parameterName
argument_list|,
name|ScriptType
operator|.
name|INLINE
operator|.
name|getParseField
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|parameterValue
operator|instanceof
name|String
operator|||
name|parameterValue
operator|==
literal|null
condition|)
block|{
name|script
operator|=
operator|(
name|String
operator|)
name|parameterValue
expr_stmt|;
name|type
operator|=
name|ScriptType
operator|.
name|INLINE
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"Value must be of type String: ["
operator|+
name|parameterName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
name|parseFieldMatcher
operator|.
name|match
argument_list|(
name|parameterName
argument_list|,
name|ScriptType
operator|.
name|FILE
operator|.
name|getParseField
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|parameterValue
operator|instanceof
name|String
operator|||
name|parameterValue
operator|==
literal|null
condition|)
block|{
name|script
operator|=
operator|(
name|String
operator|)
name|parameterValue
expr_stmt|;
name|type
operator|=
name|ScriptType
operator|.
name|FILE
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"Value must be of type String: ["
operator|+
name|parameterName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
name|parseFieldMatcher
operator|.
name|match
argument_list|(
name|parameterName
argument_list|,
name|ScriptType
operator|.
name|STORED
operator|.
name|getParseField
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|parameterValue
operator|instanceof
name|String
operator|||
name|parameterValue
operator|==
literal|null
condition|)
block|{
name|script
operator|=
operator|(
name|String
operator|)
name|parameterValue
expr_stmt|;
name|type
operator|=
name|ScriptType
operator|.
name|STORED
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"Value must be of type String: ["
operator|+
name|parameterName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
block|}
if|if
condition|(
name|script
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"expected one of [{}], [{}] or [{}] fields, but found none"
argument_list|,
name|ScriptType
operator|.
name|INLINE
operator|.
name|getParseField
argument_list|()
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|ScriptType
operator|.
name|FILE
operator|.
name|getParseField
argument_list|()
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|ScriptType
operator|.
name|STORED
operator|.
name|getParseField
argument_list|()
operator|.
name|getPreferredName
argument_list|()
argument_list|)
throw|;
block|}
assert|assert
name|type
operator|!=
literal|null
operator|:
literal|"if script is not null, type should definitely not be null"
assert|;
return|return
operator|new
name|Script
argument_list|(
name|script
argument_list|,
name|type
argument_list|,
name|lang
argument_list|,
name|params
argument_list|)
return|;
block|}
block|}
end_class

end_unit

