begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.support
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|support
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|ClusterState
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|metadata
operator|.
name|IndexNameExpressionResolver
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
name|Booleans
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
name|Strings
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
name|collect
operator|.
name|Tuple
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
name|inject
operator|.
name|Inject
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
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|settings
operator|.
name|Setting
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
name|settings
operator|.
name|Setting
operator|.
name|SettingsProperty
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
name|settings
operator|.
name|Settings
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
name|MapperService
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

begin_comment
comment|/**  * Encapsulates the logic of whether a new index should be automatically created when  * a write operation is about to happen in a non existing index.  */
end_comment

begin_class
DECL|class|AutoCreateIndex
specifier|public
specifier|final
class|class
name|AutoCreateIndex
block|{
DECL|field|AUTO_CREATE_INDEX_SETTING
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|AutoCreate
argument_list|>
name|AUTO_CREATE_INDEX_SETTING
init|=
operator|new
name|Setting
argument_list|<>
argument_list|(
literal|"action.auto_create_index"
argument_list|,
literal|"true"
argument_list|,
name|AutoCreate
operator|::
operator|new
argument_list|,
name|SettingsProperty
operator|.
name|ClusterScope
argument_list|)
decl_stmt|;
DECL|field|dynamicMappingDisabled
specifier|private
specifier|final
name|boolean
name|dynamicMappingDisabled
decl_stmt|;
DECL|field|resolver
specifier|private
specifier|final
name|IndexNameExpressionResolver
name|resolver
decl_stmt|;
DECL|field|autoCreate
specifier|private
specifier|final
name|AutoCreate
name|autoCreate
decl_stmt|;
annotation|@
name|Inject
DECL|method|AutoCreateIndex
specifier|public
name|AutoCreateIndex
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|IndexNameExpressionResolver
name|resolver
parameter_list|)
block|{
name|this
operator|.
name|resolver
operator|=
name|resolver
expr_stmt|;
name|dynamicMappingDisabled
operator|=
operator|!
name|MapperService
operator|.
name|INDEX_MAPPER_DYNAMIC_SETTING
operator|.
name|get
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|autoCreate
operator|=
name|AUTO_CREATE_INDEX_SETTING
operator|.
name|get
argument_list|(
name|settings
argument_list|)
expr_stmt|;
block|}
comment|/**      * Do we really need to check if an index should be auto created?      */
DECL|method|needToCheck
specifier|public
name|boolean
name|needToCheck
parameter_list|()
block|{
return|return
name|this
operator|.
name|autoCreate
operator|.
name|autoCreateIndex
return|;
block|}
comment|/**      * Should the index be auto created?      */
DECL|method|shouldAutoCreate
specifier|public
name|boolean
name|shouldAutoCreate
parameter_list|(
name|String
name|index
parameter_list|,
name|ClusterState
name|state
parameter_list|)
block|{
if|if
condition|(
name|autoCreate
operator|.
name|autoCreateIndex
operator|==
literal|false
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|dynamicMappingDisabled
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|resolver
operator|.
name|hasIndexOrAlias
argument_list|(
name|index
argument_list|,
name|state
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// matches not set, default value of "true"
if|if
condition|(
name|autoCreate
operator|.
name|expressions
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
for|for
control|(
name|Tuple
argument_list|<
name|String
argument_list|,
name|Boolean
argument_list|>
name|expression
range|:
name|autoCreate
operator|.
name|expressions
control|)
block|{
name|String
name|indexExpression
init|=
name|expression
operator|.
name|v1
argument_list|()
decl_stmt|;
name|boolean
name|include
init|=
name|expression
operator|.
name|v2
argument_list|()
decl_stmt|;
if|if
condition|(
name|Regex
operator|.
name|simpleMatch
argument_list|(
name|indexExpression
argument_list|,
name|index
argument_list|)
condition|)
block|{
return|return
name|include
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
DECL|class|AutoCreate
specifier|private
specifier|static
class|class
name|AutoCreate
block|{
DECL|field|autoCreateIndex
specifier|private
specifier|final
name|boolean
name|autoCreateIndex
decl_stmt|;
DECL|field|expressions
specifier|private
specifier|final
name|List
argument_list|<
name|Tuple
argument_list|<
name|String
argument_list|,
name|Boolean
argument_list|>
argument_list|>
name|expressions
decl_stmt|;
DECL|method|AutoCreate
specifier|private
name|AutoCreate
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|boolean
name|autoCreateIndex
decl_stmt|;
name|List
argument_list|<
name|Tuple
argument_list|<
name|String
argument_list|,
name|Boolean
argument_list|>
argument_list|>
name|expressions
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
try|try
block|{
name|autoCreateIndex
operator|=
name|Booleans
operator|.
name|parseBooleanExact
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ex
parameter_list|)
block|{
try|try
block|{
name|String
index|[]
name|patterns
init|=
name|Strings
operator|.
name|commaDelimitedListToStringArray
argument_list|(
name|value
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|pattern
range|:
name|patterns
control|)
block|{
if|if
condition|(
name|pattern
operator|==
literal|null
operator|||
name|pattern
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Can't parse ["
operator|+
name|value
operator|+
literal|"] for setting [action.auto_create_index] must be either [true, false, or a comma separated list of index patterns]"
argument_list|)
throw|;
block|}
name|Tuple
argument_list|<
name|String
argument_list|,
name|Boolean
argument_list|>
name|expression
decl_stmt|;
if|if
condition|(
name|pattern
operator|.
name|startsWith
argument_list|(
literal|"-"
argument_list|)
condition|)
block|{
if|if
condition|(
name|pattern
operator|.
name|length
argument_list|()
operator|==
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Can't parse ["
operator|+
name|value
operator|+
literal|"] for setting [action.auto_create_index] must contain an index name after [-]"
argument_list|)
throw|;
block|}
name|expression
operator|=
operator|new
name|Tuple
argument_list|<>
argument_list|(
name|pattern
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|pattern
operator|.
name|startsWith
argument_list|(
literal|"+"
argument_list|)
condition|)
block|{
if|if
condition|(
name|pattern
operator|.
name|length
argument_list|()
operator|==
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Can't parse ["
operator|+
name|value
operator|+
literal|"] for setting [action.auto_create_index] must contain an index name after [+]"
argument_list|)
throw|;
block|}
name|expression
operator|=
operator|new
name|Tuple
argument_list|<>
argument_list|(
name|pattern
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|expression
operator|=
operator|new
name|Tuple
argument_list|<>
argument_list|(
name|pattern
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|expressions
operator|.
name|add
argument_list|(
name|expression
argument_list|)
expr_stmt|;
block|}
name|autoCreateIndex
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ex1
parameter_list|)
block|{
name|ex1
operator|.
name|addSuppressed
argument_list|(
name|ex
argument_list|)
expr_stmt|;
throw|throw
name|ex1
throw|;
block|}
block|}
name|this
operator|.
name|expressions
operator|=
name|expressions
expr_stmt|;
name|this
operator|.
name|autoCreateIndex
operator|=
name|autoCreateIndex
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

