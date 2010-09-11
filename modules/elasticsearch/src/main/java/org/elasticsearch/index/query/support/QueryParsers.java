begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.query.support
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
operator|.
name|support
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|Filter
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
name|search
operator|.
name|FilteredQuery
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
name|search
operator|.
name|Query
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
name|ImmutableList
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
name|lucene
operator|.
name|search
operator|.
name|AndFilter
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
name|DocumentMapper
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
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
operator|.
name|xcontent
operator|.
name|QueryParseContext
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|QueryParsers
specifier|public
specifier|final
class|class
name|QueryParsers
block|{
DECL|method|QueryParsers
specifier|private
name|QueryParsers
parameter_list|()
block|{      }
DECL|method|wrapSmartNameQuery
specifier|public
specifier|static
name|Query
name|wrapSmartNameQuery
parameter_list|(
name|Query
name|query
parameter_list|,
annotation|@
name|Nullable
name|MapperService
operator|.
name|SmartNameFieldMappers
name|smartFieldMappers
parameter_list|,
name|QueryParseContext
name|parseContext
parameter_list|)
block|{
if|if
condition|(
name|smartFieldMappers
operator|==
literal|null
condition|)
block|{
return|return
name|query
return|;
block|}
if|if
condition|(
operator|!
name|smartFieldMappers
operator|.
name|hasDocMapper
argument_list|()
condition|)
block|{
return|return
name|query
return|;
block|}
name|DocumentMapper
name|docMapper
init|=
name|smartFieldMappers
operator|.
name|docMapper
argument_list|()
decl_stmt|;
return|return
operator|new
name|FilteredQuery
argument_list|(
name|query
argument_list|,
name|parseContext
operator|.
name|cacheFilterIfPossible
argument_list|(
name|docMapper
operator|.
name|typeFilter
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
DECL|method|wrapSmartNameFilter
specifier|public
specifier|static
name|Filter
name|wrapSmartNameFilter
parameter_list|(
name|Filter
name|filter
parameter_list|,
annotation|@
name|Nullable
name|MapperService
operator|.
name|SmartNameFieldMappers
name|smartFieldMappers
parameter_list|,
name|QueryParseContext
name|parseContext
parameter_list|)
block|{
if|if
condition|(
name|smartFieldMappers
operator|==
literal|null
condition|)
block|{
return|return
name|filter
return|;
block|}
if|if
condition|(
operator|!
name|smartFieldMappers
operator|.
name|hasDocMapper
argument_list|()
condition|)
block|{
return|return
name|filter
return|;
block|}
name|DocumentMapper
name|docMapper
init|=
name|smartFieldMappers
operator|.
name|docMapper
argument_list|()
decl_stmt|;
return|return
operator|new
name|AndFilter
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|parseContext
operator|.
name|cacheFilterIfPossible
argument_list|(
name|docMapper
operator|.
name|typeFilter
argument_list|()
argument_list|)
argument_list|,
name|filter
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

