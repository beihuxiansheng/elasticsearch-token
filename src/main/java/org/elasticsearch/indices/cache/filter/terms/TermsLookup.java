begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.indices.cache.filter.terms
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|cache
operator|.
name|filter
operator|.
name|terms
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
name|Nullable
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
name|FieldMapper
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
name|QueryParseContext
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|TermsLookup
specifier|public
class|class
name|TermsLookup
block|{
DECL|field|fieldMapper
specifier|private
specifier|final
name|FieldMapper
name|fieldMapper
decl_stmt|;
DECL|field|index
specifier|private
specifier|final
name|String
name|index
decl_stmt|;
DECL|field|type
specifier|private
specifier|final
name|String
name|type
decl_stmt|;
DECL|field|id
specifier|private
specifier|final
name|String
name|id
decl_stmt|;
DECL|field|routing
specifier|private
specifier|final
name|String
name|routing
decl_stmt|;
DECL|field|path
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
annotation|@
name|Nullable
DECL|field|queryParseContext
specifier|private
specifier|final
name|QueryParseContext
name|queryParseContext
decl_stmt|;
DECL|method|TermsLookup
specifier|public
name|TermsLookup
parameter_list|(
name|FieldMapper
name|fieldMapper
parameter_list|,
name|String
name|index
parameter_list|,
name|String
name|type
parameter_list|,
name|String
name|id
parameter_list|,
name|String
name|routing
parameter_list|,
name|String
name|path
parameter_list|,
annotation|@
name|Nullable
name|QueryParseContext
name|queryParseContext
parameter_list|)
block|{
name|this
operator|.
name|fieldMapper
operator|=
name|fieldMapper
expr_stmt|;
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|routing
operator|=
name|routing
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|queryParseContext
operator|=
name|queryParseContext
expr_stmt|;
block|}
DECL|method|getFieldMapper
specifier|public
name|FieldMapper
name|getFieldMapper
parameter_list|()
block|{
return|return
name|fieldMapper
return|;
block|}
DECL|method|getIndex
specifier|public
name|String
name|getIndex
parameter_list|()
block|{
return|return
name|index
return|;
block|}
DECL|method|getType
specifier|public
name|String
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
DECL|method|getId
specifier|public
name|String
name|getId
parameter_list|()
block|{
return|return
name|id
return|;
block|}
DECL|method|getRouting
specifier|public
name|String
name|getRouting
parameter_list|()
block|{
return|return
name|this
operator|.
name|routing
return|;
block|}
DECL|method|getPath
specifier|public
name|String
name|getPath
parameter_list|()
block|{
return|return
name|path
return|;
block|}
annotation|@
name|Nullable
DECL|method|getQueryParseContext
specifier|public
name|QueryParseContext
name|getQueryParseContext
parameter_list|()
block|{
return|return
name|queryParseContext
return|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|fieldMapper
operator|.
name|names
argument_list|()
operator|.
name|fullName
argument_list|()
operator|+
literal|":"
operator|+
name|index
operator|+
literal|"/"
operator|+
name|type
operator|+
literal|"/"
operator|+
name|id
operator|+
literal|"/"
operator|+
name|path
return|;
block|}
block|}
end_class

end_unit

