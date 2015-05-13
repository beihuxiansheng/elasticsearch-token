begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.mapper.externalvalues
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|externalvalues
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
name|document
operator|.
name|Field
operator|.
name|Store
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
name|document
operator|.
name|StringField
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
name|xcontent
operator|.
name|XContentBuilder
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
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|Iterator
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
DECL|class|ExternalRootMapper
specifier|public
class|class
name|ExternalRootMapper
implements|implements
name|RootMapper
block|{
DECL|field|CONTENT_TYPE
specifier|static
specifier|final
name|String
name|CONTENT_TYPE
init|=
literal|"_external_root"
decl_stmt|;
DECL|field|FIELD_NAME
specifier|static
specifier|final
name|String
name|FIELD_NAME
init|=
literal|"_is_external"
decl_stmt|;
DECL|field|FIELD_VALUE
specifier|static
specifier|final
name|String
name|FIELD_VALUE
init|=
literal|"true"
decl_stmt|;
annotation|@
name|Override
DECL|method|name
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
name|CONTENT_TYPE
return|;
block|}
annotation|@
name|Override
DECL|method|merge
specifier|public
name|void
name|merge
parameter_list|(
name|Mapper
name|mergeWith
parameter_list|,
name|MergeResult
name|mergeResult
parameter_list|)
throws|throws
name|MergeMappingException
block|{
if|if
condition|(
operator|!
operator|(
name|mergeWith
operator|instanceof
name|ExternalRootMapper
operator|)
condition|)
block|{
name|mergeResult
operator|.
name|addConflict
argument_list|(
literal|"Trying to merge "
operator|+
name|mergeWith
operator|+
literal|" with "
operator|+
name|this
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|Iterator
argument_list|<
name|Mapper
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|emptyIterator
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{     }
annotation|@
name|Override
DECL|method|toXContent
specifier|public
name|XContentBuilder
name|toXContent
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|,
name|Params
name|params
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|builder
operator|.
name|startObject
argument_list|(
name|CONTENT_TYPE
argument_list|)
operator|.
name|endObject
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|preParse
specifier|public
name|void
name|preParse
parameter_list|(
name|ParseContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{     }
annotation|@
name|Override
DECL|method|postParse
specifier|public
name|void
name|postParse
parameter_list|(
name|ParseContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|context
operator|.
name|doc
argument_list|()
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
name|FIELD_NAME
argument_list|,
name|FIELD_VALUE
argument_list|,
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
extends|extends
name|Mapper
operator|.
name|Builder
argument_list|<
name|Builder
argument_list|,
name|ExternalRootMapper
argument_list|>
block|{
DECL|method|Builder
specifier|protected
name|Builder
parameter_list|()
block|{
name|super
argument_list|(
name|CONTENT_TYPE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|build
specifier|public
name|ExternalRootMapper
name|build
parameter_list|(
name|BuilderContext
name|context
parameter_list|)
block|{
return|return
operator|new
name|ExternalRootMapper
argument_list|()
return|;
block|}
block|}
DECL|class|TypeParser
specifier|public
specifier|static
class|class
name|TypeParser
implements|implements
name|Mapper
operator|.
name|TypeParser
block|{
annotation|@
name|Override
DECL|method|parse
specifier|public
name|Mapper
operator|.
name|Builder
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|parse
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
name|node
parameter_list|,
name|ParserContext
name|parserContext
parameter_list|)
throws|throws
name|MapperParsingException
block|{
return|return
operator|new
name|Builder
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

