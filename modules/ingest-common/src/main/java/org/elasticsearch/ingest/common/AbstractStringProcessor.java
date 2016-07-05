begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.ingest.common
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|ingest
operator|.
name|common
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
name|AbstractProcessor
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
name|IngestDocument
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
name|Processor
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
comment|/**  * Base class for processors that manipulate strings and require a single "fields" array config value, which  * holds a list of field names in string format.  */
end_comment

begin_class
DECL|class|AbstractStringProcessor
specifier|abstract
class|class
name|AbstractStringProcessor
extends|extends
name|AbstractProcessor
block|{
DECL|field|field
specifier|private
specifier|final
name|String
name|field
decl_stmt|;
DECL|method|AbstractStringProcessor
specifier|protected
name|AbstractStringProcessor
parameter_list|(
name|String
name|tag
parameter_list|,
name|String
name|field
parameter_list|)
block|{
name|super
argument_list|(
name|tag
argument_list|)
expr_stmt|;
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
block|}
DECL|method|getField
specifier|public
name|String
name|getField
parameter_list|()
block|{
return|return
name|field
return|;
block|}
annotation|@
name|Override
DECL|method|execute
specifier|public
specifier|final
name|void
name|execute
parameter_list|(
name|IngestDocument
name|document
parameter_list|)
block|{
name|String
name|val
init|=
name|document
operator|.
name|getFieldValue
argument_list|(
name|field
argument_list|,
name|String
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|val
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"field ["
operator|+
name|field
operator|+
literal|"] is null, cannot process it."
argument_list|)
throw|;
block|}
name|document
operator|.
name|setFieldValue
argument_list|(
name|field
argument_list|,
name|process
argument_list|(
name|val
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|process
specifier|protected
specifier|abstract
name|String
name|process
parameter_list|(
name|String
name|value
parameter_list|)
function_decl|;
DECL|class|Factory
specifier|abstract
specifier|static
class|class
name|Factory
implements|implements
name|Processor
operator|.
name|Factory
block|{
DECL|field|processorType
specifier|protected
specifier|final
name|String
name|processorType
decl_stmt|;
DECL|method|Factory
specifier|protected
name|Factory
parameter_list|(
name|String
name|processorType
parameter_list|)
block|{
name|this
operator|.
name|processorType
operator|=
name|processorType
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|create
specifier|public
name|AbstractStringProcessor
name|create
parameter_list|(
name|String
name|processorTag
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|config
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|field
init|=
name|ConfigurationUtils
operator|.
name|readStringProperty
argument_list|(
name|processorType
argument_list|,
name|processorTag
argument_list|,
name|config
argument_list|,
literal|"field"
argument_list|)
decl_stmt|;
return|return
name|newProcessor
argument_list|(
name|processorTag
argument_list|,
name|field
argument_list|)
return|;
block|}
DECL|method|newProcessor
specifier|protected
specifier|abstract
name|AbstractStringProcessor
name|newProcessor
parameter_list|(
name|String
name|processorTag
parameter_list|,
name|String
name|field
parameter_list|)
function_decl|;
block|}
block|}
end_class

end_unit

