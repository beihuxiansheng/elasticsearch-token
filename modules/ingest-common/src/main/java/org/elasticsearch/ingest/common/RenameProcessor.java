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
comment|/**  * Processor that allows to rename existing fields. Will throw exception if the field is not present.  */
end_comment

begin_class
DECL|class|RenameProcessor
specifier|public
specifier|final
class|class
name|RenameProcessor
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
literal|"rename"
decl_stmt|;
DECL|field|field
specifier|private
specifier|final
name|String
name|field
decl_stmt|;
DECL|field|targetField
specifier|private
specifier|final
name|String
name|targetField
decl_stmt|;
DECL|field|ignoreMissing
specifier|private
specifier|final
name|boolean
name|ignoreMissing
decl_stmt|;
DECL|method|RenameProcessor
name|RenameProcessor
parameter_list|(
name|String
name|tag
parameter_list|,
name|String
name|field
parameter_list|,
name|String
name|targetField
parameter_list|,
name|boolean
name|ignoreMissing
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
name|targetField
operator|=
name|targetField
expr_stmt|;
name|this
operator|.
name|ignoreMissing
operator|=
name|ignoreMissing
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
DECL|method|getTargetField
name|String
name|getTargetField
parameter_list|()
block|{
return|return
name|targetField
return|;
block|}
DECL|method|isIgnoreMissing
name|boolean
name|isIgnoreMissing
parameter_list|()
block|{
return|return
name|ignoreMissing
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
if|if
condition|(
name|document
operator|.
name|hasField
argument_list|(
name|field
argument_list|,
literal|true
argument_list|)
operator|==
literal|false
condition|)
block|{
if|if
condition|(
name|ignoreMissing
condition|)
block|{
return|return;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"field ["
operator|+
name|field
operator|+
literal|"] doesn't exist"
argument_list|)
throw|;
block|}
block|}
comment|// We fail here if the target field point to an array slot that is out of range.
comment|// If we didn't do this then we would fail if we set the value in the target_field
comment|// and then on failure processors would not see that value we tried to rename as we already
comment|// removed it.
if|if
condition|(
name|document
operator|.
name|hasField
argument_list|(
name|targetField
argument_list|,
literal|true
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"field ["
operator|+
name|targetField
operator|+
literal|"] already exists"
argument_list|)
throw|;
block|}
name|Object
name|value
init|=
name|document
operator|.
name|getFieldValue
argument_list|(
name|field
argument_list|,
name|Object
operator|.
name|class
argument_list|)
decl_stmt|;
name|document
operator|.
name|removeField
argument_list|(
name|field
argument_list|)
expr_stmt|;
try|try
block|{
name|document
operator|.
name|setFieldValue
argument_list|(
name|targetField
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// setting the value back to the original field shouldn't as we just fetched it from that field:
name|document
operator|.
name|setFieldValue
argument_list|(
name|field
argument_list|,
name|value
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
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
specifier|final
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
name|RenameProcessor
name|create
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Processor
operator|.
name|Factory
argument_list|>
name|registry
parameter_list|,
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
name|String
name|targetField
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
literal|"target_field"
argument_list|)
decl_stmt|;
name|boolean
name|ignoreMissing
init|=
name|ConfigurationUtils
operator|.
name|readBooleanProperty
argument_list|(
name|TYPE
argument_list|,
name|processorTag
argument_list|,
name|config
argument_list|,
literal|"ignore_missing"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
return|return
operator|new
name|RenameProcessor
argument_list|(
name|processorTag
argument_list|,
name|field
argument_list|,
name|targetField
argument_list|,
name|ignoreMissing
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit
