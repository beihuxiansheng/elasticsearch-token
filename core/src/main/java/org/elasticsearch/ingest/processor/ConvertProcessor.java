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
name|core
operator|.
name|ConfigurationUtils
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|ingest
operator|.
name|core
operator|.
name|ConfigurationUtils
operator|.
name|newConfigurationException
import|;
end_import

begin_comment
comment|/**  * Processor that converts fields content to a different type. Supported types are: integer, float, boolean and string.  * Throws exception if the field is not there or the conversion fails.  */
end_comment

begin_class
DECL|class|ConvertProcessor
specifier|public
specifier|final
class|class
name|ConvertProcessor
extends|extends
name|AbstractProcessor
block|{
DECL|enum|Type
enum|enum
name|Type
block|{
DECL|enum constant|INTEGER
name|INTEGER
block|{
annotation|@
name|Override
specifier|public
name|Object
name|convert
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
try|try
block|{
return|return
name|Integer
operator|.
name|parseInt
argument_list|(
name|value
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"unable to convert ["
operator|+
name|value
operator|+
literal|"] to integer"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|enum constant|FLOAT
block|}
block|,
name|FLOAT
block|{
annotation|@
name|Override
specifier|public
name|Object
name|convert
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
try|try
block|{
return|return
name|Float
operator|.
name|parseFloat
argument_list|(
name|value
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"unable to convert ["
operator|+
name|value
operator|+
literal|"] to float"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|enum constant|BOOLEAN
block|}
block|,
name|BOOLEAN
block|{
annotation|@
name|Override
specifier|public
name|Object
name|convert
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|.
name|toString
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"true"
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|value
operator|.
name|toString
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"false"
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"["
operator|+
name|value
operator|+
literal|"] is not a boolean value, cannot convert to boolean"
argument_list|)
throw|;
block|}
block|}
DECL|enum constant|STRING
block|}
block|,
name|STRING
block|{
annotation|@
name|Override
specifier|public
name|Object
name|convert
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
return|return
name|value
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
block|;
annotation|@
name|Override
DECL|method|toString
specifier|public
specifier|final
name|String
name|toString
parameter_list|()
block|{
return|return
name|name
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
return|;
block|}
DECL|method|convert
specifier|public
specifier|abstract
name|Object
name|convert
parameter_list|(
name|Object
name|value
parameter_list|)
function_decl|;
DECL|method|fromString
specifier|public
specifier|static
name|Type
name|fromString
parameter_list|(
name|String
name|processorTag
parameter_list|,
name|String
name|propertyName
parameter_list|,
name|String
name|type
parameter_list|)
block|{
try|try
block|{
return|return
name|Type
operator|.
name|valueOf
argument_list|(
name|type
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
throw|throw
name|newConfigurationException
argument_list|(
name|TYPE
argument_list|,
name|processorTag
argument_list|,
name|propertyName
argument_list|,
literal|"type ["
operator|+
name|type
operator|+
literal|"] not supported, cannot convert field."
argument_list|)
throw|;
block|}
block|}
block|}
DECL|field|TYPE
specifier|public
specifier|static
specifier|final
name|String
name|TYPE
init|=
literal|"convert"
decl_stmt|;
DECL|field|field
specifier|private
specifier|final
name|String
name|field
decl_stmt|;
DECL|field|convertType
specifier|private
specifier|final
name|Type
name|convertType
decl_stmt|;
DECL|method|ConvertProcessor
name|ConvertProcessor
parameter_list|(
name|String
name|tag
parameter_list|,
name|String
name|field
parameter_list|,
name|Type
name|convertType
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
name|convertType
operator|=
name|convertType
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
DECL|method|getConvertType
name|Type
name|getConvertType
parameter_list|()
block|{
return|return
name|convertType
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
name|Object
name|oldValue
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
name|Object
name|newValue
decl_stmt|;
if|if
condition|(
name|oldValue
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Field ["
operator|+
name|field
operator|+
literal|"] is null, cannot be converted to type ["
operator|+
name|convertType
operator|+
literal|"]"
argument_list|)
throw|;
block|}
if|if
condition|(
name|oldValue
operator|instanceof
name|List
condition|)
block|{
name|List
argument_list|<
name|?
argument_list|>
name|list
init|=
operator|(
name|List
argument_list|<
name|?
argument_list|>
operator|)
name|oldValue
decl_stmt|;
name|List
argument_list|<
name|Object
argument_list|>
name|newList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Object
name|value
range|:
name|list
control|)
block|{
name|newList
operator|.
name|add
argument_list|(
name|convertType
operator|.
name|convert
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|newValue
operator|=
name|newList
expr_stmt|;
block|}
else|else
block|{
name|newValue
operator|=
name|convertType
operator|.
name|convert
argument_list|(
name|oldValue
argument_list|)
expr_stmt|;
block|}
name|document
operator|.
name|setFieldValue
argument_list|(
name|field
argument_list|,
name|newValue
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
specifier|final
class|class
name|Factory
extends|extends
name|AbstractProcessorFactory
argument_list|<
name|ConvertProcessor
argument_list|>
block|{
annotation|@
name|Override
DECL|method|doCreate
specifier|public
name|ConvertProcessor
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
name|typeProperty
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
literal|"type"
argument_list|)
decl_stmt|;
name|Type
name|convertType
init|=
name|Type
operator|.
name|fromString
argument_list|(
name|processorTag
argument_list|,
literal|"type"
argument_list|,
name|typeProperty
argument_list|)
decl_stmt|;
return|return
operator|new
name|ConvertProcessor
argument_list|(
name|processorTag
argument_list|,
name|field
argument_list|,
name|convertType
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit
