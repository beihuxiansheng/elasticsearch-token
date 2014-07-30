begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.fetch.source
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|fetch
operator|.
name|source
package|;
end_package

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
name|io
operator|.
name|stream
operator|.
name|StreamInput
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
name|io
operator|.
name|stream
operator|.
name|StreamOutput
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
name|io
operator|.
name|stream
operator|.
name|Streamable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|RestRequest
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
name|Arrays
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|FetchSourceContext
specifier|public
class|class
name|FetchSourceContext
implements|implements
name|Streamable
block|{
DECL|field|FETCH_SOURCE
specifier|public
specifier|static
specifier|final
name|FetchSourceContext
name|FETCH_SOURCE
init|=
operator|new
name|FetchSourceContext
argument_list|(
literal|true
argument_list|)
decl_stmt|;
DECL|field|DO_NOT_FETCH_SOURCE
specifier|public
specifier|static
specifier|final
name|FetchSourceContext
name|DO_NOT_FETCH_SOURCE
init|=
operator|new
name|FetchSourceContext
argument_list|(
literal|false
argument_list|)
decl_stmt|;
DECL|field|fetchSource
specifier|private
name|boolean
name|fetchSource
decl_stmt|;
DECL|field|transformSource
specifier|private
name|boolean
name|transformSource
decl_stmt|;
DECL|field|includes
specifier|private
name|String
index|[]
name|includes
decl_stmt|;
DECL|field|excludes
specifier|private
name|String
index|[]
name|excludes
decl_stmt|;
DECL|method|FetchSourceContext
name|FetchSourceContext
parameter_list|()
block|{      }
DECL|method|FetchSourceContext
specifier|public
name|FetchSourceContext
parameter_list|(
name|boolean
name|fetchSource
parameter_list|)
block|{
name|this
argument_list|(
name|fetchSource
argument_list|,
name|Strings
operator|.
name|EMPTY_ARRAY
argument_list|,
name|Strings
operator|.
name|EMPTY_ARRAY
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|FetchSourceContext
specifier|public
name|FetchSourceContext
parameter_list|(
name|String
name|include
parameter_list|)
block|{
name|this
argument_list|(
name|include
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|FetchSourceContext
specifier|public
name|FetchSourceContext
parameter_list|(
name|String
name|include
parameter_list|,
name|String
name|exclude
parameter_list|)
block|{
name|this
argument_list|(
literal|true
argument_list|,
name|include
operator|==
literal|null
condition|?
name|Strings
operator|.
name|EMPTY_ARRAY
else|:
operator|new
name|String
index|[]
block|{
name|include
block|}
argument_list|,
name|exclude
operator|==
literal|null
condition|?
name|Strings
operator|.
name|EMPTY_ARRAY
else|:
operator|new
name|String
index|[]
block|{
name|exclude
block|}
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|FetchSourceContext
specifier|public
name|FetchSourceContext
parameter_list|(
name|String
index|[]
name|includes
parameter_list|)
block|{
name|this
argument_list|(
literal|true
argument_list|,
name|includes
argument_list|,
name|Strings
operator|.
name|EMPTY_ARRAY
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|FetchSourceContext
specifier|public
name|FetchSourceContext
parameter_list|(
name|String
index|[]
name|includes
parameter_list|,
name|String
index|[]
name|excludes
parameter_list|)
block|{
name|this
argument_list|(
literal|true
argument_list|,
name|includes
argument_list|,
name|excludes
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|FetchSourceContext
specifier|public
name|FetchSourceContext
parameter_list|(
name|boolean
name|fetchSource
parameter_list|,
name|String
index|[]
name|includes
parameter_list|,
name|String
index|[]
name|excludes
parameter_list|,
name|boolean
name|transform
parameter_list|)
block|{
name|this
operator|.
name|fetchSource
operator|=
name|fetchSource
expr_stmt|;
name|this
operator|.
name|includes
operator|=
name|includes
operator|==
literal|null
condition|?
name|Strings
operator|.
name|EMPTY_ARRAY
else|:
name|includes
expr_stmt|;
name|this
operator|.
name|excludes
operator|=
name|excludes
operator|==
literal|null
condition|?
name|Strings
operator|.
name|EMPTY_ARRAY
else|:
name|excludes
expr_stmt|;
name|this
operator|.
name|transformSource
operator|=
name|transform
expr_stmt|;
block|}
DECL|method|fetchSource
specifier|public
name|boolean
name|fetchSource
parameter_list|()
block|{
return|return
name|this
operator|.
name|fetchSource
return|;
block|}
DECL|method|fetchSource
specifier|public
name|FetchSourceContext
name|fetchSource
parameter_list|(
name|boolean
name|fetchSource
parameter_list|)
block|{
name|this
operator|.
name|fetchSource
operator|=
name|fetchSource
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Should the document be transformed after the source is loaded?      */
DECL|method|transformSource
specifier|public
name|boolean
name|transformSource
parameter_list|()
block|{
return|return
name|this
operator|.
name|transformSource
return|;
block|}
comment|/**      * Should the document be transformed after the source is loaded?      * @return this for chaining      */
DECL|method|transformSource
specifier|public
name|FetchSourceContext
name|transformSource
parameter_list|(
name|boolean
name|transformSource
parameter_list|)
block|{
name|this
operator|.
name|transformSource
operator|=
name|transformSource
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|includes
specifier|public
name|String
index|[]
name|includes
parameter_list|()
block|{
return|return
name|this
operator|.
name|includes
return|;
block|}
DECL|method|includes
specifier|public
name|FetchSourceContext
name|includes
parameter_list|(
name|String
index|[]
name|includes
parameter_list|)
block|{
name|this
operator|.
name|includes
operator|=
name|includes
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|excludes
specifier|public
name|String
index|[]
name|excludes
parameter_list|()
block|{
return|return
name|this
operator|.
name|excludes
return|;
block|}
DECL|method|excludes
specifier|public
name|FetchSourceContext
name|excludes
parameter_list|(
name|String
index|[]
name|excludes
parameter_list|)
block|{
name|this
operator|.
name|excludes
operator|=
name|excludes
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|optionalReadFromStream
specifier|public
specifier|static
name|FetchSourceContext
name|optionalReadFromStream
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|FetchSourceContext
name|context
init|=
operator|new
name|FetchSourceContext
argument_list|()
decl_stmt|;
name|context
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|context
return|;
block|}
DECL|method|optionalWriteToStream
specifier|public
specifier|static
name|void
name|optionalWriteToStream
parameter_list|(
name|FetchSourceContext
name|context
parameter_list|,
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|context
operator|==
literal|null
condition|)
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|false
argument_list|)
expr_stmt|;
return|return;
block|}
name|out
operator|.
name|writeBoolean
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|context
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
DECL|method|parseFromRestRequest
specifier|public
specifier|static
name|FetchSourceContext
name|parseFromRestRequest
parameter_list|(
name|RestRequest
name|request
parameter_list|)
block|{
name|Boolean
name|fetchSource
init|=
literal|null
decl_stmt|;
name|String
index|[]
name|source_excludes
init|=
literal|null
decl_stmt|;
name|String
index|[]
name|source_includes
init|=
literal|null
decl_stmt|;
name|String
name|source
init|=
name|request
operator|.
name|param
argument_list|(
literal|"_source"
argument_list|)
decl_stmt|;
if|if
condition|(
name|source
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|Booleans
operator|.
name|isExplicitTrue
argument_list|(
name|source
argument_list|)
condition|)
block|{
name|fetchSource
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|Booleans
operator|.
name|isExplicitFalse
argument_list|(
name|source
argument_list|)
condition|)
block|{
name|fetchSource
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|source_includes
operator|=
name|Strings
operator|.
name|splitStringByCommaToArray
argument_list|(
name|source
argument_list|)
expr_stmt|;
block|}
block|}
name|String
name|sIncludes
init|=
name|request
operator|.
name|param
argument_list|(
literal|"_source_includes"
argument_list|)
decl_stmt|;
name|sIncludes
operator|=
name|request
operator|.
name|param
argument_list|(
literal|"_source_include"
argument_list|,
name|sIncludes
argument_list|)
expr_stmt|;
if|if
condition|(
name|sIncludes
operator|!=
literal|null
condition|)
block|{
name|source_includes
operator|=
name|Strings
operator|.
name|splitStringByCommaToArray
argument_list|(
name|sIncludes
argument_list|)
expr_stmt|;
block|}
name|String
name|sExcludes
init|=
name|request
operator|.
name|param
argument_list|(
literal|"_source_excludes"
argument_list|)
decl_stmt|;
name|sExcludes
operator|=
name|request
operator|.
name|param
argument_list|(
literal|"_source_exclude"
argument_list|,
name|sExcludes
argument_list|)
expr_stmt|;
if|if
condition|(
name|sExcludes
operator|!=
literal|null
condition|)
block|{
name|source_excludes
operator|=
name|Strings
operator|.
name|splitStringByCommaToArray
argument_list|(
name|sExcludes
argument_list|)
expr_stmt|;
block|}
name|boolean
name|transform
init|=
name|request
operator|.
name|paramAsBoolean
argument_list|(
literal|"_source_transform"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|fetchSource
operator|!=
literal|null
operator|||
name|source_includes
operator|!=
literal|null
operator|||
name|source_excludes
operator|!=
literal|null
operator|||
name|transform
condition|)
block|{
return|return
operator|new
name|FetchSourceContext
argument_list|(
name|fetchSource
operator|==
literal|null
condition|?
literal|true
else|:
name|fetchSource
argument_list|,
name|source_includes
argument_list|,
name|source_excludes
argument_list|,
name|transform
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|readFrom
specifier|public
name|void
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|fetchSource
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|includes
operator|=
name|in
operator|.
name|readStringArray
argument_list|()
expr_stmt|;
name|excludes
operator|=
name|in
operator|.
name|readStringArray
argument_list|()
expr_stmt|;
if|if
condition|(
name|in
operator|.
name|getVersion
argument_list|()
operator|.
name|onOrAfter
argument_list|(
name|Version
operator|.
name|V_1_3_0
argument_list|)
condition|)
block|{
name|transformSource
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|writeTo
specifier|public
name|void
name|writeTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
name|fetchSource
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeStringArray
argument_list|(
name|includes
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeStringArray
argument_list|(
name|excludes
argument_list|)
expr_stmt|;
if|if
condition|(
name|out
operator|.
name|getVersion
argument_list|()
operator|.
name|onOrAfter
argument_list|(
name|Version
operator|.
name|V_1_3_0
argument_list|)
condition|)
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
name|transformSource
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|FetchSourceContext
name|that
init|=
operator|(
name|FetchSourceContext
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|fetchSource
operator|!=
name|that
operator|.
name|fetchSource
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|Arrays
operator|.
name|equals
argument_list|(
name|excludes
argument_list|,
name|that
operator|.
name|excludes
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|Arrays
operator|.
name|equals
argument_list|(
name|includes
argument_list|,
name|that
operator|.
name|includes
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|result
init|=
operator|(
name|fetchSource
condition|?
literal|1
else|:
literal|0
operator|)
decl_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
operator|(
name|includes
operator|!=
literal|null
condition|?
name|Arrays
operator|.
name|hashCode
argument_list|(
name|includes
argument_list|)
else|:
literal|0
operator|)
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
operator|(
name|excludes
operator|!=
literal|null
condition|?
name|Arrays
operator|.
name|hashCode
argument_list|(
name|excludes
argument_list|)
else|:
literal|0
operator|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

