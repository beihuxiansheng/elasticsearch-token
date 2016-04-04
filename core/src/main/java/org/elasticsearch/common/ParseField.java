begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common
package|package
name|org
operator|.
name|elasticsearch
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
name|common
operator|.
name|logging
operator|.
name|DeprecationLogger
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
name|logging
operator|.
name|Loggers
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
name|Set
import|;
end_import

begin_comment
comment|/**  * Holds a field that can be found in a request while parsing and its different variants, which may be deprecated.  */
end_comment

begin_class
DECL|class|ParseField
specifier|public
class|class
name|ParseField
block|{
DECL|field|DEPRECATION_LOGGER
specifier|private
specifier|static
specifier|final
name|DeprecationLogger
name|DEPRECATION_LOGGER
init|=
operator|new
name|DeprecationLogger
argument_list|(
name|Loggers
operator|.
name|getLogger
argument_list|(
name|ParseField
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|camelCaseName
specifier|private
specifier|final
name|String
name|camelCaseName
decl_stmt|;
DECL|field|underscoreName
specifier|private
specifier|final
name|String
name|underscoreName
decl_stmt|;
DECL|field|deprecatedNames
specifier|private
specifier|final
name|String
index|[]
name|deprecatedNames
decl_stmt|;
DECL|field|allReplacedWith
specifier|private
name|String
name|allReplacedWith
init|=
literal|null
decl_stmt|;
DECL|field|allNames
specifier|private
specifier|final
name|String
index|[]
name|allNames
decl_stmt|;
DECL|method|ParseField
specifier|public
name|ParseField
parameter_list|(
name|String
name|value
parameter_list|,
name|String
modifier|...
name|deprecatedNames
parameter_list|)
block|{
name|camelCaseName
operator|=
name|Strings
operator|.
name|toCamelCase
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|underscoreName
operator|=
name|Strings
operator|.
name|toUnderscoreCase
argument_list|(
name|value
argument_list|)
expr_stmt|;
if|if
condition|(
name|deprecatedNames
operator|==
literal|null
operator|||
name|deprecatedNames
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|this
operator|.
name|deprecatedNames
operator|=
name|Strings
operator|.
name|EMPTY_ARRAY
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|HashSet
argument_list|<
name|String
argument_list|>
name|set
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|depName
range|:
name|deprecatedNames
control|)
block|{
name|set
operator|.
name|add
argument_list|(
name|Strings
operator|.
name|toCamelCase
argument_list|(
name|depName
argument_list|)
argument_list|)
expr_stmt|;
name|set
operator|.
name|add
argument_list|(
name|Strings
operator|.
name|toUnderscoreCase
argument_list|(
name|depName
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|deprecatedNames
operator|=
name|set
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|set
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
block|}
name|Set
argument_list|<
name|String
argument_list|>
name|allNames
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|allNames
operator|.
name|add
argument_list|(
name|camelCaseName
argument_list|)
expr_stmt|;
name|allNames
operator|.
name|add
argument_list|(
name|underscoreName
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|addAll
argument_list|(
name|allNames
argument_list|,
name|this
operator|.
name|deprecatedNames
argument_list|)
expr_stmt|;
name|this
operator|.
name|allNames
operator|=
name|allNames
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|allNames
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
block|}
DECL|method|getPreferredName
specifier|public
name|String
name|getPreferredName
parameter_list|()
block|{
return|return
name|underscoreName
return|;
block|}
DECL|method|getAllNamesIncludedDeprecated
specifier|public
name|String
index|[]
name|getAllNamesIncludedDeprecated
parameter_list|()
block|{
return|return
name|allNames
return|;
block|}
DECL|method|withDeprecation
specifier|public
name|ParseField
name|withDeprecation
parameter_list|(
name|String
modifier|...
name|deprecatedNames
parameter_list|)
block|{
return|return
operator|new
name|ParseField
argument_list|(
name|this
operator|.
name|underscoreName
argument_list|,
name|deprecatedNames
argument_list|)
return|;
block|}
comment|/**      * Return a new ParseField where all field names are deprecated and replaced with {@code allReplacedWith}.      */
DECL|method|withAllDeprecated
specifier|public
name|ParseField
name|withAllDeprecated
parameter_list|(
name|String
name|allReplacedWith
parameter_list|)
block|{
name|ParseField
name|parseField
init|=
name|this
operator|.
name|withDeprecation
argument_list|(
name|getAllNamesIncludedDeprecated
argument_list|()
argument_list|)
decl_stmt|;
name|parseField
operator|.
name|allReplacedWith
operator|=
name|allReplacedWith
expr_stmt|;
return|return
name|parseField
return|;
block|}
DECL|method|match
name|boolean
name|match
parameter_list|(
name|String
name|currentFieldName
parameter_list|,
name|boolean
name|strict
parameter_list|)
block|{
if|if
condition|(
name|allReplacedWith
operator|==
literal|null
operator|&&
operator|(
name|currentFieldName
operator|.
name|equals
argument_list|(
name|camelCaseName
argument_list|)
operator|||
name|currentFieldName
operator|.
name|equals
argument_list|(
name|underscoreName
argument_list|)
operator|)
condition|)
block|{
return|return
literal|true
return|;
block|}
name|String
name|msg
decl_stmt|;
for|for
control|(
name|String
name|depName
range|:
name|deprecatedNames
control|)
block|{
if|if
condition|(
name|currentFieldName
operator|.
name|equals
argument_list|(
name|depName
argument_list|)
condition|)
block|{
name|msg
operator|=
literal|"Deprecated field ["
operator|+
name|currentFieldName
operator|+
literal|"] used, expected ["
operator|+
name|underscoreName
operator|+
literal|"] instead"
expr_stmt|;
if|if
condition|(
name|allReplacedWith
operator|!=
literal|null
condition|)
block|{
name|msg
operator|=
literal|"Deprecated field ["
operator|+
name|currentFieldName
operator|+
literal|"] used, replaced by ["
operator|+
name|allReplacedWith
operator|+
literal|"]"
expr_stmt|;
block|}
if|if
condition|(
name|strict
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
else|else
block|{
name|DEPRECATION_LOGGER
operator|.
name|deprecated
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|getPreferredName
argument_list|()
return|;
block|}
DECL|method|getAllReplacedWith
specifier|public
name|String
name|getAllReplacedWith
parameter_list|()
block|{
return|return
name|allReplacedWith
return|;
block|}
DECL|method|getCamelCaseName
specifier|public
name|String
name|getCamelCaseName
parameter_list|()
block|{
return|return
name|camelCaseName
return|;
block|}
DECL|method|getDeprecatedNames
specifier|public
name|String
index|[]
name|getDeprecatedNames
parameter_list|()
block|{
return|return
name|deprecatedNames
return|;
block|}
block|}
end_class

end_unit

