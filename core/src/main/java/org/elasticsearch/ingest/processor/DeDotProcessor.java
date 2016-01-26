begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.ingest.processor
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|ingest
operator|.
name|processor
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
name|core
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
name|core
operator|.
name|AbstractProcessorFactory
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
name|core
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
name|core
operator|.
name|IngestDocument
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
comment|/**  * Processor that replaces dots in document field names with a  * specified separator.  */
end_comment

begin_class
DECL|class|DeDotProcessor
specifier|public
class|class
name|DeDotProcessor
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
literal|"dedot"
decl_stmt|;
DECL|field|DEFAULT_SEPARATOR
specifier|static
specifier|final
name|String
name|DEFAULT_SEPARATOR
init|=
literal|"_"
decl_stmt|;
DECL|field|separator
specifier|private
specifier|final
name|String
name|separator
decl_stmt|;
DECL|method|DeDotProcessor
name|DeDotProcessor
parameter_list|(
name|String
name|tag
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
name|separator
operator|=
name|separator
expr_stmt|;
block|}
DECL|method|getSeparator
specifier|public
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
name|deDot
argument_list|(
name|document
operator|.
name|getSourceAndMetadata
argument_list|()
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
comment|/**      * Recursively iterates through Maps and Lists in search of map entries with      * keys containing dots. The dots in these fields are replaced with {@link #separator}.      *      * @param obj The current object in context to be checked for dots in its fields.      */
DECL|method|deDot
specifier|private
name|void
name|deDot
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|instanceof
name|Map
condition|)
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|doc
init|=
operator|(
name|Map
operator|)
name|obj
decl_stmt|;
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
name|it
init|=
name|doc
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|deDottedFields
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
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
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|deDot
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|fieldName
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
if|if
condition|(
name|fieldName
operator|.
name|contains
argument_list|(
literal|"."
argument_list|)
condition|)
block|{
name|String
name|deDottedFieldName
init|=
name|fieldName
operator|.
name|replaceAll
argument_list|(
literal|"\\."
argument_list|,
name|separator
argument_list|)
decl_stmt|;
name|deDottedFields
operator|.
name|put
argument_list|(
name|deDottedFieldName
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
name|doc
operator|.
name|putAll
argument_list|(
name|deDottedFields
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|obj
operator|instanceof
name|List
condition|)
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|List
argument_list|<
name|Object
argument_list|>
name|list
init|=
operator|(
name|List
operator|)
name|obj
decl_stmt|;
for|for
control|(
name|Object
name|value
range|:
name|list
control|)
block|{
name|deDot
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|Factory
specifier|public
specifier|static
class|class
name|Factory
extends|extends
name|AbstractProcessorFactory
argument_list|<
name|DeDotProcessor
argument_list|>
block|{
annotation|@
name|Override
DECL|method|doCreate
specifier|public
name|DeDotProcessor
name|doCreate
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
name|separator
init|=
name|ConfigurationUtils
operator|.
name|readOptionalStringProperty
argument_list|(
name|config
argument_list|,
literal|"separator"
argument_list|)
decl_stmt|;
if|if
condition|(
name|separator
operator|==
literal|null
condition|)
block|{
name|separator
operator|=
name|DEFAULT_SEPARATOR
expr_stmt|;
block|}
return|return
operator|new
name|DeDotProcessor
argument_list|(
name|processorTag
argument_list|,
name|separator
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

