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
name|ingest
operator|.
name|processor
operator|.
name|ConfigurationUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ingest
operator|.
name|processor
operator|.
name|Processor
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
name|*
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
DECL|field|processors
specifier|private
specifier|final
name|List
argument_list|<
name|Processor
argument_list|>
name|processors
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
name|List
argument_list|<
name|Processor
argument_list|>
name|processors
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
name|processors
operator|=
name|processors
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
for|for
control|(
name|Processor
name|processor
range|:
name|processors
control|)
block|{
name|processor
operator|.
name|execute
argument_list|(
name|ingestDocument
argument_list|)
expr_stmt|;
block|}
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
name|processors
return|;
block|}
DECL|class|Factory
specifier|public
specifier|final
specifier|static
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
name|Map
argument_list|<
name|String
argument_list|,
name|Processor
operator|.
name|Factory
argument_list|>
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
name|config
argument_list|,
literal|"description"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Processor
argument_list|>
name|processors
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
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
operator|(
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
operator|)
name|config
operator|.
name|get
argument_list|(
literal|"processors"
argument_list|)
decl_stmt|;
if|if
condition|(
name|processorConfigs
operator|!=
literal|null
condition|)
block|{
for|for
control|(
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
name|processor
range|:
name|processorConfigs
control|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
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
name|entry
range|:
name|processor
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Processor
operator|.
name|Factory
name|factory
init|=
name|processorRegistry
operator|.
name|get
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|factory
operator|!=
literal|null
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|processorConfig
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|processors
operator|.
name|add
argument_list|(
name|factory
operator|.
name|create
argument_list|(
name|processorConfig
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|processorConfig
operator|.
name|isEmpty
argument_list|()
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"processor ["
operator|+
name|entry
operator|.
name|getKey
argument_list|()
operator|+
literal|"] doesn't support one or more provided configuration parameters "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|processorConfig
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
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"No processor type exist with name ["
operator|+
name|entry
operator|.
name|getKey
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
block|}
block|}
return|return
operator|new
name|Pipeline
argument_list|(
name|id
argument_list|,
name|description
argument_list|,
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|processors
argument_list|)
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

