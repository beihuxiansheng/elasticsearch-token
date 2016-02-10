begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.sort
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|sort
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
name|Writeable
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
name|Locale
import|;
end_import

begin_comment
comment|/**  * A sorting order.  *  *  */
end_comment

begin_enum
DECL|enum|SortOrder
specifier|public
enum|enum
name|SortOrder
implements|implements
name|Writeable
argument_list|<
name|SortOrder
argument_list|>
block|{
comment|/**      * Ascending order.      */
DECL|enum constant|ASC
name|ASC
block|{
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"asc"
return|;
block|}
block|}
block|,
comment|/**      * Descending order.      */
DECL|enum constant|DESC
name|DESC
block|{
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"desc"
return|;
block|}
block|}
block|;
DECL|field|PROTOTYPE
specifier|private
specifier|static
specifier|final
name|SortOrder
name|PROTOTYPE
init|=
name|ASC
decl_stmt|;
annotation|@
name|Override
DECL|method|readFrom
specifier|public
name|SortOrder
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|ordinal
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|ordinal
operator|<
literal|0
operator|||
name|ordinal
operator|>=
name|values
argument_list|()
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unknown SortOrder ordinal ["
operator|+
name|ordinal
operator|+
literal|"]"
argument_list|)
throw|;
block|}
return|return
name|values
argument_list|()
index|[
name|ordinal
index|]
return|;
block|}
DECL|method|readOrderFrom
specifier|public
specifier|static
name|SortOrder
name|readOrderFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|PROTOTYPE
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
return|;
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
name|writeVInt
argument_list|(
name|this
operator|.
name|ordinal
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|fromString
specifier|public
specifier|static
name|SortOrder
name|fromString
parameter_list|(
name|String
name|op
parameter_list|)
block|{
return|return
name|valueOf
argument_list|(
name|op
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
return|;
block|}
block|}
end_enum

end_unit

