begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
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
name|AbstractModule
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
name|Scopes
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
name|assistedinject
operator|.
name|FactoryProvider
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
name|multibindings
operator|.
name|MapBinder
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
name|java
operator|.
name|util
operator|.
name|LinkedList
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
DECL|class|IndexQueryParserModule
specifier|public
class|class
name|IndexQueryParserModule
extends|extends
name|AbstractModule
block|{
comment|/**      * A custom processor that can be extended to process and bind custom implementations of      * {@link QueryParserFactory}, and {@link FilterParser}.      */
DECL|class|QueryParsersProcessor
specifier|public
specifier|static
class|class
name|QueryParsersProcessor
block|{
comment|/**          * Extension point to bind a custom {@link QueryParserFactory}.          */
DECL|method|processXContentQueryParsers
specifier|public
name|void
name|processXContentQueryParsers
parameter_list|(
name|XContentQueryParsersBindings
name|bindings
parameter_list|)
block|{          }
DECL|class|XContentQueryParsersBindings
specifier|public
specifier|static
class|class
name|XContentQueryParsersBindings
block|{
DECL|field|binder
specifier|private
specifier|final
name|MapBinder
argument_list|<
name|String
argument_list|,
name|QueryParserFactory
argument_list|>
name|binder
decl_stmt|;
DECL|field|groupSettings
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Settings
argument_list|>
name|groupSettings
decl_stmt|;
DECL|method|XContentQueryParsersBindings
specifier|public
name|XContentQueryParsersBindings
parameter_list|(
name|MapBinder
argument_list|<
name|String
argument_list|,
name|QueryParserFactory
argument_list|>
name|binder
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Settings
argument_list|>
name|groupSettings
parameter_list|)
block|{
name|this
operator|.
name|binder
operator|=
name|binder
expr_stmt|;
name|this
operator|.
name|groupSettings
operator|=
name|groupSettings
expr_stmt|;
block|}
DECL|method|binder
specifier|public
name|MapBinder
argument_list|<
name|String
argument_list|,
name|QueryParserFactory
argument_list|>
name|binder
parameter_list|()
block|{
return|return
name|binder
return|;
block|}
DECL|method|groupSettings
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Settings
argument_list|>
name|groupSettings
parameter_list|()
block|{
return|return
name|groupSettings
return|;
block|}
DECL|method|processXContentQueryParser
specifier|public
name|void
name|processXContentQueryParser
parameter_list|(
name|String
name|name
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|QueryParser
argument_list|>
name|xcontentQueryParser
parameter_list|)
block|{
if|if
condition|(
operator|!
name|groupSettings
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|binder
operator|.
name|addBinding
argument_list|(
name|name
argument_list|)
operator|.
name|toProvider
argument_list|(
name|FactoryProvider
operator|.
name|newFactory
argument_list|(
name|QueryParserFactory
operator|.
name|class
argument_list|,
name|xcontentQueryParser
argument_list|)
argument_list|)
operator|.
name|in
argument_list|(
name|Scopes
operator|.
name|SINGLETON
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**          * Extension point to bind a custom {@link FilterParserFactory}.          */
DECL|method|processXContentFilterParsers
specifier|public
name|void
name|processXContentFilterParsers
parameter_list|(
name|XContentFilterParsersBindings
name|bindings
parameter_list|)
block|{          }
DECL|class|XContentFilterParsersBindings
specifier|public
specifier|static
class|class
name|XContentFilterParsersBindings
block|{
DECL|field|binder
specifier|private
specifier|final
name|MapBinder
argument_list|<
name|String
argument_list|,
name|FilterParserFactory
argument_list|>
name|binder
decl_stmt|;
DECL|field|groupSettings
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Settings
argument_list|>
name|groupSettings
decl_stmt|;
DECL|method|XContentFilterParsersBindings
specifier|public
name|XContentFilterParsersBindings
parameter_list|(
name|MapBinder
argument_list|<
name|String
argument_list|,
name|FilterParserFactory
argument_list|>
name|binder
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Settings
argument_list|>
name|groupSettings
parameter_list|)
block|{
name|this
operator|.
name|binder
operator|=
name|binder
expr_stmt|;
name|this
operator|.
name|groupSettings
operator|=
name|groupSettings
expr_stmt|;
block|}
DECL|method|binder
specifier|public
name|MapBinder
argument_list|<
name|String
argument_list|,
name|FilterParserFactory
argument_list|>
name|binder
parameter_list|()
block|{
return|return
name|binder
return|;
block|}
DECL|method|groupSettings
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Settings
argument_list|>
name|groupSettings
parameter_list|()
block|{
return|return
name|groupSettings
return|;
block|}
DECL|method|processXContentQueryFilter
specifier|public
name|void
name|processXContentQueryFilter
parameter_list|(
name|String
name|name
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|FilterParser
argument_list|>
name|xcontentFilterParser
parameter_list|)
block|{
if|if
condition|(
operator|!
name|groupSettings
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|binder
operator|.
name|addBinding
argument_list|(
name|name
argument_list|)
operator|.
name|toProvider
argument_list|(
name|FactoryProvider
operator|.
name|newFactory
argument_list|(
name|FilterParserFactory
operator|.
name|class
argument_list|,
name|xcontentFilterParser
argument_list|)
argument_list|)
operator|.
name|in
argument_list|(
name|Scopes
operator|.
name|SINGLETON
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|field|settings
specifier|private
specifier|final
name|Settings
name|settings
decl_stmt|;
DECL|field|processors
specifier|private
specifier|final
name|LinkedList
argument_list|<
name|QueryParsersProcessor
argument_list|>
name|processors
init|=
name|Lists
operator|.
name|newLinkedList
argument_list|()
decl_stmt|;
DECL|field|queries
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|?
extends|extends
name|QueryParser
argument_list|>
argument_list|>
name|queries
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
DECL|field|filters
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|?
extends|extends
name|FilterParser
argument_list|>
argument_list|>
name|filters
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
DECL|method|IndexQueryParserModule
specifier|public
name|IndexQueryParserModule
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|this
operator|.
name|settings
operator|=
name|settings
expr_stmt|;
block|}
comment|/**      * Adds a custom query parser.      *      * @param name        The name of the query parser      * @param queryParser the class of the query parser      */
DECL|method|addQueryParser
specifier|public
name|void
name|addQueryParser
parameter_list|(
name|String
name|name
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|QueryParser
argument_list|>
name|queryParser
parameter_list|)
block|{
name|queries
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|queryParser
argument_list|)
expr_stmt|;
block|}
comment|/**      * Adds a custom filter parser.      *      * @param name         The name of the filter parser      * @param filterParser the class of the filter parser      */
DECL|method|addFilterParser
specifier|public
name|void
name|addFilterParser
parameter_list|(
name|String
name|name
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|FilterParser
argument_list|>
name|filterParser
parameter_list|)
block|{
name|filters
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|filterParser
argument_list|)
expr_stmt|;
block|}
DECL|method|addProcessor
specifier|public
name|IndexQueryParserModule
name|addProcessor
parameter_list|(
name|QueryParsersProcessor
name|processor
parameter_list|)
block|{
name|processors
operator|.
name|addFirst
argument_list|(
name|processor
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|configure
specifier|protected
name|void
name|configure
parameter_list|()
block|{
name|bind
argument_list|(
name|IndexQueryParserService
operator|.
name|class
argument_list|)
operator|.
name|asEagerSingleton
argument_list|()
expr_stmt|;
comment|// handle XContenQueryParsers
name|MapBinder
argument_list|<
name|String
argument_list|,
name|QueryParserFactory
argument_list|>
name|queryBinder
init|=
name|MapBinder
operator|.
name|newMapBinder
argument_list|(
name|binder
argument_list|()
argument_list|,
name|String
operator|.
name|class
argument_list|,
name|QueryParserFactory
operator|.
name|class
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Settings
argument_list|>
name|xContentQueryParserGroups
init|=
name|settings
operator|.
name|getGroups
argument_list|(
name|IndexQueryParserService
operator|.
name|Defaults
operator|.
name|QUERY_PREFIX
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Settings
argument_list|>
name|entry
range|:
name|xContentQueryParserGroups
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|qName
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Settings
name|qSettings
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Class
argument_list|<
name|?
extends|extends
name|QueryParser
argument_list|>
name|type
init|=
name|qSettings
operator|.
name|getAsClass
argument_list|(
literal|"type"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Query Parser ["
operator|+
name|qName
operator|+
literal|"] must be provided with a type"
argument_list|)
throw|;
block|}
name|queryBinder
operator|.
name|addBinding
argument_list|(
name|qName
argument_list|)
operator|.
name|toProvider
argument_list|(
name|FactoryProvider
operator|.
name|newFactory
argument_list|(
name|QueryParserFactory
operator|.
name|class
argument_list|,
name|qSettings
operator|.
name|getAsClass
argument_list|(
literal|"type"
argument_list|,
literal|null
argument_list|)
argument_list|)
argument_list|)
operator|.
name|in
argument_list|(
name|Scopes
operator|.
name|SINGLETON
argument_list|)
expr_stmt|;
block|}
name|QueryParsersProcessor
operator|.
name|XContentQueryParsersBindings
name|xContentQueryParsersBindings
init|=
operator|new
name|QueryParsersProcessor
operator|.
name|XContentQueryParsersBindings
argument_list|(
name|queryBinder
argument_list|,
name|xContentQueryParserGroups
argument_list|)
decl_stmt|;
for|for
control|(
name|QueryParsersProcessor
name|processor
range|:
name|processors
control|)
block|{
name|processor
operator|.
name|processXContentQueryParsers
argument_list|(
name|xContentQueryParsersBindings
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|?
extends|extends
name|QueryParser
argument_list|>
argument_list|>
name|entry
range|:
name|queries
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|queryBinder
operator|.
name|addBinding
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
operator|.
name|toProvider
argument_list|(
name|FactoryProvider
operator|.
name|newFactory
argument_list|(
name|QueryParserFactory
operator|.
name|class
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
operator|.
name|in
argument_list|(
name|Scopes
operator|.
name|SINGLETON
argument_list|)
expr_stmt|;
block|}
comment|// handle XContentFilterParsers
name|MapBinder
argument_list|<
name|String
argument_list|,
name|FilterParserFactory
argument_list|>
name|filterBinder
init|=
name|MapBinder
operator|.
name|newMapBinder
argument_list|(
name|binder
argument_list|()
argument_list|,
name|String
operator|.
name|class
argument_list|,
name|FilterParserFactory
operator|.
name|class
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Settings
argument_list|>
name|xContentFilterParserGroups
init|=
name|settings
operator|.
name|getGroups
argument_list|(
name|IndexQueryParserService
operator|.
name|Defaults
operator|.
name|FILTER_PREFIX
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Settings
argument_list|>
name|entry
range|:
name|xContentFilterParserGroups
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|fName
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Settings
name|fSettings
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Class
argument_list|<
name|?
extends|extends
name|FilterParser
argument_list|>
name|type
init|=
name|fSettings
operator|.
name|getAsClass
argument_list|(
literal|"type"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Filter Parser ["
operator|+
name|fName
operator|+
literal|"] must be provided with a type"
argument_list|)
throw|;
block|}
name|filterBinder
operator|.
name|addBinding
argument_list|(
name|fName
argument_list|)
operator|.
name|toProvider
argument_list|(
name|FactoryProvider
operator|.
name|newFactory
argument_list|(
name|FilterParserFactory
operator|.
name|class
argument_list|,
name|fSettings
operator|.
name|getAsClass
argument_list|(
literal|"type"
argument_list|,
literal|null
argument_list|)
argument_list|)
argument_list|)
operator|.
name|in
argument_list|(
name|Scopes
operator|.
name|SINGLETON
argument_list|)
expr_stmt|;
block|}
name|QueryParsersProcessor
operator|.
name|XContentFilterParsersBindings
name|xContentFilterParsersBindings
init|=
operator|new
name|QueryParsersProcessor
operator|.
name|XContentFilterParsersBindings
argument_list|(
name|filterBinder
argument_list|,
name|xContentFilterParserGroups
argument_list|)
decl_stmt|;
for|for
control|(
name|QueryParsersProcessor
name|processor
range|:
name|processors
control|)
block|{
name|processor
operator|.
name|processXContentFilterParsers
argument_list|(
name|xContentFilterParsersBindings
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|?
extends|extends
name|FilterParser
argument_list|>
argument_list|>
name|entry
range|:
name|filters
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|filterBinder
operator|.
name|addBinding
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
operator|.
name|toProvider
argument_list|(
name|FactoryProvider
operator|.
name|newFactory
argument_list|(
name|FilterParserFactory
operator|.
name|class
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
operator|.
name|in
argument_list|(
name|Scopes
operator|.
name|SINGLETON
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

