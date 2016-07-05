begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.ingest
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|ingest
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
name|java
operator|.
name|util
operator|.
name|Arrays
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
comment|/**  * A pipeline is a list of {@link Processor} instances grouped under a unique id.  */
end_comment

begin_class
DECL|class|Pipeline
specifier|public
specifier|final
class|class
name|Pipeline
block|{
DECL|field|DESCRIPTION_KEY
specifier|static
specifier|final
name|String
name|DESCRIPTION_KEY
init|=
literal|"description"
decl_stmt|;
DECL|field|PROCESSORS_KEY
specifier|static
specifier|final
name|String
name|PROCESSORS_KEY
init|=
literal|"processors"
decl_stmt|;
DECL|field|ON_FAILURE_KEY
specifier|static
specifier|final
name|String
name|ON_FAILURE_KEY
init|=
literal|"on_failure"
decl_stmt|;
DECL|field|id
specifier|private
specifier|final
name|String
name|id
decl_stmt|;
DECL|field|description
specifier|private
specifier|final
name|String
name|description
decl_stmt|;
DECL|field|compoundProcessor
specifier|private
specifier|final
name|CompoundProcessor
name|compoundProcessor
decl_stmt|;
DECL|method|Pipeline
specifier|public
name|Pipeline
parameter_list|(
name|String
name|id
parameter_list|,
name|String
name|description
parameter_list|,
name|CompoundProcessor
name|compoundProcessor
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|description
operator|=
name|description
expr_stmt|;
name|this
operator|.
name|compoundProcessor
operator|=
name|compoundProcessor
expr_stmt|;
block|}
comment|/**      * Modifies the data of a document to be indexed based on the processor this pipeline holds      */
DECL|method|execute
specifier|public
name|void
name|execute
parameter_list|(
name|IngestDocument
name|ingestDocument
parameter_list|)
throws|throws
name|Exception
block|{
name|compoundProcessor
operator|.
name|execute
argument_list|(
name|ingestDocument
argument_list|)
expr_stmt|;
block|}
comment|/**      * The unique id of this pipeline      */
DECL|method|getId
specifier|public
name|String
name|getId
parameter_list|()
block|{
return|return
name|id
return|;
block|}
comment|/**      * An optional description of what this pipeline is doing to the data gets processed by this pipeline.      */
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
name|description
return|;
block|}
comment|/**      * Get the underlying {@link CompoundProcessor} containing the Pipeline's processors      */
DECL|method|getCompoundProcessor
specifier|public
name|CompoundProcessor
name|getCompoundProcessor
parameter_list|()
block|{
return|return
name|compoundProcessor
return|;
block|}
comment|/**      * Unmodifiable list containing each processor that operates on the data.      */
DECL|method|getProcessors
specifier|public
name|List
argument_list|<
name|Processor
argument_list|>
name|getProcessors
parameter_list|()
block|{
return|return
name|compoundProcessor
operator|.
name|getProcessors
argument_list|()
return|;
block|}
comment|/**      * Unmodifiable list containing each on_failure processor that operates on the data in case of      * exception thrown in pipeline processors      */
DECL|method|getOnFailureProcessors
specifier|public
name|List
argument_list|<
name|Processor
argument_list|>
name|getOnFailureProcessors
parameter_list|()
block|{
return|return
name|compoundProcessor
operator|.
name|getOnFailureProcessors
argument_list|()
return|;
block|}
comment|/**      * Flattens the normal and on failure processors into a single list. The original order is lost.      * This can be useful for pipeline validation purposes.      */
DECL|method|flattenAllProcessors
specifier|public
name|List
argument_list|<
name|Processor
argument_list|>
name|flattenAllProcessors
parameter_list|()
block|{
return|return
name|compoundProcessor
operator|.
name|flattenProcessors
argument_list|()
return|;
block|}
DECL|class|Factory
specifier|public
specifier|static
specifier|final
class|class
name|Factory
block|{
DECL|method|create
specifier|public
name|Pipeline
name|create
parameter_list|(
name|String
name|id
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|config
parameter_list|,
name|ProcessorsRegistry
name|processorRegistry
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|description
init|=
name|ConfigurationUtils
operator|.
name|readOptionalStringProperty
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
name|config
argument_list|,
name|DESCRIPTION_KEY
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|>
name|processorConfigs
init|=
name|ConfigurationUtils
operator|.
name|readList
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
name|config
argument_list|,
name|PROCESSORS_KEY
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Processor
argument_list|>
name|processors
init|=
name|ConfigurationUtils
operator|.
name|readProcessorConfigs
argument_list|(
name|processorConfigs
argument_list|,
name|processorRegistry
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|>
name|onFailureProcessorConfigs
init|=
name|ConfigurationUtils
operator|.
name|readOptionalList
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
name|config
argument_list|,
name|ON_FAILURE_KEY
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Processor
argument_list|>
name|onFailureProcessors
init|=
name|ConfigurationUtils
operator|.
name|readProcessorConfigs
argument_list|(
name|onFailureProcessorConfigs
argument_list|,
name|processorRegistry
argument_list|)
decl_stmt|;
if|if
condition|(
name|config
operator|.
name|isEmpty
argument_list|()
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"pipeline ["
operator|+
name|id
operator|+
literal|"] doesn't support one or more provided configuration parameters "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|config
operator|.
name|keySet
argument_list|()
operator|.
name|toArray
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
if|if
condition|(
name|onFailureProcessorConfigs
operator|!=
literal|null
operator|&&
name|onFailureProcessors
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"pipeline ["
operator|+
name|id
operator|+
literal|"] cannot have an empty on_failure option defined"
argument_list|)
throw|;
block|}
name|CompoundProcessor
name|compoundProcessor
init|=
operator|new
name|CompoundProcessor
argument_list|(
literal|false
argument_list|,
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|processors
argument_list|)
argument_list|,
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|onFailureProcessors
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|Pipeline
argument_list|(
name|id
argument_list|,
name|description
argument_list|,
name|compoundProcessor
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

