begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.suggest.completion2x.context
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|suggest
operator|.
name|completion2x
operator|.
name|context
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
name|org
operator|.
name|elasticsearch
operator|.
name|Version
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
name|DocumentMapperParser
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
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|SortedMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import

begin_class
DECL|class|ContextBuilder
specifier|public
specifier|abstract
class|class
name|ContextBuilder
parameter_list|<
name|E
extends|extends
name|ContextMapping
parameter_list|>
block|{
DECL|field|name
specifier|protected
name|String
name|name
decl_stmt|;
DECL|method|ContextBuilder
specifier|public
name|ContextBuilder
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
DECL|method|build
specifier|public
specifier|abstract
name|E
name|build
parameter_list|()
function_decl|;
comment|/**      * Create a new {@link GeolocationContextMapping}      */
DECL|method|location
specifier|public
specifier|static
name|GeolocationContextMapping
operator|.
name|Builder
name|location
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|GeolocationContextMapping
operator|.
name|Builder
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**      * Create a new {@link GeolocationContextMapping} with given precision and      * neighborhood usage      *      * @param precision geohash length      * @param neighbors use neighbor cells      */
DECL|method|location
specifier|public
specifier|static
name|GeolocationContextMapping
operator|.
name|Builder
name|location
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|precision
parameter_list|,
name|boolean
name|neighbors
parameter_list|)
block|{
return|return
operator|new
name|GeolocationContextMapping
operator|.
name|Builder
argument_list|(
name|name
argument_list|,
name|neighbors
argument_list|,
name|precision
argument_list|)
return|;
block|}
comment|/**      * Create a new {@link CategoryContextMapping}      */
DECL|method|category
specifier|public
specifier|static
name|CategoryContextMapping
operator|.
name|Builder
name|category
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|CategoryContextMapping
operator|.
name|Builder
argument_list|(
name|name
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**      * Create a new {@link CategoryContextMapping} with default category      *      * @param defaultCategory category to use, if it is not provided      */
DECL|method|category
specifier|public
specifier|static
name|CategoryContextMapping
operator|.
name|Builder
name|category
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|defaultCategory
parameter_list|)
block|{
return|return
operator|new
name|CategoryContextMapping
operator|.
name|Builder
argument_list|(
name|name
argument_list|,
literal|null
argument_list|)
operator|.
name|addDefaultValue
argument_list|(
name|defaultCategory
argument_list|)
return|;
block|}
comment|/**      * Create a new {@link CategoryContextMapping}      *      * @param fieldname      *            name of the field to use      */
DECL|method|reference
specifier|public
specifier|static
name|CategoryContextMapping
operator|.
name|Builder
name|reference
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|fieldname
parameter_list|)
block|{
return|return
operator|new
name|CategoryContextMapping
operator|.
name|Builder
argument_list|(
name|name
argument_list|,
name|fieldname
argument_list|)
return|;
block|}
comment|/**      * Create a new {@link CategoryContextMapping}      *      * @param fieldname name of the field to use      * @param defaultValues values to use, if the document not provides      *        a field with the given name      */
DECL|method|reference
specifier|public
specifier|static
name|CategoryContextMapping
operator|.
name|Builder
name|reference
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|fieldname
parameter_list|,
name|Iterable
argument_list|<
name|String
argument_list|>
name|defaultValues
parameter_list|)
block|{
return|return
operator|new
name|CategoryContextMapping
operator|.
name|Builder
argument_list|(
name|name
argument_list|,
name|fieldname
argument_list|)
operator|.
name|addDefaultValues
argument_list|(
name|defaultValues
argument_list|)
return|;
block|}
DECL|method|loadMappings
specifier|public
specifier|static
name|SortedMap
argument_list|<
name|String
argument_list|,
name|ContextMapping
argument_list|>
name|loadMappings
parameter_list|(
name|Object
name|configuration
parameter_list|,
name|Version
name|indexVersionCreated
parameter_list|)
throws|throws
name|ElasticsearchParseException
block|{
if|if
condition|(
name|configuration
operator|instanceof
name|Map
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|configurations
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|configuration
decl_stmt|;
name|SortedMap
argument_list|<
name|String
argument_list|,
name|ContextMapping
argument_list|>
name|mappings
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|config
range|:
name|configurations
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|name
init|=
name|config
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|mappings
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|loadMapping
argument_list|(
name|name
argument_list|,
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|config
operator|.
name|getValue
argument_list|()
argument_list|,
name|indexVersionCreated
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|mappings
return|;
block|}
elseif|else
if|if
condition|(
name|configuration
operator|==
literal|null
condition|)
block|{
return|return
name|ContextMapping
operator|.
name|EMPTY_MAPPING
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"no valid context configuration"
argument_list|)
throw|;
block|}
block|}
DECL|method|loadMapping
specifier|protected
specifier|static
name|ContextMapping
name|loadMapping
parameter_list|(
name|String
name|name
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|config
parameter_list|,
name|Version
name|indexVersionCreated
parameter_list|)
throws|throws
name|ElasticsearchParseException
block|{
specifier|final
name|Object
name|argType
init|=
name|config
operator|.
name|get
argument_list|(
name|ContextMapping
operator|.
name|FIELD_TYPE
argument_list|)
decl_stmt|;
if|if
condition|(
name|argType
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"missing [{}] in context mapping"
argument_list|,
name|ContextMapping
operator|.
name|FIELD_TYPE
argument_list|)
throw|;
block|}
specifier|final
name|String
name|type
init|=
name|argType
operator|.
name|toString
argument_list|()
decl_stmt|;
name|ContextMapping
name|contextMapping
decl_stmt|;
if|if
condition|(
name|GeolocationContextMapping
operator|.
name|TYPE
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
name|contextMapping
operator|=
name|GeolocationContextMapping
operator|.
name|load
argument_list|(
name|name
argument_list|,
name|config
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|CategoryContextMapping
operator|.
name|TYPE
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
name|contextMapping
operator|=
name|CategoryContextMapping
operator|.
name|load
argument_list|(
name|name
argument_list|,
name|config
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"unknown context type [{}]"
argument_list|,
name|type
argument_list|)
throw|;
block|}
name|config
operator|.
name|remove
argument_list|(
name|ContextMapping
operator|.
name|FIELD_TYPE
argument_list|)
expr_stmt|;
name|DocumentMapperParser
operator|.
name|checkNoRemainingFields
argument_list|(
name|name
argument_list|,
name|config
argument_list|,
name|indexVersionCreated
argument_list|)
expr_stmt|;
return|return
name|contextMapping
return|;
block|}
block|}
end_class

end_unit

