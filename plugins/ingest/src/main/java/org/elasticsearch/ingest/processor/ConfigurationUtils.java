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

begin_class
DECL|class|ConfigurationUtils
specifier|public
specifier|final
class|class
name|ConfigurationUtils
block|{
DECL|method|ConfigurationUtils
specifier|private
name|ConfigurationUtils
parameter_list|()
block|{     }
comment|/**      * Returns and removes the specified optional property from the specified configuration map.      *      * If the property value isn't of type string a {@link IllegalArgumentException} is thrown.      */
DECL|method|readOptionalStringProperty
specifier|public
specifier|static
name|String
name|readOptionalStringProperty
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|configuration
parameter_list|,
name|String
name|propertyName
parameter_list|)
block|{
name|Object
name|value
init|=
name|configuration
operator|.
name|remove
argument_list|(
name|propertyName
argument_list|)
decl_stmt|;
return|return
name|readString
argument_list|(
name|propertyName
argument_list|,
name|value
argument_list|)
return|;
block|}
comment|/**      * Returns and removes the specified property from the specified configuration map.      *      * If the property value isn't of type string an {@link IllegalArgumentException} is thrown.      * If the property is missing an {@link IllegalArgumentException} is thrown      */
DECL|method|readStringProperty
specifier|public
specifier|static
name|String
name|readStringProperty
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|configuration
parameter_list|,
name|String
name|propertyName
parameter_list|)
block|{
return|return
name|readStringProperty
argument_list|(
name|configuration
argument_list|,
name|propertyName
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**      * Returns and removes the specified property from the specified configuration map.      *      * If the property value isn't of type string a {@link IllegalArgumentException} is thrown.      * If the property is missing and no default value has been specified a {@link IllegalArgumentException} is thrown      */
DECL|method|readStringProperty
specifier|public
specifier|static
name|String
name|readStringProperty
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|configuration
parameter_list|,
name|String
name|propertyName
parameter_list|,
name|String
name|defaultValue
parameter_list|)
block|{
name|Object
name|value
init|=
name|configuration
operator|.
name|remove
argument_list|(
name|propertyName
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
operator|&&
name|defaultValue
operator|!=
literal|null
condition|)
block|{
return|return
name|defaultValue
return|;
block|}
elseif|else
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"required property ["
operator|+
name|propertyName
operator|+
literal|"] is missing"
argument_list|)
throw|;
block|}
return|return
name|readString
argument_list|(
name|propertyName
argument_list|,
name|value
argument_list|)
return|;
block|}
DECL|method|readString
specifier|private
specifier|static
name|String
name|readString
parameter_list|(
name|String
name|propertyName
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|value
operator|instanceof
name|String
condition|)
block|{
return|return
operator|(
name|String
operator|)
name|value
return|;
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"property ["
operator|+
name|propertyName
operator|+
literal|"] isn't a string, but of type ["
operator|+
name|value
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
comment|/**      * Returns and removes the specified property of type list from the specified configuration map.      *      * If the property value isn't of type list an {@link IllegalArgumentException} is thrown.      */
DECL|method|readOptionalList
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|List
argument_list|<
name|T
argument_list|>
name|readOptionalList
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|configuration
parameter_list|,
name|String
name|propertyName
parameter_list|)
block|{
name|Object
name|value
init|=
name|configuration
operator|.
name|remove
argument_list|(
name|propertyName
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|readList
argument_list|(
name|propertyName
argument_list|,
name|value
argument_list|)
return|;
block|}
comment|/**      * Returns and removes the specified property of type list from the specified configuration map.      *      * If the property value isn't of type list an {@link IllegalArgumentException} is thrown.      * If the property is missing an {@link IllegalArgumentException} is thrown      */
DECL|method|readList
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|List
argument_list|<
name|T
argument_list|>
name|readList
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|configuration
parameter_list|,
name|String
name|propertyName
parameter_list|)
block|{
name|Object
name|value
init|=
name|configuration
operator|.
name|remove
argument_list|(
name|propertyName
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"required property ["
operator|+
name|propertyName
operator|+
literal|"] is missing"
argument_list|)
throw|;
block|}
return|return
name|readList
argument_list|(
name|propertyName
argument_list|,
name|value
argument_list|)
return|;
block|}
DECL|method|readList
specifier|private
specifier|static
parameter_list|<
name|T
parameter_list|>
name|List
argument_list|<
name|T
argument_list|>
name|readList
parameter_list|(
name|String
name|propertyName
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
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
name|T
argument_list|>
name|stringList
init|=
operator|(
name|List
argument_list|<
name|T
argument_list|>
operator|)
name|value
decl_stmt|;
return|return
name|stringList
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"property ["
operator|+
name|propertyName
operator|+
literal|"] isn't a list, but of type ["
operator|+
name|value
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
comment|/**      * Returns and removes the specified property of type map from the specified configuration map.      *      * If the property value isn't of type map an {@link IllegalArgumentException} is thrown.      * If the property is missing an {@link IllegalArgumentException} is thrown      */
DECL|method|readMap
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Map
argument_list|<
name|String
argument_list|,
name|T
argument_list|>
name|readMap
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|configuration
parameter_list|,
name|String
name|propertyName
parameter_list|)
block|{
name|Object
name|value
init|=
name|configuration
operator|.
name|remove
argument_list|(
name|propertyName
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"required property ["
operator|+
name|propertyName
operator|+
literal|"] is missing"
argument_list|)
throw|;
block|}
return|return
name|readMap
argument_list|(
name|propertyName
argument_list|,
name|value
argument_list|)
return|;
block|}
comment|/**      * Returns and removes the specified property of type map from the specified configuration map.      *      * If the property value isn't of type map an {@link IllegalArgumentException} is thrown.      */
DECL|method|readOptionalMap
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Map
argument_list|<
name|String
argument_list|,
name|T
argument_list|>
name|readOptionalMap
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|configuration
parameter_list|,
name|String
name|propertyName
parameter_list|)
block|{
name|Object
name|value
init|=
name|configuration
operator|.
name|remove
argument_list|(
name|propertyName
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|readMap
argument_list|(
name|propertyName
argument_list|,
name|value
argument_list|)
return|;
block|}
DECL|method|readMap
specifier|private
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Map
argument_list|<
name|String
argument_list|,
name|T
argument_list|>
name|readMap
parameter_list|(
name|String
name|propertyName
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
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
name|T
argument_list|>
name|map
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|T
argument_list|>
operator|)
name|value
decl_stmt|;
return|return
name|map
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"property ["
operator|+
name|propertyName
operator|+
literal|"] isn't a map, but of type ["
operator|+
name|value
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
comment|/**      * Returns and removes the specified property as an {@link Object} from the specified configuration map.      */
DECL|method|readObject
specifier|public
specifier|static
name|Object
name|readObject
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|configuration
parameter_list|,
name|String
name|propertyName
parameter_list|)
block|{
name|Object
name|value
init|=
name|configuration
operator|.
name|remove
argument_list|(
name|propertyName
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"required property ["
operator|+
name|propertyName
operator|+
literal|"] is missing"
argument_list|)
throw|;
block|}
return|return
name|value
return|;
block|}
block|}
end_class

end_unit

