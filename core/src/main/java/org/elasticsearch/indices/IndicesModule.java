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
name|admin
operator|.
name|indices
operator|.
name|rollover
operator|.
name|Condition
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
name|admin
operator|.
name|indices
operator|.
name|rollover
operator|.
name|MaxAgeCondition
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
name|admin
operator|.
name|indices
operator|.
name|rollover
operator|.
name|MaxDocsCondition
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
name|io
operator|.
name|stream
operator|.
name|NamedWriteableRegistry
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
name|BinaryFieldMapper
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
name|BooleanFieldMapper
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
name|CompletionFieldMapper
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
name|DateFieldMapper
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
name|KeywordFieldMapper
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
name|StringFieldMapper
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
name|TextFieldMapper
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
name|TokenCountFieldMapper
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
name|NumberFieldMapper
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
name|AllFieldMapper
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
name|FieldNamesFieldMapper
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
name|IdFieldMapper
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
name|IndexFieldMapper
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
name|ParentFieldMapper
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
name|RoutingFieldMapper
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
name|SourceFieldMapper
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
name|TTLFieldMapper
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
name|TimestampFieldMapper
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
name|TypeFieldMapper
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
name|UidFieldMapper
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
name|VersionFieldMapper
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
name|RecoveryTargetService
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
DECL|field|namedWritableRegistry
specifier|private
specifier|final
name|NamedWriteableRegistry
name|namedWritableRegistry
decl_stmt|;
DECL|method|IndicesModule
specifier|public
name|IndicesModule
parameter_list|(
name|NamedWriteableRegistry
name|namedWriteableRegistry
parameter_list|)
block|{
name|this
operator|.
name|namedWritableRegistry
operator|=
name|namedWriteableRegistry
expr_stmt|;
name|registerBuiltInMappers
argument_list|()
expr_stmt|;
name|registerBuiltInMetadataMappers
argument_list|()
expr_stmt|;
name|registerBuildInWritables
argument_list|()
expr_stmt|;
block|}
DECL|method|registerBuildInWritables
specifier|private
name|void
name|registerBuildInWritables
parameter_list|()
block|{
name|namedWritableRegistry
operator|.
name|register
argument_list|(
name|Condition
operator|.
name|class
argument_list|,
name|MaxAgeCondition
operator|.
name|NAME
argument_list|,
name|MaxAgeCondition
operator|::
operator|new
argument_list|)
expr_stmt|;
name|namedWritableRegistry
operator|.
name|register
argument_list|(
name|Condition
operator|.
name|class
argument_list|,
name|MaxDocsCondition
operator|.
name|NAME
argument_list|,
name|MaxDocsCondition
operator|::
operator|new
argument_list|)
expr_stmt|;
block|}
DECL|method|registerBuiltInMappers
specifier|private
name|void
name|registerBuiltInMappers
parameter_list|()
block|{
for|for
control|(
name|NumberFieldMapper
operator|.
name|NumberType
name|type
range|:
name|NumberFieldMapper
operator|.
name|NumberType
operator|.
name|values
argument_list|()
control|)
block|{
name|registerMapper
argument_list|(
name|type
operator|.
name|typeName
argument_list|()
argument_list|,
operator|new
name|NumberFieldMapper
operator|.
name|TypeParser
argument_list|(
name|type
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|TextFieldMapper
operator|.
name|CONTENT_TYPE
argument_list|,
operator|new
name|TextFieldMapper
operator|.
name|TypeParser
argument_list|()
argument_list|)
expr_stmt|;
name|registerMapper
argument_list|(
name|KeywordFieldMapper
operator|.
name|CONTENT_TYPE
argument_list|,
operator|new
name|KeywordFieldMapper
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
operator|&&
name|ShapesAvailability
operator|.
name|SPATIAL4J_AVAILABLE
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
name|RecoveryTargetService
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
block|}
end_class

end_unit

