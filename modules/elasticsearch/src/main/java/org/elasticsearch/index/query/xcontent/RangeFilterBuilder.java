begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.query.xcontent
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
operator|.
name|xcontent
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
name|xcontent
operator|.
name|builder
operator|.
name|XContentBuilder
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

begin_comment
comment|/**  * A filter that restricts search results to values that are within the given range.  *  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|RangeFilterBuilder
specifier|public
class|class
name|RangeFilterBuilder
extends|extends
name|BaseFilterBuilder
block|{
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|from
specifier|private
name|Object
name|from
decl_stmt|;
DECL|field|to
specifier|private
name|Object
name|to
decl_stmt|;
DECL|field|includeLower
specifier|private
name|boolean
name|includeLower
init|=
literal|true
decl_stmt|;
DECL|field|includeUpper
specifier|private
name|boolean
name|includeUpper
init|=
literal|true
decl_stmt|;
DECL|field|filterName
specifier|private
name|String
name|filterName
decl_stmt|;
comment|/**      * A filter that restricts search results to values that are within the given range.      *      * @param name The field name      */
DECL|method|RangeFilterBuilder
specifier|public
name|RangeFilterBuilder
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
comment|/**      * The from part of the filter query. Null indicates unbounded.      */
DECL|method|from
specifier|public
name|RangeFilterBuilder
name|from
parameter_list|(
name|Object
name|from
parameter_list|)
block|{
name|this
operator|.
name|from
operator|=
name|from
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The from part of the filter query. Null indicates unbounded.      */
DECL|method|from
specifier|public
name|RangeFilterBuilder
name|from
parameter_list|(
name|int
name|from
parameter_list|)
block|{
name|this
operator|.
name|from
operator|=
name|from
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The from part of the filter query. Null indicates unbounded.      */
DECL|method|from
specifier|public
name|RangeFilterBuilder
name|from
parameter_list|(
name|long
name|from
parameter_list|)
block|{
name|this
operator|.
name|from
operator|=
name|from
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The from part of the filter query. Null indicates unbounded.      */
DECL|method|from
specifier|public
name|RangeFilterBuilder
name|from
parameter_list|(
name|float
name|from
parameter_list|)
block|{
name|this
operator|.
name|from
operator|=
name|from
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The from part of the filter query. Null indicates unbounded.      */
DECL|method|from
specifier|public
name|RangeFilterBuilder
name|from
parameter_list|(
name|double
name|from
parameter_list|)
block|{
name|this
operator|.
name|from
operator|=
name|from
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The from part of the filter query. Null indicates unbounded.      */
DECL|method|gt
specifier|public
name|RangeFilterBuilder
name|gt
parameter_list|(
name|Object
name|from
parameter_list|)
block|{
name|this
operator|.
name|from
operator|=
name|from
expr_stmt|;
name|this
operator|.
name|includeLower
operator|=
literal|false
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The from part of the filter query. Null indicates unbounded.      */
DECL|method|gt
specifier|public
name|RangeFilterBuilder
name|gt
parameter_list|(
name|int
name|from
parameter_list|)
block|{
name|this
operator|.
name|from
operator|=
name|from
expr_stmt|;
name|this
operator|.
name|includeLower
operator|=
literal|false
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The from part of the filter query. Null indicates unbounded.      */
DECL|method|gt
specifier|public
name|RangeFilterBuilder
name|gt
parameter_list|(
name|long
name|from
parameter_list|)
block|{
name|this
operator|.
name|from
operator|=
name|from
expr_stmt|;
name|this
operator|.
name|includeLower
operator|=
literal|false
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The from part of the filter query. Null indicates unbounded.      */
DECL|method|gt
specifier|public
name|RangeFilterBuilder
name|gt
parameter_list|(
name|float
name|from
parameter_list|)
block|{
name|this
operator|.
name|from
operator|=
name|from
expr_stmt|;
name|this
operator|.
name|includeLower
operator|=
literal|false
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The from part of the filter query. Null indicates unbounded.      */
DECL|method|gt
specifier|public
name|RangeFilterBuilder
name|gt
parameter_list|(
name|double
name|from
parameter_list|)
block|{
name|this
operator|.
name|from
operator|=
name|from
expr_stmt|;
name|this
operator|.
name|includeLower
operator|=
literal|false
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The from part of the filter query. Null indicates unbounded.      */
DECL|method|gte
specifier|public
name|RangeFilterBuilder
name|gte
parameter_list|(
name|Object
name|from
parameter_list|)
block|{
name|this
operator|.
name|from
operator|=
name|from
expr_stmt|;
name|this
operator|.
name|includeLower
operator|=
literal|true
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The from part of the filter query. Null indicates unbounded.      */
DECL|method|gte
specifier|public
name|RangeFilterBuilder
name|gte
parameter_list|(
name|int
name|from
parameter_list|)
block|{
name|this
operator|.
name|from
operator|=
name|from
expr_stmt|;
name|this
operator|.
name|includeLower
operator|=
literal|true
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The from part of the filter query. Null indicates unbounded.      */
DECL|method|gte
specifier|public
name|RangeFilterBuilder
name|gte
parameter_list|(
name|long
name|from
parameter_list|)
block|{
name|this
operator|.
name|from
operator|=
name|from
expr_stmt|;
name|this
operator|.
name|includeLower
operator|=
literal|true
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The from part of the filter query. Null indicates unbounded.      */
DECL|method|gte
specifier|public
name|RangeFilterBuilder
name|gte
parameter_list|(
name|float
name|from
parameter_list|)
block|{
name|this
operator|.
name|from
operator|=
name|from
expr_stmt|;
name|this
operator|.
name|includeLower
operator|=
literal|true
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The from part of the filter query. Null indicates unbounded.      */
DECL|method|gte
specifier|public
name|RangeFilterBuilder
name|gte
parameter_list|(
name|double
name|from
parameter_list|)
block|{
name|this
operator|.
name|from
operator|=
name|from
expr_stmt|;
name|this
operator|.
name|includeLower
operator|=
literal|true
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The to part of the filter query. Null indicates unbounded.      */
DECL|method|to
specifier|public
name|RangeFilterBuilder
name|to
parameter_list|(
name|Object
name|to
parameter_list|)
block|{
name|this
operator|.
name|to
operator|=
name|to
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The to part of the filter query. Null indicates unbounded.      */
DECL|method|to
specifier|public
name|RangeFilterBuilder
name|to
parameter_list|(
name|int
name|to
parameter_list|)
block|{
name|this
operator|.
name|to
operator|=
name|to
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The to part of the filter query. Null indicates unbounded.      */
DECL|method|to
specifier|public
name|RangeFilterBuilder
name|to
parameter_list|(
name|long
name|to
parameter_list|)
block|{
name|this
operator|.
name|to
operator|=
name|to
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The to part of the filter query. Null indicates unbounded.      */
DECL|method|to
specifier|public
name|RangeFilterBuilder
name|to
parameter_list|(
name|float
name|to
parameter_list|)
block|{
name|this
operator|.
name|to
operator|=
name|to
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The to part of the filter query. Null indicates unbounded.      */
DECL|method|to
specifier|public
name|RangeFilterBuilder
name|to
parameter_list|(
name|double
name|to
parameter_list|)
block|{
name|this
operator|.
name|to
operator|=
name|to
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The to part of the filter query. Null indicates unbounded.      */
DECL|method|lt
specifier|public
name|RangeFilterBuilder
name|lt
parameter_list|(
name|Object
name|to
parameter_list|)
block|{
name|this
operator|.
name|to
operator|=
name|to
expr_stmt|;
name|this
operator|.
name|includeUpper
operator|=
literal|false
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The to part of the filter query. Null indicates unbounded.      */
DECL|method|lt
specifier|public
name|RangeFilterBuilder
name|lt
parameter_list|(
name|int
name|to
parameter_list|)
block|{
name|this
operator|.
name|to
operator|=
name|to
expr_stmt|;
name|this
operator|.
name|includeUpper
operator|=
literal|false
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The to part of the filter query. Null indicates unbounded.      */
DECL|method|lt
specifier|public
name|RangeFilterBuilder
name|lt
parameter_list|(
name|long
name|to
parameter_list|)
block|{
name|this
operator|.
name|to
operator|=
name|to
expr_stmt|;
name|this
operator|.
name|includeUpper
operator|=
literal|false
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The to part of the filter query. Null indicates unbounded.      */
DECL|method|lt
specifier|public
name|RangeFilterBuilder
name|lt
parameter_list|(
name|float
name|to
parameter_list|)
block|{
name|this
operator|.
name|to
operator|=
name|to
expr_stmt|;
name|this
operator|.
name|includeUpper
operator|=
literal|false
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The to part of the filter query. Null indicates unbounded.      */
DECL|method|lt
specifier|public
name|RangeFilterBuilder
name|lt
parameter_list|(
name|double
name|to
parameter_list|)
block|{
name|this
operator|.
name|to
operator|=
name|to
expr_stmt|;
name|this
operator|.
name|includeUpper
operator|=
literal|false
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The to part of the filter query. Null indicates unbounded.      */
DECL|method|lte
specifier|public
name|RangeFilterBuilder
name|lte
parameter_list|(
name|String
name|to
parameter_list|)
block|{
name|this
operator|.
name|to
operator|=
name|to
expr_stmt|;
name|this
operator|.
name|includeUpper
operator|=
literal|true
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The to part of the filter query. Null indicates unbounded.      */
DECL|method|lte
specifier|public
name|RangeFilterBuilder
name|lte
parameter_list|(
name|int
name|to
parameter_list|)
block|{
name|this
operator|.
name|to
operator|=
name|to
expr_stmt|;
name|this
operator|.
name|includeUpper
operator|=
literal|true
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The to part of the filter query. Null indicates unbounded.      */
DECL|method|lte
specifier|public
name|RangeFilterBuilder
name|lte
parameter_list|(
name|long
name|to
parameter_list|)
block|{
name|this
operator|.
name|to
operator|=
name|to
expr_stmt|;
name|this
operator|.
name|includeUpper
operator|=
literal|true
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The to part of the filter query. Null indicates unbounded.      */
DECL|method|lte
specifier|public
name|RangeFilterBuilder
name|lte
parameter_list|(
name|float
name|to
parameter_list|)
block|{
name|this
operator|.
name|to
operator|=
name|to
expr_stmt|;
name|this
operator|.
name|includeUpper
operator|=
literal|true
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The to part of the filter query. Null indicates unbounded.      */
DECL|method|lte
specifier|public
name|RangeFilterBuilder
name|lte
parameter_list|(
name|double
name|to
parameter_list|)
block|{
name|this
operator|.
name|to
operator|=
name|to
expr_stmt|;
name|this
operator|.
name|includeUpper
operator|=
literal|true
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Should the lower bound be included or not. Defaults to<tt>true</tt>.      */
DECL|method|includeLower
specifier|public
name|RangeFilterBuilder
name|includeLower
parameter_list|(
name|boolean
name|includeLower
parameter_list|)
block|{
name|this
operator|.
name|includeLower
operator|=
name|includeLower
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Should the upper bound be included or not. Defaults to<tt>true</tt>.      */
DECL|method|includeUpper
specifier|public
name|RangeFilterBuilder
name|includeUpper
parameter_list|(
name|boolean
name|includeUpper
parameter_list|)
block|{
name|this
operator|.
name|includeUpper
operator|=
name|includeUpper
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|filterName
specifier|public
name|RangeFilterBuilder
name|filterName
parameter_list|(
name|String
name|filterName
parameter_list|)
block|{
name|this
operator|.
name|filterName
operator|=
name|filterName
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|doXContent
annotation|@
name|Override
specifier|protected
name|void
name|doXContent
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
name|builder
operator|.
name|startObject
argument_list|(
name|RangeFilterParser
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startObject
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"from"
argument_list|,
name|from
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"to"
argument_list|,
name|to
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"include_lower"
argument_list|,
name|includeLower
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"include_upper"
argument_list|,
name|includeUpper
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
if|if
condition|(
name|filterName
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"_name"
argument_list|,
name|filterName
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

