begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.mapper
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|collect
operator|.
name|CopyOnWriteHashMap
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
name|regex
operator|.
name|Regex
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
name|Collection
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
name|HashSet
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
name|Objects
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * An immutable container for looking up {@link MappedFieldType}s by their name.  */
end_comment

begin_class
DECL|class|FieldTypeLookup
class|class
name|FieldTypeLookup
implements|implements
name|Iterable
argument_list|<
name|MappedFieldType
argument_list|>
block|{
comment|/** Full field name to field type */
DECL|field|fullNameToFieldType
specifier|final
name|CopyOnWriteHashMap
argument_list|<
name|String
argument_list|,
name|MappedFieldType
argument_list|>
name|fullNameToFieldType
decl_stmt|;
comment|/** Full field name to types containing a mapping for this full name. */
DECL|field|fullNameToTypes
specifier|final
name|CopyOnWriteHashMap
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|fullNameToTypes
decl_stmt|;
comment|/** Create a new empty instance. */
DECL|method|FieldTypeLookup
specifier|public
name|FieldTypeLookup
parameter_list|()
block|{
name|fullNameToFieldType
operator|=
operator|new
name|CopyOnWriteHashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|fullNameToTypes
operator|=
operator|new
name|CopyOnWriteHashMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
DECL|method|FieldTypeLookup
specifier|private
name|FieldTypeLookup
parameter_list|(
name|CopyOnWriteHashMap
argument_list|<
name|String
argument_list|,
name|MappedFieldType
argument_list|>
name|fullName
parameter_list|,
name|CopyOnWriteHashMap
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|fullNameToTypes
parameter_list|)
block|{
name|this
operator|.
name|fullNameToFieldType
operator|=
name|fullName
expr_stmt|;
name|this
operator|.
name|fullNameToTypes
operator|=
name|fullNameToTypes
expr_stmt|;
block|}
DECL|method|addType
specifier|private
specifier|static
name|CopyOnWriteHashMap
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|addType
parameter_list|(
name|CopyOnWriteHashMap
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|map
parameter_list|,
name|String
name|key
parameter_list|,
name|String
name|type
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|types
init|=
name|map
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|types
operator|==
literal|null
condition|)
block|{
return|return
name|map
operator|.
name|copyAndPut
argument_list|(
name|key
argument_list|,
name|Collections
operator|.
name|singleton
argument_list|(
name|type
argument_list|)
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|types
operator|.
name|contains
argument_list|(
name|type
argument_list|)
condition|)
block|{
comment|// noting to do
return|return
name|map
return|;
block|}
else|else
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|newTypes
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|types
operator|.
name|size
argument_list|()
operator|+
literal|1
argument_list|)
decl_stmt|;
name|newTypes
operator|.
name|addAll
argument_list|(
name|types
argument_list|)
expr_stmt|;
name|newTypes
operator|.
name|add
argument_list|(
name|type
argument_list|)
expr_stmt|;
assert|assert
name|newTypes
operator|.
name|size
argument_list|()
operator|==
name|types
operator|.
name|size
argument_list|()
operator|+
literal|1
assert|;
name|newTypes
operator|=
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
name|newTypes
argument_list|)
expr_stmt|;
return|return
name|map
operator|.
name|copyAndPut
argument_list|(
name|key
argument_list|,
name|newTypes
argument_list|)
return|;
block|}
block|}
comment|/**      * Return a new instance that contains the union of this instance and the field types      * from the provided fields. If a field already exists, the field type will be updated      * to use the new mappers field type.      */
DECL|method|copyAndAddAll
specifier|public
name|FieldTypeLookup
name|copyAndAddAll
parameter_list|(
name|String
name|type
parameter_list|,
name|Collection
argument_list|<
name|FieldMapper
argument_list|>
name|fieldMappers
parameter_list|,
name|boolean
name|updateAllTypes
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|type
argument_list|,
literal|"type must not be null"
argument_list|)
expr_stmt|;
if|if
condition|(
name|MapperService
operator|.
name|DEFAULT_MAPPING
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Default mappings should not be added to the lookup"
argument_list|)
throw|;
block|}
name|CopyOnWriteHashMap
argument_list|<
name|String
argument_list|,
name|MappedFieldType
argument_list|>
name|fullName
init|=
name|this
operator|.
name|fullNameToFieldType
decl_stmt|;
name|CopyOnWriteHashMap
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|fullNameToTypes
init|=
name|this
operator|.
name|fullNameToTypes
decl_stmt|;
for|for
control|(
name|FieldMapper
name|fieldMapper
range|:
name|fieldMappers
control|)
block|{
name|MappedFieldType
name|fieldType
init|=
name|fieldMapper
operator|.
name|fieldType
argument_list|()
decl_stmt|;
name|MappedFieldType
name|fullNameFieldType
init|=
name|fullName
operator|.
name|get
argument_list|(
name|fieldType
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
comment|// is the update even legal?
name|checkCompatibility
argument_list|(
name|type
argument_list|,
name|fieldMapper
argument_list|,
name|updateAllTypes
argument_list|)
expr_stmt|;
if|if
condition|(
name|fieldType
operator|!=
name|fullNameFieldType
condition|)
block|{
name|fullName
operator|=
name|fullName
operator|.
name|copyAndPut
argument_list|(
name|fieldType
operator|.
name|name
argument_list|()
argument_list|,
name|fieldMapper
operator|.
name|fieldType
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|fullNameToTypes
operator|=
name|addType
argument_list|(
name|fullNameToTypes
argument_list|,
name|fieldType
operator|.
name|name
argument_list|()
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|FieldTypeLookup
argument_list|(
name|fullName
argument_list|,
name|fullNameToTypes
argument_list|)
return|;
block|}
DECL|method|beStrict
specifier|private
specifier|static
name|boolean
name|beStrict
parameter_list|(
name|String
name|type
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|types
parameter_list|,
name|boolean
name|updateAllTypes
parameter_list|)
block|{
assert|assert
name|types
operator|.
name|size
argument_list|()
operator|>=
literal|1
assert|;
if|if
condition|(
name|updateAllTypes
condition|)
block|{
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
name|types
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|&&
name|types
operator|.
name|contains
argument_list|(
name|type
argument_list|)
condition|)
block|{
comment|// we are implicitly updating all types
return|return
literal|false
return|;
block|}
else|else
block|{
return|return
literal|true
return|;
block|}
block|}
comment|/**      * Checks if the given field type is compatible with an existing field type.      * An IllegalArgumentException is thrown in case of incompatibility.      * If updateAllTypes is true, only basic compatibility is checked.      */
DECL|method|checkCompatibility
specifier|private
name|void
name|checkCompatibility
parameter_list|(
name|String
name|type
parameter_list|,
name|FieldMapper
name|fieldMapper
parameter_list|,
name|boolean
name|updateAllTypes
parameter_list|)
block|{
name|MappedFieldType
name|fieldType
init|=
name|fullNameToFieldType
operator|.
name|get
argument_list|(
name|fieldMapper
operator|.
name|fieldType
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldType
operator|!=
literal|null
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|conflicts
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|types
init|=
name|fullNameToTypes
operator|.
name|get
argument_list|(
name|fieldMapper
operator|.
name|fieldType
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
name|boolean
name|strict
init|=
name|beStrict
argument_list|(
name|type
argument_list|,
name|types
argument_list|,
name|updateAllTypes
argument_list|)
decl_stmt|;
name|fieldType
operator|.
name|checkCompatibility
argument_list|(
name|fieldMapper
operator|.
name|fieldType
argument_list|()
argument_list|,
name|conflicts
argument_list|,
name|strict
argument_list|)
expr_stmt|;
if|if
condition|(
name|conflicts
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
literal|"Mapper for ["
operator|+
name|fieldMapper
operator|.
name|fieldType
argument_list|()
operator|.
name|name
argument_list|()
operator|+
literal|"] conflicts with existing mapping in other types:\n"
operator|+
name|conflicts
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
comment|/** Returns the field for the given field */
DECL|method|get
specifier|public
name|MappedFieldType
name|get
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
name|fullNameToFieldType
operator|.
name|get
argument_list|(
name|field
argument_list|)
return|;
block|}
comment|/** Get the set of types that have a mapping for the given field. */
DECL|method|getTypes
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getTypes
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|types
init|=
name|fullNameToTypes
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|types
operator|==
literal|null
condition|)
block|{
name|types
operator|=
name|Collections
operator|.
name|emptySet
argument_list|()
expr_stmt|;
block|}
return|return
name|types
return|;
block|}
comment|/**      * Returns a list of the full names of a simple match regex like pattern against full name and index name.      */
DECL|method|simpleMatchToFullName
specifier|public
name|Collection
argument_list|<
name|String
argument_list|>
name|simpleMatchToFullName
parameter_list|(
name|String
name|pattern
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|fields
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|MappedFieldType
name|fieldType
range|:
name|this
control|)
block|{
if|if
condition|(
name|Regex
operator|.
name|simpleMatch
argument_list|(
name|pattern
argument_list|,
name|fieldType
operator|.
name|name
argument_list|()
argument_list|)
condition|)
block|{
name|fields
operator|.
name|add
argument_list|(
name|fieldType
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|Regex
operator|.
name|simpleMatch
argument_list|(
name|pattern
argument_list|,
name|fieldType
operator|.
name|name
argument_list|()
argument_list|)
condition|)
block|{
name|fields
operator|.
name|add
argument_list|(
name|fieldType
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|fields
return|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|Iterator
argument_list|<
name|MappedFieldType
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|fullNameToFieldType
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
return|;
block|}
block|}
end_class

end_unit

