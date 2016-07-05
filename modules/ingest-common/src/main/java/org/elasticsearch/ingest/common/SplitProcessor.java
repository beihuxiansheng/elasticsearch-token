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
name|ArrayList
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
comment|/**  * Processor that splits fields content into different items based on the occurrence of a specified separator.  * New field value will be an array containing all of the different extracted items.  * Throws exception if the field is null or a type other than string.  */
end_comment

begin_class
DECL|class|SplitProcessor
specifier|public
specifier|final
class|class
name|SplitProcessor
extends|extends
name|AbstractProcessor
block|{
DECL|field|TYPE
specifier|public
specifier|static
specifier|final
name|String
name|TYPE
init|=
literal|"split"
decl_stmt|;
DECL|field|field
specifier|private
specifier|final
name|String
name|field
decl_stmt|;
DECL|field|separator
specifier|private
specifier|final
name|String
name|separator
decl_stmt|;
DECL|method|SplitProcessor
name|SplitProcessor
parameter_list|(
name|String
name|tag
parameter_list|,
name|String
name|field
parameter_list|,
name|String
name|separator
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
name|this
operator|.
name|separator
operator|=
name|separator
expr_stmt|;
block|}
DECL|method|getField
name|String
name|getField
parameter_list|()
block|{
return|return
name|field
return|;
block|}
DECL|method|getSeparator
name|String
name|getSeparator
parameter_list|()
block|{
return|return
name|separator
return|;
block|}
annotation|@
name|Override
DECL|method|execute
specifier|public
name|void
name|execute
parameter_list|(
name|IngestDocument
name|document
parameter_list|)
block|{
name|String
name|oldVal
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
name|oldVal
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
literal|"] is null, cannot split."
argument_list|)
throw|;
block|}
name|String
index|[]
name|strings
init|=
name|oldVal
operator|.
name|split
argument_list|(
name|separator
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|splitList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|strings
operator|.
name|length
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|addAll
argument_list|(
name|splitList
argument_list|,
name|strings
argument_list|)
expr_stmt|;
name|document
operator|.
name|setFieldValue
argument_list|(
name|field
argument_list|,
name|splitList
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getType
specifier|public
name|String
name|getType
parameter_list|()
block|{
return|return
name|TYPE
return|;
block|}
DECL|class|Factory
specifier|public
specifier|static
class|class
name|Factory
implements|implements
name|Processor
operator|.
name|Factory
block|{
annotation|@
name|Override
DECL|method|create
specifier|public
name|SplitProcessor
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
name|TYPE
argument_list|,
name|processorTag
argument_list|,
name|config
argument_list|,
literal|"field"
argument_list|)
decl_stmt|;
return|return
operator|new
name|SplitProcessor
argument_list|(
name|processorTag
argument_list|,
name|field
argument_list|,
name|ConfigurationUtils
operator|.
name|readStringProperty
argument_list|(
name|TYPE
argument_list|,
name|processorTag
argument_list|,
name|config
argument_list|,
literal|"separator"
argument_list|)
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

