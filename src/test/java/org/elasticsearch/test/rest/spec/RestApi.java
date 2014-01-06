begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one  * or more contributor license agreements. See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elasticsearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.rest.spec
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|rest
operator|.
name|spec
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|client
operator|.
name|methods
operator|.
name|HttpPost
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|client
operator|.
name|methods
operator|.
name|HttpPut
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|PriorityQueue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_comment
comment|/**  * Represents an elasticsearch REST endpoint (api)  */
end_comment

begin_class
DECL|class|RestApi
specifier|public
class|class
name|RestApi
block|{
DECL|field|ALL
specifier|private
specifier|static
specifier|final
name|String
name|ALL
init|=
literal|"_all"
decl_stmt|;
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|methods
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|methods
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
DECL|field|paths
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|paths
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
DECL|field|pathParts
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|pathParts
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
DECL|method|RestApi
name|RestApi
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
DECL|method|RestApi
name|RestApi
parameter_list|(
name|RestApi
name|restApi
parameter_list|,
name|String
name|name
parameter_list|,
name|String
modifier|...
name|methods
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|methods
operator|=
name|Arrays
operator|.
name|asList
argument_list|(
name|methods
argument_list|)
expr_stmt|;
name|paths
operator|.
name|addAll
argument_list|(
name|restApi
operator|.
name|getPaths
argument_list|()
argument_list|)
expr_stmt|;
name|pathParts
operator|.
name|addAll
argument_list|(
name|restApi
operator|.
name|getPathParts
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|RestApi
name|RestApi
parameter_list|(
name|RestApi
name|restApi
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|paths
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|restApi
operator|.
name|getName
argument_list|()
expr_stmt|;
name|this
operator|.
name|methods
operator|=
name|restApi
operator|.
name|getMethods
argument_list|()
expr_stmt|;
name|this
operator|.
name|paths
operator|.
name|addAll
argument_list|(
name|paths
argument_list|)
expr_stmt|;
name|pathParts
operator|.
name|addAll
argument_list|(
name|restApi
operator|.
name|getPathParts
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
DECL|method|getMethods
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getMethods
parameter_list|()
block|{
return|return
name|methods
return|;
block|}
comment|/**      * Returns the supported http methods given the rest parameters provided      */
DECL|method|getSupportedMethods
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getSupportedMethods
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|restParams
parameter_list|)
block|{
comment|//we try to avoid hardcoded mappings but the index api is the exception
if|if
condition|(
literal|"index"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
operator|||
literal|"create"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|indexMethods
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|method
range|:
name|methods
control|)
block|{
if|if
condition|(
name|restParams
operator|.
name|contains
argument_list|(
literal|"id"
argument_list|)
condition|)
block|{
comment|//PUT when the id is provided
if|if
condition|(
name|HttpPut
operator|.
name|METHOD_NAME
operator|.
name|equals
argument_list|(
name|method
argument_list|)
condition|)
block|{
name|indexMethods
operator|.
name|add
argument_list|(
name|method
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|//POST without id
if|if
condition|(
name|HttpPost
operator|.
name|METHOD_NAME
operator|.
name|equals
argument_list|(
name|method
argument_list|)
condition|)
block|{
name|indexMethods
operator|.
name|add
argument_list|(
name|method
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|indexMethods
return|;
block|}
return|return
name|methods
return|;
block|}
DECL|method|addMethod
name|void
name|addMethod
parameter_list|(
name|String
name|method
parameter_list|)
block|{
name|this
operator|.
name|methods
operator|.
name|add
argument_list|(
name|method
argument_list|)
expr_stmt|;
block|}
DECL|method|getPaths
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getPaths
parameter_list|()
block|{
return|return
name|paths
return|;
block|}
DECL|method|addPath
name|void
name|addPath
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|this
operator|.
name|paths
operator|.
name|add
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
DECL|method|getPathParts
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getPathParts
parameter_list|()
block|{
return|return
name|pathParts
return|;
block|}
DECL|method|addPathPart
name|void
name|addPathPart
parameter_list|(
name|String
name|pathPart
parameter_list|)
block|{
name|this
operator|.
name|pathParts
operator|.
name|add
argument_list|(
name|pathPart
argument_list|)
expr_stmt|;
block|}
comment|/**      * Finds the best matching rest path given the current parameters and replaces      * placeholders with their corresponding values received as arguments      */
DECL|method|getFinalPath
specifier|public
name|String
name|getFinalPath
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|pathParams
parameter_list|)
block|{
name|RestPath
name|matchingRestPath
init|=
name|findMatchingRestPath
argument_list|(
name|pathParams
operator|.
name|keySet
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|path
init|=
name|matchingRestPath
operator|.
name|path
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|paramEntry
range|:
name|matchingRestPath
operator|.
name|params
operator|.
name|entrySet
argument_list|()
control|)
block|{
comment|//replace path placeholders with actual values
name|String
name|value
init|=
name|pathParams
operator|.
name|get
argument_list|(
name|paramEntry
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
comment|//there might be additional placeholder to replace, not available as input params
comment|//it can only be {index} or {type} to be replaced with _all
if|if
condition|(
name|paramEntry
operator|.
name|getValue
argument_list|()
operator|.
name|equals
argument_list|(
literal|"index"
argument_list|)
operator|||
name|paramEntry
operator|.
name|getValue
argument_list|()
operator|.
name|equals
argument_list|(
literal|"type"
argument_list|)
condition|)
block|{
name|value
operator|=
name|ALL
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"path ["
operator|+
name|path
operator|+
literal|"] contains placeholders that weren't replaced with proper values"
argument_list|)
throw|;
block|}
block|}
name|path
operator|=
name|path
operator|.
name|replace
argument_list|(
name|paramEntry
operator|.
name|getKey
argument_list|()
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
return|return
name|path
return|;
block|}
comment|/**      * Finds the best matching rest path out of the available ones with the current api (based on REST spec).      *      * The best path is the one that has exactly the same number of placeholders to replace      * (e.g. /{index}/{type}/{id} when the params are exactly index, type and id).      * Otherwise there might be additional placeholders, thus we use the path with the least additional placeholders.      * (e.g. get with only index and id as parameters, the closest (and only) path contains {type} too, which becomes _all)      */
DECL|method|findMatchingRestPath
specifier|private
name|RestPath
name|findMatchingRestPath
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|restParams
parameter_list|)
block|{
name|RestPath
index|[]
name|restPaths
init|=
name|buildRestPaths
argument_list|()
decl_stmt|;
comment|//We need to find the path that has exactly the placeholders corresponding to our params
comment|//If there's no exact match we fallback to the closest one (with as less additional placeholders as possible)
comment|//The fallback is needed for:
comment|//1) get, get_source and exists with only index and id => /{index}/_all/{id} (
comment|//2) search with only type => /_all/{type/_search
name|PriorityQueue
argument_list|<
name|RestPath
argument_list|>
name|restPathQueue
init|=
operator|new
name|PriorityQueue
argument_list|<
name|RestPath
argument_list|>
argument_list|(
literal|1
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|boolean
name|lessThan
parameter_list|(
name|RestPath
name|a
parameter_list|,
name|RestPath
name|b
parameter_list|)
block|{
return|return
name|a
operator|.
name|params
operator|.
name|size
argument_list|()
operator|>=
name|b
operator|.
name|params
operator|.
name|size
argument_list|()
return|;
block|}
block|}
decl_stmt|;
for|for
control|(
name|RestPath
name|restPath
range|:
name|restPaths
control|)
block|{
if|if
condition|(
name|restPath
operator|.
name|params
operator|.
name|values
argument_list|()
operator|.
name|containsAll
argument_list|(
name|restParams
argument_list|)
condition|)
block|{
name|restPathQueue
operator|.
name|insertWithOverflow
argument_list|(
name|restPath
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|restPathQueue
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
return|return
name|restPathQueue
operator|.
name|top
argument_list|()
return|;
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"unable to find best path for api ["
operator|+
name|name
operator|+
literal|"] and params "
operator|+
name|restParams
argument_list|)
throw|;
block|}
DECL|method|buildRestPaths
specifier|private
name|RestPath
index|[]
name|buildRestPaths
parameter_list|()
block|{
name|RestPath
index|[]
name|restPaths
init|=
operator|new
name|RestPath
index|[
name|paths
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|restPaths
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|restPaths
index|[
name|i
index|]
operator|=
operator|new
name|RestPath
argument_list|(
name|paths
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|restPaths
return|;
block|}
DECL|class|RestPath
specifier|private
specifier|static
class|class
name|RestPath
block|{
DECL|field|PLACEHOLDERS_PATTERN
specifier|private
specifier|static
specifier|final
name|Pattern
name|PLACEHOLDERS_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"(\\{(.*?)})"
argument_list|)
decl_stmt|;
DECL|field|path
specifier|final
name|String
name|path
decl_stmt|;
comment|//contains param to replace (e.g. {index}) and param key to use for lookup in the current values map (e.g. index)
DECL|field|params
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
decl_stmt|;
DECL|method|RestPath
name|RestPath
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|params
operator|=
name|extractParams
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
DECL|method|extractParams
specifier|private
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|extractParams
parameter_list|(
name|String
name|input
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
name|Matcher
name|matcher
init|=
name|PLACEHOLDERS_PATTERN
operator|.
name|matcher
argument_list|(
name|input
argument_list|)
decl_stmt|;
while|while
condition|(
name|matcher
operator|.
name|find
argument_list|()
condition|)
block|{
comment|//key is e.g. {index}
name|String
name|key
init|=
name|input
operator|.
name|substring
argument_list|(
name|matcher
operator|.
name|start
argument_list|()
argument_list|,
name|matcher
operator|.
name|end
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|matcher
operator|.
name|groupCount
argument_list|()
operator|!=
literal|2
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"no lookup key found for param ["
operator|+
name|key
operator|+
literal|"]"
argument_list|)
throw|;
block|}
comment|//to be replaced with current value found with key e.g. index
name|String
name|value
init|=
name|matcher
operator|.
name|group
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|params
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
return|return
name|params
return|;
block|}
block|}
block|}
end_class

end_unit

