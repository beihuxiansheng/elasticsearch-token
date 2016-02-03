begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.xcontent.support.filtering
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|support
operator|.
name|filtering
package|;
end_package

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|core
operator|.
name|filter
operator|.
name|TokenFilter
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
name|util
operator|.
name|CollectionUtils
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

begin_class
DECL|class|FilterPathBasedFilter
specifier|public
class|class
name|FilterPathBasedFilter
extends|extends
name|TokenFilter
block|{
comment|/**      * Marker value that should be used to indicate that a property name      * or value matches one of the filter paths.      */
DECL|field|MATCHING
specifier|private
specifier|static
specifier|final
name|TokenFilter
name|MATCHING
init|=
operator|new
name|TokenFilter
argument_list|()
block|{     }
decl_stmt|;
comment|/**      * Marker value that should be used to indicate that none of the      * property names/values matches one of the filter paths.      */
DECL|field|NO_MATCHING
specifier|private
specifier|static
specifier|final
name|TokenFilter
name|NO_MATCHING
init|=
operator|new
name|TokenFilter
argument_list|()
block|{     }
decl_stmt|;
DECL|field|filters
specifier|private
specifier|final
name|FilterPath
index|[]
name|filters
decl_stmt|;
DECL|field|inclusive
specifier|private
specifier|final
name|boolean
name|inclusive
decl_stmt|;
DECL|method|FilterPathBasedFilter
specifier|public
name|FilterPathBasedFilter
parameter_list|(
name|FilterPath
index|[]
name|filters
parameter_list|,
name|boolean
name|inclusive
parameter_list|)
block|{
if|if
condition|(
name|CollectionUtils
operator|.
name|isEmpty
argument_list|(
name|filters
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"filters cannot be null or empty"
argument_list|)
throw|;
block|}
name|this
operator|.
name|inclusive
operator|=
name|inclusive
expr_stmt|;
name|this
operator|.
name|filters
operator|=
name|filters
expr_stmt|;
block|}
DECL|method|FilterPathBasedFilter
specifier|public
name|FilterPathBasedFilter
parameter_list|(
name|boolean
name|inclusive
parameter_list|,
name|String
index|[]
name|filters
parameter_list|)
block|{
name|this
argument_list|(
name|FilterPath
operator|.
name|compile
argument_list|(
name|filters
argument_list|)
argument_list|,
name|inclusive
argument_list|)
expr_stmt|;
block|}
comment|/**      * Evaluates if a property name matches one of the given filter paths.      */
DECL|method|evaluate
specifier|private
name|TokenFilter
name|evaluate
parameter_list|(
name|String
name|name
parameter_list|,
name|FilterPath
index|[]
name|filters
parameter_list|)
block|{
if|if
condition|(
name|filters
operator|!=
literal|null
condition|)
block|{
name|List
argument_list|<
name|FilterPath
argument_list|>
name|nextFilters
init|=
literal|null
decl_stmt|;
for|for
control|(
name|FilterPath
name|filter
range|:
name|filters
control|)
block|{
name|FilterPath
name|next
init|=
name|filter
operator|.
name|matchProperty
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|next
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|next
operator|.
name|matches
argument_list|()
condition|)
block|{
return|return
name|MATCHING
return|;
block|}
else|else
block|{
if|if
condition|(
name|nextFilters
operator|==
literal|null
condition|)
block|{
name|nextFilters
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|filter
operator|.
name|isDoubleWildcard
argument_list|()
condition|)
block|{
name|nextFilters
operator|.
name|add
argument_list|(
name|filter
argument_list|)
expr_stmt|;
block|}
name|nextFilters
operator|.
name|add
argument_list|(
name|next
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
operator|(
name|nextFilters
operator|!=
literal|null
operator|)
operator|&&
operator|(
name|nextFilters
operator|.
name|isEmpty
argument_list|()
operator|==
literal|false
operator|)
condition|)
block|{
return|return
operator|new
name|FilterPathBasedFilter
argument_list|(
name|nextFilters
operator|.
name|toArray
argument_list|(
operator|new
name|FilterPath
index|[
name|nextFilters
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|,
name|inclusive
argument_list|)
return|;
block|}
block|}
return|return
name|NO_MATCHING
return|;
block|}
annotation|@
name|Override
DECL|method|includeProperty
specifier|public
name|TokenFilter
name|includeProperty
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|TokenFilter
name|filter
init|=
name|evaluate
argument_list|(
name|name
argument_list|,
name|filters
argument_list|)
decl_stmt|;
if|if
condition|(
name|filter
operator|==
name|MATCHING
condition|)
block|{
return|return
name|inclusive
condition|?
name|TokenFilter
operator|.
name|INCLUDE_ALL
else|:
literal|null
return|;
block|}
if|if
condition|(
name|filter
operator|==
name|NO_MATCHING
condition|)
block|{
return|return
name|inclusive
condition|?
literal|null
else|:
name|TokenFilter
operator|.
name|INCLUDE_ALL
return|;
block|}
return|return
name|filter
return|;
block|}
annotation|@
name|Override
DECL|method|_includeScalar
specifier|protected
name|boolean
name|_includeScalar
parameter_list|()
block|{
for|for
control|(
name|FilterPath
name|filter
range|:
name|filters
control|)
block|{
if|if
condition|(
name|filter
operator|.
name|matches
argument_list|()
condition|)
block|{
return|return
name|inclusive
return|;
block|}
block|}
return|return
operator|!
name|inclusive
return|;
block|}
block|}
end_class

end_unit

