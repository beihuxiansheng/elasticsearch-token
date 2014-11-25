begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.query
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
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
name|action
operator|.
name|deletebyquery
operator|.
name|TransportShardDeleteByQueryAction
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
name|internal
operator|.
name|SearchContext
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|QueryParserUtils
specifier|public
specifier|final
class|class
name|QueryParserUtils
block|{
DECL|method|QueryParserUtils
specifier|private
name|QueryParserUtils
parameter_list|()
block|{     }
comment|/**      * Ensures that the query parsing wasn't invoked via the delete by query api.      */
DECL|method|ensureNotDeleteByQuery
specifier|public
specifier|static
name|void
name|ensureNotDeleteByQuery
parameter_list|(
name|String
name|name
parameter_list|,
name|QueryParseContext
name|parseContext
parameter_list|)
block|{
name|SearchContext
name|context
init|=
name|SearchContext
operator|.
name|current
argument_list|()
decl_stmt|;
if|if
condition|(
name|context
operator|==
literal|null
condition|)
block|{
comment|// We can't do the api check, because there is no search context.
comment|// Because the delete by query shard transport action sets the search context this isn't an issue.
return|return;
block|}
if|if
condition|(
name|TransportShardDeleteByQueryAction
operator|.
name|DELETE_BY_QUERY_API
operator|.
name|equals
argument_list|(
name|context
operator|.
name|source
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|parseContext
operator|.
name|index
argument_list|()
argument_list|,
literal|"["
operator|+
name|name
operator|+
literal|"] query and filter unsupported in delete_by_query api"
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

