begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.indices
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|indices
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
name|update
operator|.
name|UpdateHelper
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
name|metadata
operator|.
name|MetaDataIndexUpgradeService
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
name|geo
operator|.
name|ShapesAvailability
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
name|util
operator|.
name|ExtensionPoint
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
name|NodeServicesProvider
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
name|mapper
operator|.
name|Mapper
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
name|mapper
operator|.
name|MetadataFieldMapper
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
name|mapper
operator|.
name|core
operator|.
name|*
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
name|mapper
operator|.
name|geo
operator|.
name|GeoPointFieldMapper
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
name|mapper
operator|.
name|geo
operator|.
name|GeoShapeFieldMapper
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
name|mapper
operator|.
name|internal
operator|.
name|*
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
name|mapper
operator|.
name|ip
operator|.
name|IpFieldMapper
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
name|mapper
operator|.
name|object
operator|.
name|ObjectMapper
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
name|query
operator|.
name|*
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
name|query
operator|.
name|functionscore
operator|.
name|FunctionScoreQueryParser
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
name|termvectors
operator|.
name|TermVectorsService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|cache
operator|.
name|query
operator|.
name|IndicesQueryCache
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|cache
operator|.
name|request
operator|.
name|IndicesRequestCache
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|cluster
operator|.
name|IndicesClusterStateService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|fielddata
operator|.
name|cache
operator|.
name|IndicesFieldDataCache
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|fielddata
operator|.
name|cache
operator|.
name|IndicesFieldDataCacheListener
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|flush
operator|.
name|SyncedFlushService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|mapper
operator|.
name|MapperRegistry
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|memory
operator|.
name|IndexingMemoryController
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|query
operator|.
name|IndicesQueriesRegistry
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|recovery
operator|.
name|RecoverySettings
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|recovery
operator|.
name|RecoverySource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|recovery
operator|.
name|RecoveryTarget
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|store
operator|.
name|IndicesStore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|store
operator|.
name|TransportNodesListShardStoreMetaData
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|ttl
operator|.
name|IndicesTTLService
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
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
comment|/**  * Configures classes and services that are shared by indices on each node.  */
end_comment

begin_class
DECL|class|IndicesModule
specifier|public
class|class
name|IndicesModule
extends|extends
name|AbstractModule
block|{
DECL|field|queryParsers
specifier|private
specifier|final
name|ExtensionPoint
operator|.
name|ClassSet
argument_list|<
name|QueryParser
argument_list|>
name|queryParsers
init|=
operator|new
name|ExtensionPoint
operator|.
name|ClassSet
argument_list|<>
argument_list|(
literal|"query_parser"
argument_list|,
name|QueryParser
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|mapperParsers
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Mapper
operator|.
name|TypeParser
argument_list|>
name|mapperParsers
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|// Use a LinkedHashMap for metadataMappers because iteration order matters
DECL|field|metadataMapperParsers
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|MetadataFieldMapper
operator|.
name|TypeParser
argument_list|>
name|metadataMapperParsers
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|IndicesModule
specifier|public
name|IndicesModule
parameter_list|()
block|{
name|registerBuiltinQueryParsers
argument_list|()
expr_stmt|;
name|registerBuiltInMappers
argument_list|()
expr_stmt|;
name|registerBuiltInMetadataMappers
argument_list|()
expr_stmt|;
block|}
DECL|method|registerBuiltinQueryParsers
specifier|private
name|void
name|registerBuiltinQueryParsers
parameter_list|()
block|{
name|registerQueryParser
argument_list|(
name|MatchQueryParser
operator|.
name|class
argument_list|)
expr_stmt|;
name|registerQueryParser
argument_list|(
name|MultiMatchQueryParser
operator|.
name|class
argument_list|)
expr_stmt|;
name|registerQueryParser
argument_list|(
name|NestedQueryParser
operator|.
name|class
argument_list|)
expr_stmt|;
name|registerQueryParser
argument_list|(
name|HasChildQueryParser
operator|.
name|class
argument_list|)
expr_stmt|;
name|registerQueryParser
argument_list|(
name|HasParentQueryParser
operator|.
name|class
argument_list|)
expr_stmt|;
name|registerQueryParser
argument_list|(
name|DisMaxQueryParser
operator|.
name|class
argument_list|)
expr_stmt|;
name|registerQueryParser
argument_list|(
name|IdsQueryParser
operator|.
name|class
argument_list|)
expr_stmt|;
name|registerQueryParser
argument_list|(
name|MatchAllQueryParser
operator|.
name|class
argument_list|)
expr_stmt|;
name|registerQueryParser
argument_list|(
name|QueryStringQueryParser
operator|.
name|class
argument_list|)
expr_stmt|;
name|registerQueryParser
argument_list|(
name|BoostingQueryParser
operator|.
name|class
argument_list|)
expr_stmt|;
name|registerQueryParser
argument_list|(
name|BoolQueryParser
operator|.
name|class
argument_list|)
expr_stmt|;
name|registerQueryParser
argument_list|(
name|TermQueryParser
operator|.
name|class
argument_list|)
expr_stmt|;
name|registerQueryParser
argument_list|(
name|TermsQueryParser
operator|.
name|class
argument_list|)
expr_stmt|;
name|registerQueryParser
argument_list|(
name|FuzzyQueryParser
operator|.
name|class
argument_list|)
expr_stmt|;
name|registerQueryParser
argument_list|(
name|RegexpQueryParser
operator|.
name|class
argument_list|)
expr_stmt|;
name|registerQueryParser
argument_list|(
name|RangeQueryParser
operator|.
name|class
argument_list|)
expr_stmt|;
name|registerQueryParser
argument_list|(
name|PrefixQueryParser
operator|.
name|class
argument_list|)
expr_stmt|;
name|registerQueryParser
argument_list|(
name|WildcardQueryParser
operator|.
name|class
argument_list|)
expr_stmt|;
name|registerQueryParser
argument_list|(
name|ConstantScoreQueryParser
operator|.
name|class
argument_list|)
expr_stmt|;
name|registerQueryParser
argument_list|(
name|SpanTermQueryParser
operator|.
name|class
argument_list|)
expr_stmt|;
name|registerQueryParser
argument_list|(
name|SpanNotQueryParser
operator|.
name|class
argument_list|)
expr_stmt|;
name|registerQueryParser
argument_list|(
name|SpanWithinQueryParser
operator|.
name|class
argument_list|)
expr_stmt|;
name|registerQueryParser
argument_list|(
name|SpanContainingQueryParser
operator|.
name|class
argument_list|)
expr_stmt|;
name|registerQueryParser
argument_list|(
name|FieldMaskingSpanQueryParser
operator|.
name|class
argument_list|)
expr_stmt|;
name|registerQueryParser
argument_list|(
name|SpanFirstQueryParser
operator|.
name|class
argument_list|)
expr_stmt|;
name|registerQueryParser
argument_list|(
name|SpanNearQueryParser
operator|.
name|class
argument_list|)
expr_stmt|;
name|registerQueryParser
argument_list|(
name|SpanOrQueryParser
operator|.
name|class
argument_list|)
expr_stmt|;
name|registerQueryParser
argument_list|(
name|MoreLikeThisQueryParser
operator|.
name|class
argument_list|)
expr_stmt|;
name|registerQueryParser
argument_list|(
name|WrapperQueryParser
operator|.
name|class
argument_list|)
expr_stmt|;
name|registerQueryParser
argument_list|(
name|IndicesQueryParser
operator|.
name|class
argument_list|)
expr_stmt|;
name|registerQueryParser
argument_list|(
name|CommonTermsQueryParser
operator|.
name|class
argument_list|)
expr_stmt|;
name|registerQueryParser
argument_list|(
name|SpanMultiTermQueryParser
operator|.
name|class
argument_list|)
expr_stmt|;
name|registerQueryParser
argument_list|(
name|FunctionScoreQueryParser
operator|.
name|class
argument_list|)
expr_stmt|;
name|registerQueryParser
argument_list|(
name|SimpleQueryStringParser
operator|.
name|class
argument_list|)
expr_stmt|;
name|registerQueryParser
argument_list|(
name|TemplateQueryParser
operator|.
name|class
argument_list|)
expr_stmt|;
name|registerQueryParser
argument_list|(
name|TypeQueryParser
operator|.
name|class
argument_list|)
expr_stmt|;
name|registerQueryParser
argument_list|(
name|ScriptQueryParser
operator|.
name|class
argument_list|)
expr_stmt|;
name|registerQueryParser
argument_list|(
name|GeoDistanceQueryParser
operator|.
name|class
argument_list|)
expr_stmt|;
name|registerQueryParser
argument_list|(
name|GeoDistanceRangeQueryParser
operator|.
name|class
argument_list|)
expr_stmt|;
name|registerQueryParser
argument_list|(
name|GeoBoundingBoxQueryParser
operator|.
name|class
argument_list|)
expr_stmt|;
name|registerQueryParser
argument_list|(
name|GeohashCellQuery
operator|.
name|Parser
operator|.
name|class
argument_list|)
expr_stmt|;
name|registerQueryParser
argument_list|(
name|GeoPolygonQueryParser
operator|.
name|class
argument_list|)
expr_stmt|;
name|registerQueryParser
argument_list|(
name|ExistsQueryParser
operator|.
name|class
argument_list|)
expr_stmt|;
name|registerQueryParser
argument_list|(
name|MissingQueryParser
operator|.
name|class
argument_list|)
expr_stmt|;
name|registerQueryParser
argument_list|(
name|MatchNoneQueryParser
operator|.
name|class
argument_list|)
expr_stmt|;
if|if
condition|(
name|ShapesAvailability
operator|.
name|JTS_AVAILABLE
condition|)
block|{
name|registerQueryParser
argument_list|(
name|GeoShapeQueryParser
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|registerBuiltInMappers
specifier|private
name|void
name|registerBuiltInMappers
parameter_list|()
block|{
name|registerMapper
argument_list|(
name|ByteFieldMapper
operator|.
name|CONTENT_TYPE
argument_list|,
operator|new
name|ByteFieldMapper
operator|.
name|TypeParser
argument_list|()
argument_list|)
expr_stmt|;
name|registerMapper
argument_list|(
name|ShortFieldMapper
operator|.
name|CONTENT_TYPE
argument_list|,
operator|new
name|ShortFieldMapper
operator|.
name|TypeParser
argument_list|()
argument_list|)
expr_stmt|;
name|registerMapper
argument_list|(
name|IntegerFieldMapper
operator|.
name|CONTENT_TYPE
argument_list|,
operator|new
name|IntegerFieldMapper
operator|.
name|TypeParser
argument_list|()
argument_list|)
expr_stmt|;
name|registerMapper
argument_list|(
name|LongFieldMapper
operator|.
name|CONTENT_TYPE
argument_list|,
operator|new
name|LongFieldMapper
operator|.
name|TypeParser
argument_list|()
argument_list|)
expr_stmt|;
name|registerMapper
argument_list|(
name|FloatFieldMapper
operator|.
name|CONTENT_TYPE
argument_list|,
operator|new
name|FloatFieldMapper
operator|.
name|TypeParser
argument_list|()
argument_list|)
expr_stmt|;
name|registerMapper
argument_list|(
name|DoubleFieldMapper
operator|.
name|CONTENT_TYPE
argument_list|,
operator|new
name|DoubleFieldMapper
operator|.
name|TypeParser
argument_list|()
argument_list|)
expr_stmt|;
name|registerMapper
argument_list|(
name|BooleanFieldMapper
operator|.
name|CONTENT_TYPE
argument_list|,
operator|new
name|BooleanFieldMapper
operator|.
name|TypeParser
argument_list|()
argument_list|)
expr_stmt|;
name|registerMapper
argument_list|(
name|BinaryFieldMapper
operator|.
name|CONTENT_TYPE
argument_list|,
operator|new
name|BinaryFieldMapper
operator|.
name|TypeParser
argument_list|()
argument_list|)
expr_stmt|;
name|registerMapper
argument_list|(
name|DateFieldMapper
operator|.
name|CONTENT_TYPE
argument_list|,
operator|new
name|DateFieldMapper
operator|.
name|TypeParser
argument_list|()
argument_list|)
expr_stmt|;
name|registerMapper
argument_list|(
name|IpFieldMapper
operator|.
name|CONTENT_TYPE
argument_list|,
operator|new
name|IpFieldMapper
operator|.
name|TypeParser
argument_list|()
argument_list|)
expr_stmt|;
name|registerMapper
argument_list|(
name|StringFieldMapper
operator|.
name|CONTENT_TYPE
argument_list|,
operator|new
name|StringFieldMapper
operator|.
name|TypeParser
argument_list|()
argument_list|)
expr_stmt|;
name|registerMapper
argument_list|(
name|TokenCountFieldMapper
operator|.
name|CONTENT_TYPE
argument_list|,
operator|new
name|TokenCountFieldMapper
operator|.
name|TypeParser
argument_list|()
argument_list|)
expr_stmt|;
name|registerMapper
argument_list|(
name|ObjectMapper
operator|.
name|CONTENT_TYPE
argument_list|,
operator|new
name|ObjectMapper
operator|.
name|TypeParser
argument_list|()
argument_list|)
expr_stmt|;
name|registerMapper
argument_list|(
name|ObjectMapper
operator|.
name|NESTED_CONTENT_TYPE
argument_list|,
operator|new
name|ObjectMapper
operator|.
name|TypeParser
argument_list|()
argument_list|)
expr_stmt|;
name|registerMapper
argument_list|(
name|TypeParsers
operator|.
name|MULTI_FIELD_CONTENT_TYPE
argument_list|,
name|TypeParsers
operator|.
name|multiFieldConverterTypeParser
argument_list|)
expr_stmt|;
name|registerMapper
argument_list|(
name|CompletionFieldMapper
operator|.
name|CONTENT_TYPE
argument_list|,
operator|new
name|CompletionFieldMapper
operator|.
name|TypeParser
argument_list|()
argument_list|)
expr_stmt|;
name|registerMapper
argument_list|(
name|GeoPointFieldMapper
operator|.
name|CONTENT_TYPE
argument_list|,
operator|new
name|GeoPointFieldMapper
operator|.
name|TypeParser
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|ShapesAvailability
operator|.
name|JTS_AVAILABLE
condition|)
block|{
name|registerMapper
argument_list|(
name|GeoShapeFieldMapper
operator|.
name|CONTENT_TYPE
argument_list|,
operator|new
name|GeoShapeFieldMapper
operator|.
name|TypeParser
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|registerBuiltInMetadataMappers
specifier|private
name|void
name|registerBuiltInMetadataMappers
parameter_list|()
block|{
comment|// NOTE: the order is important
comment|// UID first so it will be the first stored field to load (so will benefit from "fields: []" early termination
name|registerMetadataMapper
argument_list|(
name|UidFieldMapper
operator|.
name|NAME
argument_list|,
operator|new
name|UidFieldMapper
operator|.
name|TypeParser
argument_list|()
argument_list|)
expr_stmt|;
name|registerMetadataMapper
argument_list|(
name|IdFieldMapper
operator|.
name|NAME
argument_list|,
operator|new
name|IdFieldMapper
operator|.
name|TypeParser
argument_list|()
argument_list|)
expr_stmt|;
name|registerMetadataMapper
argument_list|(
name|RoutingFieldMapper
operator|.
name|NAME
argument_list|,
operator|new
name|RoutingFieldMapper
operator|.
name|TypeParser
argument_list|()
argument_list|)
expr_stmt|;
name|registerMetadataMapper
argument_list|(
name|IndexFieldMapper
operator|.
name|NAME
argument_list|,
operator|new
name|IndexFieldMapper
operator|.
name|TypeParser
argument_list|()
argument_list|)
expr_stmt|;
name|registerMetadataMapper
argument_list|(
name|SourceFieldMapper
operator|.
name|NAME
argument_list|,
operator|new
name|SourceFieldMapper
operator|.
name|TypeParser
argument_list|()
argument_list|)
expr_stmt|;
name|registerMetadataMapper
argument_list|(
name|TypeFieldMapper
operator|.
name|NAME
argument_list|,
operator|new
name|TypeFieldMapper
operator|.
name|TypeParser
argument_list|()
argument_list|)
expr_stmt|;
name|registerMetadataMapper
argument_list|(
name|AllFieldMapper
operator|.
name|NAME
argument_list|,
operator|new
name|AllFieldMapper
operator|.
name|TypeParser
argument_list|()
argument_list|)
expr_stmt|;
name|registerMetadataMapper
argument_list|(
name|TimestampFieldMapper
operator|.
name|NAME
argument_list|,
operator|new
name|TimestampFieldMapper
operator|.
name|TypeParser
argument_list|()
argument_list|)
expr_stmt|;
name|registerMetadataMapper
argument_list|(
name|TTLFieldMapper
operator|.
name|NAME
argument_list|,
operator|new
name|TTLFieldMapper
operator|.
name|TypeParser
argument_list|()
argument_list|)
expr_stmt|;
name|registerMetadataMapper
argument_list|(
name|VersionFieldMapper
operator|.
name|NAME
argument_list|,
operator|new
name|VersionFieldMapper
operator|.
name|TypeParser
argument_list|()
argument_list|)
expr_stmt|;
name|registerMetadataMapper
argument_list|(
name|ParentFieldMapper
operator|.
name|NAME
argument_list|,
operator|new
name|ParentFieldMapper
operator|.
name|TypeParser
argument_list|()
argument_list|)
expr_stmt|;
comment|// _field_names is not registered here, see #getMapperRegistry: we need to register it
comment|// last so that it can see all other mappers, including those coming from plugins
block|}
DECL|method|registerQueryParser
specifier|public
name|void
name|registerQueryParser
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|QueryParser
argument_list|>
name|queryParser
parameter_list|)
block|{
name|queryParsers
operator|.
name|registerExtension
argument_list|(
name|queryParser
argument_list|)
expr_stmt|;
block|}
comment|/**      * Register a mapper for the given type.      */
DECL|method|registerMapper
specifier|public
specifier|synchronized
name|void
name|registerMapper
parameter_list|(
name|String
name|type
parameter_list|,
name|Mapper
operator|.
name|TypeParser
name|parser
parameter_list|)
block|{
if|if
condition|(
name|mapperParsers
operator|.
name|containsKey
argument_list|(
name|type
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"A mapper is already registered for type ["
operator|+
name|type
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|mapperParsers
operator|.
name|put
argument_list|(
name|type
argument_list|,
name|parser
argument_list|)
expr_stmt|;
block|}
comment|/**      * Register a root mapper under the given name.      */
DECL|method|registerMetadataMapper
specifier|public
specifier|synchronized
name|void
name|registerMetadataMapper
parameter_list|(
name|String
name|name
parameter_list|,
name|MetadataFieldMapper
operator|.
name|TypeParser
name|parser
parameter_list|)
block|{
if|if
condition|(
name|metadataMapperParsers
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"A mapper is already registered for metadata mapper ["
operator|+
name|name
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|metadataMapperParsers
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|parser
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|configure
specifier|protected
name|void
name|configure
parameter_list|()
block|{
name|bindQueryParsersExtension
argument_list|()
expr_stmt|;
name|bindMapperExtension
argument_list|()
expr_stmt|;
name|bind
argument_list|(
name|IndicesService
operator|.
name|class
argument_list|)
operator|.
name|asEagerSingleton
argument_list|()
expr_stmt|;
name|bind
argument_list|(
name|RecoverySettings
operator|.
name|class
argument_list|)
operator|.
name|asEagerSingleton
argument_list|()
expr_stmt|;
name|bind
argument_list|(
name|RecoveryTarget
operator|.
name|class
argument_list|)
operator|.
name|asEagerSingleton
argument_list|()
expr_stmt|;
name|bind
argument_list|(
name|RecoverySource
operator|.
name|class
argument_list|)
operator|.
name|asEagerSingleton
argument_list|()
expr_stmt|;
name|bind
argument_list|(
name|IndicesStore
operator|.
name|class
argument_list|)
operator|.
name|asEagerSingleton
argument_list|()
expr_stmt|;
name|bind
argument_list|(
name|IndicesClusterStateService
operator|.
name|class
argument_list|)
operator|.
name|asEagerSingleton
argument_list|()
expr_stmt|;
name|bind
argument_list|(
name|IndexingMemoryController
operator|.
name|class
argument_list|)
operator|.
name|asEagerSingleton
argument_list|()
expr_stmt|;
name|bind
argument_list|(
name|SyncedFlushService
operator|.
name|class
argument_list|)
operator|.
name|asEagerSingleton
argument_list|()
expr_stmt|;
name|bind
argument_list|(
name|IndicesQueryCache
operator|.
name|class
argument_list|)
operator|.
name|asEagerSingleton
argument_list|()
expr_stmt|;
name|bind
argument_list|(
name|IndicesRequestCache
operator|.
name|class
argument_list|)
operator|.
name|asEagerSingleton
argument_list|()
expr_stmt|;
name|bind
argument_list|(
name|IndicesFieldDataCache
operator|.
name|class
argument_list|)
operator|.
name|asEagerSingleton
argument_list|()
expr_stmt|;
name|bind
argument_list|(
name|TransportNodesListShardStoreMetaData
operator|.
name|class
argument_list|)
operator|.
name|asEagerSingleton
argument_list|()
expr_stmt|;
name|bind
argument_list|(
name|IndicesTTLService
operator|.
name|class
argument_list|)
operator|.
name|asEagerSingleton
argument_list|()
expr_stmt|;
name|bind
argument_list|(
name|IndicesWarmer
operator|.
name|class
argument_list|)
operator|.
name|asEagerSingleton
argument_list|()
expr_stmt|;
name|bind
argument_list|(
name|UpdateHelper
operator|.
name|class
argument_list|)
operator|.
name|asEagerSingleton
argument_list|()
expr_stmt|;
name|bind
argument_list|(
name|MetaDataIndexUpgradeService
operator|.
name|class
argument_list|)
operator|.
name|asEagerSingleton
argument_list|()
expr_stmt|;
name|bind
argument_list|(
name|IndicesFieldDataCacheListener
operator|.
name|class
argument_list|)
operator|.
name|asEagerSingleton
argument_list|()
expr_stmt|;
name|bind
argument_list|(
name|TermVectorsService
operator|.
name|class
argument_list|)
operator|.
name|asEagerSingleton
argument_list|()
expr_stmt|;
name|bind
argument_list|(
name|NodeServicesProvider
operator|.
name|class
argument_list|)
operator|.
name|asEagerSingleton
argument_list|()
expr_stmt|;
block|}
comment|// public for testing
DECL|method|getMapperRegistry
specifier|public
specifier|synchronized
name|MapperRegistry
name|getMapperRegistry
parameter_list|()
block|{
comment|// NOTE: we register _field_names here so that it has a chance to see all other
comment|// mappers, including from plugins
if|if
condition|(
name|metadataMapperParsers
operator|.
name|containsKey
argument_list|(
name|FieldNamesFieldMapper
operator|.
name|NAME
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Metadata mapper ["
operator|+
name|FieldNamesFieldMapper
operator|.
name|NAME
operator|+
literal|"] is already registered"
argument_list|)
throw|;
block|}
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|MetadataFieldMapper
operator|.
name|TypeParser
argument_list|>
name|metadataMapperParsers
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|(
name|this
operator|.
name|metadataMapperParsers
argument_list|)
decl_stmt|;
name|metadataMapperParsers
operator|.
name|put
argument_list|(
name|FieldNamesFieldMapper
operator|.
name|NAME
argument_list|,
operator|new
name|FieldNamesFieldMapper
operator|.
name|TypeParser
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|new
name|MapperRegistry
argument_list|(
name|mapperParsers
argument_list|,
name|metadataMapperParsers
argument_list|)
return|;
block|}
DECL|method|bindMapperExtension
specifier|protected
name|void
name|bindMapperExtension
parameter_list|()
block|{
name|bind
argument_list|(
name|MapperRegistry
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|getMapperRegistry
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|bindQueryParsersExtension
specifier|protected
name|void
name|bindQueryParsersExtension
parameter_list|()
block|{
name|queryParsers
operator|.
name|bind
argument_list|(
name|binder
argument_list|()
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|IndicesQueriesRegistry
operator|.
name|class
argument_list|)
operator|.
name|asEagerSingleton
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

